package com.acme.data360agent.data360;

import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.support.Ids;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.data360.client", havingValue = "mock", matchIfMissing = true)
public class MockData360Client implements Data360Client {
    @Override
    public Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
        var output = switch (step.action()) {
            case SEARCH -> search(resolvedInput);
            case METADATA_DESCRIBE -> describeMetadata(resolvedInput);
            case QUERY -> query(resolvedInput);
            case CREATE_SNOWFLAKE_DATA_STREAM -> createDataStream(resolvedInput);
            case CREATE_CRM_DATA_STREAM -> createDataStream(resolvedInput);
            case CREATE_MAPPING -> createMapping(resolvedInput);
            case CREATE_CALCULATED_INSIGHT -> createCalculatedInsight(resolvedInput);
            case RUN_CALCULATED_INSIGHT -> runCalculatedInsight(resolvedInput);
            case CREATE_SEGMENT -> createSegment(resolvedInput);
            case UPDATE_SEGMENT -> updateSegment(resolvedInput);
            case PUBLISH_SEGMENT -> publishSegment(resolvedInput);
            case CREATE_ACTIVATION -> createActivation(resolvedInput);
            case RUN_ACTIVATION -> runActivation(resolvedInput);
            case GET_IDENTITY_RULESET -> getIdentityRuleset(resolvedInput);
            case CREATE_IDENTITY_RULESET -> createIdentityRuleset(resolvedInput);
            case RUN_IDENTITY_RESOLUTION -> runIdentityResolution(resolvedInput);
            case MONITOR_METRIC -> monitorMetric(resolvedInput);
        };
        var raw = Map.<String, Object>of(
                "mode", "mock",
                "mcpOperation", operation.mcpOperation(),
                "calledAt", Instant.now().toString(),
                "inputKeys", resolvedInput.keySet().stream().sorted().toList()
        );
        return new Data360CallResult(output, raw);
    }

    private Map<String, Object> search(Map<String, Object> input) {
        return Map.of(
                "matches", List.of(
                        Map.of("id", "UnifiedIndividual", "name", "UnifiedIndividual", "type", "DMO", "description", "Unified profile record"),
                        Map.of("id", "HighValueChurnRisk", "name", "High Value Churn Risk", "type", "Segment", "description", "Existing related draft segment")
                ),
                "query", input.get("query")
        );
    }

    private Map<String, Object> query(Map<String, Object> input) {
        return Map.of(
                "columns", List.of("unified_individual_id", "lifetime_value", "churn_score"),
                "rowCount", 87,
                "rows", List.of(
                        Map.of("unified_individual_id", "masked_001", "lifetime_value", 12840, "churn_score", 0.82),
                        Map.of("unified_individual_id", "masked_002", "lifetime_value", 18110, "churn_score", 0.77)
                ),
                "sql", input.get("sql")
        );
    }

    private Map<String, Object> describeMetadata(Map<String, Object> input) {
        return Map.of(
                "objects", input.getOrDefault("objects", input.getOrDefault("objectApiNames", List.of())),
                "available", List.of(
                        Map.of("apiName", "UnifiedAccount", "category", "DMO", "status", "available"),
                        Map.of("apiName", "UnifiedIndividual", "category", "DMO", "status", "available"),
                        Map.of("apiName", "Engagement", "category", "DMO", "status", "available")
                )
        );
    }

    private Map<String, Object> createCalculatedInsight(Map<String, Object> input) {
        return Map.of(
                "insightId", Ids.prefixed("ci"),
                "apiName", input.getOrDefault("apiName", Ids.prefixed("Travel_LTV")),
                "name", input.get("name"),
                "status", "draft"
        );
    }

    private Map<String, Object> runCalculatedInsight(Map<String, Object> input) {
        return Map.of(
                "insightId", input.get("insightId"),
                "jobId", Ids.prefixed("job"),
                "status", "completed"
        );
    }

    private Map<String, Object> createSegment(Map<String, Object> input) {
        return Map.of(
                "segmentId", Ids.prefixed("seg"),
                "name", input.get("name"),
                "status", "draft"
        );
    }

    private Map<String, Object> updateSegment(Map<String, Object> input) {
        return Map.of(
                "segmentId", input.get("segmentId"),
                "name", input.get("name"),
                "status", "updated"
        );
    }

    private Map<String, Object> publishSegment(Map<String, Object> input) {
        return Map.of(
                "segmentId", input.get("segmentId"),
                "jobId", Ids.prefixed("job"),
                "status", "published"
        );
    }

    private Map<String, Object> createActivation(Map<String, Object> input) {
        return Map.of(
                "activationId", Ids.prefixed("act"),
                "name", input.get("name"),
                "destination", input.get("destination"),
                "status", "draft"
        );
    }

    private Map<String, Object> runActivation(Map<String, Object> input) {
        return Map.of(
                "activationId", input.get("activationId"),
                "jobId", Ids.prefixed("job"),
                "status", "completed"
        );
    }

    private Map<String, Object> getIdentityRuleset(Map<String, Object> input) {
        return Map.of(
                "rulesetId", input.getOrDefault("rulesetId", Ids.prefixed("irs")),
                "rulesetName", input.getOrDefault("rulesetName", "Default Identity Resolution"),
                "status", "active"
        );
    }

    private Map<String, Object> createDataStream(Map<String, Object> input) {
        return Map.of(
                "dataStreamId", Ids.prefixed("ds"),
                "dataStreamName", input.getOrDefault("streamName", Ids.prefixed("stream")),
                "dloName", input.getOrDefault("dloName", ""),
                "status", "draft"
        );
    }

    private Map<String, Object> createMapping(Map<String, Object> input) {
        return Map.of(
                "mappingId", Ids.prefixed("map"),
                "mappingName", input.getOrDefault("mappingName", Ids.prefixed("mapping")),
                "sourceDloName", input.getOrDefault("sourceDloName", ""),
                "targetDmoName", input.getOrDefault("targetDmoName", ""),
                "status", "draft"
        );
    }

    private Map<String, Object> createIdentityRuleset(Map<String, Object> input) {
        return Map.of(
                "rulesetId", Ids.prefixed("irs"),
                "rulesetName", input.getOrDefault("name", "Travel Customer Identity Ruleset"),
                "status", "draft"
        );
    }

    private Map<String, Object> runIdentityResolution(Map<String, Object> input) {
        return Map.of(
                "rulesetId", input.get("rulesetId"),
                "jobId", Ids.prefixed("job"),
                "status", "completed",
                "unifiedProfileObjectApiName", "UnifiedIndividual",
                "unifiedProfileIdField", "UnifiedIndividualId"
        );
    }

    private Map<String, Object> monitorMetric(Map<String, Object> input) {
        return Map.of(
                "metric", input.get("metric"),
                "observedValue", 0.11,
                "threshold", input.get("threshold"),
                "status", "attention_required",
                "checkedAt", Instant.now().toString()
        );
    }

}
