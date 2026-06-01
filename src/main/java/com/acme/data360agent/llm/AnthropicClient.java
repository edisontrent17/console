package com.acme.data360agent.llm;

import com.acme.data360agent.config.AnthropicProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class AnthropicClient implements LlmClient {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final AnthropicProperties properties;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public AnthropicClient(AnthropicProperties properties, ObjectMapper objectMapper, WebClient.Builder builder) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.webClient = builder.baseUrl(properties.baseUrl()).build();
    }

    public boolean configured() {
        return properties.configured();
    }

    @Override
    public String provider() {
        return "anthropic";
    }

    @Override
    public LlmCompletion completeJson(LlmPrompt prompt) {
        var model = model(prompt);
        return new LlmCompletion(provider(), model, completeJson(prompt.system(), prompt.user(), model));
    }

    public LlmCompletion completeJson(LlmPrompt prompt, EffectiveLlmSettings settings) {
        var model = settings.model() == null || settings.model().isBlank() ? model(prompt) : settings.model();
        return new LlmCompletion(provider(), model, completeJson(prompt.system(), prompt.user(), model, settings.apiKey()));
    }

    public LlmCompletion completeText(LlmPrompt prompt, EffectiveLlmSettings settings) {
        return completeJson(prompt, settings);
    }

    public String completeJson(String system, String user) {
        return completeJson(system, user, properties.model());
    }

    private String completeJson(String system, String user, String model) {
        return completeJson(system, user, model, properties.apiKey());
    }

    private String completeJson(String system, String user, String model, String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("ANTHROPIC_API_KEY is not configured.");
        }

        var request = Map.of(
                "model", model,
                "max_tokens", 2400,
                "temperature", 0,
                "system", system,
                "messages", List.of(Map.of("role", "user", "content", user))
        );

        var raw = webClient.post()
                .uri("/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            var response = objectMapper.readValue(raw, MAP);
            var content = (List<?>) response.getOrDefault("content", List.of());
            for (var item : content) {
                if (item instanceof Map<?, ?> map && "text".equals(map.get("type"))) {
                    return String.valueOf(map.get("text"));
                }
            }
            throw new IllegalStateException("Anthropic response did not contain text content.");
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse Anthropic response.", e);
        }
    }

    private String model(LlmPrompt prompt) {
        if (prompt != null && prompt.model() != null && !prompt.model().isBlank()) {
            return prompt.model();
        }
        return properties.model();
    }
}
