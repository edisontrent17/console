package com.acme.data360agent.monitor;

import com.acme.data360agent.execution.PlanStore;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record MonitorRecommendation(
        String id,
        String organizationId,
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
        organizationId = normalizeOrganizationId(organizationId);
        threshold = threshold == null ? Map.of() : Map.copyOf(threshold);
    }

    public MonitorRecommendation(
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
    ) {
        this(id, PlanStore.DEFAULT_ORGANIZATION_ID, monitorId, monitorRunId, metric, observedValue, threshold, summary, status, createdAt, reviewedAt);
    }

    public MonitorRecommendation withStatus(MonitorRecommendationStatus status, Instant reviewedAt) {
        return new MonitorRecommendation(id, organizationId, monitorId, monitorRunId, metric, observedValue, threshold, summary, status, createdAt, reviewedAt);
    }

    private static String normalizeOrganizationId(String organizationId) {
        return organizationId == null || organizationId.isBlank() ? PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
    }
}
