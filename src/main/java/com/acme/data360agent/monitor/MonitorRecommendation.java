package com.acme.data360agent.monitor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record MonitorRecommendation(
        String id,
        String monitorId,
        String monitorRunId,
        String metric,
        double observedValue,
        Map<String, Object> threshold,
        String summary,
        MonitorRecommendationStatus status,
        Instant createdAt,
        Instant reviewedAt
) implements Serializable {
    public MonitorRecommendation {
        threshold = threshold == null ? Map.of() : Map.copyOf(threshold);
    }

    public MonitorRecommendation withStatus(MonitorRecommendationStatus status, Instant reviewedAt) {
        return new MonitorRecommendation(id, monitorId, monitorRunId, metric, observedValue, threshold, summary, status, createdAt, reviewedAt);
    }
}
