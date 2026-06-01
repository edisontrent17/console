package com.acme.data360agent.llm;

public interface LlmClient {
    String provider();

    boolean configured();

    LlmCompletion completeJson(LlmPrompt prompt);

    default LlmCompletion completeText(LlmPrompt prompt) {
        return completeJson(prompt);
    }
}
