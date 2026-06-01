package com.acme.data360agent.llm;

import com.acme.data360agent.config.LlmProperties;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LlmGateway {
    private final LlmProperties properties;
    private final AnthropicClient anthropic;
    private final OpenRouterClient openRouter;
    private final LlmSettingsService settings;

    public LlmGateway(LlmProperties properties, AnthropicClient anthropic, OpenRouterClient openRouter, LlmSettingsService settings) {
        this.properties = properties;
        this.anthropic = anthropic;
        this.openRouter = openRouter;
        this.settings = settings;
    }

    public boolean configured() {
        var effective = settings.effective();
        return selected(effective.provider()) != null && effective.configured();
    }

    public String provider() {
        return settings.effective().provider();
    }

    public LlmCompletion completeJson(String system, String user) {
        return complete(system, user, true);
    }

    public LlmCompletion completeText(String system, String user) {
        return complete(system, user, false);
    }

    private LlmCompletion complete(String system, String user, boolean json) {
        var effective = settings.effective();
        var selected = selected(effective.provider());
        if (selected == null) {
            throw new IllegalStateException("Unsupported LLM provider: " + effective.provider());
        }
        if (!effective.configured()) {
            throw new IllegalStateException("LLM provider is not configured: " + selected.provider());
        }
        var prompt = new LlmPrompt(system, user, effective.model());
        return switch (effective.provider().toLowerCase(Locale.ROOT)) {
            case "anthropic" -> json ? anthropic.completeJson(prompt, effective) : anthropic.completeText(prompt, effective);
            case "openrouter" -> json ? openRouter.completeJson(prompt, effective) : openRouter.completeText(prompt, effective);
            default -> json ? selected.completeJson(prompt) : selected.completeText(prompt);
        };
    }

    private LlmClient selected(String provider) {
        return switch (provider.toLowerCase(Locale.ROOT)) {
            case "anthropic" -> anthropic;
            case "openrouter" -> openRouter;
            case "fallback", "none", "mock" -> null;
            default -> null;
        };
    }
}
