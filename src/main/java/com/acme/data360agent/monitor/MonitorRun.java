package com.acme.data360agent.monitor;

import com.acme.data360agent.support.SensitiveData;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record MonitorRun(
        String id,
        String monitorId,
        double observedValue,
        boolean thresholdBreached,
        MonitorStatus status,
        String recommendation,
        Map<String, Object> raw,
        Instant createdAt
) implements Serializable {
    public MonitorRun {
        raw = raw == null ? Map.of() : SensitiveData.redactMap(raw);
    }
}
