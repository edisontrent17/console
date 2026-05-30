package com.acme.data360agent.monitor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record MonitorDefinition(
        String id,
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
        this(id, planId, runId, stepId, metric, cadence, threshold, status, createdAt, lastRunAt, null, null);
    }

    public MonitorDefinition withStatus(MonitorStatus status, Instant lastRunAt) {
        return withStatus(status, lastRunAt, nextRunAt);
    }

    public MonitorDefinition withStatus(MonitorStatus status, Instant lastRunAt, Instant nextRunAt) {
        return new MonitorDefinition(id, planId, runId, stepId, metric, cadence, threshold, status, createdAt, lastRunAt, nextRunAt, null);
    }

    public MonitorDefinition withLease(Instant leaseUntil) {
        return new MonitorDefinition(id, planId, runId, stepId, metric, cadence, threshold, status, createdAt, lastRunAt, nextRunAt, leaseUntil);
    }
}
