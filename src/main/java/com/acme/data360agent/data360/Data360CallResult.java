package com.acme.data360agent.data360;

import java.util.Map;

public record Data360CallResult(
        Map<String, Object> output,
        Map<String, Object> raw
) {
}
