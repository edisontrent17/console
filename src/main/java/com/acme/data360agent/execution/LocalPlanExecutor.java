package com.acme.data360agent.execution;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.data360.Data360Client;
import com.acme.data360agent.monitor.MonitorService;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import com.acme.data360agent.support.SensitiveData;
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
    private final Data360Client data360Client;
    private final MonitorService monitorService;
    private final AuditService audit;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    public LocalPlanExecutor(PlanStore store, OperationRegistry operations, Data360Client data360Client, MonitorService monitorService, AuditService audit) {
        this.store = store;
        this.data360Client = data360Client;
        this.monitorService = monitorService;
        this.audit = audit;
    }

    public LocalPlanExecutor(PlanStore store, OperationRegistry operations, Data360Client data360Client) {
        this(store, operations, data360Client, null, null);
    }

    @Override
    public PlanRun start(ApprovedExecutablePlan approvedPlan) {
        return start(PlanStore.DEFAULT_ORGANIZATION_ID, approvedPlan);
    }

    @Override
    public PlanRun start(String organizationId, ApprovedExecutablePlan approvedPlan) {
        var run = store.createRun(organizationId, approvedPlan);
        event(run, null, "run_started", Map.of("status", run.getStatus().name()));
        resumeAsync(run.getOrganizationId(), run.getId());
        return run;
    }

    @Override
    public PlanRun approveStep(String runId, String stepId, String approvedBy) {
        return approveStep(PlanStore.DEFAULT_ORGANIZATION_ID, runId, stepId, approvedBy);
    }

    @Override
    public PlanRun approveStep(String organizationId, String runId, String stepId, String approvedBy) {
        var run = store.withRunLock(organizationId, runId, lockedRun -> {
            var runRef = lockedRun;
            if (runRef.getStatus() == RunStatus.CANCELED) {
                throw new IllegalArgumentException("Run is canceled: " + runId);
            }
            var planStep = PlanRunSupport.planStep(runRef, stepId);
            if (!requiresApproval(runRef, planStep)) {
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
                audit.approval(runRef.getOrganizationId(), runRef.getId(), stepId, approvedBy, "APPROVED", Map.of("planId", runRef.getPlan().id()));
            }
            event(runRef, stepId, "step_approved", Map.of("status", current.getStatus().name()));
            return runRef;
        });
        resumeAsync(organizationId, runId);
        return run;
    }

    @Override
    public PlanRun cancelRun(String organizationId, String runId, String reason, String canceledBy) {
        return store.withRunLock(organizationId, runId, run -> {
            if (PlanRunSupport.terminal(run.getStatus())) {
                return run;
            }
            run.setStatus(RunStatus.CANCELED);
            var now = Instant.now();
            for (var step : run.getSteps()) {
                if (!PlanRunSupport.terminal(step.getStatus())) {
                    step.setStatus(StepStatus.CANCELED);
                    step.setFinishedAt(now);
                }
            }
            store.saveRun(run);
            event(run, null, "run_canceled", Map.of(
                    "reason", reason == null ? "" : reason,
                    "canceledBy", canceledBy == null || canceledBy.isBlank() ? "system" : canceledBy
            ));
            return run;
        });
    }

    private void resumeAsync(String organizationId, String runId) {
        executor.submit(() -> resume(organizationId, runId));
    }

    private void resume(String organizationId, String runId) {
        while (true) {
            var execution = store.withRunLock(organizationId, runId, run -> {
                if (run.getStatus() == RunStatus.CANCELED) {
                    return null;
                }
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
                if (requiresApproval(run, next) && !run.getApprovedSteps().contains(next.id())) {
                    stepRun.setStatus(StepStatus.WAITING_APPROVAL);
                    run.setStatus(RunStatus.WAITING_APPROVAL);
                    store.saveRun(run);
                    event(run, next.id(), "step_waiting_approval", stepDetail(run, next));
                    return null;
                }
                stepRun.setStatus(StepStatus.RUNNING);
                stepRun.setStartedAt(Instant.now());
                run.setStatus(RunStatus.RUNNING);
                store.saveRun(run);
                event(run, next.id(), "step_started", stepDetail(run, next));
                return new ExecutionStep(run.getPlan(), next);
            });
            if (execution == null) {
                return;
            }

            try {
                var runSnapshot = store.run(organizationId, runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
                var binding = runSnapshot.bindingForStep(execution.step());
                var definition = OperationBindingDefinitions.from(binding);
                var resolved = resolveInput(runSnapshot, execution.step());
                var idempotencyKey = ExecutionIdempotency.forStep(organizationId, runId, execution.plan().id(), execution.step(), binding, resolved);
                var preparedRun = store.withRunLock(organizationId, runId, run -> {
                    if (run.getStatus() == RunStatus.CANCELED) {
                        return run;
                    }
                    var stepRun = PlanRunSupport.stepRun(run, execution.step().id());
                    stepRun.setBinding(binding);
                    stepRun.setResolvedInput(resolved);
                    store.saveRun(run);
                    event(run, execution.step().id(), "step_tool_call_prepared", Map.of(
                            "action", execution.step().action().value(),
                            "binding", binding.auditSummary(),
                            "idempotencyKey", idempotencyKey,
                            "resolvedInputKeys", resolved.keySet().stream().sorted().toList()
                    ));
                    return run;
                });
                if (preparedRun.getStatus() == RunStatus.CANCELED) {
                    return;
                }
                var result = data360Client.call(definition, binding, execution.step(), resolved, new RunContext(organizationId, runId, execution.plan().id(), execution.plan().context(), idempotencyKey));
                var output = StepOutputSelector.apply(execution.step(), result.output(), binding);
                store.withRunLock(organizationId, runId, run -> {
                    var stepRun = PlanRunSupport.stepRun(run, execution.step().id());
                    if (run.getStatus() == RunStatus.CANCELED) {
                        if (!PlanRunSupport.terminal(stepRun.getStatus())) {
                            stepRun.setStatus(StepStatus.CANCELED);
                            stepRun.setFinishedAt(Instant.now());
                            store.saveRun(run);
                            event(run, execution.step().id(), "step_canceled", Map.of("idempotencyKey", idempotencyKey));
                        }
                        return run;
                    }
                    stepRun.setOutput(output);
                    stepRun.setRaw(result.raw());
                    stepRun.setStatus(StepStatus.SUCCEEDED);
                    stepRun.setFinishedAt(Instant.now());
                    store.saveRun(run);
                    event(run, execution.step().id(), "step_succeeded", stepDetail(run, execution.step()));
                    return run;
                });
            } catch (Exception e) {
                store.withRunLock(organizationId, runId, run -> {
                    if (run.getStatus() == RunStatus.CANCELED) {
                        return run;
                    }
                    var error = SensitiveData.redactText(e.getMessage());
                    var stepRun = PlanRunSupport.stepRun(run, execution.step().id());
                    stepRun.setError(error);
                    stepRun.setStatus(StepStatus.FAILED);
                    stepRun.setFinishedAt(Instant.now());
                    run.setStatus(RunStatus.FAILED);
                    store.saveRun(run);
                    event(run, execution.step().id(), "step_failed", Map.of("error", error == null ? "" : error));
                    return run;
                });
                return;
            }
        }
    }

    private PlanStep nextRunnableStep(PlanRun run) {
        for (var step : PlanExecutionOrder.steps(run)) {
            if (step.phase() == PlanPhase.MONITOR) {
                continue;
            }
            var current = PlanRunSupport.stepRun(run, step.id());
            if (PlanRunSupport.terminal(current.getStatus()) || current.getStatus() == StepStatus.RUNNING || current.getStatus() == StepStatus.WAITING_APPROVAL) {
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
        for (var step : PlanExecutionOrder.steps(run)) {
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
            audit.event(run.getOrganizationId(), run.getId(), run.getPlan().id(), stepId, type, detail);
        }
    }

    private Map<String, Object> resolveInput(PlanRun run, PlanStep step) {
        return PlanInputResolver.resolve(step, stepId -> PlanRunSupport.outputForStep(run, stepId));
    }

    private Map<String, Object> stepDetail(PlanRun run, PlanStep step) {
        return Map.of(
            "action", step.action().value(),
            "binding", run.bindingForStep(step).auditSummary()
        );
    }

    private boolean requiresApproval(PlanRun run, PlanStep step) {
        return step.needsApproval() || run.bindingForStep(step).requiresApproval();
    }

    private record ExecutionStep(PlanSpec plan, PlanStep step) {
    }
}
