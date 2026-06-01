package com.acme.data360agent.temporal;

import com.acme.data360agent.execution.PlanInputResolver;
import com.acme.data360agent.execution.ExecutionIdempotency;
import com.acme.data360agent.execution.StepOutputSelector;
import com.acme.data360agent.execution.RunStatus;
import com.acme.data360agent.execution.StepStatus;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationBindingResolver;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanTopology;
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
    private static final ActivityOptions DATA360_READ_ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(3)
                    .setInitialInterval(Duration.ofSeconds(2))
                    .setBackoffCoefficient(2)
                    .build())
            .build();
    private static final ActivityOptions DATA360_MUTATION_ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(1)
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

    private final Data360Activities data360Reads = Workflow.newActivityStub(Data360Activities.class, DATA360_READ_ACTIVITY_OPTIONS);
    private final Data360Activities data360Mutations = Workflow.newActivityStub(Data360Activities.class, DATA360_MUTATION_ACTIVITY_OPTIONS);
    private final PlanRunActivities state = Workflow.newActivityStub(PlanRunActivities.class, STATE_ACTIVITY_OPTIONS);

    private final Set<String> approvedSteps = new LinkedHashSet<>();
    private final Map<String, String> approvalActorsByStep = new LinkedHashMap<>();
    private final Map<String, OperationBindingSnapshot> bindingsByResource = new LinkedHashMap<>();
    private final Map<String, StepStatus> stepStatuses = new LinkedHashMap<>();
    private final Map<String, Map<String, Object>> outputsByStep = new LinkedHashMap<>();
    private List<String> executionOrder = List.of();
    private RunStatus runStatus = RunStatus.RUNNING;
    private String waitingStepId;
    private String currentStepId;
    private String cancelReason;

    @Override
    public String run(String organizationId, String runId, PlanSpec plan, List<OperationBindingSnapshot> operationBindings, List<String> executionOrder) {
        initialize(plan, operationBindings, executionOrder);
        while (cancelReason == null) {
            skipReadyMonitorSteps(organizationId, runId, plan);
            var next = nextRunnableStep(plan);
            if (next == null) {
                if (allTerminal()) {
                    runStatus = RunStatus.SUCCEEDED;
                    state.completeRun(organizationId, runId, plan.id());
                }
                return runId;
            }

            if (requiresApproval(next) && !approvedSteps.contains(next.id())) {
                waitForApproval(organizationId, runId, plan, next);
                if (cancelReason != null) {
                    break;
                }
            }

            executeStep(organizationId, runId, plan, next);
            if (runStatus == RunStatus.FAILED) {
                return runId;
            }
        }
        runStatus = RunStatus.CANCELED;
        state.cancelRun(organizationId, runId, plan.id(), cancelReason);
        return runId;
    }

    @Override
    public void approveStep(String stepId, String approvedBy) {
        approvedSteps.add(stepId);
        approvalActorsByStep.put(stepId, approvedBy == null || approvedBy.isBlank() ? "system" : approvedBy);
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

    private void initialize(PlanSpec plan, List<OperationBindingSnapshot> operationBindings, List<String> approvedExecutionOrder) {
        if (!stepStatuses.isEmpty()) {
            return;
        }
        executionOrder = normalizeExecutionOrder(plan, approvedExecutionOrder);
        for (var binding : operationBindings == null ? List.<OperationBindingSnapshot>of() : operationBindings) {
            bindingsByResource.put(binding.resource(), binding);
        }
        for (var step : plan.steps()) {
            stepStatuses.put(step.id(), StepStatus.PENDING);
            bindingFor(step);
        }
    }

    private void waitForApproval(String organizationId, String runId, PlanSpec plan, PlanStep step) {
        waitingStepId = step.id();
        currentStepId = null;
        runStatus = RunStatus.WAITING_APPROVAL;
        stepStatuses.put(step.id(), StepStatus.WAITING_APPROVAL);
        state.waitingForApproval(organizationId, runId, plan.id(), step.id(), step.action().value());
        Workflow.await(() -> approvedSteps.contains(step.id()) || cancelReason != null);
        waitingStepId = null;
        if (cancelReason == null) {
            state.stepApproved(organizationId, runId, plan.id(), step.id(), approvalActorsByStep.getOrDefault(step.id(), "system"));
            stepStatuses.put(step.id(), StepStatus.PENDING);
            runStatus = RunStatus.RUNNING;
        }
    }

    private void executeStep(String organizationId, String runId, PlanSpec plan, PlanStep step) {
        currentStepId = step.id();
        runStatus = RunStatus.RUNNING;
        stepStatuses.put(step.id(), StepStatus.RUNNING);
        state.stepStarted(organizationId, runId, plan.id(), step.id(), step.action().value());
        try {
            var binding = bindingFor(step);
            var resolved = PlanInputResolver.resolve(step, this::outputForStep);
            var idempotencyKey = ExecutionIdempotency.forStep(organizationId, runId, plan.id(), step, binding, resolved);
            state.stepToolCallPrepared(organizationId, runId, plan.id(), step.id(), step.action().value(), binding, resolved, idempotencyKey);
            var result = data360(binding).executeStep(new ActivityCommand(organizationId, runId, plan.id(), plan.context(), binding, step, resolved, idempotencyKey));
            var output = StepOutputSelector.apply(step, result.output(), binding);
            outputsByStep.put(step.id(), output);
            stepStatuses.put(step.id(), StepStatus.SUCCEEDED);
            state.stepSucceeded(organizationId, runId, plan.id(), step.id(), step.action().value(), output, result.raw());
        } catch (Exception e) {
            runStatus = RunStatus.FAILED;
            stepStatuses.put(step.id(), StepStatus.FAILED);
            state.stepFailed(organizationId, runId, plan.id(), step.id(), message(e));
        } finally {
            currentStepId = null;
        }
    }

    private OperationBindingSnapshot bindingFor(PlanStep step) {
        var resource = OperationBindingResolver.resourceFor(step);
        var binding = bindingsByResource.get(resource);
        if (binding == null) {
            throw new IllegalArgumentException("No operation binding snapshot for resource: " + resource);
        }
        return binding;
    }

    private boolean requiresApproval(PlanStep step) {
        return step.needsApproval() || bindingFor(step).requiresApproval();
    }

    private Data360Activities data360(OperationBindingSnapshot binding) {
        return binding.effect() == Effect.READ ? data360Reads : data360Mutations;
    }

    private Map<String, Object> outputForStep(String stepId) {
        var output = outputsByStep.get(stepId);
        if (output == null) {
            throw new IllegalStateException("Step " + stepId + " has not produced output.");
        }
        return output;
    }

    private PlanStep nextRunnableStep(PlanSpec plan) {
        for (var step : PlanTopology.steps(plan, executionOrder)) {
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

    private void skipReadyMonitorSteps(String organizationId, String runId, PlanSpec plan) {
        for (var step : PlanTopology.steps(plan, executionOrder)) {
            if (step.phase() != PlanPhase.MONITOR || stepStatuses.get(step.id()) != StepStatus.PENDING) {
                continue;
            }
            if (dependenciesSucceeded(step.dependsOn())) {
                stepStatuses.put(step.id(), StepStatus.SKIPPED);
                outputsByStep.put(step.id(), Map.of("registeredAsMonitor", true));
                state.skipMonitorStep(organizationId, runId, plan.id(), step.id());
            }
        }
    }

    private boolean dependenciesSucceeded(List<String> dependencies) {
        return dependencies.stream().allMatch(dep -> stepStatuses.get(dep) == StepStatus.SUCCEEDED);
    }

    private boolean allTerminal() {
        return stepStatuses.values().stream().allMatch(status -> status == StepStatus.SUCCEEDED || status == StepStatus.SKIPPED);
    }

    private List<String> normalizeExecutionOrder(PlanSpec plan, List<String> approvedExecutionOrder) {
        if (approvedExecutionOrder == null || approvedExecutionOrder.isEmpty()) {
            return PlanTopology.stepIds(plan);
        }
        var known = plan.steps().stream().map(PlanStep::id).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        var ordered = new ArrayList<String>();
        for (var id : approvedExecutionOrder) {
            if (known.remove(id)) {
                ordered.add(id);
            }
        }
        ordered.addAll(known);
        return List.copyOf(ordered);
    }

    private String message(Exception e) {
        var message = e.getMessage();
        return message == null || message.isBlank() ? e.getClass().getSimpleName() : message;
    }
}
