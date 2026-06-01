package com.acme.data360agent.llm;

import com.acme.data360agent.config.AnthropicProperties;
import com.acme.data360agent.config.LlmProperties;
import com.acme.data360agent.identity.IdentityService;
import com.acme.data360agent.security.CurrentUserService;
import com.acme.data360agent.support.SecretCipher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Service
public class LlmSettingsService {
    private final JdbcTemplate jdbc;
    private final SecretCipher secrets;
    private final IdentityService identities;
    private final CurrentUserService users;
    private final LlmProperties llmProperties;
    private final AnthropicProperties anthropicProperties;

    public LlmSettingsService(JdbcTemplate jdbc, SecretCipher secrets, IdentityService identities, CurrentUserService users, LlmProperties llmProperties, AnthropicProperties anthropicProperties) {
        this.jdbc = jdbc;
        this.secrets = secrets;
        this.identities = identities;
        this.users = users;
        this.llmProperties = llmProperties;
        this.anthropicProperties = anthropicProperties;
    }

    public LlmSettings current() {
        var organizationId = organizationId();
        var row = row(organizationId);
        var effective = effectiveFor(row);
        return new LlmSettings(
                organizationId,
                effective.provider(),
                effective.model(),
                effective.configured(),
                last4(effective.apiKey())
        );
    }

    @Transactional
    public LlmSettings save(LlmSettingsRequest request) {
        var organizationId = organizationId();
        identities.ensureDefaultOrganization();
        var existing = row(organizationId);
        var encrypted = existing == null ? null : existing.encryptedApiKey();
        if (request.clearApiKey()) {
            encrypted = null;
        } else if (request.apiKey() != null && !request.apiKey().isBlank()) {
            encrypted = secrets.encrypt(request.apiKey().trim());
        }
        var updated = jdbc.update("""
                UPDATE llm_settings
                SET provider = ?, model = ?, encrypted_api_key = ?, updated_by = ?, updated_at = ?
                WHERE organization_id = ?
                """,
                normalizeProvider(request.provider()),
                request.model().trim(),
                encrypted,
                users.currentUser().userId(),
                Instant.now(),
                organizationId);
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO llm_settings (organization_id, provider, model, encrypted_api_key, updated_by, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                organizationId,
                normalizeProvider(request.provider()),
                request.model().trim(),
                encrypted,
                users.currentUser().userId(),
                Instant.now());
        }
        return current();
    }

    public EffectiveLlmSettings effective() {
        return effectiveFor(row(organizationId()));
    }

    private EffectiveLlmSettings effectiveFor(SettingsRow row) {
        var provider = row == null ? normalizeProvider(llmProperties.resolvedProvider()) : normalizeProvider(row.provider());
        var model = row == null ? fallbackModel(provider) : row.model();
        var apiKey = row == null ? fallbackApiKey(provider) : decrypt(row.encryptedApiKey());
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = fallbackApiKey(provider);
        }
        return new EffectiveLlmSettings(provider, model, apiKey);
    }

    private SettingsRow row(String organizationId) {
        var rows = jdbc.query("""
                SELECT organization_id, provider, model, encrypted_api_key
                FROM llm_settings
                WHERE organization_id = ?
                """, (rs, rowNum) -> new SettingsRow(
                rs.getString("organization_id"),
                rs.getString("provider"),
                rs.getString("model"),
                rs.getString("encrypted_api_key")
        ), organizationId);
        return rows.stream().findFirst().orElse(null);
    }

    private String organizationId() {
        return users.organizationId();
    }

    private String normalizeProvider(String provider) {
        var value = provider == null || provider.isBlank() ? "anthropic" : provider.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "anthropic", "openrouter" -> value;
            default -> "anthropic";
        };
    }

    private String fallbackModel(String provider) {
        if ("openrouter".equals(provider)) {
            return llmProperties.openrouter() == null ? "anthropic/claude-sonnet-4.6" : llmProperties.openrouter().resolvedModel(llmProperties.model());
        }
        if (llmProperties.model() != null && !llmProperties.model().isBlank()) {
            return llmProperties.model();
        }
        return anthropicProperties.model();
    }

    private String fallbackApiKey(String provider) {
        if ("openrouter".equals(provider)) {
            return llmProperties.openrouter() == null ? null : llmProperties.openrouter().apiKey();
        }
        return anthropicProperties.apiKey();
    }

    private String decrypt(String encrypted) {
        return encrypted == null || encrypted.isBlank() ? null : secrets.decrypt(encrypted);
    }

    private String last4(String value) {
        return value == null || value.isBlank() ? "" : value.substring(Math.max(0, value.length() - 4));
    }

    private record SettingsRow(
            String organizationId,
            String provider,
            String model,
            String encryptedApiKey
    ) {
    }
}
