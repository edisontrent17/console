package com.acme.data360agent.mcp;

import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.support.SensitiveData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class McpToolRegistryService {
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };
    private static final List<String> DISCOVERY_QUERIES = List.of(
            "data lake object data model object data streams mapping",
            "identity resolution calculated insights segment activation",
            "dataspace connection query metadata sql",
            "data transform datakit search index semantic data model retriever",
            "data action eventing gdpr standard mapping smart tools"
    );
    private final McpSettingsService settings;
    private final ObjectMapper objectMapper;
    private final Map<String, McpToolRegistrySnapshot> cachedSnapshots = new LinkedHashMap<>();

    public McpToolRegistryService(McpSettingsService settings, ObjectMapper objectMapper) {
        this.settings = settings;
        this.objectMapper = objectMapper;
    }

    public synchronized McpToolRegistrySnapshot current() {
        return current(currentOrganizationId());
    }

    public synchronized McpToolRegistrySnapshot current(String organizationId) {
        var normalizedOrganizationId = normalizeOrganizationId(organizationId);
        var cachedSnapshot = cachedSnapshots.get(normalizedOrganizationId);
        if (cachedSnapshot != null && Duration.between(cachedSnapshot.discoveredAt(), Instant.now()).compareTo(CACHE_TTL) < 0) {
            return cachedSnapshot;
        }
        return discover(normalizedOrganizationId);
    }

    public synchronized McpToolRegistrySnapshot discover() {
        return discover(currentOrganizationId());
    }

    public synchronized McpToolRegistrySnapshot discover(String organizationId) {
        var normalizedOrganizationId = normalizeOrganizationId(organizationId);
        var tools = new ArrayList<McpToolDescriptor>();
        var statuses = new ArrayList<McpToolRegistrySnapshot.McpServerRegistryStatus>();
        if (settings == null) {
            var snapshot = new McpToolRegistrySnapshot(Instant.now(), tools, statuses);
            cachedSnapshots.put(normalizedOrganizationId, snapshot);
            return snapshot;
        }
        for (var serverId : settings.enabledServerIds(normalizedOrganizationId)) {
            var executionServer = settings.isExecutionServer(serverId);
            var connectorDefinitionHash = settings.connectorDefinitionHash(normalizedOrganizationId, serverId);
            var launch = settings.launchConfigurationFor(normalizedOrganizationId, serverId);
            if (launch.isEmpty()) {
                statuses.add(new McpToolRegistrySnapshot.McpServerRegistryStatus(serverId, "skipped", "Server is enabled but has no launch configuration.", 0, executionServer, connectorDefinitionHash));
                continue;
            }
            try {
                var client = new McpStdioClient(
                        launch.get().commandLine(),
                        launch.get().environment(),
                        launch.get().workingDirectory(),
                        Duration.ofSeconds(8),
                        objectMapper
                );
                var discovered = client.listTools();
                var payloadMetadata = payloadExampleMetadata(launch.get());
                discovered.stream()
                        .map(tool -> descriptor(serverId, tool.name(), tool.description(), classify(tool.name()), tool.inputSchema(), payloadMetadata.get(tool.name())))
                        .forEach(tools::add);
                var underlyingTools = discoverUnderlyingTools(serverId, discovered, launch.get(), payloadMetadata);
                tools.addAll(underlyingTools);
                var count = discovered.size() + underlyingTools.size();
                statuses.add(new McpToolRegistrySnapshot.McpServerRegistryStatus(serverId, "passed", "Discovered " + count + " tools.", count, executionServer, connectorDefinitionHash));
            } catch (Exception e) {
                statuses.add(new McpToolRegistrySnapshot.McpServerRegistryStatus(serverId, "failed", message(e), 0, executionServer, connectorDefinitionHash));
            }
        }
        var snapshot = new McpToolRegistrySnapshot(Instant.now(), tools, statuses);
        cachedSnapshots.put(normalizedOrganizationId, snapshot);
        return snapshot;
    }

    public synchronized void invalidate() {
        cachedSnapshots.clear();
    }

    public synchronized void invalidate(String organizationId) {
        cachedSnapshots.remove(normalizeOrganizationId(organizationId));
    }

    public String classify(String toolName) {
        var name = toolName == null ? "" : toolName.toLowerCase(Locale.ROOT);
        if (containsAny(name, "delete", "drop", "truncate", "revoke", "destroy", "purge")) {
            return "destructive";
        }
        if (containsAny(name, "list", "get", "describe", "search", "query", "preview", "status", "metadata")) {
            return "read";
        }
        if (containsAny(name, "create", "update", "upsert", "run", "publish", "activate", "mapping_create")) {
            return "write";
        }
        return "read";
    }

    private boolean containsAny(String value, String... tokens) {
        for (var token : tokens) {
            if (value.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String message(Exception e) {
        var message = e.getMessage();
        return message == null || message.isBlank() ? e.getClass().getSimpleName() : message;
    }

    private String currentOrganizationId() {
        return settings == null ? PlanStore.DEFAULT_ORGANIZATION_ID : settings.current().organizationId();
    }

    private String normalizeOrganizationId(String organizationId) {
        return organizationId == null || organizationId.isBlank() ? PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
    }

    private List<McpToolDescriptor> discoverUnderlyingTools(String serverId, List<McpTool> facadeTools, McpLaunchConfiguration launch, Map<String, ToolPayloadMetadata> payloadMetadata) {
        var facadeNames = facadeTools.stream().map(McpTool::name).collect(java.util.stream.Collectors.toSet());
        if (!facadeNames.contains("execute")) {
            return List.of();
        }
        var names = new LinkedHashSet<String>();
        if (facadeNames.contains("search")) {
            for (var query : DISCOVERY_QUERIES) {
                names.addAll(searchToolNames(launch, query));
            }
        }
        names.addAll(payloadMetadata.keySet());
        return names.stream()
                .filter(name -> !facadeNames.contains(name))
                .map(name -> descriptor(serverId, name, "", classify(name), Map.of(), payloadMetadata.get(name)))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private Map<String, ToolPayloadMetadata> payloadExampleMetadata(McpLaunchConfiguration launch) {
        try {
            var raw = new McpStdioClient(
                    launch.commandLine(),
                    launch.environment(),
                    launch.workingDirectory(),
                    Duration.ofSeconds(8),
                    objectMapper
            ).callTool("payload_examples", Map.of());
            var response = readToolResponse(raw);
            var available = response.get("availableExamples");
            if (!(available instanceof Iterable<?> iterable)) {
                return Map.of();
            }
            var metadata = new LinkedHashMap<String, ToolPayloadMetadata>();
            for (var item : iterable) {
                if (item != null && !String.valueOf(item).isBlank()) {
                    var toolName = String.valueOf(item);
                    var detail = readToolResponse(new McpStdioClient(
                            launch.commandLine(),
                            launch.environment(),
                            launch.workingDirectory(),
                            Duration.ofSeconds(8),
                            objectMapper
                    ).callTool("payload_examples", Map.of("toolName", toolName)));
                    metadata.put(toolName, ToolPayloadMetadata.from(detail));
                }
            }
            return Map.copyOf(metadata);
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> searchToolNames(McpLaunchConfiguration launch, String query) {
        try {
            var raw = new McpStdioClient(
                    launch.commandLine(),
                    launch.environment(),
                    launch.workingDirectory(),
                    Duration.ofSeconds(8),
                    objectMapper
            ).callTool("search", Map.of("query", query));
            var response = readToolResponse(raw);
            if (!(response.get("results") instanceof Iterable<?> results)) {
                return List.of();
            }
            var names = new LinkedHashSet<String>();
            for (var result : results) {
                if (!(result instanceof Map<?, ?> family) || !(family.get("tools") instanceof Iterable<?> tools)) {
                    continue;
                }
                for (var tool : tools) {
                    if (tool != null && !String.valueOf(tool).isBlank()) {
                        names.add(String.valueOf(tool));
                    }
                }
            }
            return List.copyOf(names);
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private Map<String, Object> readToolResponse(McpToolCallResult result) {
        var text = result.text();
        if (text != null && !text.isBlank()) {
            try {
                return objectMapper.readValue(text, MAP);
            } catch (Exception ignored) {
                return Map.of();
            }
        }
        return result.rawResult();
    }

    private McpToolDescriptor descriptor(String serverId, String name, String description, String effect, Map<String, Object> inputSchema, ToolPayloadMetadata metadata) {
        var effectiveDescription = hasText(description)
                ? description
                : metadata == null ? "" : metadata.description();
        var effectiveInputSchema = inputSchema == null || inputSchema.isEmpty()
                ? metadata == null ? Map.<String, Object>of() : metadata.inputSchema()
                : inputSchema;
        var selectorContracts = selectorContracts(name);
        return new McpToolDescriptor(
                serverId,
                name,
                effectiveDescription,
                effect,
                effectiveInputSchema,
                outputSchema(selectorContracts),
                metadata == null ? List.of() : metadata.examples(),
                selectorContracts
        );
    }

    private Map<String, String> selectorContracts(String toolName) {
        var name = toolName == null ? "" : toolName.toLowerCase(Locale.ROOT);
        var selectors = new LinkedHashMap<String, String>();
        if (containsAny(name, "segment_create")) {
            selectors.put("segmentId", "$.output.id");
            selectors.put("segmentApiName", "$.output.apiName");
        } else if (containsAny(name, "segment_publish")) {
            selectors.put("publishId", "$.output.id");
            selectors.put("segmentId", "$.output.segmentId");
        } else if (containsAny(name, "activation_create")) {
            selectors.put("activationId", "$.output.id");
            selectors.put("activationApiName", "$.output.apiName");
        } else if (containsAny(name, "ci_create", "calculated_insight_create")) {
            selectors.put("calculatedInsightId", "$.output.id");
            selectors.put("calculatedInsightApiName", "$.output.apiName");
        } else if (containsAny(name, "ir_create", "identity_resolution_create")) {
            selectors.put("rulesetId", "$.output.id");
            selectors.put("rulesetApiName", "$.output.apiName");
        } else if (containsAny(name, "data_stream_create", "datastream_create")) {
            selectors.put("dataStreamId", "$.output.id");
            selectors.put("dataStreamApiName", "$.output.apiName");
        } else if (containsAny(name, "dmo_create", "dlo_create", "mapping_create")) {
            selectors.put("objectId", "$.output.id");
            selectors.put("objectApiName", "$.output.apiName");
        } else if (containsAny(name, "create", "update", "upsert", "run")) {
            selectors.put("id", "$.output.id");
        }
        return Map.copyOf(selectors);
    }

    private Map<String, Object> outputSchema(Map<String, String> selectorContracts) {
        if (selectorContracts == null || selectorContracts.isEmpty()) {
            return Map.of();
        }
        var properties = new LinkedHashMap<String, Object>();
        selectorContracts.forEach((name, path) -> properties.put(name, "string"));
        return Map.of(
                "type", "object",
                "properties", properties
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private record ToolPayloadMetadata(String description, Map<String, Object> inputSchema, List<Map<String, Object>> examples) {
        static ToolPayloadMetadata from(Map<String, Object> response) {
            var description = response.get("description") == null ? "" : String.valueOf(response.get("description"));
            var inputSchema = response.get("inputSchema") instanceof Map<?, ?> schema
                    ? copyMap(schema)
                    : Map.<String, Object>of();
            var examples = response.get("example") instanceof Map<?, ?> example
                    ? List.of(SensitiveData.redactMap(copyMap(example)))
                    : List.<Map<String, Object>>of();
            return new ToolPayloadMetadata(description, inputSchema, examples);
        }

        private static Map<String, Object> copyMap(Map<?, ?> input) {
            var copy = new LinkedHashMap<String, Object>();
            input.forEach((key, value) -> copy.put(String.valueOf(key), value));
            return Map.copyOf(copy);
        }
    }
}
