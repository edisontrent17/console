package com.acme.data360agent.data360;

import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.mcp.McpStdioClient;
import com.acme.data360agent.mcp.McpToolCallResult;
import com.acme.data360agent.mcp.McpToolRegistryService;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanStep;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.data360.client", havingValue = "mcp")
public class McpData360Client implements Data360Client {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final McpSettingsService mcpSettings;
    private final ObjectMapper objectMapper;
    private final McpToolRegistryService registry;

    public McpData360Client(McpSettingsService mcpSettings, ObjectMapper objectMapper) {
        this(mcpSettings, objectMapper, (McpToolRegistryService) null);
    }

    @Autowired
    public McpData360Client(McpSettingsService mcpSettings, ObjectMapper objectMapper, ObjectProvider<McpToolRegistryService> registry) {
        this(mcpSettings, objectMapper, registry == null ? null : registry.getIfAvailable());
    }

    public McpData360Client(McpSettingsService mcpSettings, ObjectMapper objectMapper, McpToolRegistryService registry) {
        this.mcpSettings = mcpSettings;
        this.objectMapper = objectMapper;
        this.registry = registry;
    }

    @Override
    public Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
        return call(operation, OperationBindingSnapshot.data360Mcp(operation), step, resolvedInput, context);
    }

    @Override
    public Data360CallResult call(OperationDefinition operation, OperationBindingSnapshot binding, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
        var organizationId = context == null ? PlanStore.DEFAULT_ORGANIZATION_ID : context.organizationId();
        var serverId = binding.mcpServerId() == null || binding.mcpServerId().isBlank() ? "data360" : binding.mcpServerId();
        var launch = mcpSettings.launchConfigurationFor(organizationId, serverId)
                .orElse(null);
        if (launch == null) {
            throw new IllegalStateException("Data 360 MCP is not enabled or no stdio command is configured for this organization.");
        }

        var facadeTool = facadeTool(binding, step);
        var arguments = compileArguments(binding, step, resolvedInput, context);
        validateMcpBinding(organizationId, binding, step);
        var raw = new McpStdioClient(launch, objectMapper).callTool(facadeTool, arguments);
        var audit = new LinkedHashMap<String, Object>();
        audit.put("mode", "mcp");
        audit.put("calledAt", java.time.Instant.now().toString());
        audit.put("organizationId", organizationId);
        audit.put("resource", binding.resource());
        audit.put("mcpServerId", nullToEmpty(binding.mcpServerId()));
        audit.put("facadeTool", facadeTool);
        audit.put("underlyingTool", nullToEmpty(binding.underlyingTool()));
        audit.put("schemaHash", binding.schemaHash());
        audit.put("bindingVersion", binding.bindingVersion());
        audit.put("connectorDefinitionHash", binding.connectorDefinitionHash());
        audit.put("idempotencyKey", context == null ? "" : context.idempotencyKey());
        audit.put("arguments", Map.of(
                "toolName", arguments.getOrDefault("toolName", facadeTool),
                "params", step.action() == Data360Action.MCP_EXECUTE ? resolvedInput.getOrDefault("params", Map.of()) : resolvedInput
        ));
        audit.put("argumentKeys", arguments.keySet().stream().sorted().toList());
        audit.put("rawKeys", raw.rawResult().keySet().stream().sorted().toList());
        audit.put("rawResult", raw.rawResult());
        audit.put("text", raw.text() == null ? "" : raw.text());
        var output = step.action() == Data360Action.MCP_EXECUTE
                ? Map.<String, Object>of(
                "output", normalizeOutput(raw),
                "raw", raw.rawResult(),
                "text", raw.text() == null ? "" : raw.text()
        )
                : normalizeOutput(raw);
        return new Data360CallResult(output, audit);
    }

    private Map<String, Object> compileArguments(OperationBindingSnapshot binding, PlanStep step, Map<String, Object> input, RunContext context) {
        if ("search".equals(facadeTool(binding, step))) {
            return Map.of("query", input.get("query"));
        }
        if (step.action() == Data360Action.MCP_EXECUTE) {
            var toolName = binding.underlyingTool() == null || binding.underlyingTool().isBlank()
                    ? String.valueOf(input.get("toolName"))
                    : binding.underlyingTool();
            var params = input.get("params") instanceof Map<?, ?> map ? withIdempotencyParam(binding, map, context) : Map.of();
            try {
                return Map.of(
                        "toolName", toolName,
                        "paramsJson", objectMapper.writeValueAsString(params)
                );
            } catch (Exception e) {
                throw new IllegalStateException("Unable to compile generic MCP execute arguments.", e);
            }
        }
        if (step.action() == Data360Action.RUN_ACTIVATION) {
            throw new IllegalArgumentException("The current Data 360 MCP catalog does not expose a generic run activation tool. Use createActivation for a draft activation or add a target-specific operation mapping.");
        }
        if (binding.underlyingTool() == null || binding.underlyingTool().isBlank()) {
            throw new IllegalArgumentException("MCP execute binding requires an underlying tool name for " + binding.resource());
        }
        try {
            var payload = withIdempotencyParam(binding, input, context);
            return Map.of(
                    "toolName", binding.underlyingTool(),
                    "paramsJson", objectMapper.writeValueAsString(payload)
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compile MCP execute arguments.", e);
        }
    }

    private Map<String, Object> withIdempotencyParam(OperationBindingSnapshot binding, Map<?, ?> input, RunContext context) {
        var idempotencyKey = context == null ? "" : context.idempotencyKey();
        if (idempotencyKey.isBlank()) {
            return copyStringKeyed(input);
        }
        var field = idempotencyField(binding);
        var payload = new LinkedHashMap<>(copyStringKeyed(input));
        if (field != null && !payload.containsKey(field)) {
            payload.put(field, idempotencyKey);
        }
        return Map.copyOf(payload);
    }

    @SuppressWarnings("unchecked")
    private String idempotencyField(OperationBindingSnapshot binding) {
        var schema = binding.parameterSchema();
        var toolSchema = schema.get("toolInputSchema") instanceof Map<?, ?> map ? map : Map.of();
        var properties = toolSchema.get("properties") instanceof Map<?, ?> propertiesMap ? propertiesMap : Map.of();
        for (var candidate : List.of("idempotencyKey", "clientRequestId", "requestId", "externalId")) {
            if (properties.containsKey(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private Map<String, Object> copyStringKeyed(Map<?, ?> input) {
        var copied = new LinkedHashMap<String, Object>();
        input.forEach((key, value) -> copied.put(String.valueOf(key), value));
        return Map.copyOf(copied);
    }

    private String facadeTool(OperationBindingSnapshot binding, PlanStep step) {
        if (binding.facadeTool() != null && !binding.facadeTool().isBlank()) {
            return binding.facadeTool();
        }
        return step.action() == Data360Action.SEARCH ? "search" : "execute";
    }

    private void validateMcpBinding(String organizationId, OperationBindingSnapshot binding, PlanStep step) {
        if (registry == null || (!hasText(binding.registryHash()) && !hasText(binding.toolSchemaHash()) && !hasText(binding.connectorDefinitionHash()))) {
            return;
        }
        var snapshot = registry.current(organizationId);
        if (hasText(binding.registryHash()) && !binding.registryHash().equals(snapshot.registryHash())) {
            throw new IllegalStateException("MCP registry drift detected for " + binding.resource() + ". Revalidate and approve the PlanSpec before execution.");
        }
        var serverId = binding.mcpServerId() == null || binding.mcpServerId().isBlank() ? "data360" : binding.mcpServerId();
        var server = snapshot.serverStatus(serverId)
                .orElseThrow(() -> new IllegalStateException("MCP server is not enabled or was not discovered: " + serverId));
        if (!"passed".equals(server.status())) {
            throw new IllegalStateException("MCP server is not available for execution: " + serverId + " (" + server.status() + ": " + server.message() + ")");
        }
        if (!server.executionServer()) {
            throw new IllegalStateException("MCP server is discovery-only and cannot execute PlanSpec tasks: " + serverId);
        }
        if (hasText(binding.connectorDefinitionHash()) && !binding.connectorDefinitionHash().equals(server.connectorDefinitionHash())) {
            throw new IllegalStateException("MCP connector definition drift detected for " + serverId + ". Revalidate and approve the PlanSpec before execution.");
        }
        if (step.action() == Data360Action.MCP_EXECUTE && !"execute".equals(binding.facadeTool())) {
            throw new IllegalStateException("Generic MCP execution must use the execute facade.");
        }
        var toolName = binding.underlyingTool();
        var tool = snapshot.findTool(serverId, toolName)
                .orElseThrow(() -> new IllegalStateException("MCP tool is not available on server " + serverId + ": " + toolName));
        if (hasText(binding.toolSchemaHash()) && !binding.toolSchemaHash().equals(tool.schemaHash())) {
            throw new IllegalStateException("MCP tool schema drift detected for " + toolName + ". Revalidate and approve the PlanSpec before execution.");
        }
        var discovered = effectFromRegistry(tool.effect());
        if (effectRisk(discovered) > effectRisk(binding.effect())) {
            throw new IllegalStateException(
                    "MCP binding effect understates registry effect for " + toolName + ": binding "
                            + binding.effect().name().toLowerCase(Locale.ROOT) + ", registry "
                            + discovered.name().toLowerCase(Locale.ROOT)
            );
        }
    }

    private Effect effectFromRegistry(String value) {
        var normalized = value == null ? "read" : value.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "destructive" -> Effect.DESTRUCTIVE;
            case "write" -> Effect.WRITE;
            case "publish" -> Effect.PUBLISH;
            case "activate" -> Effect.ACTIVATE;
            default -> Effect.READ;
        };
    }

    private int effectRisk(Effect effect) {
        return switch (effect) {
            case READ -> 0;
            case WRITE -> 1;
            case PUBLISH, ACTIVATE -> 2;
            case DESTRUCTIVE -> 3;
        };
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> normalizeOutput(McpToolCallResult raw) {
        var text = raw.text();
        if (text != null && !text.isBlank()) {
            try {
                return objectMapper.readValue(text, MAP);
            } catch (Exception ignored) {
                return Map.of("text", text);
            }
        }
        if (raw.rawResult() instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of("rawResult", raw.rawResult());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
