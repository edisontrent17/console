package com.acme.data360agent.operation;

import com.acme.data360agent.mcp.McpToolRegistrySnapshot;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanStep;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public record OperationBindingSnapshot(
        String resource,
        OperationTransport transport,
        String mcpServerId,
        String facadeTool,
        String underlyingTool,
        Effect effect,
        boolean requiresApproval,
        Map<String, Object> parameterSchema,
        Map<String, Object> outputSchema,
        String schemaHash,
        String bindingVersion,
        String registryHash,
        String toolSchemaHash,
        String connectorDefinitionHash
) implements Serializable {
    public static final String DEFAULT_BINDING_VERSION = "data360-local-catalog-2026-05-31";

    public OperationBindingSnapshot(String resource,
                                    OperationTransport transport,
                                    String mcpServerId,
                                    String facadeTool,
                                    String underlyingTool,
                                    Effect effect,
                                    boolean requiresApproval,
                                    Map<String, Object> parameterSchema,
                                    Map<String, Object> outputSchema,
                                    String schemaHash,
                                    String bindingVersion) {
        this(resource, transport, mcpServerId, facadeTool, underlyingTool, effect, requiresApproval, parameterSchema, outputSchema, schemaHash, bindingVersion, null, null, null);
    }

    public OperationBindingSnapshot(String resource,
                                    OperationTransport transport,
                                    String mcpServerId,
                                    String facadeTool,
                                    String underlyingTool,
                                    Effect effect,
                                    boolean requiresApproval,
                                    Map<String, Object> parameterSchema,
                                    Map<String, Object> outputSchema,
                                    String schemaHash,
                                    String bindingVersion,
                                    String registryHash,
                                    String toolSchemaHash) {
        this(resource, transport, mcpServerId, facadeTool, underlyingTool, effect, requiresApproval, parameterSchema, outputSchema, schemaHash, bindingVersion, registryHash, toolSchemaHash, null);
    }

    public OperationBindingSnapshot {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("Operation resource is required.");
        }
        transport = transport == null ? OperationTransport.INTERNAL : transport;
        effect = effect == null ? Effect.READ : effect;
        parameterSchema = parameterSchema == null ? Map.of() : Map.copyOf(parameterSchema);
        outputSchema = outputSchema == null ? Map.of() : Map.copyOf(outputSchema);
        schemaHash = schemaHash == null || schemaHash.isBlank()
                ? hash(Map.of("parameters", parameterSchema, "outputs", outputSchema))
                : schemaHash;
        bindingVersion = bindingVersion == null || bindingVersion.isBlank() ? DEFAULT_BINDING_VERSION : bindingVersion;
        registryHash = registryHash == null ? "" : registryHash;
        toolSchemaHash = toolSchemaHash == null ? "" : toolSchemaHash;
        connectorDefinitionHash = connectorDefinitionHash == null ? "" : connectorDefinitionHash;
    }

    public static OperationBindingSnapshot data360Mcp(OperationDefinition definition) {
        return data360Mcp(definition, null);
    }

    public static OperationBindingSnapshot data360Mcp(OperationDefinition definition, McpToolRegistrySnapshot registrySnapshot) {
        var action = definition.action();
        if (action == Data360Action.MCP_EXECUTE) {
            return new OperationBindingSnapshot(
                    action.resource(),
                    OperationTransport.MCP,
                    "data360",
                    "execute",
                    "execute",
                    Effect.READ,
                    false,
                    parameterSchema(definition),
                    outputSchema(definition),
                    null,
                    DEFAULT_BINDING_VERSION
            );
        }
        var facadeTool = action == Data360Action.SEARCH ? "search" : "execute";
        var underlyingTool = action == Data360Action.SEARCH ? "search" : definition.mcpOperation();
        var tool = registrySnapshot == null ? null : registrySnapshot.findTool("data360", underlyingTool).orElse(null);
        var toolInputSchema = tool == null ? Map.<String, Object>of() : tool.inputSchema();
        var toolOutputSchema = tool == null ? Map.<String, Object>of() : tool.outputSchema();
        var toolExamples = tool == null ? List.<Map<String, Object>>of() : tool.examples();
        var selectorContracts = tool == null ? Map.<String, String>of() : tool.selectorContracts();
        var toolSchemaHash = tool == null ? "" : tool.schemaHash();
        var registryHash = registrySnapshot == null ? "" : registrySnapshot.registryHash();
        var connectorDefinitionHash = connectorDefinitionHash(registrySnapshot, "data360");
        return new OperationBindingSnapshot(
                action.resource(),
                OperationTransport.MCP,
                "data360",
                facadeTool,
                underlyingTool,
                definition.effect(),
                definition.alwaysRequiresApproval(),
                withToolMetadata(parameterSchema(definition), toolInputSchema, toolExamples, selectorContracts, Map.of(
                        "resource", action.resource(),
                        "serverId", "data360",
                        "facadeTool", facadeTool,
                        "toolName", underlyingTool,
                        "effect", definition.effect().name(),
                        "registryHash", registryHash,
                        "toolSchemaHash", toolSchemaHash,
                        "connectorDefinitionHash", connectorDefinitionHash
                )),
                withOutputMetadata(outputSchema(definition), toolOutputSchema, selectorContracts),
                null,
                DEFAULT_BINDING_VERSION,
                registryHash,
                toolSchemaHash,
                connectorDefinitionHash
        );
    }

    public static OperationBindingSnapshot mcpExecute(PlanStep step) {
        return mcpExecute(step, null);
    }

    public static OperationBindingSnapshot mcpExecute(PlanStep step, McpToolRegistrySnapshot registrySnapshot) {
        var input = step.input();
        var effect = effect(input.get("effect"));
        var serverId = string(input.getOrDefault("serverId", "data360"));
        var toolName = string(input.get("toolName"));
        var facadeTool = "execute";
        var requiresApproval = effect != Effect.READ || Boolean.TRUE.equals(input.get("approvalRequired"));
        var selectorNames = outputSelectorNames(input);
        var resolvedServerId = serverId.isBlank() ? "data360" : serverId;
        var resolvedToolName = toolName.isBlank() ? "execute" : toolName;
        var tool = registrySnapshot == null ? null : registrySnapshot.findTool(resolvedServerId, resolvedToolName).orElse(null);
        var toolInputSchema = tool == null ? Map.<String, Object>of() : tool.inputSchema();
        var toolOutputSchema = tool == null ? Map.<String, Object>of() : tool.outputSchema();
        var toolExamples = tool == null ? List.<Map<String, Object>>of() : tool.examples();
        var selectorContracts = tool == null ? Map.<String, String>of() : tool.selectorContracts();
        var toolSchemaHash = tool == null ? "" : tool.schemaHash();
        var registryHash = registrySnapshot == null ? "" : registrySnapshot.registryHash();
        var connectorDefinitionHash = connectorDefinitionHash(registrySnapshot, resolvedServerId);
        return new OperationBindingSnapshot(
                Data360Action.MCP_EXECUTE.resource() + "#" + step.id(),
                OperationTransport.MCP,
                resolvedServerId,
                facadeTool.isBlank() ? "execute" : facadeTool,
                resolvedToolName,
                effect,
                requiresApproval,
                Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "serverId", "string",
                                "toolName", "string",
                                "params", "object",
                                "effect", "string",
                                "outputSelectors", "object"
                        ),
                        "requiredAllOf", List.of("serverId", "toolName", "params"),
                        "toolInputSchema", toolInputSchema,
                        "toolExamples", toolExamples,
                        "selectorContracts", selectorContracts,
                        "frozenTask", Map.of(
                                "stepId", step.id(),
                                "serverId", resolvedServerId,
                                "facadeTool", facadeTool.isBlank() ? "execute" : facadeTool,
                                "toolName", resolvedToolName,
                                "effect", effect.name(),
                                "outputSelectors", selectorNames,
                                "registryHash", registryHash,
                                "toolSchemaHash", toolSchemaHash,
                                "connectorDefinitionHash", connectorDefinitionHash
                        )
                ),
                Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "output", "object",
                                "raw", "object",
                                "selected", "object",
                                "text", "string"
                        ),
                        "toolOutputSchema", toolOutputSchema,
                        "outputSelectors", selectorNames,
                        "selectorContracts", selectorContracts
                ),
                null,
                DEFAULT_BINDING_VERSION,
                registryHash,
                toolSchemaHash,
                connectorDefinitionHash
        );
    }

    public Map<String, Object> auditSummary() {
        var summary = new java.util.LinkedHashMap<String, Object>();
        summary.put("resource", resource);
        summary.put("transport", transport.name());
        summary.put("mcpServerId", mcpServerId == null ? "" : mcpServerId);
        summary.put("facadeTool", facadeTool == null ? "" : facadeTool);
        summary.put("underlyingTool", underlyingTool == null ? "" : underlyingTool);
        summary.put("effect", effect.name());
        summary.put("requiresApproval", requiresApproval);
        summary.put("schemaHash", schemaHash);
        summary.put("registryHash", registryHash);
        summary.put("toolSchemaHash", toolSchemaHash);
        summary.put("connectorDefinitionHash", connectorDefinitionHash);
        summary.put("bindingVersion", bindingVersion);
        summary.put("outputFields", outputSchema.getOrDefault("properties", Map.of()));
        return Map.copyOf(summary);
    }

    private static Map<String, Object> parameterSchema(OperationDefinition definition) {
        return Map.of(
                "type", "object",
                "properties", Map.copyOf(definition.inputFields()),
                "requiredAllOf", List.copyOf(definition.requiredAllOf()),
                "requiredAnyOf", List.copyOf(definition.requiredAnyOf())
        );
    }

    private static Map<String, Object> outputSchema(OperationDefinition definition) {
        return Map.of(
                "type", "object",
                "properties", Map.copyOf(definition.outputFields())
        );
    }

    private static Map<String, Object> withToolMetadata(Map<String, Object> schema,
                                                        Map<String, Object> toolInputSchema,
                                                        List<Map<String, Object>> toolExamples,
                                                        Map<String, String> selectorContracts,
                                                        Map<String, Object> frozenTask) {
        if (toolInputSchema.isEmpty() && toolExamples.isEmpty() && selectorContracts.isEmpty()) {
            return schema;
        }
        var updated = new java.util.LinkedHashMap<>(schema);
        updated.put("toolInputSchema", toolInputSchema);
        updated.put("toolExamples", toolExamples);
        updated.put("selectorContracts", selectorContracts);
        updated.put("frozenTask", frozenTask);
        return Map.copyOf(updated);
    }

    private static Map<String, Object> withOutputMetadata(Map<String, Object> schema,
                                                         Map<String, Object> toolOutputSchema,
                                                         Map<String, String> selectorContracts) {
        if (toolOutputSchema.isEmpty() && selectorContracts.isEmpty()) {
            return schema;
        }
        var updated = new java.util.LinkedHashMap<>(schema);
        updated.put("toolOutputSchema", toolOutputSchema);
        updated.put("selectorContracts", selectorContracts);
        return Map.copyOf(updated);
    }

    private static String hash(Map<String, Object> value) {
        return "sha256:" + OperationCatalogHash.sha256Hex(value);
    }

    private static String connectorDefinitionHash(McpToolRegistrySnapshot registrySnapshot, String serverId) {
        return registrySnapshot == null
                ? ""
                : registrySnapshot.serverStatus(serverId)
                .map(McpToolRegistrySnapshot.McpServerRegistryStatus::connectorDefinitionHash)
                .orElse("");
    }

    private static Effect effect(Object raw) {
        var value = string(raw).toUpperCase(Locale.ROOT);
        return switch (value) {
            case "", "READ" -> Effect.READ;
            case "WRITE" -> Effect.WRITE;
            case "PUBLISH" -> Effect.PUBLISH;
            case "ACTIVATE" -> Effect.ACTIVATE;
            case "DESTRUCTIVE" -> Effect.DESTRUCTIVE;
            default -> throw new IllegalArgumentException("Unsupported MCP execute effect: " + value.toLowerCase(Locale.ROOT));
        };
    }

    private static String string(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static List<String> outputSelectorNames(Map<String, Object> input) {
        if (!(input.get("outputSelectors") instanceof Map<?, ?> selectors)) {
            return List.of();
        }
        return selectors.keySet().stream()
                .map(String::valueOf)
                .sorted()
                .toList();
    }
}
