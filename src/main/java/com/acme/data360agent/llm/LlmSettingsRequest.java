package com.acme.data360agent.llm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record LlmSettingsRequest(
        @NotBlank @Pattern(regexp = "anthropic|openrouter") String provider,
        @NotBlank @Size(max = 255) String model,
        @Size(max = 4000) String apiKey,
        boolean clearApiKey
) implements Serializable {
}
