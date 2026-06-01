package com.acme.data360agent.data360;

import com.acme.data360agent.support.SensitiveData;

import java.util.Map;

public record Data360CallResult(
        Map<String, Object> output,
        Map<String, Object> raw
) {
    public Data360CallResult {
        output = output == null ? Map.of() : SensitiveData.redactMap(output);
        raw = raw == null ? Map.of() : SensitiveData.redactMap(raw);
    }
}
