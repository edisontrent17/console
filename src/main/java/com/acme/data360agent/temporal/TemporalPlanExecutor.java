package com.acme.data360agent.temporal;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.config.TemporalProperties;
import com.acme.data360agent.execution.PlanExecutor;
import com.acme.data360agent.execution.PlanExecutionOrder;
import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.execution.PlanRunSupport;
import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.execution.StepStatus;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnProperty(name = "app.executor", havingValue = "temporal")
public class TemporalPlanExecutor implements PlanExecutor {
    private final PlanStore store;
    private final WorkflowClient client;
    private final TemporalProperties properties;
    private final AuditService audit;

    public TemporalPlanExecutor(PlanStore store, WorkflowClient client, TemporalProperties properties, AuditService audit) {
        this.store = store;
        this.client = client;
        this.properties = properties;
        this.audit = audit;
    }

    @Override
    public PlanRun start(ApprovedExecutablePlan approvedPlan) {
        return start(PlanStore.DEFAULT_ORGANIZATION_ID, approvedPlan);
    }

    @Override
    public PlanRun start(String organizationId, ApprovedExecutablePlan approvedPlan) {
        var run = store.createRun(organizationId, approvedPlan);
        audit.event(run.getOrganizationId(), run.getId(), approvedPlan.plan().id(), null, "run_started", Map.of("status", run.getStatus().name(), "executor", "temporal"));
        var workflow = client.newWorkflowStub(Data360PlanWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(properties.workflowId(run.getId()))
                .setTaskQueue(properties.resolvedTaskQueue())
                .build());
        WorkflowClient.start(workflow::run, run.getOrganizationId(), run.getId(), approvedPlan.plan(), run.getOperationBindings(), PlanExecutionOrder.stepIds(approvedPlan));
        return run;
    }

    @Override
    public PlanRun approveStep(String runId, String stepId, String approvedBy) {
        return approveStep(PlanStore.DEFAULT_ORGANIZATION_ID, runId, stepId, approvedBy);
    }

    @Override
    public PlanRun approveStep(String organizationId, String runId, String stepId, String approvedBy) {
        var run = store.withRunLock(organizationId, runId, lockedRun -> {
            var planStep = PlanRunSupport.planStep(lockedRun, stepId);
            if (!planStep.needsApproval() && !lockedRun.bindingForStep(planStep).requiresApproval()) {
                throw new IllegalArgumentException("Step does not require approval: " + stepId);
            }
            var current = PlanRunSupport.stepRun(lockedRun, stepId);
            if (current.getStatus() == StepStatus.SUCCEEDED || current.getStatus() == StepStatus.SKIPPED) {
                return lockedRun;
            }
            if (current.getStatus() != StepStatus.WAITING_APPROVAL) {
                throw new IllegalArgumentException("Step is not waiting for approval: " + stepId);
            }
            return lockedRun;
        });
        workflow(runId).approveStep(stepId, approvedBy);
        return run;
    }

    @Override
    public PlanRun cancelRun(String organizationId, String runId, String reason, String canceledBy) {
        var run = store.run(organizationId, runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        workflow(runId).cancel(reason == null || reason.isBlank() ? "Canceled by " + (canceledBy == null || canceledBy.isBlank() ? "system" : canceledBy) : reason);
        return run;
    }

    private Data360PlanWorkflow workflow(String runId) {
        return client.newWorkflowStub(Data360PlanWorkflow.class, properties.workflowId(runId));
    }
}
