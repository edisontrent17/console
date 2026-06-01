package com.acme.data360agent.execution;

import com.acme.data360agent.planner.ApprovedExecutablePlan;

public interface PlanExecutor {
    PlanRun start(ApprovedExecutablePlan approvedPlan);

    default PlanRun start(String organizationId, ApprovedExecutablePlan approvedPlan) {
        return start(approvedPlan);
    }

    default PlanRun approveStep(String runId, String stepId) {
        return approveStep(runId, stepId, "system");
    }

    PlanRun approveStep(String runId, String stepId, String approvedBy);

    default PlanRun approveStep(String organizationId, String runId, String stepId, String approvedBy) {
        return approveStep(runId, stepId, approvedBy);
    }

    default PlanRun cancelRun(String runId, String reason, String canceledBy) {
        return cancelRun(PlanStore.DEFAULT_ORGANIZATION_ID, runId, reason, canceledBy);
    }

    default PlanRun cancelRun(String organizationId, String runId, String reason, String canceledBy) {
        throw new UnsupportedOperationException("Run cancellation is not supported by this executor.");
    }
}
