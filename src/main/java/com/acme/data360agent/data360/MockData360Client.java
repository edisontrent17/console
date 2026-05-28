package com.acme.data360agent.data360;

import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanStep;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.data360.client", havingValue = "mock", matchIfMissing = true)
public class MockData360Client implements Data360Client {
    @Override
    public Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
        var output = switch (step.action()) {
            case SEARCH -> search(resolvedInput);
            case QUERY -> query(resolvedInput);
            case CREATE_SEGMENT -> createSegment(resolvedInput);
            case PUBLISH_SEGMENT -> publishSegment(resolvedInput);
            case CREATE_ACTIVATION -> createActivation(resolvedInput);
            case RUN_ACTIVATION -> runActivation(resolvedInput);
        };
        var raw = Map.<String, Object>of(
                "mode", "mock",
                "mcpOperation", operation.mcpOperation(),
                "calledAt", Instant.now().toString(),
                "resolvedInput", resolvedInput
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

    private Map<String, Object> createSegment(Map<String, Object> input) {
        return Map.of(
                "segmentId", "seg_" + shortId(),
                "name", input.get("name"),
                "status", "draft"
        );
    }

    private Map<String, Object> publishSegment(Map<String, Object> input) {
        return Map.of(
                "segmentId", input.get("segmentId"),
                "jobId", "job_" + shortId(),
                "status", "published"
        );
    }

    private Map<String, Object> createActivation(Map<String, Object> input) {
        return Map.of(
                "activationId", "act_" + shortId(),
                "name", input.get("name"),
                "destination", input.get("destination"),
                "status", "draft"
        );
    }

    private Map<String, Object> runActivation(Map<String, Object> input) {
        return Map.of(
                "activationId", input.get("activationId"),
                "jobId", "job_" + shortId(),
                "status", "completed"
        );
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
