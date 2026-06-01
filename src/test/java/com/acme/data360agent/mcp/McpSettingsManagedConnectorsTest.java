package com.acme.data360agent.mcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:mcp-settings-managed-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.security.enabled=false",
        "app.mcp.managed-connectors.enabled=true",
        "app.data360.mcp.command=java -jar /opt/d360-mcp-server.jar"
})
class McpSettingsManagedConnectorsTest {
    @Autowired
    private McpSettingsService settings;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanTables() {
        jdbc.update("DELETE FROM mcp_server_settings");
        jdbc.update("DELETE FROM app_users");
        jdbc.update("DELETE FROM organizations");
    }

    @Test
    void managedConnectorModeRejectsTenantSuppliedLaunchConfiguration() {
        var request = new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server(
                        "data360",
                        "Data 360",
                        true,
                        "stdio",
                        "node",
                        null,
                        List.of("/tmp/tenant-controlled.js"),
                        List.of(),
                        List.of("PATH"),
                        "/tmp"
                )
        ));

        assertThatThrownBy(() -> settings.save(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Managed MCP connector mode");
    }

    @Test
    void managedConnectorModeAllowsEnablementAndDeclaredCredentialsOnly() {
        settings.save(new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server(
                        "data360",
                        "Data 360",
                        true,
                        "stdio",
                        null,
                        null,
                        List.of(),
                        List.of(new McpEnvironmentVariable("DATA360_INSTANCE_URL", "https://example.my.salesforce.com")),
                        List.of(),
                        null
                )
        )));

        var saved = server(settings.current(), "data360");

        assertThat(saved.enabled()).isTrue();
        assertThat(saved.command()).isEqualTo("java");
        assertThat(saved.arguments()).containsExactly("-jar", "/opt/d360-mcp-server.jar");
        assertThat(saved.environment()).containsExactly(new McpEnvironmentVariable("DATA360_INSTANCE_URL", "https://example.my.salesforce.com"));
        assertThat(settings.commandFor("data360")).contains("java -jar /opt/d360-mcp-server.jar");
    }

    @Test
    void connectorDefinitionHashFreezesLaunchShapeWithoutSecretValues() {
        settings.save(new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server(
                        "data360",
                        "Data 360",
                        true,
                        "stdio",
                        null,
                        null,
                        List.of(),
                        List.of(
                                new McpEnvironmentVariable("DATA360_INSTANCE_URL", "https://example.my.salesforce.com"),
                                new McpEnvironmentVariable("DATA360_ACCESS_TOKEN", "secret-one")
                        ),
                        List.of(),
                        null
                )
        )));
        var first = settings.connectorDefinitionHash("data360");

        settings.save(new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server(
                        "data360",
                        "Data 360",
                        true,
                        "stdio",
                        null,
                        null,
                        List.of(),
                        List.of(
                                new McpEnvironmentVariable("DATA360_INSTANCE_URL", "https://example.my.salesforce.com"),
                                new McpEnvironmentVariable("DATA360_ACCESS_TOKEN", "secret-two")
                        ),
                        List.of(),
                        null
                )
        )));

        assertThat(first).startsWith("sha256:");
        assertThat(settings.connectorDefinitionHash("data360")).isEqualTo(first);
    }

    private McpServerSetting server(McpSettings settings, String id) {
        return settings.servers().stream()
                .filter(server -> server.id().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
