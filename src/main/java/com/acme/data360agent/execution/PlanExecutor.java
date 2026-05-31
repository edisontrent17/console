package com.acme.data360agent.execution;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.plan.PlanSpec;

import java.util.List;

public interface PlanExecutor {
    PlanRun start(PlanSpec plan);

    default PlanRun start(PlanSpec plan, List<OperationBindingSnapshot> operationBindings) {
        return start(plan);
    }

    default PlanRun approveStep(String runId, String stepId) {
        return approveStep(runId, stepId, "system");
    }

    PlanRun approveStep(String runId, String stepId, String approvedBy);
}
