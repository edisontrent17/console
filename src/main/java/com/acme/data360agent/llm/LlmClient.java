package com.acme.data360agent.llm;

public interface LlmClient {
    String provider();

    boolean configured();

    LlmCompletion completeJson(LlmPrompt prompt);
}
