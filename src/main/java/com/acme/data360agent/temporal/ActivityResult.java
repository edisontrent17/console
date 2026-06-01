package com.acme.data360agent.temporal;

import java.io.Serializable;
import java.util.Map;

public record ActivityResult(
        Map<String, Object> output,
        Map<String, Object> raw
) implements Serializable {
    public ActivityResult {
        output = output == null ? Map.of() : Map.copyOf(output);
        raw = raw == null ? Map.of() : Map.copyOf(raw);
    }
}
