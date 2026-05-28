package com.acme.data360agent.plan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

public record PlanSpec(
        @NotBlank String id,
        @NotBlank String goal,
        @Valid PlanContext context,
        @NotEmpty List<@Valid PlanStep> steps
) implements Serializable {
}
