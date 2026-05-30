package com.acme.data360agent.llm;

import com.acme.data360agent.config.LlmProperties;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LlmGateway {
    private final LlmProperties properties;
    private final AnthropicClient anthropic;
    private final OpenRouterClient openRouter;

    public LlmGateway(LlmProperties properties, AnthropicClient anthropic, OpenRouterClient openRouter) {
        this.properties = properties;
        this.anthropic = anthropic;
        this.openRouter = openRouter;
    }

    public boolean configured() {
        var selected = selected();
        return selected != null && selected.configured();
    }

    public String provider() {
        return properties.resolvedProvider();
    }

    public LlmCompletion completeJson(String system, String user) {
        var selected = selected();
        if (selected == null) {
            throw new IllegalStateException("Unsupported LLM provider: " + provider());
        }
        if (!selected.configured()) {
            throw new IllegalStateException("LLM provider is not configured: " + selected.provider());
        }
        return selected.completeJson(new LlmPrompt(system, user, properties.model()));
    }

    private LlmClient selected() {
        return switch (provider().toLowerCase(Locale.ROOT)) {
            case "anthropic" -> anthropic;
            case "openrouter" -> openRouter;
            case "fallback", "none", "mock" -> null;
            default -> null;
        };
    }
}
