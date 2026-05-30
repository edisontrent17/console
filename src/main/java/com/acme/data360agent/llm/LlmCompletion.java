package com.acme.data360agent.llm;

public record LlmCompletion(
        String provider,
        String model,
        String text
) {
}
