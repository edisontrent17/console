package com.acme.data360agent.support;

import org.junit.jupiter.api.Test;

import java.util.List;
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

    @Test
    void redactsEmbeddedSecretsInStringsAndNestedValues() {
        var redacted = SensitiveData.redactMap(Map.of(
                "safeError", "MCP error: Authorization: Bearer 00Dxx.secret and client_secret=abc123",
                "nested", Map.of("message", "access_token\":\"token-value\""),
                "items", new Object[]{"api_key=my-key", "normal"}
        ));

        assertThat(redacted.get("safeError").toString())
                .contains("Authorization: ***")
                .contains("client_secret=***")
                .doesNotContain("00Dxx.secret")
                .doesNotContain("abc123");
        assertThat(redacted.get("nested").toString())
                .contains("access_token\":\"***")
                .doesNotContain("token-value");
        assertThat(redacted.get("items")).isEqualTo(List.of("api_key=***", "normal"));
    }

    @Test
    void capsLongTraceStrings() {
        var redacted = SensitiveData.redactText("x".repeat(5000));

        assertThat(redacted).endsWith("...[truncated]");
        assertThat(redacted.length()).isLessThan(4200);
    }

    @Test
    void detectsSensitiveValuesWithoutTreatingLongSafeTextAsSecret() {
        assertThat(SensitiveData.containsSensitiveData("segmentId", "seg_123")).isFalse();
        assertThat(SensitiveData.containsSensitiveData("message", "Authorization: Bearer 00Dxx.secret")).isTrue();
        assertThat(SensitiveData.containsSensitiveData("payload", "x".repeat(5000))).isFalse();
        assertThat(SensitiveData.containsSensitiveData("nested", Map.of("clientSecret", "secret"))).isTrue();
    }
}
