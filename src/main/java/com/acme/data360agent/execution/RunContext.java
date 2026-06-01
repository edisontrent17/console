package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanContext;

public record RunContext(
        String organizationId,
        String runId,
        String planId,
        PlanContext planContext,
        String idempotencyKey
) {
    public RunContext(String runId, String planId, PlanContext planContext) {
        this(PlanStore.DEFAULT_ORGANIZATION_ID, runId, planId, planContext, "");
    }

    public RunContext(String organizationId, String runId, String planId, PlanContext planContext) {
        this(organizationId, runId, planId, planContext, "");
    }

    public RunContext {
        organizationId = organizationId == null || organizationId.isBlank() ? PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
        idempotencyKey = idempotencyKey == null ? "" : idempotencyKey;
    }
}
