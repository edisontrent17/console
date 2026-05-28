package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanSpec;

public interface PlanExecutor {
    PlanRun start(PlanSpec plan);

    PlanRun approveStep(String runId, String stepId);
}
