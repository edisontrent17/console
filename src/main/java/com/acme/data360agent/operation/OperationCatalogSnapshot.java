package com.acme.data360agent.operation;

import com.acme.data360agent.plan.Data360Action;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public record OperationCatalogSnapshot(
        String schemaVersion,
        Map<String, OperationBinding> bindings,
        String catalogHash
) implements Serializable {
    public static final String CURRENT_SCHEMA_VERSION = "operation-catalog.v1";

    public OperationCatalogSnapshot {
        schemaVersion = schemaVersion == null || schemaVersion.isBlank() ? CURRENT_SCHEMA_VERSION : schemaVersion;
        bindings = Map.copyOf(new TreeMap<>(Objects.requireNonNull(bindings, "bindings are required")));
        var computedHash = OperationCatalogHash.sha256Hex(Map.of(
                "schemaVersion", schemaVersion,
                "bindings", bindings
        ));
        if (catalogHash == null || catalogHash.isBlank()) {
            catalogHash = computedHash;
        } else if (!catalogHash.equals(computedHash)) {
            throw new IllegalArgumentException("catalogHash does not match operation bindings");
        }
    }

    public static OperationCatalogSnapshot from(Map<Data360Action, OperationDefinition> definitions) {
        var bindings = new TreeMap<String, OperationBinding>();
        definitions.forEach((action, definition) -> bindings.put(action.value(), definition.binding()));
        return new OperationCatalogSnapshot(CURRENT_SCHEMA_VERSION, bindings, null);
    }
}
