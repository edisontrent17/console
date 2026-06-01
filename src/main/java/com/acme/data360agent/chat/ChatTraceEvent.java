package com.acme.data360agent.chat;

import java.util.Map;

public record ChatTraceEvent(
        String stage,
        String label,
        String status,
        String detail,
        Long durationMs,
        Map<String, Object> data
) {
    public ChatTraceEvent {
        data = data == null ? Map.of() : Map.copyOf(data);
    }

    public static ChatTraceEvent step(String stage, String label, String status, String detail) {
        return new ChatTraceEvent(stage, label, status, detail, null, Map.of());
    }

    public static ChatTraceEvent timed(String stage, String label, String status, String detail, long durationMs, Map<String, Object> data) {
        return new ChatTraceEvent(stage, label, status, detail, durationMs, data);
    }
}
