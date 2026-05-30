package com.acme.data360agent.audit;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record ApprovalRecord(
        String id,
        String runId,
        String stepId,
        String approvedBy,
        String decision,
        Map<String, Object> payload,
        Instant createdAt
) implements Serializable {
    public ApprovalRecord {
        payload = payload == null ? Map.of() : Map.copyOf(payload);
    }
}
