package com.acme.data360agent.planner;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationBindingResolver;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanTopology;
import com.acme.data360agent.plan.PlanValidationResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public record PlanArtifact(
        String type,
        String title,
        Map<String, Object> data
) implements Serializable {
    public PlanArtifact {
        data = data == null ? Map.of() : Map.copyOf(data);
    }

    public static PlanArtifact planDag(PlanSpec plan, PlanValidationResult validation) {
        return planDag(plan, validation, List.of());
    }

    public static PlanArtifact planDag(PlanSpec plan, PlanValidationResult validation, List<OperationBindingSnapshot> operationBindings) {
        var registry = new OperationRegistry();
        var bindingsByResource = new LinkedHashMap<String, OperationBindingSnapshot>();
        if (operationBindings != null) {
            operationBindings.forEach(binding -> bindingsByResource.put(binding.resource(), binding));
        }
        var nodes = new ArrayList<Map<String, Object>>();
        var edges = new ArrayList<Map<String, Object>>();
        var edgeKeys = new LinkedHashSet<String>();
        var groups = new LinkedHashMap<String, List<String>>();

        for (var step : plan.steps()) {
            var definition = registry.require(step.action());
            var node = new LinkedHashMap<String, Object>();
            node.put("id", step.id());
            node.put("label", step.title());
            node.put("phase", step.phase().value());
            node.put("action", step.action().value());
            node.put("resource", step.action().resource());
            node.put("bindingResource", bindingResource(step));
            node.put("approvalRequired", step.needsApproval());
            node.put("effect", effect(step, definition.effect().name().toLowerCase()));
            node.put("taskSnapshotHash", taskSnapshotHash(step, definition, bindingsByResource.get(bindingResource(step))));
            var selectors = outputSelectors(step);
            if (!selectors.isEmpty()) {
                node.put("outputSelectors", selectors);
            }
            nodes.add(Map.copyOf(node));
            groups.computeIfAbsent(step.phase().value(), ignored -> new ArrayList<>()).add(step.id());
            step.dependsOn().forEach(dependency -> addEdge(edges, edgeKeys, dependency, step.id(), "control", "depends_on", null));
            step.inputBindings().forEach((inputName, binding) -> addEdge(edges, edgeKeys, binding.fromStep(), step.id(), "data", "input_binding", inputName + " <- " + binding.path()));
        }

        var definition = plan.definition();
        if (definition != null && definition.states() != null) {
            definition.states().forEach((stateName, state) -> {
                if (state.next() != null && !state.next().isBlank()) {
                    addEdge(edges, edgeKeys, stateName, state.next(), "control", "asl_next", null);
                }
                state.parameters().forEach((key, value) -> {
                    if (key.endsWith(".$")) {
                        addDataEdgeFromPath(edges, edgeKeys, String.valueOf(value), stateName, key.substring(0, key.length() - 2));
                    }
                });
            });
        }

        var warnings = (validation == null ? List.<com.acme.data360agent.plan.ValidationIssue>of() : validation.issues()).stream()
                .map(issue -> {
                    var warning = new LinkedHashMap<String, Object>();
                    warning.put("severity", issue.severity());
                    warning.put("nodeId", issue.stepId() == null ? "plan" : issue.stepId());
                    warning.put("message", issue.message());
                    return Map.copyOf(warning);
                })
                .toList();

        var groupData = groups.entrySet().stream()
                .map(entry -> Map.<String, Object>of(
                        "id", entry.getKey(),
                        "label", label(entry.getKey()),
                        "nodeIds", List.copyOf(entry.getValue())
                ))
                .toList();

        return new PlanArtifact("plan_dag", "Plan DAG", Map.of(
                "nodes", List.copyOf(nodes),
                "edges", List.copyOf(edges),
                "groups", groupData,
                "topologicalOrder", PlanTopology.stepIds(plan),
                "warnings", warnings
        ));
    }

    private static void addDataEdgeFromPath(List<Map<String, Object>> edges, LinkedHashSet<String> edgeKeys, String path, String to, String label) {
        if (path == null || !path.startsWith("$.")) {
            return;
        }
        var remainder = path.substring(2);
        var split = remainder.split("\\.", 2);
        if (split.length == 0 || split[0].isBlank()) {
            return;
        }
        addEdge(edges, edgeKeys, split[0], to, "data", "asl_parameter", label);
    }

    private static String bindingResource(PlanStep step) {
        return OperationBindingResolver.resourceFor(step);
    }

    private static String effect(PlanStep step, String fallback) {
        if (step.action() != Data360Action.MCP_EXECUTE) {
            return fallback;
        }
        return String.valueOf(step.input().getOrDefault("effect", fallback)).toLowerCase();
    }

    private static String taskSnapshotHash(PlanStep step, com.acme.data360agent.operation.OperationDefinition definition, OperationBindingSnapshot approvedBinding) {
        if (approvedBinding != null) {
            return approvedBinding.schemaHash();
        }
        if (step.action() == Data360Action.MCP_EXECUTE) {
            return OperationBindingResolver.snapshotFor(null, step).schemaHash();
        }
        return OperationBindingSnapshot.data360Mcp(definition).schemaHash();
    }

    private static List<String> outputSelectors(PlanStep step) {
        if (!(step.input().get("outputSelectors") instanceof Map<?, ?> selectors)) {
            return List.of();
        }
        return selectors.keySet().stream()
                .map(String::valueOf)
                .sorted()
                .toList();
    }

    private static void addEdge(List<Map<String, Object>> edges, LinkedHashSet<String> edgeKeys, String from, String to, String type, String kind, String label) {
        if (from == null || from.isBlank() || to == null || to.isBlank() || from.equals(to)) {
            return;
        }
        var key = from + "->" + to + ":" + type + ":" + kind + ":" + (label == null ? "" : label);
        if (!edgeKeys.add(key)) {
            return;
        }
        var edge = new LinkedHashMap<String, Object>();
        edge.put("from", from);
        edge.put("to", to);
        edge.put("type", type);
        edge.put("kind", kind);
        if (label != null && !label.isBlank()) {
            edge.put("label", label);
        }
        edges.add(Map.copyOf(edge));
    }

    private static String label(String value) {
        if (value == null || value.isBlank()) {
            return "Plan";
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
