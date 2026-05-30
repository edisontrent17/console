package com.acme.data360agent.execution;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.data360.Data360Client;
import com.acme.data360agent.monitor.MonitorService;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@ConditionalOnProperty(name = "app.executor", havingValue = "local", matchIfMissing = true)
public class LocalPlanExecutor implements PlanExecutor {
    private final PlanStore store;
    private final OperationRegistry operations;
    private final Data360Client data360Client;
    private final MonitorService monitorService;
    private final AuditService audit;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    public LocalPlanExecutor(PlanStore store, OperationRegistry operations, Data360Client data360Client, MonitorService monitorService, AuditService audit) {
        this.store = store;
        this.operations = operations;
        this.data360Client = data360Client;
        this.monitorService = monitorService;
        this.audit = audit;
    }

    public LocalPlanExecutor(PlanStore store, OperationRegistry operations, Data360Client data360Client) {
        this(store, operations, data360Client, null, null);
    }

    @Override
    public PlanRun start(PlanSpec plan) {
        var run = store.createRun(plan);
        event(run, null, "run_started", Map.of("status", run.getStatus().name()));
        resumeAsync(run.getId());
        return run;
    }

    @Override
    public PlanRun approveStep(String runId, String stepId) {
        var run = store.withRunLock(runId, lockedRun -> {
            var runRef = lockedRun;
            var planStep = PlanRunSupport.planStep(runRef, stepId);
            if (!planStep.needsApproval()) {
                throw new IllegalArgumentException("Step does not require approval: " + stepId);
            }
            var current = PlanRunSupport.stepRun(runRef, stepId);
            if (current.getStatus() == StepStatus.SUCCEEDED || current.getStatus() == StepStatus.SKIPPED) {
                return runRef;
            }
            if (current.getStatus() != StepStatus.WAITING_APPROVAL) {
                throw new IllegalArgumentException("Step is not waiting for approval: " + stepId);
            }
            runRef.getApprovedSteps().add(stepId);
            current.setStatus(StepStatus.PENDING);
            runRef.setStatus(RunStatus.RUNNING);
            store.saveRun(runRef);
            if (audit != null) {
                audit.approval(runRef.getId(), stepId, "local-user", "APPROVED", Map.of("planId", runRef.getPlan().id()));
            }
            event(runRef, stepId, "step_approved", Map.of("status", current.getStatus().name()));
            return runRef;
        });
        resumeAsync(runId);
        return run;
    }

    private void resumeAsync(String runId) {
        executor.submit(() -> resume(runId));
    }

    private void resume(String runId) {
        while (true) {
            var execution = store.withRunLock(runId, run -> {
                skipReadyMonitorSteps(run);
                var next = nextRunnableStep(run);
                if (next == null) {
                    if (run.getSteps().stream().allMatch(step -> step.getStatus() == StepStatus.SUCCEEDED || step.getStatus() == StepStatus.SKIPPED)) {
                        run.setStatus(RunStatus.SUCCEEDED);
                        registerMonitors(run);
                        store.saveRun(run);
                        event(run, null, "run_succeeded", Map.of("status", run.getStatus().name()));
                    }
                    return null;
                }
                var stepRun = PlanRunSupport.stepRun(run, next.id());
                if (next.needsApproval() && !run.getApprovedSteps().contains(next.id())) {
                    stepRun.setStatus(StepStatus.WAITING_APPROVAL);
                    run.setStatus(RunStatus.WAITING_APPROVAL);
                    store.saveRun(run);
                    event(run, next.id(), "step_waiting_approval", Map.of("action", next.action().value()));
                    return null;
                }
                stepRun.setStatus(StepStatus.RUNNING);
                stepRun.setStartedAt(Instant.now());
                run.setStatus(RunStatus.RUNNING);
                store.saveRun(run);
                event(run, next.id(), "step_started", Map.of("action", next.action().value()));
                return new ExecutionStep(run.getPlan(), next);
            });
            if (execution == null) {
                return;
            }

            try {
                var definition = operations.require(execution.step().action());
                var runSnapshot = store.run(runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
                var resolved = resolveInput(runSnapshot, execution.step());
                var result = data360Client.call(definition, execution.step(), resolved, new RunContext(runId, execution.plan().id(), execution.plan().context()));
                store.withRunLock(runId, run -> {
                    var stepRun = PlanRunSupport.stepRun(run, execution.step().id());
                    stepRun.setOutput(result.output());
                    stepRun.setRaw(result.raw());
                    stepRun.setStatus(StepStatus.SUCCEEDED);
                    stepRun.setFinishedAt(Instant.now());
                    store.saveRun(run);
                    event(run, execution.step().id(), "step_succeeded", Map.of("action", execution.step().action().value()));
                    return run;
                });
            } catch (Exception e) {
                store.withRunLock(runId, run -> {
                    var stepRun = PlanRunSupport.stepRun(run, execution.step().id());
                    stepRun.setError(e.getMessage());
                    stepRun.setStatus(StepStatus.FAILED);
                    stepRun.setFinishedAt(Instant.now());
                    run.setStatus(RunStatus.FAILED);
                    store.saveRun(run);
                    event(run, execution.step().id(), "step_failed", Map.of("error", e.getMessage()));
                    return run;
                });
                return;
            }
        }
    }

    private PlanStep nextRunnableStep(PlanRun run) {
        for (var step : run.getPlan().steps()) {
            if (step.phase() == PlanPhase.MONITOR) {
                continue;
            }
            var current = PlanRunSupport.stepRun(run, step.id());
            if (current.getStatus() == StepStatus.SUCCEEDED || current.getStatus() == StepStatus.SKIPPED) {
                continue;
            }
            var depsReady = step.dependsOn().stream().allMatch(dep -> PlanRunSupport.stepRun(run, dep).getStatus() == StepStatus.SUCCEEDED);
            if (depsReady) {
                return step;
            }
        }
        return null;
    }

    private void skipReadyMonitorSteps(PlanRun run) {
        for (var step : run.getPlan().steps()) {
            if (step.phase() != PlanPhase.MONITOR) {
                continue;
            }
            var current = PlanRunSupport.stepRun(run, step.id());
            if (current.getStatus() != StepStatus.PENDING) {
                continue;
            }
            var depsReady = step.dependsOn().stream().allMatch(dep -> PlanRunSupport.stepRun(run, dep).getStatus() == StepStatus.SUCCEEDED);
            if (depsReady) {
                current.setStatus(StepStatus.SKIPPED);
                current.setOutput(Map.of("registeredAsMonitor", true));
                current.setFinishedAt(Instant.now());
            }
        }
    }

    private void registerMonitors(PlanRun run) {
        if (monitorService != null) {
            monitorService.registerReadyFromRun(run);
        }
    }

    private void event(PlanRun run, String stepId, String type, Map<String, Object> detail) {
        if (audit != null) {
            audit.event(run.getId(), run.getPlan().id(), stepId, type, detail);
        }
    }

    private Map<String, Object> resolveInput(PlanRun run, PlanStep step) {
        return PlanInputResolver.resolve(step, stepId -> PlanRunSupport.outputForStep(run, stepId));
    }

    private record ExecutionStep(PlanSpec plan, PlanStep step) {
    }
}
