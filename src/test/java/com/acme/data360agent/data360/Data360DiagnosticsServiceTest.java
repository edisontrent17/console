package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.operation.OperationRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Data360DiagnosticsServiceTest {
    @Test
    void reportsMockModeAndRunsReadOnlySmoke() {
        var service = new Data360DiagnosticsService(
                new AppProperties("local", new AppProperties.Data360("mock", new AppProperties.Mcp(""), null)),
                new MockData360Client(),
                new OperationRegistry()
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
                new OperationRegistry()
        );

        var diagnostics = service.diagnostics();

        assertThat(diagnostics.mode()).isEqualTo("connect");
        assertThat(diagnostics.configured()).isTrue();
        assertThat(diagnostics.details()).containsEntry("directData360TokenConfigured", true);
        assertThat(diagnostics.details().toString()).doesNotContain("secret-token");
    }
}
