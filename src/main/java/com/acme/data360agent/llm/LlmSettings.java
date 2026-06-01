package com.acme.data360agent.llm;

import java.io.Serializable;

public record LlmSettings(
        String organizationId,
        String provider,
        String model,
        boolean apiKeyConfigured,
        String apiKeyLast4
) implements Serializable {
}
