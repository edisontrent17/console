package com.acme.data360agent.operation;

import com.acme.data360agent.plan.Data360Action;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OperationRegistry {
    private final Map<Data360Action, OperationDefinition> definitions;
    private final Map<Data360Action, OperationBinding> bindings;
    private final OperationCatalogSnapshot snapshot;

    public OperationRegistry() {
        var defs = new EnumMap<Data360Action, OperationDefinition>(Data360Action.class);
        defs.put(Data360Action.SEARCH, new OperationDefinition(
                Data360Action.SEARCH,
                "Search Data 360 metadata",
                Effect.READ,
                false,
                false,
                "search",
                List.of(),
                List.of("query")
        ));
        defs.put(Data360Action.METADATA_DESCRIBE, new OperationDefinition(
                Data360Action.METADATA_DESCRIBE,
                "Describe Data 360 metadata",
                Effect.READ,
                false,
                false,
                "d360_metadata_describe",
                List.of("objects", "objectApiNames"),
                List.of()
        ));
        defs.put(Data360Action.QUERY, new OperationDefinition(
                Data360Action.QUERY,
                "Run limited Data 360 SQL preview",
                Effect.READ,
                false,
                false,
                "d360_query_sql",
                List.of(),
                List.of("sql")
        ));
        defs.put(Data360Action.CREATE_CALCULATED_INSIGHT, new OperationDefinition(
                Data360Action.CREATE_CALCULATED_INSIGHT,
                "Create calculated insight",
                Effect.WRITE,
                true,
                false,
                "d360_calculated_insight_create",
                List.of("sql", "definition"),
                List.of("name")
        ));
        defs.put(Data360Action.RUN_CALCULATED_INSIGHT, new OperationDefinition(
                Data360Action.RUN_CALCULATED_INSIGHT,
                "Run calculated insight",
                Effect.WRITE,
                true,
                false,
                "d360_calculated_insight_run",
                List.of("insightId", "insightIdFromStep"),
                List.of()
        ));
        defs.put(Data360Action.CREATE_SEGMENT, new OperationDefinition(
                Data360Action.CREATE_SEGMENT,
                "Create Data 360 segment",
                Effect.WRITE,
                true,
                false,
                "d360_segment_create",
                List.of("criteria", "criteriaFromStep"),
                List.of("name")
        ));
        defs.put(Data360Action.UPDATE_SEGMENT, new OperationDefinition(
                Data360Action.UPDATE_SEGMENT,
                "Update Data 360 segment",
                Effect.WRITE,
                true,
                false,
                "d360_segment_update",
                List.of("segmentId", "segmentIdFromStep"),
                List.of("name", "criteria")
        ));
        defs.put(Data360Action.PUBLISH_SEGMENT, new OperationDefinition(
                Data360Action.PUBLISH_SEGMENT,
                "Publish Data 360 segment",
                Effect.PUBLISH,
                true,
                true,
                "d360_segment_publish",
                List.of("segmentId", "segmentIdFromStep"),
                List.of()
        ));
        defs.put(Data360Action.CREATE_ACTIVATION, new OperationDefinition(
                Data360Action.CREATE_ACTIVATION,
                "Create activation",
                Effect.WRITE,
                true,
                false,
                "d360_activation_create",
                List.of("segmentId", "segmentIdFromStep"),
                List.of("name", "destination")
        ));
        defs.put(Data360Action.RUN_ACTIVATION, new OperationDefinition(
                Data360Action.RUN_ACTIVATION,
                "Run activation",
                Effect.ACTIVATE,
                true,
                true,
                "d360_activation_get",
                List.of("activationId", "activationIdFromStep"),
                List.of()
        ));
        defs.put(Data360Action.GET_IDENTITY_RULESET, new OperationDefinition(
                Data360Action.GET_IDENTITY_RULESET,
                "Get identity resolution ruleset",
                Effect.READ,
                false,
                false,
                "d360_identity_ruleset_get",
                List.of("rulesetId", "rulesetName"),
                List.of()
        ));
        defs.put(Data360Action.MONITOR_METRIC, new OperationDefinition(
                Data360Action.MONITOR_METRIC,
                "Monitor a Data 360 goal metric",
                Effect.READ,
                false,
                false,
                "d360_monitor_metric",
                List.of("query", "queryFromStep", "metric"),
                List.of("cadence", "threshold")
        ));
        definitions = Map.copyOf(defs);

        var operationBindings = new EnumMap<Data360Action, OperationBinding>(Data360Action.class);
        definitions.forEach((action, definition) -> operationBindings.put(action, definition.binding()));
        bindings = Map.copyOf(operationBindings);
        snapshot = OperationCatalogSnapshot.from(definitions);
    }

    public OperationDefinition require(Data360Action action) {
        var definition = definitions.get(action);
        if (definition == null) {
            throw new IllegalArgumentException("No operation definition for " + action.value());
        }
        return definition;
    }

    public OperationBindingSnapshot bindingFor(Data360Action action) {
        return OperationBindingSnapshot.data360Mcp(require(action));
    }

    public Map<Data360Action, OperationDefinition> all() {
        return definitions;
    }

    public OperationBinding binding(Data360Action action) {
        var binding = bindings.get(action);
        if (binding == null) {
            throw new IllegalArgumentException("No operation binding for " + action.value());
        }
        return binding;
    }

    public Map<Data360Action, OperationBinding> allBindings() {
        return bindings;
    }

    public OperationCatalogSnapshot snapshot() {
        return snapshot;
    }
}
