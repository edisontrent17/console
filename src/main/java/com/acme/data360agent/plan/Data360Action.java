package com.acme.data360agent.plan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;

public enum Data360Action {
    SEARCH("data360.search"),
    METADATA_DESCRIBE("data360.metadata.describe"),
    QUERY("data360.query"),
    CREATE_SNOWFLAKE_DATA_STREAM("data360.dataStream.snowflake.create"),
    CREATE_CRM_DATA_STREAM("data360.dataStream.crm.create"),
    CREATE_MAPPING("data360.mapping.create"),
    CREATE_CALCULATED_INSIGHT("data360.calculatedInsight.create"),
    RUN_CALCULATED_INSIGHT("data360.calculatedInsight.run"),
    CREATE_SEGMENT("data360.createSegment"),
    UPDATE_SEGMENT("data360.updateSegment"),
    PUBLISH_SEGMENT("data360.publishSegment"),
    CREATE_ACTIVATION("data360.createActivation"),
    RUN_ACTIVATION("data360.runActivation"),
    GET_IDENTITY_RULESET("data360.identityRuleset.get"),
    CREATE_IDENTITY_RULESET("data360.identityResolution.ruleset.create"),
    RUN_IDENTITY_RESOLUTION("data360.identityResolution.run"),
    MONITOR_METRIC("data360.monitor.metric");

    private final String value;

    Data360Action(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    public String resource() {
        return switch (this) {
            case SEARCH -> "urn:salesforce:data360:capability:search";
            case METADATA_DESCRIBE -> "urn:salesforce:data360:capability:metadata.describe";
            case QUERY -> "urn:salesforce:data360:capability:query";
            case CREATE_SNOWFLAKE_DATA_STREAM -> "urn:salesforce:data360:capability:dataStream.snowflake.create";
            case CREATE_CRM_DATA_STREAM -> "urn:salesforce:data360:capability:dataStream.crm.create";
            case CREATE_MAPPING -> "urn:salesforce:data360:capability:mapping.create";
            case CREATE_CALCULATED_INSIGHT -> "urn:salesforce:data360:capability:calculatedInsight.create";
            case RUN_CALCULATED_INSIGHT -> "urn:salesforce:data360:capability:calculatedInsight.run";
            case CREATE_SEGMENT -> "urn:salesforce:data360:capability:segment.create";
            case UPDATE_SEGMENT -> "urn:salesforce:data360:capability:segment.update";
            case PUBLISH_SEGMENT -> "urn:salesforce:data360:capability:segment.publish";
            case CREATE_ACTIVATION -> "urn:salesforce:data360:capability:activation.create";
            case RUN_ACTIVATION -> "urn:salesforce:data360:capability:activation.run";
            case GET_IDENTITY_RULESET -> "urn:salesforce:data360:capability:identityRuleset.get";
            case CREATE_IDENTITY_RULESET -> "urn:salesforce:data360:capability:identityResolution.ruleset.create";
            case RUN_IDENTITY_RESOLUTION -> "urn:salesforce:data360:capability:identityResolution.run";
            case MONITOR_METRIC -> "urn:salesforce:data360:capability:monitor.metric";
        };
    }

    public PlanPhase defaultPhase() {
        return switch (this) {
            case SEARCH, METADATA_DESCRIBE, QUERY, GET_IDENTITY_RULESET -> PlanPhase.DISCOVER;
            case MONITOR_METRIC -> PlanPhase.MONITOR;
            default -> PlanPhase.SETUP;
        };
    }

    public boolean requiresApproval() {
        return switch (this) {
            case CREATE_CALCULATED_INSIGHT,
                    RUN_CALCULATED_INSIGHT,
                    CREATE_SNOWFLAKE_DATA_STREAM,
                    CREATE_CRM_DATA_STREAM,
                    CREATE_MAPPING,
                    CREATE_SEGMENT,
                    UPDATE_SEGMENT,
                    PUBLISH_SEGMENT,
                    CREATE_ACTIVATION,
                    RUN_ACTIVATION,
                    CREATE_IDENTITY_RULESET,
                    RUN_IDENTITY_RESOLUTION -> true;
            default -> false;
        };
    }

    @JsonCreator
    public static Data360Action from(String value) {
        return Arrays.stream(values())
                .filter(action -> action.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported Data 360 action: " + value));
    }

    public static Data360Action fromResource(String resource) {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("Task Resource is required.");
        }
        var normalized = resource.trim();
        return Arrays.stream(values())
                .filter(action -> action.resource().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported Data 360 capability resource: " + resource.toLowerCase(Locale.ROOT)));
    }
}
