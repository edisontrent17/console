package com.acme.data360agent.llm;

import com.acme.data360agent.config.LlmProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenRouterClient implements LlmClient {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final LlmProperties properties;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder builder;

    public OpenRouterClient(LlmProperties properties, ObjectMapper objectMapper, WebClient.Builder builder) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.builder = builder;
    }

    @Override
    public String provider() {
        return "openrouter";
    }

    @Override
    public boolean configured() {
        return openRouter() != null && openRouter().configured();
    }

    @Override
    public LlmCompletion completeJson(LlmPrompt prompt) {
        if (!configured()) {
            throw new IllegalStateException("OPENROUTER_API_KEY is not configured.");
        }

        var openRouter = openRouter();
        var model = openRouter.resolvedModel(prompt.model());
        var request = Map.of(
                "model", model,
                "temperature", 0,
                "messages", List.of(
                        Map.of("role", "system", "content", prompt.system()),
                        Map.of("role", "user", "content", prompt.user())
                )
        );

        var client = builder.baseUrl(openRouter.resolvedBaseUrl()).build();
        var spec = client.post()
                .uri("/api/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + openRouter.apiKey());

        if (openRouter.siteUrl() != null && !openRouter.siteUrl().isBlank()) {
            spec = spec.header("HTTP-Referer", openRouter.siteUrl());
        }
        if (openRouter.appName() != null && !openRouter.appName().isBlank()) {
            spec = spec.header("X-Title", openRouter.appName());
        }

        var raw = spec.bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(45));

        try {
            var response = objectMapper.readValue(raw, MAP);
            var choices = (List<?>) response.getOrDefault("choices", List.of());
            if (choices.isEmpty() || !(choices.getFirst() instanceof Map<?, ?> first)) {
                throw new IllegalStateException("OpenRouter response did not contain choices.");
            }
            var message = first.get("message");
            if (!(message instanceof Map<?, ?> messageMap)) {
                throw new IllegalStateException("OpenRouter response did not contain a message.");
            }
            var content = messageMap.get("content");
            if (content instanceof String text) {
                return new LlmCompletion(provider(), model, text);
            }
            if (content instanceof List<?> parts) {
                return new LlmCompletion(provider(), model, flattenContent(parts));
            }
            throw new IllegalStateException("OpenRouter response did not contain text content.");
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse OpenRouter response.", e);
        }
    }

    private LlmProperties.OpenRouter openRouter() {
        return properties.openrouter();
    }

    private String flattenContent(List<?> parts) {
        var text = new StringBuilder();
        for (var part : parts) {
            if (part instanceof Map<?, ?> map && map.get("text") != null) {
                text.append(map.get("text"));
            }
        }
        return text.toString();
    }
}
