package com.acme.data360agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "anthropic")
public record AnthropicProperties(
        String apiKey,
        String model,
        String baseUrl
) {
    public boolean configured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
