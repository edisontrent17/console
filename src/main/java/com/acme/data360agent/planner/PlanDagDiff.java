package com.acme.data360agent.planner;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public record PlanDagDiff(
        List<String> addedNodes,
        List<String> removedNodes,
        List<String> changedNodes,
        List<String> addedEdges,
        List<String> removedEdges
) implements Serializable {
    public PlanDagDiff {
        addedNodes = addedNodes == null ? List.of() : List.copyOf(addedNodes);
        removedNodes = removedNodes == null ? List.of() : List.copyOf(removedNodes);
        changedNodes = changedNodes == null ? List.of() : List.copyOf(changedNodes);
        addedEdges = addedEdges == null ? List.of() : List.copyOf(addedEdges);
        removedEdges = removedEdges == null ? List.of() : List.copyOf(removedEdges);
    }

    public boolean empty() {
        return addedNodes.isEmpty() && removedNodes.isEmpty() && changedNodes.isEmpty() && addedEdges.isEmpty() && removedEdges.isEmpty();
    }

    public static PlanDagDiff between(List<PlanArtifact> beforeArtifacts, List<PlanArtifact> afterArtifacts) {
        var before = planDag(beforeArtifacts);
        var after = planDag(afterArtifacts);
        var beforeNodes = nodesById(before);
        var afterNodes = nodesById(after);
        var beforeEdges = edgeKeys(before);
        var afterEdges = edgeKeys(after);

        return new PlanDagDiff(
                difference(afterNodes.keySet(), beforeNodes.keySet()),
                difference(beforeNodes.keySet(), afterNodes.keySet()),
                changedNodes(beforeNodes, afterNodes),
                difference(afterEdges, beforeEdges),
                difference(beforeEdges, afterEdges)
        );
    }

    private static PlanArtifact planDag(List<PlanArtifact> artifacts) {
        return artifacts == null ? null : artifacts.stream()
                .filter(artifact -> "plan_dag".equals(artifact.type()))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, Object>> nodesById(PlanArtifact artifact) {
        if (artifact == null || !(artifact.data().get("nodes") instanceof List<?> nodes)) {
            return Map.of();
        }
        var byId = new LinkedHashMap<String, Map<String, Object>>();
        for (var node : nodes) {
            if (node instanceof Map<?, ?> map && map.get("id") != null) {
                byId.put(String.valueOf(map.get("id")), (Map<String, Object>) map);
            }
        }
        return Map.copyOf(byId);
    }

    private static LinkedHashSet<String> edgeKeys(PlanArtifact artifact) {
        var keys = new LinkedHashSet<String>();
        if (artifact == null || !(artifact.data().get("edges") instanceof List<?> edges)) {
            return keys;
        }
        for (var edge : edges) {
            if (edge instanceof Map<?, ?> map) {
                keys.add(String.join(
                        "->",
                        String.valueOf(map.get("from")),
                        String.valueOf(map.get("to")),
                        String.valueOf(map.get("type")),
                        String.valueOf(map.get("kind")),
                        String.valueOf(map.get("label"))
                ));
            }
        }
        return keys;
    }

    private static List<String> changedNodes(Map<String, Map<String, Object>> beforeNodes, Map<String, Map<String, Object>> afterNodes) {
        return beforeNodes.keySet().stream()
                .filter(afterNodes::containsKey)
                .filter(nodeId -> !beforeNodes.get(nodeId).equals(afterNodes.get(nodeId)))
                .toList();
    }

    private static List<String> difference(java.util.Set<String> left, java.util.Set<String> right) {
        return left.stream()
                .filter(value -> !right.contains(value))
                .toList();
    }
}
