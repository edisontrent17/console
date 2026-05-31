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
                List.of("query"),
                fields("query", "string"),
                fields("matches", "array", "query", "string")
        ));
        defs.put(Data360Action.METADATA_DESCRIBE, new OperationDefinition(
                Data360Action.METADATA_DESCRIBE,
                "Describe Data 360 metadata",
                Effect.READ,
                false,
                false,
                "d360_metadata_describe",
                List.of("objects", "objectApiNames"),
                List.of(),
                fields("objects", "array", "objectApiNames", "array"),
                fields("objects", "array", "available", "array")
        ));
        defs.put(Data360Action.QUERY, new OperationDefinition(
                Data360Action.QUERY,
                "Run limited Data 360 SQL preview",
                Effect.READ,
                false,
                false,
                "d360_query_sql",
                List.of(),
                List.of("sql"),
                fields("sql", "string", "limit", "number", "sqlParameters", "array"),
                fields("columns", "array", "rowCount", "number", "rows", "array", "sql", "string", "observedValue", "number")
        ));
        defs.put(Data360Action.CREATE_SNOWFLAKE_DATA_STREAM, new OperationDefinition(
                Data360Action.CREATE_SNOWFLAKE_DATA_STREAM,
                "Create Snowflake data stream",
                Effect.WRITE,
                true,
                false,
                "d360_datastream_create_snowflake",
                List.of(),
                List.of("streamName", "label", "connectionName", "database", "schema", "objectName", "dataSpaceName", "dloName"),
                fields(
                        "streamName", "string",
                        "label", "string",
                        "connectionName", "string",
                        "warehouse", "string",
                        "database", "string",
                        "schema", "string",
                        "objectName", "string",
                        "dataSpaceName", "string",
                        "dloName", "string",
                        "dloLabel", "string",
                        "category", "string",
                        "eventDateTimeFieldName", "string",
                        "refreshMode", "string",
                        "dataAccessMode", "string",
                        "fields", "array"
                ),
                fields("dataStreamId", "string", "dataStreamName", "string", "dloName", "string", "status", "string")
        ));
        defs.put(Data360Action.CREATE_CRM_DATA_STREAM, new OperationDefinition(
                Data360Action.CREATE_CRM_DATA_STREAM,
                "Create CRM data stream",
                Effect.WRITE,
                true,
                false,
                "d360_datastream_create_sfdc",
                List.of(),
                List.of("streamName", "label", "sourceObject", "dataSpaceName", "dloName"),
                fields(
                        "streamName", "string",
                        "label", "string",
                        "sourceObject", "string",
                        "dataSpaceName", "string",
                        "dloName", "string",
                        "dloLabel", "string",
                        "category", "string",
                        "fields", "array"
                ),
                fields("dataStreamId", "string", "dataStreamName", "string", "dloName", "string", "status", "string")
        ));
        defs.put(Data360Action.CREATE_MAPPING, new OperationDefinition(
                Data360Action.CREATE_MAPPING,
                "Create DLO to DMO mapping",
                Effect.WRITE,
                true,
                false,
                "d360_dmo_mapping_create",
                List.of(),
                List.of("sourceDloName", "targetDmoName", "fieldMappings"),
                fields("sourceDloName", "string", "targetDmoName", "string", "fieldMappings", "object", "dataspace", "string"),
                fields("mappingId", "string", "mappingName", "string", "sourceDloName", "string", "targetDmoName", "string", "status", "string")
        ));
        defs.put(Data360Action.CREATE_CALCULATED_INSIGHT, new OperationDefinition(
                Data360Action.CREATE_CALCULATED_INSIGHT,
                "Create calculated insight",
                Effect.WRITE,
                true,
                false,
                "d360_calculated_insight_create",
                List.of("sql", "definition", "semanticDefinition", "measure"),
                List.of("name"),
                fields(
                        "name", "string",
                        "apiName", "string",
                        "description", "string",
                        "sql", "string",
                        "definition", "string",
                        "semanticDefinition", "object",
                        "unifiedProfileObjectApiName", "string",
                        "unifiedProfileIdField", "string",
                        "transactionObjectApiName", "string",
                        "transactionCustomerKeyField", "string",
                        "measure", "object",
                        "groupBy", "array"
                ),
                fields("insightId", "string", "apiName", "string", "name", "string", "status", "string")
        ));
        defs.put(Data360Action.RUN_CALCULATED_INSIGHT, new OperationDefinition(
                Data360Action.RUN_CALCULATED_INSIGHT,
                "Run calculated insight",
                Effect.WRITE,
                true,
                false,
                "d360_calculated_insight_run",
                List.of("insightId", "insightIdFromStep"),
                List.of(),
                fields("insightId", "string", "insightIdFromStep", "string"),
                fields("insightId", "string", "jobId", "string", "status", "string")
        ));
        defs.put(Data360Action.CREATE_SEGMENT, new OperationDefinition(
                Data360Action.CREATE_SEGMENT,
                "Create Data 360 segment",
                Effect.WRITE,
                true,
                false,
                "d360_segment_create",
                List.of("criteria", "criteriaFromStep"),
                List.of("name"),
                fields("name", "string", "description", "string", "criteria", "object", "criteriaFromStep", "string", "sql", "string"),
                fields("segmentId", "string", "name", "string", "status", "string")
        ));
        defs.put(Data360Action.UPDATE_SEGMENT, new OperationDefinition(
                Data360Action.UPDATE_SEGMENT,
                "Update Data 360 segment",
                Effect.WRITE,
                true,
                false,
                "d360_segment_update",
                List.of("segmentId", "segmentIdFromStep"),
                List.of("name", "criteria"),
                fields("segmentId", "string", "segmentIdFromStep", "string", "name", "string", "criteria", "object"),
                fields("segmentId", "string", "name", "string", "status", "string")
        ));
        defs.put(Data360Action.PUBLISH_SEGMENT, new OperationDefinition(
                Data360Action.PUBLISH_SEGMENT,
                "Publish Data 360 segment",
                Effect.PUBLISH,
                true,
                true,
                "d360_segment_publish",
                List.of("segmentId", "segmentIdFromStep"),
                List.of(),
                fields("segmentId", "string", "segmentIdFromStep", "string"),
                fields("segmentId", "string", "jobId", "string", "status", "string")
        ));
        defs.put(Data360Action.CREATE_ACTIVATION, new OperationDefinition(
                Data360Action.CREATE_ACTIVATION,
                "Create activation",
                Effect.WRITE,
                true,
                false,
                "d360_activation_create",
                List.of("segmentId", "segmentIdFromStep"),
                List.of("name", "destination"),
                fields("name", "string", "destination", "string", "segmentId", "string", "segmentIdFromStep", "string"),
                fields("activationId", "string", "name", "string", "destination", "string", "status", "string")
        ));
        defs.put(Data360Action.RUN_ACTIVATION, new OperationDefinition(
                Data360Action.RUN_ACTIVATION,
                "Run activation",
                Effect.ACTIVATE,
                true,
                true,
                "d360_activation_get",
                List.of("activationId", "activationIdFromStep"),
                List.of(),
                fields("activationId", "string", "activationIdFromStep", "string"),
                fields("activationId", "string", "jobId", "string", "status", "string")
        ));
        defs.put(Data360Action.GET_IDENTITY_RULESET, new OperationDefinition(
                Data360Action.GET_IDENTITY_RULESET,
                "Get identity resolution ruleset",
                Effect.READ,
                false,
                false,
                "d360_identity_ruleset_get",
                List.of("rulesetId", "rulesetName"),
                List.of(),
                fields("rulesetId", "string", "rulesetName", "string", "identityResolution", "string"),
                fields("rulesetId", "string", "rulesetName", "string", "status", "string")
        ));
        defs.put(Data360Action.CREATE_IDENTITY_RULESET, new OperationDefinition(
                Data360Action.CREATE_IDENTITY_RULESET,
                "Create identity resolution ruleset",
                Effect.WRITE,
                true,
                false,
                "d360_ir_create",
                List.of(),
                List.of("name", "rules"),
                fields("name", "string", "description", "string", "profileObject", "string", "rules", "array"),
                fields("rulesetId", "string", "rulesetName", "string", "status", "string")
        ));
        defs.put(Data360Action.RUN_IDENTITY_RESOLUTION, new OperationDefinition(
                Data360Action.RUN_IDENTITY_RESOLUTION,
                "Run identity resolution ruleset",
                Effect.WRITE,
                true,
                true,
                "d360_ir_run",
                List.of("rulesetId", "rulesetIdFromStep"),
                List.of(),
                fields("rulesetId", "string", "rulesetIdFromStep", "string"),
                fields(
                        "rulesetId", "string",
                        "jobId", "string",
                        "status", "string",
                        "unifiedProfileObjectApiName", "string",
                        "unifiedProfileIdField", "string"
                )
        ));
        defs.put(Data360Action.MONITOR_METRIC, new OperationDefinition(
                Data360Action.MONITOR_METRIC,
                "Monitor a Data 360 goal metric",
                Effect.READ,
                false,
                false,
                "d360_monitor_metric",
                List.of("query", "queryFromStep", "metric"),
                List.of("cadence", "threshold"),
                fields("query", "string", "queryFromStep", "string", "metric", "string", "cadence", "string", "threshold", "object"),
                fields("metric", "string", "observedValue", "number", "threshold", "object", "status", "string", "checkedAt", "string")
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

    private static Map<String, String> fields(String... entries) {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("Field entries must be key/type pairs.");
        }
        var fields = new java.util.LinkedHashMap<String, String>();
        for (var i = 0; i < entries.length; i += 2) {
            fields.put(entries[i], entries[i + 1]);
        }
        return Map.copyOf(fields);
    }
}
