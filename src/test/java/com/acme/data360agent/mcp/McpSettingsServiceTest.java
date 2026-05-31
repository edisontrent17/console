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
        "app.data360.client=mcp",
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
        assertThat(settings.commandFor("data360")).contains("java -jar /opt/d360-mcp-server.jar");
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

    private McpServerSetting server(McpSettings settings, String id) {
        return settings.servers().stream()
                .filter(server -> server.id().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
