package com.acme.data360agent.chat;

import java.util.List;

public record ChatResponse(
        String text,
        String provider,
        String model,
        List<ChatTraceEvent> trace
) {
    public ChatResponse(String text, String provider, String model) {
        this(text, provider, model, List.of());
    }

    public ChatResponse {
        trace = trace == null ? List.of() : List.copyOf(trace);
    }
}
