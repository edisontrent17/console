package com.acme.data360agent.data360;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record Data360DiagnosticsResult(
        String mode,
        boolean configured,
        String status,
        Map<String, Object> details,
        Instant checkedAt
) implements Serializable {
    public Data360DiagnosticsResult {
        details = details == null ? Map.of() : Map.copyOf(details);
    }
}
