package com.acme.data360agent.mcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:mcp-settings-service-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.security.enabled=false",
        "app.data360.mcp.command=java -jar /opt/d360-mcp-server.jar",
        "app.mcp.snowflake.command=npx -y @modelcontextprotocol/server-snowflake"
})
class McpSettingsServiceTest {
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
    void returnsConfiguredDefaultsBeforeSave() {
        var current = settings.current();

        assertThat(current.organizationId()).isEqualTo("org_default");
        assertThat(server(current, "data360").enabled()).isTrue();
        assertThat(server(current, "data360").commandConfigured()).isTrue();
        assertThat(server(current, "data360").command()).isEqualTo("java");
        assertThat(server(current, "data360").arguments()).containsExactly("-jar", "/opt/d360-mcp-server.jar");
        assertThat(settings.commandFor("data360")).contains("java -jar /opt/d360-mcp-server.jar");
        assertThat(server(current, "data360").environmentPassthrough())
                .contains("DATA360_INSTANCE_URL", "DATA360_ACCESS_TOKEN", "DATA360_CLIENT_ID", "DATA360_CLIENT_SECRET", "DATA360_AUTH_FLOW");
        assertThat(server(current, "snowflake").enabled()).isTrue();
    }

    @Test
    void saveOverridesEnabledServersForOrganization() {
        var saved = settings.save(new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server("data360", false, "java -jar /opt/d360-mcp-server.jar"),
                new McpSettingsRequest.Server("snowflake", true, "node /opt/snowflake-mcp.js")
        )));

        assertThat(server(saved, "data360").enabled()).isFalse();
        assertThat(settings.commandFor("data360")).isEmpty();
        assertThat(server(saved, "snowflake").enabled()).isTrue();
        assertThat(settings.commandFor("snowflake")).contains("node /opt/snowflake-mcp.js");
    }

    @Test
    void savesLaunchConfigurationDetails() {
        settings.save(new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server(
                        "data360",
                        "Data Cloud Tools",
                        true,
                        "stdio",
                        "node",
                        null,
                        List.of("/opt/d360-mcp-server/index.js", "--stdio"),
                        List.of(new McpEnvironmentVariable("SALESFORCE_ORG", "demo")),
                        List.of("SALESFORCE_ACCESS_TOKEN"),
                        "/opt/d360-mcp-server"
                )
        )));

        var saved = server(settings.current(), "data360");
        assertThat(saved.name()).isEqualTo("Data Cloud Tools");
        assertThat(saved.arguments()).containsExactly("/opt/d360-mcp-server/index.js", "--stdio");
        assertThat(saved.environment()).containsExactly(new McpEnvironmentVariable("SALESFORCE_ORG", "demo"));
        assertThat(saved.environmentPassthrough()).containsExactly("SALESFORCE_ACCESS_TOKEN");
        assertThat(saved.workingDirectory()).isEqualTo("/opt/d360-mcp-server");

        var launch = settings.launchConfigurationFor("data360").orElseThrow();
        assertThat(launch.commandLine()).containsExactly("node", "/opt/d360-mcp-server/index.js", "--stdio");
        assertThat(launch.environment()).containsEntry("SALESFORCE_ORG", "demo");
    }

    @Test
    void redactsEnvironmentValuesAndPreservesMaskedValuesOnSave() {
        settings.save(new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server(
                        "data360",
                        "Data Cloud Tools",
                        true,
                        "stdio",
                        "node",
                        null,
                        List.of("/opt/d360-mcp-server/index.js", "--stdio"),
                        List.of(new McpEnvironmentVariable("SALESFORCE_ORG", "demo")),
                        List.of(),
                        "/opt/d360-mcp-server"
                )
        )));

        var redacted = settings.current().redacted();
        assertThat(server(redacted, "data360").environment())
                .containsExactly(new McpEnvironmentVariable("SALESFORCE_ORG", McpEnvironmentVariable.REDACTED_VALUE));

        settings.save(new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server(
                        "data360",
                        "Data Cloud Tools",
                        true,
                        "stdio",
                        "node",
                        null,
                        List.of("/opt/d360-mcp-server/index.js", "--stdio"),
                        server(redacted, "data360").environment(),
                        List.of(),
                        "/opt/d360-mcp-server"
                )
        )));

        assertThat(settings.launchConfigurationFor("data360").orElseThrow().environment())
                .containsEntry("SALESFORCE_ORG", "demo");
    }

    @Test
    void validationReportsInvalidEnabledLaunchCommand() {
        settings.save(new McpSettingsRequest(List.of(
                new McpSettingsRequest.Server("data360", true, "__missing_mcp_binary__")
        )));

        var validation = settings.validateCurrent();

        assertThat(validation.status()).isEqualTo("failed");
        assertThat(serverValidation(validation, "data360").status()).isEqualTo("failed");
        assertThat(serverValidation(validation, "data360").message()).isNotBlank();
    }

    private McpServerSetting server(McpSettings settings, String id) {
        return settings.servers().stream()
                .filter(server -> server.id().equals(id))
                .findFirst()
                .orElseThrow();
    }

    private McpServerValidation serverValidation(McpSettingsValidation validation, String id) {
        return validation.servers().stream()
                .filter(server -> server.id().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
