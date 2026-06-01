package com.acme.data360agent.audit;

import com.acme.data360agent.support.SensitiveData;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record AuditEvent(
        String id,
        String runId,
        String planId,
        String stepId,
        String eventType,
        Map<String, Object> detail,
        Instant createdAt
) implements Serializable {
    public AuditEvent {
        detail = detail == null ? Map.of() : SensitiveData.redactMap(detail);
    }
}
