package com.acme.data360agent.plan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Data360Action {
    SEARCH("data360.search"),
    QUERY("data360.query"),
    CREATE_SEGMENT("data360.createSegment"),
    PUBLISH_SEGMENT("data360.publishSegment"),
    CREATE_ACTIVATION("data360.createActivation"),
    RUN_ACTIVATION("data360.runActivation");

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
