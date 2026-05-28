package com.acme.data360agent.plan;

import com.acme.data360agent.operation.OperationRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PlanValidatorTest {
    private final PlanValidator validator = new PlanValidator(new OperationRegistry());

    @Test
    void rejectsQueryWithoutLimit() {
        var plan = new PlanSpec(
                "plan_test",
                "Preview records",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview records",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual"),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("LIMIT"));
    }

    @Test
    void requiresApprovalForPublish() {
        var plan = new PlanSpec(
                "plan_test",
                "Publish segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "publish",
                        "Publish segment",
                        Data360Action.PUBLISH_SEGMENT,
                        Map.of("segmentId", "seg_123"),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("needsApproval=true"));
    }
}
