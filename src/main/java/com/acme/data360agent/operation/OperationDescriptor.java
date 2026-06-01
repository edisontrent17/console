package com.acme.data360agent.operation;

import java.io.Serializable;
import java.util.Objects;

public record OperationDescriptor(
        String id,
        String capabilityResource,
        String label,
        Effect effect,
        boolean requiresApproval,
        boolean async
) implements Serializable {
    public OperationDescriptor {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(capabilityResource, "capabilityResource is required");
        Objects.requireNonNull(label, "label is required");
        Objects.requireNonNull(effect, "effect is required");
    }

    public static OperationDescriptor from(OperationDefinition definition) {
        return new OperationDescriptor(
                definition.action().value(),
                definition.action().resource(),
                definition.label(),
                definition.effect(),
                definition.alwaysRequiresApproval(),
                definition.async()
        );
    }

    public String schemaHash() {
        return OperationCatalogHash.sha256Hex(this);
    }
}
