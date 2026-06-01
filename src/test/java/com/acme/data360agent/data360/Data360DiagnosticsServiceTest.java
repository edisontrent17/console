package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.operation.OperationRegistry;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Data360DiagnosticsServiceTest {
    @Test
    void reportsMockModeAndRunsReadOnlySmoke() {
        var service = new Data360DiagnosticsService(
                new AppProperties("local", new AppProperties.Data360("mock", new AppProperties.Mcp(""), null)),
                new MockData360Client(),
                new OperationRegistry(),
                mcpSettings("")
        );

        var diagnostics = service.diagnostics();
        var smoke = service.smokeMetadata();

        assertThat(diagnostics.mode()).isEqualTo("mock");
        assertThat(diagnostics.configured()).isTrue();
        assertThat(smoke.status()).isEqualTo("ok");
        assertThat(smoke.details()).containsEntry("smoke", "metadata");
    }

    @Test
    void connectDiagnosticsExposeOnlyConfigurationBooleans() {
        var service = new Data360DiagnosticsService(
                new AppProperties("local", new AppProperties.Data360(
                        "connect",
                        new AppProperties.Mcp(""),
                        new AppProperties.Connect("v66.0", "https://data360.example", "secret-token", "", "", "", "", "", "", "", "", 45, 60)
                )),
                new MockData360Client(),
                new OperationRegistry(),
                mcpSettings("")
        );

        var diagnostics = service.diagnostics();

        assertThat(diagnostics.mode()).isEqualTo("connect");
        assertThat(diagnostics.configured()).isTrue();
        assertThat(diagnostics.details()).containsEntry("directData360TokenConfigured", true);
        assertThat(diagnostics.details().toString()).doesNotContain("secret-token");
    }

    private McpSettingsService mcpSettings(String command) {
        var settings = mock(McpSettingsService.class);
        when(settings.commandFor("data360")).thenReturn(command == null || command.isBlank() ? Optional.empty() : Optional.of(command));
        return settings;
    }
}
