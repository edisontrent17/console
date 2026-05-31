package com.acme.data360agent.operation;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public record OperationBinding(
        OperationDescriptor descriptor,
        OperationTransport transport,
        String operationName,
        List<String> requiredAnyOf,
        List<String> requiredAllOf
) implements Serializable {
    public OperationBinding {
        Objects.requireNonNull(descriptor, "descriptor is required");
        Objects.requireNonNull(transport, "transport is required");
        Objects.requireNonNull(operationName, "operationName is required");
        requiredAnyOf = requiredAnyOf == null ? List.of() : List.copyOf(requiredAnyOf);
        requiredAllOf = requiredAllOf == null ? List.of() : List.copyOf(requiredAllOf);
    }

    public static OperationBinding from(OperationDefinition definition) {
        return new OperationBinding(
                definition.descriptor(),
                OperationTransport.MCP,
                definition.mcpOperation(),
                definition.requiredAnyOf(),
                definition.requiredAllOf()
        );
    }

    public String bindingHash() {
        return OperationCatalogHash.sha256Hex(this);
    }
}
