package com.acme.data360agent.llm;

import com.acme.data360agent.config.LlmProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LlmModelCatalogService {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };
    private static final List<LlmModelOption> ANTHROPIC_MODELS = List.of(
            new LlmModelOption("claude-sonnet-4-6", "Claude Sonnet 4.6"),
            new LlmModelOption("claude-opus-4-7", "Claude Opus 4.7"),
            new LlmModelOption("claude-haiku-4-5", "Claude Haiku 4.5"),
            new LlmModelOption("claude-3-5-sonnet-latest", "Claude 3.5 Sonnet")
    );
    private static final List<LlmModelOption> OPENROUTER_FALLBACK_MODELS = List.of(
            new LlmModelOption("anthropic/claude-sonnet-4.6", "Anthropic: Claude Sonnet 4.6"),
            new LlmModelOption("anthropic/claude-opus-4.7", "Anthropic: Claude Opus 4.7"),
            new LlmModelOption("anthropic/claude-haiku-4.5", "Anthropic: Claude Haiku 4.5"),
            new LlmModelOption("openai/gpt-4.1", "OpenAI: GPT-4.1"),
            new LlmModelOption("google/gemini-2.5-pro", "Google: Gemini 2.5 Pro"),
            new LlmModelOption("meta-llama/llama-4-maverick", "Meta: Llama 4 Maverick")
    );

    private final LlmProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient client;

    public LlmModelCatalogService(LlmProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public List<LlmModelOption> models(String provider) {
        return switch (normalize(provider)) {
            case "openrouter" -> openRouterModels();
            case "anthropic" -> ANTHROPIC_MODELS;
            default -> ANTHROPIC_MODELS;
        };
    }

    private List<LlmModelOption> openRouterModels() {
        try {
            var openRouter = properties.openrouter();
            var baseUrl = openRouter == null ? "https://openrouter.ai" : openRouter.resolvedBaseUrl();
            var request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/v1/models"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Data360-Agent-Console")
                    .GET()
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return OPENROUTER_FALLBACK_MODELS;
            }
            var raw = response.body();
            var body = objectMapper.readValue(raw, MAP);
            var data = body.get("data");
            if (!(data instanceof List<?> list)) {
                return OPENROUTER_FALLBACK_MODELS;
            }
            var models = list.stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .map(model -> option(stringValue(model.get("id")), stringValue(model.get("name"))))
                    .filter(model -> !model.id().isBlank())
                    .sorted(Comparator.comparing(LlmModelOption::label, String.CASE_INSENSITIVE_ORDER))
                    .toList();
            return models.isEmpty() ? OPENROUTER_FALLBACK_MODELS : models;
        } catch (Exception ignored) {
            return OPENROUTER_FALLBACK_MODELS;
        }
    }

    private LlmModelOption option(String id, String name) {
        var label = name == null || name.isBlank() ? id : name;
        if (!label.toLowerCase(Locale.ROOT).contains(id.toLowerCase(Locale.ROOT))) {
            label = label + " (" + id + ")";
        }
        return new LlmModelOption(id, label);
    }

    private String normalize(String provider) {
        return provider == null || provider.isBlank() ? "anthropic" : provider.trim().toLowerCase(Locale.ROOT);
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
