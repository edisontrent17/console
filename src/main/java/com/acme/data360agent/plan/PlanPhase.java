package com.acme.data360agent.plan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum PlanPhase {
    DISCOVER("discover"),
    SETUP("setup"),
    MONITOR("monitor");

    private final String value;

    PlanPhase(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static PlanPhase from(String value) {
        if (value == null || value.isBlank()) {
            return SETUP;
        }
        return Arrays.stream(values())
                .filter(phase -> phase.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported plan phase: " + value));
    }
}
