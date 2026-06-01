package com.acme.data360agent.operation;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public record OperationBinding(
        OperationDescriptor descriptor,
        OperationTransport transport,
        String operationName,
        List<String> requiredAnyOf,
        List<String> requiredAllOf,
        java.util.Map<String, String> inputFields,
        java.util.Map<String, String> outputFields
) implements Serializable {
    public OperationBinding(OperationDescriptor descriptor,
                            OperationTransport transport,
                            String operationName,
                            List<String> requiredAnyOf,
                            List<String> requiredAllOf) {
        this(descriptor, transport, operationName, requiredAnyOf, requiredAllOf, java.util.Map.of(), java.util.Map.of());
    }

    public OperationBinding {
        Objects.requireNonNull(descriptor, "descriptor is required");
        Objects.requireNonNull(transport, "transport is required");
        Objects.requireNonNull(operationName, "operationName is required");
        requiredAnyOf = requiredAnyOf == null ? List.of() : List.copyOf(requiredAnyOf);
        requiredAllOf = requiredAllOf == null ? List.of() : List.copyOf(requiredAllOf);
        inputFields = inputFields == null ? java.util.Map.of() : java.util.Map.copyOf(inputFields);
        outputFields = outputFields == null ? java.util.Map.of() : java.util.Map.copyOf(outputFields);
    }

    public static OperationBinding from(OperationDefinition definition) {
        return new OperationBinding(
                definition.descriptor(),
                OperationTransport.MCP,
                definition.mcpOperation(),
                definition.requiredAnyOf(),
                definition.requiredAllOf(),
                definition.inputFields(),
                definition.outputFields()
        );
    }

    public String bindingHash() {
        return OperationCatalogHash.sha256Hex(this);
    }
}
