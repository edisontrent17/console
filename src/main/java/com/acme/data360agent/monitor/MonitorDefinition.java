package com.acme.data360agent.monitor;

import com.acme.data360agent.execution.PlanStore;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record MonitorDefinition(
        String id,
        String organizationId,
        String planId,
        String runId,
        String stepId,
        String metric,
        String cadence,
        Map<String, Object> threshold,
        MonitorStatus status,
        Instant createdAt,
        Instant lastRunAt,
        Instant nextRunAt,
        Instant leaseUntil
) implements Serializable {
    public MonitorDefinition {
        organizationId = normalizeOrganizationId(organizationId);
        threshold = threshold == null ? Map.of() : Map.copyOf(threshold);
    }

    public MonitorDefinition(
            String id,
            String planId,
            String runId,
            String stepId,
            String metric,
            String cadence,
            Map<String, Object> threshold,
            MonitorStatus status,
            Instant createdAt,
            Instant lastRunAt
    ) {
        this(id, PlanStore.DEFAULT_ORGANIZATION_ID, planId, runId, stepId, metric, cadence, threshold, status, createdAt, lastRunAt, null, null);
    }

    public MonitorDefinition(
            String id,
            String organizationId,
            String planId,
            String runId,
            String stepId,
            String metric,
            String cadence,
            Map<String, Object> threshold,
            MonitorStatus status,
            Instant createdAt,
            Instant lastRunAt
    ) {
        this(id, organizationId, planId, runId, stepId, metric, cadence, threshold, status, createdAt, lastRunAt, null, null);
    }

    public MonitorDefinition withStatus(MonitorStatus status, Instant lastRunAt) {
        return withStatus(status, lastRunAt, nextRunAt);
    }

    public MonitorDefinition withStatus(MonitorStatus status, Instant lastRunAt, Instant nextRunAt) {
        return new MonitorDefinition(id, organizationId, planId, runId, stepId, metric, cadence, threshold, status, createdAt, lastRunAt, nextRunAt, null);
    }

    public MonitorDefinition withLease(Instant leaseUntil) {
        return new MonitorDefinition(id, organizationId, planId, runId, stepId, metric, cadence, threshold, status, createdAt, lastRunAt, nextRunAt, leaseUntil);
    }

    private static String normalizeOrganizationId(String organizationId) {
        return organizationId == null || organizationId.isBlank() ? PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
    }
}
