package com.acme.data360agent.plan;

import com.acme.data360agent.operation.OperationRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PlanValidatorTest {
    private final PlanValidator validator = new PlanValidator(new OperationRegistry());

    @Test
    void rejectsUnsupportedSchemaVersion() {
        var plan = new PlanSpec(
                "1900-01-01",
                "plan_test",
                null,
                "Preview records",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview records",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Unsupported PlanSpec schemaVersion"));
    }

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

    @Test
    void rejectsMonitorMetricOutsideMonitorPhase() {
        var plan = new PlanSpec(
                "plan_test",
                "Monitor metric",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "monitor",
                        "Monitor metric",
                        PlanPhase.SETUP,
                        Data360Action.MONITOR_METRIC,
                        Map.of("metric", "activation_rate", "cadence", "daily", "threshold", Map.of("operator", "<", "value", 0.13)),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("phase=monitor"));
    }

    @Test
    void validatesSimpleInputBindingsOnly() {
        var plan = new PlanSpec(
                "plan_test",
                "Bind inputs",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "preview",
                                "Preview records",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                false
                        ),
                        new PlanStep(
                                "publish",
                                "Publish segment",
                                PlanPhase.SETUP,
                                Data360Action.PUBLISH_SEGMENT,
                                Map.of(),
                                List.of("preview"),
                                Map.of("segmentId", new InputBinding("preview", "segmentId")),
                                true
                        )
                )
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("path must start with $."));
    }

    @Test
    void rejectsMutationActionsInMonitorPhase() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad monitor",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment later",
                        PlanPhase.MONITOR,
                        Data360Action.CREATE_SEGMENT,
                        Map.of("name", "Bad Segment", "criteria", Map.of()),
                        List.of(),
                        Map.of(),
                        true
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Monitor phase currently allows only"));
    }

    @Test
    void rejectsMalformedMonitorThresholds() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad threshold",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "monitor",
                        "Monitor metric",
                        PlanPhase.MONITOR,
                        Data360Action.MONITOR_METRIC,
                        Map.of("metric", "activation_rate", "cadence", "daily", "threshold", Map.of("operator", "around", "value", "0.13")),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("operator must be one of"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("value must be numeric"));
    }

    @Test
    void rejectsTooManySteps() {
        var steps = java.util.stream.IntStream.rangeClosed(1, 21)
                .mapToObj(index -> new PlanStep(
                        "preview_" + index,
                        "Preview records",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                        List.of(),
                        false
                ))
                .toList();
        var plan = new PlanSpec(
                "plan_test",
                "Too many previews",
                new PlanContext("org", "default", "sandbox"),
                steps
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("more than 20 steps"));
    }

    @Test
    void rejectsBlankDuplicateAndMalformedStepIds() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad ids",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "1_bad",
                                "Preview records",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                false
                        ),
                        new PlanStep(
                                "1_bad",
                                "Preview again",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                false
                        ),
                        new PlanStep(
                                "   ",
                                "Blank id",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                false
                        )
                )
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("start with a letter"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Duplicate step id"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Step id is required"));
    }

    @Test
    void rejectsOversizedInputAndRawUrls() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad input",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "search",
                        "Search",
                        Data360Action.SEARCH,
                        Map.of("query", "https://example.com/" + "x".repeat(8_200)),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("cannot exceed 8192"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Raw URL-like input"));
    }

    @Test
    void rejectsMutationKeywordsInQuerySql() {
        var plan = new PlanSpec(
                "plan_test",
                "Mutating query",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview records",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT id FROM UnifiedIndividual UNION DROP TABLE UnifiedIndividual LIMIT 10"),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("read-only"));
    }

    @Test
    void rejectsBadSegmentNameAndBlankActivationDestination() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad names",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "create_segment",
                                "Create segment",
                                Data360Action.CREATE_SEGMENT,
                                Map.of("name", "Bad/Segment", "criteria", Map.of()),
                                List.of(),
                                true
                        ),
                        new PlanStep(
                                "create_activation",
                                "Create activation",
                                Data360Action.CREATE_ACTIVATION,
                                Map.of("name", "Activation", "segmentId", "seg_123", "destination", "   "),
                                List.of(),
                                true
                        )
                )
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Segment name must be 1-80"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Activation destination must be nonblank"));
    }
}
