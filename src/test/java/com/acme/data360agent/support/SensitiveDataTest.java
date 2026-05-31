package com.acme.data360agent.support;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveDataTest {
    @Test
    void redactsApiKeysCredentialsAndAuthorizationFields() {
        var redacted = SensitiveData.redactMap(Map.of(
                "apiKey", "anthropic-key",
                "x-api-key", "router-key",
                "credential", "credential",
                "Authorization", "Bearer token",
                "safe", "value"
        ));

        assertThat(redacted)
                .containsEntry("apiKey", "***")
                .containsEntry("x-api-key", "***")
                .containsEntry("credential", "***")
                .containsEntry("Authorization", "***")
                .containsEntry("safe", "value");
    }
}
