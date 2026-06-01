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
        double monitorThreshold,
        List<SourceSystem> sourceSystems,
        List<TargetModelNote> targetModelingNotes,
        CalculatedInsight calculatedInsight,
        SegmentMetadata segment,
        List<MonitorMetadata> monitors
) implements Serializable {
    public CustomerScenario(
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
    ) {
        this(
                id,
                name,
                industry,
                sourceUrl,
                sourceSummary,
                defaultUtterances,
                goalMetrics,
                requiredActions,
                keywords,
                audienceEntity,
                previewSql,
                segmentName,
                activationName,
                activationDestination,
                monitorMetric,
                monitorThreshold,
                List.of(),
                List.of(),
                null,
                null,
                List.of()
        );
    }

    public CustomerScenario {
        defaultUtterances = List.copyOf(defaultUtterances);
        goalMetrics = List.copyOf(goalMetrics);
        requiredActions = List.copyOf(requiredActions);
        keywords = List.copyOf(keywords);
        sourceSystems = List.copyOf(sourceSystems);
        targetModelingNotes = List.copyOf(targetModelingNotes);
        monitors = List.copyOf(monitors);
    }

    public record SourceSystem(
            String name,
            String systemType,
            List<String> tables
    ) implements Serializable {
        public SourceSystem {
            tables = List.copyOf(tables);
        }
    }

    public record TargetModelNote(
            String targetObject,
            String objectType,
            String note
    ) implements Serializable {
    }

    public record CalculatedInsight(
            String name,
            String description,
            String transactionObjectApiName,
            String measureAlias
    ) implements Serializable {
    }

    public record SegmentMetadata(
            String name,
            String criteria
    ) implements Serializable {
    }

    public record MonitorMetadata(
            String metric,
            String cadence,
            String threshold,
            String recommendation
    ) implements Serializable {
    }
}
