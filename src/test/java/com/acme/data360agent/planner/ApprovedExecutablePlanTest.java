package com.acme.data360agent.planner;

import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.plan.ValidationIssue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApprovedExecutablePlanTest {
    @Test
    void freezesValidatedDraftForExecution() {
        var plan = plan();
        var draft = new PlanDraft(plan, new PlanValidationResult(List.of()), List.of("draft_plan", "validate_plan"));

        var approved = ApprovedExecutablePlan.approve(draft, draft.validation(), "tester@example.com");

        assertThat(approved.artifactType()).isEqualTo(ApprovedExecutablePlan.ARTIFACT_TYPE);
        assertThat(approved.artifactId()).startsWith("aplan_");
        assertThat(approved.planHash()).startsWith("sha256:");
        assertThat(approved.approvedBy()).isEqualTo("tester@example.com");
        assertThat(approved.plan()).isEqualTo(plan);
        assertThat(approved.validation().ok()).isTrue();
        assertThat(approved.graphStages()).containsExactly("draft_plan", "validate_plan", "approve_executable_plan");
        assertThat(approved.operationBindings()).isEqualTo(draft.operationBindings());
        assertThat(approved.artifacts())
                .extracting(PlanArtifact::type)
                .contains("plan_dag");
    }

    @Test
    void rejectsInvalidDrafts() {
        var plan = plan();
        var invalid = new PlanValidationResult(List.of(ValidationIssue.error("preview", "Bad plan.")));
        var draft = new PlanDraft(plan, invalid, List.of("draft_plan", "validate_plan"));

        assertThatThrownBy(() -> ApprovedExecutablePlan.approve(draft, invalid, "tester@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valid PlanSpec");
    }

    private PlanSpec plan() {
        return new PlanSpec(
                "plan_approved_test",
                "Preview audience",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview audience",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100"),
                        List.of(),
                        false
                ))
        );
    }
}
