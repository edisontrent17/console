package com.acme.data360agent.execution;

import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import com.acme.data360agent.planner.PlanDraft;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanStoreTenantScopeTest {
    @Test
    void isolatesDraftsByOrganization() {
        var store = new InMemoryPlanStore();
        store.saveDraft("org_a", draft(plan("plan_shared", "Preview org A")));
        store.saveDraft("org_b", draft(plan("plan_shared", "Preview org B")));

        assertThat(store.draft("org_a", "plan_shared")).get()
                .extracting(draft -> draft.plan().goal())
                .isEqualTo("Preview org A");
        assertThat(store.draft("org_b", "plan_shared")).get()
                .extracting(draft -> draft.plan().goal())
                .isEqualTo("Preview org B");
        assertThat(store.drafts("org_a"))
                .extracting(draft -> draft.plan().goal())
                .containsExactly("Preview org A");
    }

    @Test
    void isolatesRunsByOrganization() {
        var store = new InMemoryPlanStore();
        var run = store.createRun("org_a", ApprovedExecutablePlan.legacy(plan("plan_run", "Run org A"), List.of()));

        assertThat(run.getOrganizationId()).isEqualTo("org_a");
        assertThat(store.run("org_a", run.getId())).isPresent();
        assertThat(store.run("org_b", run.getId())).isEmpty();
        assertThatThrownBy(() -> store.withRunLock("org_b", run.getId(), locked -> locked))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(run.getId());
    }

    @Test
    void isolatesApprovedExecutablePlansByOrganizationAndClearsOnDraftChange() {
        var store = new InMemoryPlanStore();
        var orgAPlan = plan("plan_shared", "Approve org A");
        var orgBPlan = plan("plan_shared", "Approve org B");
        store.saveDraft("org_a", draft(orgAPlan));
        store.saveDraft("org_b", draft(orgBPlan));

        var orgAApproved = store.saveApprovedPlan("org_a", ApprovedExecutablePlan.legacy(orgAPlan, List.of()));
        var orgBApproved = store.saveApprovedPlan("org_b", ApprovedExecutablePlan.legacy(orgBPlan, List.of()));

        assertThat(store.approvedPlan("org_a", "plan_shared")).get().isEqualTo(orgAApproved);
        assertThat(store.approvedPlan("org_b", "plan_shared")).get().isEqualTo(orgBApproved);

        store.saveDraft("org_a", draft(plan("plan_shared", "Changed org A")));

        assertThat(store.approvedPlan("org_a", "plan_shared")).isEmpty();
        assertThat(store.approvedPlan("org_b", "plan_shared")).get().isEqualTo(orgBApproved);
    }

    private PlanDraft draft(PlanSpec plan) {
        return new PlanDraft(plan, new PlanValidationResult(List.of()), List.of("draft_plan", "validate_plan"));
    }

    private PlanSpec plan(String id, String goal) {
        return new PlanSpec(
                id,
                goal,
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview audience",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                        List.of(),
                        false
                ))
        );
    }
}
