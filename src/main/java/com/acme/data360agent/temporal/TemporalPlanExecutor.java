package com.acme.data360agent.temporal;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.config.TemporalProperties;
import com.acme.data360agent.execution.PlanExecutor;
import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.execution.PlanRunSupport;
import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.execution.StepStatus;
import com.acme.data360agent.plan.PlanSpec;
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
    public PlanRun start(PlanSpec plan) {
        var run = store.createRun(plan);
        audit.event(run.getId(), plan.id(), null, "run_started", Map.of("status", run.getStatus().name(), "executor", "temporal"));
        var workflow = client.newWorkflowStub(Data360PlanWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(properties.workflowId(run.getId()))
                .setTaskQueue(properties.resolvedTaskQueue())
                .build());
        WorkflowClient.start(workflow::run, run.getId(), plan);
        return run;
    }

    @Override
    public PlanRun approveStep(String runId, String stepId) {
        var run = store.withRunLock(runId, lockedRun -> {
            var planStep = PlanRunSupport.planStep(lockedRun, stepId);
            if (!planStep.needsApproval()) {
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
        workflow(runId).approveStep(stepId);
        return run;
    }

    private Data360PlanWorkflow workflow(String runId) {
        return client.newWorkflowStub(Data360PlanWorkflow.class, properties.workflowId(runId));
    }
}
