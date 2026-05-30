package com.acme.data360agent.plan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Data360Action {
    SEARCH("data360.search"),
    METADATA_DESCRIBE("data360.metadata.describe"),
    QUERY("data360.query"),
    CREATE_CALCULATED_INSIGHT("data360.calculatedInsight.create"),
    RUN_CALCULATED_INSIGHT("data360.calculatedInsight.run"),
    CREATE_SEGMENT("data360.createSegment"),
    UPDATE_SEGMENT("data360.updateSegment"),
    PUBLISH_SEGMENT("data360.publishSegment"),
    CREATE_ACTIVATION("data360.createActivation"),
    RUN_ACTIVATION("data360.runActivation"),
    GET_IDENTITY_RULESET("data360.identityRuleset.get"),
    MONITOR_METRIC("data360.monitor.metric");

    private final String value;

    Data360Action(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static Data360Action from(String value) {
        return Arrays.stream(values())
                .filter(action -> action.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported Data 360 action: " + value));
    }
}
