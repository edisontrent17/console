package com.acme.data360agent.planner;

import com.acme.data360agent.plan.PlanContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record PlanRequest(
        @NotBlank String goal,
        @Valid PlanContext context
) implements Serializable {
}
