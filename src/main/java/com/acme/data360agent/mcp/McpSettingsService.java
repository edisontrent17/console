package com.acme.data360agent.mcp;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.identity.IdentityService;
import com.acme.data360agent.security.CurrentUserService;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class McpSettingsService {
    private static final List<McpServerDefinition> DEFINITIONS = List.of(
            new McpServerDefinition("data360", "Salesforce Data 360 MCP", "Data 360 setup, metadata, segments, activations, calculated insights, and monitors.", true),
            new McpServerDefinition("snowflake", "Snowflake MCP", "Warehouse discovery and source object inspection for Snowflake-backed data streams.", false),
            new McpServerDefinition("salesforce-crm", "Salesforce CRM MCP", "CRM object discovery and source metadata for Data 360 ingestion planning.", false)
    );

    private final JdbcTemplate jdbc;
    private final CurrentUserService users;
    private final IdentityService identities;
    private final AppProperties appProperties;
    private final Environment environment;

    public McpSettingsService(JdbcTemplate jdbc, CurrentUserService users, IdentityService identities, AppProperties appProperties, Environment environment) {
        this.jdbc = jdbc;
        this.users = users;
        this.identities = identities;
        this.appProperties = appProperties;
        this.environment = environment;
    }

    public McpSettings current() {
        var organizationId = organizationId();
        return new McpSettings(organizationId, settingsFor(organizationId));
    }

    @Transactional
    public McpSettings save(McpSettingsRequest request) {
        var organizationId = organizationId();
        var known = definitionsById();
        var updatedBy = users.currentUser().userId();
        for (var server : request.servers()) {
            var serverId = normalizeId(server.id());
            if (!known.containsKey(serverId)) {
                throw new IllegalArgumentException("Unknown MCP server: " + server.id());
            }
            upsert(organizationId, serverId, server.enabled(), cleanCommand(server.command()), updatedBy);
        }
        return new McpSettings(organizationId, settingsFor(organizationId));
    }

    public Optional<String> commandFor(String serverId) {
        var setting = settingFor(organizationId(), normalizeId(serverId));
        if (setting.isEmpty() || !setting.get().enabled()) {
            return Optional.empty();
        }
        return Optional.ofNullable(setting.get().command()).filter(value -> !value.isBlank());
    }

    public List<String> enabledServerIds() {
        return current().servers().stream()
                .filter(McpServerSetting::enabled)
                .map(McpServerSetting::id)
                .toList();
    }

    public List<McpServerDefinition> definitions() {
        return DEFINITIONS;
    }

    private List<McpServerSetting> settingsFor(String organizationId) {
        var rows = settingRows(organizationId);
        return DEFINITIONS.stream()
                .map(definition -> {
                    var row = rows.get(definition.id());
                    var command = firstText(row == null ? null : row.command(), defaultCommand(definition.id()));
                    var enabled = row == null ? defaultEnabled(definition.id(), command) : row.enabled();
                    return new McpServerSetting(
                            definition.id(),
                            definition.label(),
                            definition.description(),
                            definition.executionServer(),
                            enabled,
                            command != null && !command.isBlank(),
                            command == null ? "" : command,
                            preview(command)
                    );
                })
                .toList();
    }

    private Optional<McpServerSetting> settingFor(String organizationId, String serverId) {
        return settingsFor(organizationId).stream()
                .filter(setting -> setting.id().equals(serverId))
                .findFirst();
    }

    private Map<String, Row> settingRows(String organizationId) {
        var rows = jdbc.query("""
                SELECT server_id, enabled, command
                FROM mcp_server_settings
                WHERE organization_id = ?
                """, (rs, rowNum) -> new Row(
                rs.getString("server_id"),
                rs.getBoolean("enabled"),
                rs.getString("command")
        ), organizationId);
        var byId = new LinkedHashMap<String, Row>();
        rows.forEach(row -> byId.put(row.serverId(), row));
        return byId;
    }

    private void upsert(String organizationId, String serverId, boolean enabled, String command, String updatedBy) {
        var updated = jdbc.update("""
                UPDATE mcp_server_settings
                SET enabled = ?, command = ?, updated_by = ?, updated_at = ?
                WHERE organization_id = ? AND server_id = ?
                """, enabled, command, updatedBy, Instant.now(), organizationId, serverId);
        if (updated > 0) {
            return;
        }
        jdbc.update("""
                INSERT INTO mcp_server_settings (organization_id, server_id, enabled, command, updated_by, updated_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """, organizationId, serverId, enabled, command, updatedBy, Instant.now());
    }

    private String organizationId() {
        return Optional.ofNullable(users.currentUser().organizationId())
                .filter(value -> !value.isBlank())
                .orElseGet(identities::defaultOrganizationId);
    }

    private Map<String, McpServerDefinition> definitionsById() {
        var byId = new LinkedHashMap<String, McpServerDefinition>();
        DEFINITIONS.forEach(definition -> byId.put(definition.id(), definition));
        return Map.copyOf(byId);
    }

    private boolean defaultEnabled(String serverId, String command) {
        if ("data360".equals(serverId)) {
            var client = appProperties.data360() == null ? "" : appProperties.data360().client();
            return "mcp".equalsIgnoreCase(client) && command != null && !command.isBlank();
        }
        return command != null && !command.isBlank();
    }

    private String defaultCommand(String serverId) {
        return switch (serverId) {
            case "data360" -> appProperties.data360() == null || appProperties.data360().mcp() == null
                    ? environment.getProperty("app.mcp.data360.command", "")
                    : firstText(appProperties.data360().mcp().command(), environment.getProperty("app.mcp.data360.command", ""));
            case "snowflake" -> environment.getProperty("app.mcp.snowflake.command", "");
            case "salesforce-crm" -> environment.getProperty("app.mcp.salesforce-crm.command", "");
            default -> "";
        };
    }

    private String normalizeId(String serverId) {
        if (serverId == null || serverId.isBlank()) {
            throw new IllegalArgumentException("MCP server id is required.");
        }
        return serverId.trim().toLowerCase(Locale.ROOT);
    }

    private String cleanCommand(String command) {
        return command == null || command.isBlank() ? null : command.trim();
    }

    private String firstText(String first, String second) {
        return first == null || first.isBlank() ? second : first;
    }

    private String preview(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.length() <= 100 ? value : value.substring(0, 97) + "...";
    }

    private record Row(String serverId, boolean enabled, String command) {
    }
}
