package com.acme.data360agent.execution;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.Data360Action;

import java.util.List;
import java.util.Map;

public final class OperationBindingDefinitions {
    private OperationBindingDefinitions() {
    }

    public static OperationDefinition from(OperationBindingSnapshot binding) {
        var resource = binding.resource();
        var action = Data360Action.fromResource(resource.contains("#") ? resource.substring(0, resource.indexOf('#')) : resource);
        return new OperationDefinition(
                action,
                action.value(),
                binding.effect(),
                binding.requiresApproval(),
                false,
                binding.underlyingTool(),
                requiredList(binding.parameterSchema(), "requiredAnyOf"),
                requiredList(binding.parameterSchema(), "requiredAllOf")
        );
    }

    private static List<String> requiredList(Map<String, Object> schema, String key) {
        var value = schema == null ? null : schema.get(key);
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}
