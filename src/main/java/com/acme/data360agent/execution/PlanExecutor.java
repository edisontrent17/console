package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanSpec;

public interface PlanExecutor {
    PlanRun start(PlanSpec plan);

    default PlanRun approveStep(String runId, String stepId) {
        return approveStep(runId, stepId, "system");
    }

    PlanRun approveStep(String runId, String stepId, String approvedBy);
}
