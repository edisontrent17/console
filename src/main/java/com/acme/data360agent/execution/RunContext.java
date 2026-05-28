package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanContext;

public record RunContext(
        String runId,
        String planId,
        PlanContext planContext
) {
}
