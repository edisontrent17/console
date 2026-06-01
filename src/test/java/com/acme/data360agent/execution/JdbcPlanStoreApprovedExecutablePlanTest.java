package com.acme.data360agent.execution;

import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import com.acme.data360agent.planner.PlanDraft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:approved-plan-store-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.security.enabled=false"
})
class JdbcPlanStoreApprovedExecutablePlanTest {
    @Autowired
    private PlanStore store;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanTables() {
        jdbc.update("DELETE FROM approved_plans");
        jdbc.update("DELETE FROM plan_runs");
        jdbc.update("DELETE FROM plan_drafts");
    }

    @Test
    void persistsApprovedExecutablePlanArtifactSeparatelyFromRunRecords() {
        var plan = plan("plan_jdbc_approved", "Preview audience");
        store.saveDraft("org_a", draft(plan));

        var approved = store.saveApprovedPlan("org_a", ApprovedExecutablePlan.legacy(plan, List.of()));

        assertThat(store.approvedPlan("org_a", "plan_jdbc_approved")).get().isEqualTo(approved);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM approved_plans WHERE organization_id = ? AND plan_id = ?", Integer.class, "org_a", "plan_jdbc_approved")).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plan_runs", Integer.class)).isZero();
    }

    @Test
    void createRunUsesStoredApprovedExecutablePlanArtifact() {
        var plan = plan("plan_jdbc_run", "Preview audience");
        store.saveDraft("org_a", draft(plan));
        var approved = store.saveApprovedPlan("org_a", ApprovedExecutablePlan.legacy(plan, List.of()));

        var run = store.createRun("org_a", store.approvedPlan("org_a", "plan_jdbc_run").orElseThrow());

        assertThat(run.getApprovedPlan()).isEqualTo(approved);
        assertThat(store.run("org_a", run.getId())).get()
                .extracting(PlanRun::getApprovedPlan)
                .isEqualTo(approved);
    }

    @Test
    void persistsSamePlanIdAcrossOrganizations() {
        var orgAPlan = plan("plan_shared_jdbc", "Preview org A");
        var orgBPlan = plan("plan_shared_jdbc", "Preview org B");

        store.saveDraft("org_a", draft(orgAPlan));
        store.saveDraft("org_b", draft(orgBPlan));
        var orgAApproved = store.saveApprovedPlan("org_a", ApprovedExecutablePlan.legacy(orgAPlan, List.of()));
        var orgBApproved = store.saveApprovedPlan("org_b", ApprovedExecutablePlan.legacy(orgBPlan, List.of()));

        assertThat(store.draft("org_a", "plan_shared_jdbc")).get()
                .extracting(draft -> draft.plan().goal())
                .isEqualTo("Preview org A");
        assertThat(store.draft("org_b", "plan_shared_jdbc")).get()
                .extracting(draft -> draft.plan().goal())
                .isEqualTo("Preview org B");
        assertThat(store.approvedPlan("org_a", "plan_shared_jdbc")).get().isEqualTo(orgAApproved);
        assertThat(store.approvedPlan("org_b", "plan_shared_jdbc")).get().isEqualTo(orgBApproved);
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
