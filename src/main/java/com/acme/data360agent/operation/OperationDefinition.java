package com.acme.data360agent.operation;

import com.acme.data360agent.plan.Data360Action;

import java.io.Serializable;
import java.util.List;

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
}
