package com.acme.data360agent.llm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:llm-settings-service-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.security.enabled=false",
        "app.secrets.key=test-secret",
        "anthropic.api-key=env-anthropic-key",
        "anthropic.model=claude-env"
})
class LlmSettingsServiceTest {
    @Autowired
    private LlmSettingsService settings;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanSettingsTables() {
        jdbc.update("DELETE FROM llm_settings");
        jdbc.update("DELETE FROM app_users");
        jdbc.update("DELETE FROM organizations");
    }

    @Test
    void returnsEnvironmentFallbackBeforeSave() {
        var current = settings.current();

        assertThat(current.provider()).isEqualTo("anthropic");
        assertThat(current.model()).isEqualTo("claude-env");
        assertThat(current.apiKeyConfigured()).isTrue();
        assertThat(current.apiKeyLast4()).isEqualTo("-key");
    }

    @Test
    void savesTokenWithoutReturningSecret() {
        var saved = settings.save(new LlmSettingsRequest(
                "openrouter",
                "anthropic/claude-sonnet-4.5",
                "sk-or-secret-token",
                false
        ));

        assertThat(saved.provider()).isEqualTo("openrouter");
        assertThat(saved.model()).isEqualTo("anthropic/claude-sonnet-4.5");
        assertThat(saved.apiKeyConfigured()).isTrue();
        assertThat(saved.apiKeyLast4()).isEqualTo("oken");
        assertThat(settings.effective().apiKey()).isEqualTo("sk-or-secret-token");
    }
}
