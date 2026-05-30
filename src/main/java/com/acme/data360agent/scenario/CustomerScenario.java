package com.acme.data360agent.scenario;

import com.acme.data360agent.plan.Data360Action;

import java.io.Serializable;
import java.util.List;

public record CustomerScenario(
        String id,
        String name,
        String industry,
        String sourceUrl,
        String sourceSummary,
        List<String> defaultUtterances,
        List<String> goalMetrics,
        List<Data360Action> requiredActions,
        List<String> keywords,
        String audienceEntity,
        String previewSql,
        String segmentName,
        String activationName,
        String activationDestination,
        String monitorMetric,
        double monitorThreshold
) implements Serializable {
    public CustomerScenario {
        defaultUtterances = List.copyOf(defaultUtterances);
        goalMetrics = List.copyOf(goalMetrics);
        requiredActions = List.copyOf(requiredActions);
        keywords = List.copyOf(keywords);
    }
}
