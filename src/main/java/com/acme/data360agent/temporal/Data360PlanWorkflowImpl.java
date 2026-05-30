package com.acme.data360agent.temporal;

import com.acme.data360agent.data360.Data360CallResult;
import com.acme.data360agent.execution.PlanInputResolver;
import com.acme.data360agent.execution.RunStatus;
import com.acme.data360agent.execution.StepStatus;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data360PlanWorkflowImpl implements Data360PlanWorkflow {
    private static final ActivityOptions DATA360_ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(3)
                    .setInitialInterval(Duration.ofSeconds(2))
                    .setBackoffCoefficient(2)
                    .build())
            .build();
    private static final ActivityOptions STATE_ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(5)
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setBackoffCoefficient(2)
                    .build())
            .build();

    private final Data360Activities data360 = Workflow.newActivityStub(Data360Activities.class, DATA360_ACTIVITY_OPTIONS);
    private final PlanRunActivities state = Workflow.newActivityStub(PlanRunActivities.class, STATE_ACTIVITY_OPTIONS);

    private final Set<String> approvedSteps = new LinkedHashSet<>();
    private final Map<String, StepStatus> stepStatuses = new LinkedHashMap<>();
    private final Map<String, Map<String, Object>> outputsByStep = new LinkedHashMap<>();
    private RunStatus runStatus = RunStatus.RUNNING;
    private String waitingStepId;
    private String currentStepId;
    private String cancelReason;

    @Override
    public String run(String runId, PlanSpec plan) {
        initialize(plan);
        while (cancelReason == null) {
            skipReadyMonitorSteps(runId, plan);
            var next = nextRunnableStep(plan);
            if (next == null) {
                if (allTerminal()) {
                    runStatus = RunStatus.SUCCEEDED;
                    state.completeRun(runId, plan.id());
                }
                return runId;
            }

            if (next.needsApproval() && !approvedSteps.contains(next.id())) {
                waitForApproval(runId, plan, next);
                if (cancelReason != null) {
                    break;
                }
            }

            executeStep(runId, plan, next);
            if (runStatus == RunStatus.FAILED) {
                return runId;
            }
        }
        runStatus = RunStatus.CANCELED;
        state.cancelRun(runId, plan.id(), cancelReason);
        return runId;
    }

    @Override
    public void approveStep(String stepId) {
        approvedSteps.add(stepId);
    }

    @Override
    public void cancel(String reason) {
        cancelReason = reason == null || reason.isBlank() ? "Canceled by signal." : reason;
    }

    @Override
    public Map<String, Object> status() {
        return Map.of(
                "status", runStatus.name(),
                "waitingStepId", waitingStepId == null ? "" : waitingStepId,
                "currentStepId", currentStepId == null ? "" : currentStepId,
                "approvedSteps", new ArrayList<>(approvedSteps),
                "stepStatuses", new LinkedHashMap<>(stepStatuses),
                "cancelReason", cancelReason == null ? "" : cancelReason
        );
    }

    private void initialize(PlanSpec plan) {
        if (!stepStatuses.isEmpty()) {
            return;
        }
        for (var step : plan.steps()) {
            stepStatuses.put(step.id(), StepStatus.PENDING);
        }
    }

    private void waitForApproval(String runId, PlanSpec plan, PlanStep step) {
        waitingStepId = step.id();
        currentStepId = null;
        runStatus = RunStatus.WAITING_APPROVAL;
        stepStatuses.put(step.id(), StepStatus.WAITING_APPROVAL);
        state.waitingForApproval(runId, plan.id(), step.id(), step.action().value());
        Workflow.await(() -> approvedSteps.contains(step.id()) || cancelReason != null);
        waitingStepId = null;
        if (cancelReason == null) {
            state.stepApproved(runId, plan.id(), step.id());
            stepStatuses.put(step.id(), StepStatus.PENDING);
            runStatus = RunStatus.RUNNING;
        }
    }

    private void executeStep(String runId, PlanSpec plan, PlanStep step) {
        currentStepId = step.id();
        runStatus = RunStatus.RUNNING;
        stepStatuses.put(step.id(), StepStatus.RUNNING);
        state.stepStarted(runId, plan.id(), step.id(), step.action().value());
        try {
            var operation = operation(step);
            var resolved = PlanInputResolver.resolve(step, this::outputForStep);
            Data360CallResult result = data360.executeStep(runId, plan.id(), plan.context(), operation, step, resolved);
            outputsByStep.put(step.id(), result.output());
            stepStatuses.put(step.id(), StepStatus.SUCCEEDED);
            state.stepSucceeded(runId, plan.id(), step.id(), step.action().value(), result.output(), result.raw());
        } catch (Exception e) {
            runStatus = RunStatus.FAILED;
            stepStatuses.put(step.id(), StepStatus.FAILED);
            state.stepFailed(runId, plan.id(), step.id(), message(e));
        } finally {
            currentStepId = null;
        }
    }

    private OperationDefinition operation(PlanStep step) {
        return new OperationRegistry().require(step.action());
    }

    private Map<String, Object> outputForStep(String stepId) {
        var output = outputsByStep.get(stepId);
        if (output == null) {
            throw new IllegalStateException("Step " + stepId + " has not produced output.");
        }
        return output;
    }

    private PlanStep nextRunnableStep(PlanSpec plan) {
        for (var step : plan.steps()) {
            if (step.phase() == PlanPhase.MONITOR) {
                continue;
            }
            var status = stepStatuses.get(step.id());
            if (status == StepStatus.SUCCEEDED || status == StepStatus.SKIPPED || status == StepStatus.RUNNING || status == StepStatus.WAITING_APPROVAL) {
                continue;
            }
            if (dependenciesSucceeded(step.dependsOn())) {
                return step;
            }
        }
        return null;
    }

    private void skipReadyMonitorSteps(String runId, PlanSpec plan) {
        for (var step : plan.steps()) {
            if (step.phase() != PlanPhase.MONITOR || stepStatuses.get(step.id()) != StepStatus.PENDING) {
                continue;
            }
            if (dependenciesSucceeded(step.dependsOn())) {
                stepStatuses.put(step.id(), StepStatus.SKIPPED);
                outputsByStep.put(step.id(), Map.of("registeredAsMonitor", true));
                state.skipMonitorStep(runId, plan.id(), step.id());
            }
        }
    }

    private boolean dependenciesSucceeded(List<String> dependencies) {
        return dependencies.stream().allMatch(dep -> stepStatuses.get(dep) == StepStatus.SUCCEEDED);
    }

    private boolean allTerminal() {
        return stepStatuses.values().stream().allMatch(status -> status == StepStatus.SUCCEEDED || status == StepStatus.SKIPPED);
    }

    private String message(Exception e) {
        var message = e.getMessage();
        return message == null || message.isBlank() ? e.getClass().getSimpleName() : message;
    }
}
