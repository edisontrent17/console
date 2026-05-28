package com.acme.data360agent.operation;

import com.acme.data360agent.plan.Data360Action;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OperationRegistry {
    private final Map<Data360Action, OperationDefinition> definitions;

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
        definitions = Map.copyOf(defs);
    }

    public OperationDefinition require(Data360Action action) {
        var definition = definitions.get(action);
        if (definition == null) {
            throw new IllegalArgumentException("No operation definition for " + action.value());
        }
        return definition;
    }

    public Map<Data360Action, OperationDefinition> all() {
        return definitions;
    }
}
