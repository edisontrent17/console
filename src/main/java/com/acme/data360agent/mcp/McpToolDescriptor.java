package com.acme.data360agent.mcp;

import com.acme.data360agent.operation.OperationCatalogHash;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public record McpToolDescriptor(
        String serverId,
        String name,
        String description,
        String effect,
        Map<String, Object> inputSchema,
        Map<String, Object> outputSchema,
        List<Map<String, Object>> examples,
        Map<String, String> selectorContracts
) implements Serializable {
    public McpToolDescriptor(String serverId, String name, String description, String effect, Map<String, Object> inputSchema) {
        this(serverId, name, description, effect, inputSchema, Map.of(), List.of(), Map.of());
    }

    public McpToolDescriptor {
        inputSchema = inputSchema == null ? Map.of() : Map.copyOf(inputSchema);
        outputSchema = outputSchema == null ? Map.of() : Map.copyOf(outputSchema);
        examples = examples == null ? List.of() : List.copyOf(examples);
        selectorContracts = selectorContracts == null ? Map.of() : Map.copyOf(selectorContracts);
    }

    public String schemaHash() {
        return "sha256:" + OperationCatalogHash.sha256Hex(Map.of(
                "serverId", serverId == null ? "" : serverId,
                "name", name == null ? "" : name,
                "effect", effect == null ? "" : effect,
                "inputSchema", inputSchema,
                "outputSchema", outputSchema,
                "selectorContracts", selectorContracts
        ));
    }
}
