package com.acme.data360agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.llm")
public record LlmProperties(
        String provider,
        String model,
        OpenRouter openrouter
) {
    public String resolvedProvider() {
        return provider == null || provider.isBlank() ? "anthropic" : provider;
    }

    public record OpenRouter(
            String apiKey,
            String model,
            String baseUrl,
            String siteUrl,
            String appName
    ) {
        public boolean configured() {
            return apiKey != null && !apiKey.isBlank();
        }

        public String resolvedBaseUrl() {
            return baseUrl == null || baseUrl.isBlank() ? "https://openrouter.ai" : baseUrl;
        }

        public String resolvedModel(String defaultModel) {
            if (model != null && !model.isBlank()) {
                return model;
            }
            return defaultModel == null || defaultModel.isBlank() ? "anthropic/claude-sonnet-4.6" : defaultModel;
        }
    }
}
