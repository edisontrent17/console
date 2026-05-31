package com.acme.data360agent.plan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

public record PlanSpec(
        @NotBlank String schemaVersion,
        @NotBlank String id,
        String scenarioId,
        @NotBlank String goal,
        @Valid PlanContext context,
        @NotEmpty List<@Valid PlanStep> steps
) implements Serializable {
    public static final String CURRENT_SCHEMA_VERSION = "2026-05-31";

    public PlanSpec(String id, String scenarioId, String goal, PlanContext context, List<PlanStep> steps) {
        this(CURRENT_SCHEMA_VERSION, id, scenarioId, goal, context, steps);
    }

    public PlanSpec(String id, String goal, PlanContext context, List<PlanStep> steps) {
        this(CURRENT_SCHEMA_VERSION, id, null, goal, context, steps);
    }

    public PlanSpec {
        schemaVersion = schemaVersion == null || schemaVersion.isBlank() ? CURRENT_SCHEMA_VERSION : schemaVersion;
        steps = steps == null ? List.of() : List.copyOf(steps);
    }
}
