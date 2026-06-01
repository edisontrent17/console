package com.acme.data360agent.plan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;

public record PlanSpec(
        @NotBlank String schemaVersion,
        @NotBlank String id,
        String scenarioId,
        @NotBlank String goal,
        @Valid PlanContext context,
        @Valid AslStateMachine definition,
        List<@Valid PlanStep> steps
) implements Serializable {
    public static final String CURRENT_SCHEMA_VERSION = "data360-asl-profile-2026-05-31";

    public PlanSpec(String schemaVersion, String id, String scenarioId, String goal, PlanContext context, List<PlanStep> steps) {
        this(schemaVersion, id, scenarioId, goal, context, null, steps);
    }

    public PlanSpec(String id, String scenarioId, String goal, PlanContext context, List<PlanStep> steps) {
        this(CURRENT_SCHEMA_VERSION, id, scenarioId, goal, context, null, steps);
    }

    public PlanSpec(String id, String goal, PlanContext context, List<PlanStep> steps) {
        this(CURRENT_SCHEMA_VERSION, id, null, goal, context, null, steps);
    }

    public PlanSpec {
        schemaVersion = schemaVersion == null || schemaVersion.isBlank() ? CURRENT_SCHEMA_VERSION : schemaVersion;
        if (definition == null && steps != null && !steps.isEmpty()) {
            definition = AslPlanCompiler.fromSteps(steps);
        }
        if ((steps == null || steps.isEmpty()) && definition != null) {
            try {
                steps = AslPlanCompiler.toSteps(definition);
            } catch (IllegalArgumentException ignored) {
                steps = List.of();
            }
        }
        steps = steps == null ? List.of() : List.copyOf(steps);
    }
}
