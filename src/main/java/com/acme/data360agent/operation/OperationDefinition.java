package com.acme.data360agent.operation;

import com.acme.data360agent.plan.Data360Action;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record OperationDefinition(
        Data360Action action,
        String label,
        Effect effect,
        boolean alwaysRequiresApproval,
        boolean async,
        String mcpOperation,
        List<String> requiredAnyOf,
        List<String> requiredAllOf,
        Map<String, String> inputFields,
        Map<String, String> outputFields
) implements Serializable {
    public OperationDefinition(Data360Action action,
                               String label,
                               Effect effect,
                               boolean alwaysRequiresApproval,
                               boolean async,
                               String mcpOperation,
                               List<String> requiredAnyOf,
                               List<String> requiredAllOf) {
        this(action, label, effect, alwaysRequiresApproval, async, mcpOperation, requiredAnyOf, requiredAllOf, Map.of(), Map.of());
    }

    public OperationDefinition {
        Objects.requireNonNull(action, "action is required");
        Objects.requireNonNull(label, "label is required");
        Objects.requireNonNull(effect, "effect is required");
        Objects.requireNonNull(mcpOperation, "mcpOperation is required");
        requiredAnyOf = requiredAnyOf == null ? List.of() : List.copyOf(requiredAnyOf);
        requiredAllOf = requiredAllOf == null ? List.of() : List.copyOf(requiredAllOf);
        inputFields = inputFields == null ? Map.of() : Map.copyOf(inputFields);
        outputFields = outputFields == null ? Map.of() : Map.copyOf(outputFields);
    }

    public OperationDescriptor descriptor() {
        return OperationDescriptor.from(this);
    }

    public OperationBinding binding() {
        return OperationBinding.from(this);
    }

    public boolean acceptsInput(String field) {
        return inputFields.isEmpty()
                || inputFields.containsKey(field)
                || requiredAllOf.contains(field)
                || requiredAnyOf.contains(field);
    }

    public boolean producesOutputPath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        var normalized = path.startsWith("$.") ? path.substring(2) : path;
        var topLevel = normalized.split("\\.", 2)[0];
        if (outputFields.containsKey(normalized)) {
            return true;
        }
        var topLevelType = outputFields.get(topLevel);
        return ("object".equals(topLevelType) || "array".equals(topLevelType)) && normalized.contains(".");
    }
}
