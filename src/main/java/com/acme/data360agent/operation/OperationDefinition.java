package com.acme.data360agent.operation;

import com.acme.data360agent.plan.Data360Action;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public record OperationDefinition(
        Data360Action action,
        String label,
        Effect effect,
        boolean alwaysRequiresApproval,
        boolean async,
        String mcpOperation,
        List<String> requiredAnyOf,
        List<String> requiredAllOf
) implements Serializable {
    public OperationDefinition {
        Objects.requireNonNull(action, "action is required");
        Objects.requireNonNull(label, "label is required");
        Objects.requireNonNull(effect, "effect is required");
        Objects.requireNonNull(mcpOperation, "mcpOperation is required");
        requiredAnyOf = requiredAnyOf == null ? List.of() : List.copyOf(requiredAnyOf);
        requiredAllOf = requiredAllOf == null ? List.of() : List.copyOf(requiredAllOf);
    }

    public OperationDescriptor descriptor() {
        return OperationDescriptor.from(this);
    }

    public OperationBinding binding() {
        return OperationBinding.from(this);
    }
}
