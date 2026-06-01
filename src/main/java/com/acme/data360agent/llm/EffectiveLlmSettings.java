package com.acme.data360agent.llm;

public record EffectiveLlmSettings(
        String provider,
        String model,
        String apiKey
) {
    public boolean configured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
