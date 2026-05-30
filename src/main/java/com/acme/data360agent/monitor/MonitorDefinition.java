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
        Instant lastRunAt
) implements Serializable {
    public MonitorDefinition {
        threshold = threshold == null ? Map.of() : Map.copyOf(threshold);
    }

    public MonitorDefinition withStatus(MonitorStatus status, Instant lastRunAt) {
        return new MonitorDefinition(id, planId, runId, stepId, metric, cadence, threshold, status, createdAt, lastRunAt);
    }
}
