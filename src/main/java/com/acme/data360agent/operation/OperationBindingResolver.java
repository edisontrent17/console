package com.acme.data360agent.operation;

import com.acme.data360agent.mcp.McpToolRegistrySnapshot;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;

import java.util.LinkedHashMap;
import java.util.List;

public final class OperationBindingResolver {
    private OperationBindingResolver() {
    }

    public static String resourceFor(PlanStep step) {
        if (step.action() == Data360Action.MCP_EXECUTE) {
            return step.action().resource() + "#" + step.id();
        }
        return step.action().resource();
    }

    public static OperationBindingSnapshot snapshotFor(OperationRegistry registry, PlanStep step) {
        return snapshotFor(registry, step, null);
    }

    public static OperationBindingSnapshot snapshotFor(OperationRegistry registry, PlanStep step, McpToolRegistrySnapshot mcpRegistrySnapshot) {
        var safeRegistry = registry == null ? new OperationRegistry() : registry;
        if (step.action() == Data360Action.MCP_EXECUTE) {
            return OperationBindingSnapshot.mcpExecute(step, mcpRegistrySnapshot);
        }
        return OperationBindingSnapshot.data360Mcp(safeRegistry.require(step.action()), mcpRegistrySnapshot);
    }

    public static List<OperationBindingSnapshot> snapshotsFor(OperationRegistry registry, PlanSpec plan) {
        return snapshotsFor(registry, plan, null);
    }

    public static List<OperationBindingSnapshot> snapshotsFor(OperationRegistry registry, PlanSpec plan, McpToolRegistrySnapshot mcpRegistrySnapshot) {
        if (plan == null) {
            return List.of();
        }
        var byResource = new LinkedHashMap<String, OperationBindingSnapshot>();
        for (var step : plan.steps()) {
            var binding = snapshotFor(registry, step, mcpRegistrySnapshot);
            byResource.putIfAbsent(binding.resource(), binding);
        }
        return List.copyOf(byResource.values());
    }
}
