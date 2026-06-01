package com.acme.data360agent.chat;

import java.util.List;

public record ExplorationResult(
        boolean handled,
        String text,
        List<ChatTraceEvent> trace
) {
    public ExplorationResult {
        trace = trace == null ? List.of() : List.copyOf(trace);
    }

    public static ExplorationResult unhandled() {
        return new ExplorationResult(false, "", List.of());
    }

    public static ExplorationResult handled(String text) {
        return handled(text, List.of());
    }

    public static ExplorationResult handled(String text, List<ChatTraceEvent> trace) {
        return new ExplorationResult(true, text == null ? "" : text, trace);
    }
}
