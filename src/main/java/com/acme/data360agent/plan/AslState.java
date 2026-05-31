package com.acme.data360agent.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AslState(
        @JsonProperty("Type") String type,
        @JsonProperty("Comment") String comment,
        @JsonProperty("Resource") String resource,
        @JsonProperty("Parameters") Map<String, Object> parameters,
        @JsonProperty("ResultPath") String resultPath,
        @JsonProperty("Next") String next,
        @JsonProperty("End") Boolean end,
        @JsonProperty("InputPath") String inputPath,
        @JsonProperty("OutputPath") String outputPath,
        @JsonProperty("TimeoutSeconds") Integer timeoutSeconds,
        @JsonProperty("HeartbeatSeconds") Integer heartbeatSeconds,
        @JsonProperty("Retry") List<Map<String, Object>> retry,
        @JsonProperty("Catch") List<Map<String, Object>> catchers
) implements Serializable {
    public AslState {
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
        retry = retry == null ? List.of() : List.copyOf(retry);
        catchers = catchers == null ? List.of() : List.copyOf(catchers);
    }

    public static AslState task(String comment, String resource, Map<String, Object> parameters, String resultPath, String next, boolean end) {
        return new AslState(
                "Task",
                comment,
                resource,
                parameters,
                resultPath,
                next,
                end,
                null,
                null,
                null,
                null,
                List.of(),
                List.of()
        );
    }
}
