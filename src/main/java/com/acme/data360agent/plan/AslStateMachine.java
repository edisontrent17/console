package com.acme.data360agent.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AslStateMachine(
        @JsonProperty("Version") String version,
        @JsonProperty("QueryLanguage") String queryLanguage,
        @JsonProperty("StartAt") String startAt,
        @JsonProperty("States") Map<String, AslState> states
) implements Serializable {
    public static final String VERSION = "1.0";
    public static final String QUERY_LANGUAGE = "JSONPath";

    public AslStateMachine {
        version = version == null || version.isBlank() ? VERSION : version;
        queryLanguage = queryLanguage == null || queryLanguage.isBlank() ? QUERY_LANGUAGE : queryLanguage;
        states = states == null ? Map.of() : Map.copyOf(states);
    }
}
