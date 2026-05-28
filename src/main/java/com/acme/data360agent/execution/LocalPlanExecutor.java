package com.acme.data360agent.execution;

import com.acme.data360agent.data360.Data360Client;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@ConditionalOnProperty(name = "app.executor", havingValue = "local", matchIfMissing = true)
public class LocalPlanExecutor implements PlanExecutor {
    private final InMemoryPlanStore store;
    private final OperationRegistry operations;
    private final Data360Client data360Client;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public LocalPlanExecutor(InMemoryPlanStore store, OperationRegistry operations, Data360Client data360Client) {
        this.store = store;
        this.operations = operations;
        this.data360Client = data360Client;
    }

    @Override
    public PlanRun start(PlanSpec plan) {
        var run = store.createRun(plan);
        resumeAsync(run);
        return run;
    }

    @Override
    public PlanRun approveStep(String runId, String stepId) {
        var run = store.run(runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        synchronized (run) {
            run.getApprovedSteps().add(stepId);
            stepRun(run, stepId).setStatus(StepStatus.PENDING);
            run.setStatus(RunStatus.RUNNING);
        }
        resumeAsync(run);
        return run;
    }

    private void resumeAsync(PlanRun run) {
        executor.submit(() -> resume(run));
    }

    private void resume(PlanRun run) {
        while (true) {
            PlanStep next;
            StepRun stepRun;
            synchronized (run) {
                next = nextRunnableStep(run);
                if (next == null) {
                    if (run.getSteps().stream().allMatch(step -> step.getStatus() == StepStatus.SUCCEEDED || step.getStatus() == StepStatus.SKIPPED)) {
                        run.setStatus(RunStatus.SUCCEEDED);
                    }
                    return;
                }
                stepRun = stepRun(run, next.id());
                if (next.needsApproval() && !run.getApprovedSteps().contains(next.id())) {
                    stepRun.setStatus(StepStatus.WAITING_APPROVAL);
                    run.setStatus(RunStatus.WAITING_APPROVAL);
                    return;
                }
                stepRun.setStatus(StepStatus.RUNNING);
                stepRun.setStartedAt(Instant.now());
                run.setStatus(RunStatus.RUNNING);
            }

            try {
                var definition = operations.require(next.action());
                var resolved = resolveInput(run, next);
                var result = data360Client.call(definition, next, resolved, new RunContext(run.getId(), run.getPlan().id(), run.getPlan().context()));
                synchronized (run) {
                    stepRun.setOutput(result.output());
                    stepRun.setRaw(result.raw());
                    stepRun.setStatus(StepStatus.SUCCEEDED);
                    stepRun.setFinishedAt(Instant.now());
                }
            } catch (Exception e) {
                synchronized (run) {
                    stepRun.setError(e.getMessage());
                    stepRun.setStatus(StepStatus.FAILED);
                    stepRun.setFinishedAt(Instant.now());
                    run.setStatus(RunStatus.FAILED);
                }
                return;
            }
        }
    }

    private PlanStep nextRunnableStep(PlanRun run) {
        for (var step : run.getPlan().steps()) {
            var current = stepRun(run, step.id());
            if (current.getStatus() == StepStatus.SUCCEEDED || current.getStatus() == StepStatus.SKIPPED) {
                continue;
            }
            var depsReady = step.dependsOn().stream().allMatch(dep -> stepRun(run, dep).getStatus() == StepStatus.SUCCEEDED);
            if (depsReady) {
                return step;
            }
        }
        return null;
    }

    private StepRun stepRun(PlanRun run, String stepId) {
        return run.getSteps().stream()
                .filter(step -> step.getStepId().equals(stepId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown step: " + stepId));
    }

    private Map<String, Object> resolveInput(PlanRun run, PlanStep step) {
        var resolved = new LinkedHashMap<>(step.input());
        resolveFromStep(run, resolved, "segmentIdFromStep", "segmentId");
        resolveFromStep(run, resolved, "activationIdFromStep", "activationId");
        if (resolved.containsKey("criteriaFromStep")) {
            var source = String.valueOf(resolved.get("criteriaFromStep"));
            var output = stepRun(run, source).getOutput();
            resolved.put("criteria", Map.of(
                    "sourceStep", source,
                    "rowCount", output.getOrDefault("rowCount", 0),
                    "sql", output.getOrDefault("sql", "")
            ));
        }
        return Map.copyOf(resolved);
    }

    private void resolveFromStep(PlanRun run, Map<String, Object> resolved, String refKey, String outputKey) {
        if (!resolved.containsKey(refKey)) {
            return;
        }
        var source = String.valueOf(resolved.get(refKey));
        var output = stepRun(run, source).getOutput();
        var value = output.get(outputKey);
        if (value == null) {
            throw new IllegalStateException("Step " + source + " did not produce " + outputKey);
        }
        resolved.put(outputKey, value);
    }
}
