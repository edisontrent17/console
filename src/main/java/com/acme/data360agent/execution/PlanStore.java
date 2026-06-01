package com.acme.data360agent.execution;

import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.planner.ApprovedExecutablePlan;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public interface PlanStore {
    String DEFAULT_ORGANIZATION_ID = "org_default";

    PlanDraft saveDraft(String organizationId, PlanDraft draft);

    default PlanDraft saveDraft(PlanDraft draft) {
        return saveDraft(DEFAULT_ORGANIZATION_ID, draft);
    }

    Optional<PlanDraft> draft(String organizationId, String planId);

    default Optional<PlanDraft> draft(String planId) {
        return draft(DEFAULT_ORGANIZATION_ID, planId);
    }

    Collection<PlanDraft> drafts(String organizationId);

    default Collection<PlanDraft> drafts() {
        return drafts(DEFAULT_ORGANIZATION_ID);
    }

    ApprovedExecutablePlan saveApprovedPlan(String organizationId, ApprovedExecutablePlan approvedPlan);

    default ApprovedExecutablePlan saveApprovedPlan(ApprovedExecutablePlan approvedPlan) {
        return saveApprovedPlan(DEFAULT_ORGANIZATION_ID, approvedPlan);
    }

    Optional<ApprovedExecutablePlan> approvedPlan(String organizationId, String planId);

    default Optional<ApprovedExecutablePlan> approvedPlan(String planId) {
        return approvedPlan(DEFAULT_ORGANIZATION_ID, planId);
    }

    void clearApprovedPlan(String organizationId, String planId);

    default void clearApprovedPlan(String planId) {
        clearApprovedPlan(DEFAULT_ORGANIZATION_ID, planId);
    }

    PlanRun createRun(String organizationId, ApprovedExecutablePlan approvedPlan);

    default PlanRun createRun(ApprovedExecutablePlan approvedPlan) {
        return createRun(DEFAULT_ORGANIZATION_ID, approvedPlan);
    }

    Optional<PlanRun> run(String organizationId, String runId);

    default Optional<PlanRun> run(String runId) {
        return run(DEFAULT_ORGANIZATION_ID, runId);
    }

    PlanRun saveRun(String organizationId, PlanRun run);

    default PlanRun saveRun(PlanRun run) {
        return saveRun(run.getOrganizationId(), run);
    }

    <T> T withRunLock(String organizationId, String runId, Function<PlanRun, T> work);

    default <T> T withRunLock(String runId, Function<PlanRun, T> work) {
        return withRunLock(DEFAULT_ORGANIZATION_ID, runId, work);
    }
}
