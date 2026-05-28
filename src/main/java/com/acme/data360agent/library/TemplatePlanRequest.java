package com.acme.data360agent.library;

import com.acme.data360agent.plan.PlanContext;
import jakarta.validation.Valid;

public record TemplatePlanRequest(
        @Valid PlanContext context
) {
}
