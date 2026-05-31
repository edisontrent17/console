package com.acme.data360agent.operation;

import com.acme.data360agent.plan.Data360Action;

import java.io.Serializable;
import java.util.List;
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
        String schemaHash,
        String bindingVersion
) implements Serializable {
    public static final String DEFAULT_BINDING_VERSION = "data360-local-catalog-2026-05-31";

    public OperationBindingSnapshot {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("Operation resource is required.");
        }
        transport = transport == null ? OperationTransport.INTERNAL : transport;
        effect = effect == null ? Effect.READ : effect;
        parameterSchema = parameterSchema == null ? Map.of() : Map.copyOf(parameterSchema);
        schemaHash = schemaHash == null || schemaHash.isBlank() ? hash(parameterSchema) : schemaHash;
        bindingVersion = bindingVersion == null || bindingVersion.isBlank() ? DEFAULT_BINDING_VERSION : bindingVersion;
    }

    public static OperationBindingSnapshot data360Mcp(OperationDefinition definition) {
        var action = definition.action();
        var facadeTool = action == Data360Action.SEARCH ? "search" : "execute";
        var underlyingTool = action == Data360Action.SEARCH ? "search" : definition.mcpOperation();
        return new OperationBindingSnapshot(
                action.resource(),
                OperationTransport.MCP,
                "data360",
                facadeTool,
                underlyingTool,
                definition.effect(),
                definition.alwaysRequiresApproval(),
                parameterSchema(definition),
                null,
                DEFAULT_BINDING_VERSION
        );
    }

    public Map<String, Object> auditSummary() {
        return Map.of(
                "resource", resource,
                "transport", transport.name(),
                "mcpServerId", mcpServerId == null ? "" : mcpServerId,
                "facadeTool", facadeTool == null ? "" : facadeTool,
                "underlyingTool", underlyingTool == null ? "" : underlyingTool,
                "effect", effect.name(),
                "requiresApproval", requiresApproval,
                "schemaHash", schemaHash,
                "bindingVersion", bindingVersion
        );
    }

    private static Map<String, Object> parameterSchema(OperationDefinition definition) {
        return Map.of(
                "type", "object",
                "requiredAllOf", List.copyOf(definition.requiredAllOf()),
                "requiredAnyOf", List.copyOf(definition.requiredAnyOf())
        );
    }

    private static String hash(Map<String, Object> value) {
        return "sha256:" + OperationCatalogHash.sha256Hex(value);
    }
}
