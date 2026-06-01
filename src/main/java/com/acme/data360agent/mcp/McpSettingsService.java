package com.acme.data360agent.mcp;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.identity.IdentityService;
import com.acme.data360agent.operation.OperationCatalogHash;
import com.acme.data360agent.security.CurrentUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class McpSettingsService {
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<McpEnvironmentVariable>> ENVIRONMENT_LIST = new TypeReference<>() {
    };
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
    private final ObjectMapper objectMapper;

    public McpSettingsService(JdbcTemplate jdbc, CurrentUserService users, IdentityService identities, AppProperties appProperties, Environment environment, ObjectMapper objectMapper) {
        this.jdbc = jdbc;
        this.users = users;
        this.identities = identities;
        this.appProperties = appProperties;
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    public McpSettings current() {
        var organizationId = organizationId();
        return new McpSettings(organizationId, settingsFor(organizationId));
    }

    @Transactional
    public McpSettings save(McpSettingsRequest request) {
        var organizationId = organizationId();
        var known = definitionsById();
        var existingSettings = settingsFor(organizationId).stream()
                .collect(java.util.stream.Collectors.toMap(McpServerSetting::id, setting -> setting));
        var updatedBy = users.currentUser().userId();
        for (var server : request.servers()) {
            var serverId = normalizeId(server.id());
            if (!known.containsKey(serverId)) {
                throw new IllegalArgumentException("Unknown MCP server: " + server.id());
            }
            validateManagedConnectorRequest(server);
            upsert(organizationId, serverId, server, updatedBy, existingSettings.get(serverId));
        }
        return new McpSettings(organizationId, settingsFor(organizationId));
    }

    public Optional<String> commandFor(String serverId) {
        return launchConfigurationFor(serverId).map(configuration -> String.join(" ", configuration.commandLine()));
    }

    public Optional<McpLaunchConfiguration> launchConfigurationFor(String serverId) {
        return launchConfigurationFor(organizationId(), serverId);
    }

    public Optional<McpLaunchConfiguration> launchConfigurationFor(String organizationId, String serverId) {
        var setting = settingFor(normalizeOrganizationId(organizationId), normalizeId(serverId));
        if (setting.isEmpty() || !setting.get().enabled()) {
            return Optional.empty();
        }
        var value = setting.get();
        if (!"stdio".equalsIgnoreCase(value.transport())) {
            throw new IllegalStateException("Only stdio MCP execution is supported for " + value.label() + ".");
        }
        var launchEnvironment = launchEnvironment(value);
        return Optional.ofNullable(value.command())
                .filter(command -> !command.isBlank())
                .map(command -> new McpLaunchConfiguration(
                        command,
                        value.arguments(),
                        launchEnvironment,
                        value.environmentPassthrough(),
                        value.workingDirectory()
                ));
    }

    public List<String> enabledServerIds() {
        return enabledServerIds(organizationId());
    }

    public List<String> enabledServerIds(String organizationId) {
        return new McpSettings(normalizeOrganizationId(organizationId), settingsFor(normalizeOrganizationId(organizationId))).servers().stream()
                .filter(McpServerSetting::enabled)
                .map(McpServerSetting::id)
                .toList();
    }

    public McpSettingsValidation validateCurrent() {
        var results = current().servers().stream()
                .map(this::validateServer)
                .toList();
        var failed = results.stream().filter(result -> "failed".equals(result.status())).count();
        var passed = results.stream().filter(result -> "passed".equals(result.status())).count();
        var status = failed > 0 ? "failed" : passed > 0 ? "passed" : "skipped";
        var message = switch (status) {
            case "failed" -> failed + " MCP server validation " + (failed == 1 ? "failed." : "checks failed.");
            case "passed" -> passed + " MCP server " + (passed == 1 ? "validated." : "servers validated.");
            default -> "No enabled MCP servers to validate.";
        };
        return new McpSettingsValidation(status, message, results);
    }

    public List<McpServerDefinition> definitions() {
        return DEFINITIONS;
    }

    private McpServerValidation validateServer(McpServerSetting server) {
        if (!server.enabled()) {
            return new McpServerValidation(server.id(), server.name(), "skipped", "Server is disabled.", 0, List.of());
        }
        if (!"stdio".equalsIgnoreCase(server.transport())) {
            return new McpServerValidation(server.id(), server.name(), "failed", "Only stdio MCP validation is supported.", 0, List.of());
        }
        if (server.command() == null || server.command().isBlank()) {
            return new McpServerValidation(server.id(), server.name(), "failed", "Launch command is required.", 0, List.of());
        }
        try {
            var launch = new McpLaunchConfiguration(
                    server.command(),
                    server.arguments(),
                    McpLaunchConfiguration.environmentMap(server.environment()),
                    server.environmentPassthrough(),
                    server.workingDirectory()
            );
            var tools = new McpStdioClient(
                    launch.commandLine(),
                    launch.environment(),
                    launch.workingDirectory(),
                    Duration.ofSeconds(8),
                    objectMapper
            ).listTools();
            var toolNames = tools.stream()
                    .map(McpTool::name)
                    .filter(name -> name != null && !name.isBlank())
                    .limit(8)
                    .toList();
            var message = tools.isEmpty()
                    ? "Connected, but the server did not advertise tools."
                    : "Connected and discovered " + tools.size() + " " + (tools.size() == 1 ? "tool." : "tools.");
            return new McpServerValidation(server.id(), server.name(), "passed", message, tools.size(), toolNames);
        } catch (Exception e) {
            return new McpServerValidation(server.id(), server.name(), "failed", validationMessage(e), 0, List.of());
        }
    }

    private List<McpServerSetting> settingsFor(String organizationId) {
        var rows = settingRows(organizationId);
        return DEFINITIONS.stream()
                .map(definition -> {
                    var row = rows.get(definition.id());
                    var managed = managedConnectorsEnabled();
                    var rawCommand = managed ? defaultCommand(definition.id()) : firstText(row == null ? null : row.command(), defaultCommand(definition.id()));
                    var defaultArguments = defaultArguments(definition.id(), rawCommand);
                    var arguments = managed ? defaultArguments : defaultIfEmpty(row == null ? List.of() : row.arguments(), defaultArguments);
                    var command = displayCommand(definition.id(), rawCommand, arguments);
                    var name = firstText(row == null ? null : row.name(), definition.label());
                    var enabled = row == null ? defaultEnabled(definition.id(), command) : row.enabled();
                    return new McpServerSetting(
                            definition.id(),
                            name,
                            definition.label(),
                            definition.description(),
                            definition.executionServer(),
                            enabled,
                            command != null && !command.isBlank(),
                            row == null ? "stdio" : firstText(row.transport(), "stdio"),
                            command == null ? "" : command,
                            managed || row == null ? "" : nullToEmpty(row.endpoint()),
                            arguments,
                            defaultIfEmpty(row == null ? List.of() : row.environment(), defaultEnvironment(definition.id())),
                            managed ? defaultEnvironmentPassthrough(definition.id()) : defaultIfEmpty(row == null ? List.of() : row.environmentPassthrough(), defaultEnvironmentPassthrough(definition.id())),
                            managed || row == null ? "" : nullToEmpty(row.workingDirectory()),
                            preview(commandLinePreview(command, arguments))
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
                SELECT server_id, name, enabled, transport, command, endpoint, arguments_json,
                       environment_json, environment_passthrough_json, working_directory
                FROM mcp_server_settings
                WHERE organization_id = ?
                """, (rs, rowNum) -> new Row(
                rs.getString("server_id"),
                rs.getString("name"),
                rs.getBoolean("enabled"),
                rs.getString("transport"),
                rs.getString("command"),
                rs.getString("endpoint"),
                readList(rs.getString("arguments_json"), STRING_LIST),
                readList(rs.getString("environment_json"), ENVIRONMENT_LIST),
                readList(rs.getString("environment_passthrough_json"), STRING_LIST),
                rs.getString("working_directory")
        ), organizationId);
        var byId = new LinkedHashMap<String, Row>();
        rows.forEach(row -> byId.put(row.serverId(), row));
        return byId;
    }

    public boolean isExecutionServer(String serverId) {
        var definition = definitionsById().get(normalizeId(serverId));
        return definition != null && definition.executionServer();
    }

    public String connectorDefinitionHash(String serverId) {
        return connectorDefinitionHash(organizationId(), serverId);
    }

    public String connectorDefinitionHash(String organizationId, String serverId) {
        var normalized = normalizeId(serverId);
        var definition = definitionsById().get(normalized);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown MCP server: " + serverId);
        }
        var setting = settingFor(normalizeOrganizationId(organizationId), normalized).orElse(null);
        var command = setting == null ? defaultCommand(normalized) : setting.command();
        var arguments = setting == null ? defaultArguments(normalized, command) : setting.arguments();
        var transport = setting == null ? "stdio" : setting.transport();
        var environmentKeys = setting == null
                ? defaultEnvironment(normalized).stream().map(McpEnvironmentVariable::key).sorted().toList()
                : setting.environment().stream().map(McpEnvironmentVariable::key).sorted().toList();
        var environmentPassthrough = setting == null
                ? defaultEnvironmentPassthrough(normalized)
                : setting.environmentPassthrough().stream().sorted().toList();
        return "sha256:" + OperationCatalogHash.sha256Hex(Map.ofEntries(
                Map.entry("schemaVersion", "mcp-connector-definition-2026-06-01"),
                Map.entry("id", definition.id()),
                Map.entry("label", definition.label()),
                Map.entry("description", definition.description()),
                Map.entry("executionServer", definition.executionServer()),
                Map.entry("transport", transport == null ? "stdio" : transport),
                Map.entry("command", command == null ? "" : command),
                Map.entry("arguments", arguments == null ? List.of() : arguments),
                Map.entry("environmentKeys", environmentKeys),
                Map.entry("environmentPassthrough", environmentPassthrough),
                Map.entry("workingDirectory", setting == null ? "" : nullToEmpty(setting.workingDirectory()))
        ));
    }

    private void upsert(String organizationId, String serverId, McpSettingsRequest.Server server, String updatedBy, McpServerSetting existing) {
        var command = managedConnectorsEnabled() ? null : cleanCommand(server.command());
        var now = Instant.now();
        var environment = cleanEnvironment(server.environment(), existing == null ? List.of() : existing.environment());
        var updated = jdbc.update("""
                UPDATE mcp_server_settings
                SET name = ?, enabled = ?, transport = ?, command = ?, endpoint = ?,
                    arguments_json = ?, environment_json = ?, environment_passthrough_json = ?,
                    working_directory = ?, updated_by = ?, updated_at = ?
                WHERE organization_id = ? AND server_id = ?
                """,
                cleanText(server.name()),
                server.enabled(),
                managedConnectorsEnabled() ? "stdio" : cleanTransport(server.transport()),
                command,
                managedConnectorsEnabled() ? null : cleanText(server.endpoint()),
                writeJson(managedConnectorsEnabled() ? List.of() : cleanList(server.arguments())),
                writeJson(environment),
                writeJson(managedConnectorsEnabled() ? List.of() : cleanList(server.environmentPassthrough())),
                managedConnectorsEnabled() ? null : cleanText(server.workingDirectory()),
                updatedBy,
                now,
                organizationId,
                serverId);
        if (updated > 0) {
            return;
        }
        jdbc.update("""
                INSERT INTO mcp_server_settings (
                    organization_id, server_id, name, enabled, transport, command, endpoint,
                    arguments_json, environment_json, environment_passthrough_json,
                    working_directory, updated_by, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                organizationId,
                serverId,
                cleanText(server.name()),
                server.enabled(),
                managedConnectorsEnabled() ? "stdio" : cleanTransport(server.transport()),
                command,
                managedConnectorsEnabled() ? null : cleanText(server.endpoint()),
                writeJson(managedConnectorsEnabled() ? List.of() : cleanList(server.arguments())),
                writeJson(environment),
                writeJson(managedConnectorsEnabled() ? List.of() : cleanList(server.environmentPassthrough())),
                managedConnectorsEnabled() ? null : cleanText(server.workingDirectory()),
                updatedBy,
                now);
    }

    private void validateManagedConnectorRequest(McpSettingsRequest.Server server) {
        if (!managedConnectorsEnabled()) {
            return;
        }
        if (cleanCommand(server.command()) != null
                || cleanText(server.endpoint()) != null
                || !cleanList(server.arguments()).isEmpty()
                || !cleanList(server.environmentPassthrough()).isEmpty()
                || cleanText(server.workingDirectory()) != null
                || (server.transport() != null && !server.transport().isBlank() && !"stdio".equalsIgnoreCase(server.transport()))) {
            throw new IllegalArgumentException("Managed MCP connector mode does not allow tenant-supplied launch commands, arguments, transport, working directory, endpoints, or environment passthrough.");
        }
    }

    private boolean managedConnectorsEnabled() {
        return environment.getProperty("app.mcp.managed-connectors.enabled", Boolean.class, false);
    }

    private String organizationId() {
        return users.organizationId();
    }

    private String normalizeOrganizationId(String organizationId) {
        return organizationId == null || organizationId.isBlank()
                ? com.acme.data360agent.execution.PlanStore.DEFAULT_ORGANIZATION_ID
                : organizationId;
    }

    private Map<String, McpServerDefinition> definitionsById() {
        var byId = new LinkedHashMap<String, McpServerDefinition>();
        DEFINITIONS.forEach(definition -> byId.put(definition.id(), definition));
        return Map.copyOf(byId);
    }

    private boolean defaultEnabled(String serverId, String command) {
        if ("data360".equals(serverId)) {
            var client = appProperties.data360() == null ? "" : appProperties.data360().client();
            var explicitlyConfigured = appProperties.data360() != null && appProperties.data360().mcp() != null;
            return command != null && !command.isBlank()
                    && ("mcp".equalsIgnoreCase(client)
                    || explicitlyConfigured
                    || localData360McpJar().isPresent());
        }
        return command != null && !command.isBlank();
    }

    private String defaultCommand(String serverId) {
        return switch (serverId) {
            case "data360" -> appProperties.data360() == null || appProperties.data360().mcp() == null
                    ? firstText(environment.getProperty("app.mcp.data360.command", ""), localData360McpCommand())
                    : firstText(appProperties.data360().mcp().command(), firstText(environment.getProperty("app.mcp.data360.command", ""), localData360McpCommand()));
            case "snowflake" -> environment.getProperty("app.mcp.snowflake.command", "");
            case "salesforce-crm" -> environment.getProperty("app.mcp.salesforce-crm.command", "");
            default -> "";
        };
    }

    private List<String> defaultArguments(String serverId, String command) {
        if (!"data360".equals(serverId) || command == null || command.isBlank()) {
            return List.of();
        }
        var parts = McpCommandParser.parse(command);
        if (parts.size() == 3 && "java".equals(parts.getFirst()) && "-jar".equals(parts.get(1))) {
            return List.of("-jar", parts.get(2));
        }
        return List.of();
    }

    private String displayCommand(String serverId, String command, List<String> arguments) {
        if (!"data360".equals(serverId) || command == null || command.isBlank() || arguments == null || arguments.isEmpty()) {
            return command;
        }
        var parts = McpCommandParser.parse(command);
        if (parts.size() == 3 && "java".equals(parts.getFirst()) && "-jar".equals(parts.get(1))) {
            return "java";
        }
        return command;
    }

    private List<McpEnvironmentVariable> defaultEnvironment(String serverId) {
        if (!"data360".equals(serverId) || appProperties.data360() == null || appProperties.data360().connect() == null) {
            return List.of();
        }
        var connect = appProperties.data360().connect();
        return List.of(
                        new McpEnvironmentVariable("DATA360_INSTANCE_URL", nullToEmpty(connect.instanceUrl())),
                        new McpEnvironmentVariable("DATA360_ACCESS_TOKEN", nullToEmpty(connect.accessToken())),
                        new McpEnvironmentVariable("DATA360_LOGIN_URL", connect.resolvedLoginUrl()),
                        new McpEnvironmentVariable("DATA360_CLIENT_ID", nullToEmpty(connect.clientId())),
                        new McpEnvironmentVariable("DATA360_API_VERSION", apiVersionForMcp(connect.resolvedApiVersion()))
                ).stream()
                .filter(variable -> variable.value() != null && !variable.value().isBlank())
                .toList();
    }

    private List<String> defaultEnvironmentPassthrough(String serverId) {
        if (!"data360".equals(serverId)) {
            return List.of();
        }
        return List.of(
                "DATA360_INSTANCE_URL",
                "DATA360_ACCESS_TOKEN",
                "DATA360_CLIENT_ID",
                "DATA360_CLIENT_SECRET",
                "DATA360_AUTH_FLOW",
                "DATA360_LOGIN_URL",
                "DATA360_API_VERSION",
                "DATA360_SEARCH_STRATEGY"
        );
    }

    private String apiVersionForMcp(String apiVersion) {
        return apiVersion == null ? "" : apiVersion.replaceFirst("^[vV]", "");
    }

    private <T> List<T> defaultIfEmpty(List<T> configured, List<T> defaults) {
        return configured == null || configured.isEmpty() ? defaults : configured;
    }

    private String localData360McpCommand() {
        return localData360McpJar()
                .map(path -> "java -jar " + path)
                .orElse("");
    }

    private Optional<String> localData360McpJar() {
        var home = environment.getProperty("user.home", System.getProperty("user.home", ""));
        if (home == null || home.isBlank()) {
            return Optional.empty();
        }
        var jar = Path.of(home, "Projects", "d360-mcp-server", "target", "data360-mcp-server-1.0.0.jar");
        return Files.isRegularFile(jar) ? Optional.of(jar.toString()) : Optional.empty();
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

    private String cleanTransport(String transport) {
        if (transport == null || transport.isBlank()) {
            return "stdio";
        }
        var normalized = transport.trim().toLowerCase(Locale.ROOT);
        if ("stdio".equals(normalized) || "streamable-http".equals(normalized)) {
            return normalized;
        }
        throw new IllegalArgumentException("Unsupported MCP transport: " + transport);
    }

    private String cleanText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private List<String> cleanList(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }

    private List<McpEnvironmentVariable> cleanEnvironment(List<McpEnvironmentVariable> values) {
        return cleanEnvironment(values, List.of());
    }

    private List<McpEnvironmentVariable> cleanEnvironment(List<McpEnvironmentVariable> values, List<McpEnvironmentVariable> existing) {
        if (values == null) {
            return List.of();
        }
        var existingByKey = existing == null ? Map.<String, String>of() : existing.stream()
                .filter(value -> value != null && value.key() != null)
                .collect(java.util.stream.Collectors.toMap(
                        value -> value.key().trim(),
                        value -> value.value() == null ? "" : value.value(),
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));
        return values.stream()
                .filter(value -> value != null && value.key() != null && !value.key().isBlank())
                .map(value -> {
                    var key = value.key().trim();
                    var preserved = value.hasRedactedValue() ? existingByKey.getOrDefault(key, "") : value.value();
                    return new McpEnvironmentVariable(key, preserved == null ? "" : preserved);
                })
                .toList();
    }

    private Map<String, String> launchEnvironment(McpServerSetting setting) {
        var launch = new LinkedHashMap<String, String>();
        for (var key : setting.environmentPassthrough()) {
            var normalized = key == null ? "" : key.trim();
            if (normalized.isBlank()) {
                continue;
            }
            var value = environment.getProperty(normalized);
            if (value == null) {
                value = System.getenv(normalized);
            }
            if (value != null) {
                launch.put(normalized, value);
            }
        }
        launch.putAll(McpLaunchConfiguration.environmentMap(setting.environment()));
        return launch;
    }

    private <T> List<T> readList(String value, TypeReference<List<T>> type) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(value, type);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? List.of() : value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save MCP settings.", e);
        }
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

    private String commandLinePreview(String command, List<String> arguments) {
        var value = command == null ? "" : command;
        if (arguments != null && !arguments.isEmpty()) {
            value = value + " " + String.join(" ", arguments);
        }
        return value.trim();
    }

    private String validationMessage(Exception e) {
        var cause = e;
        while (cause.getCause() instanceof Exception nested) {
            cause = nested;
        }
        var message = cause.getMessage();
        if (message == null || message.isBlank()) {
            message = e.getMessage();
        }
        return message == null || message.isBlank() ? "MCP validation failed." : message;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record Row(
            String serverId,
            String name,
            boolean enabled,
            String transport,
            String command,
            String endpoint,
            List<String> arguments,
            List<McpEnvironmentVariable> environment,
            List<String> environmentPassthrough,
            String workingDirectory
    ) {
    }
}
