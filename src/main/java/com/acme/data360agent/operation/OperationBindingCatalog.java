package com.acme.data360agent.operation;

import com.acme.data360agent.plan.PlanSpec;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public class OperationBindingCatalog {
    private final OperationRegistry registry;

    public OperationBindingCatalog(OperationRegistry registry) {
        this.registry = registry;
    }

    public List<OperationBindingSnapshot> snapshotsFor(PlanSpec plan) {
        var byResource = new LinkedHashMap<String, OperationBindingSnapshot>();
        for (var step : plan.steps()) {
            var definition = registry.require(step.action());
            byResource.putIfAbsent(step.action().resource(), OperationBindingSnapshot.data360Mcp(definition));
        }
        return List.copyOf(byResource.values());
    }
}
