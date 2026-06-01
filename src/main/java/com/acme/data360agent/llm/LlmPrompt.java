package com.acme.data360agent.llm;

public record LlmPrompt(
        String system,
        String user,
        String model
) {
}
