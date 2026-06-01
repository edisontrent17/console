package com.acme.data360agent.plan;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class PlanTopology {
    private PlanTopology() {
    }

    public static List<String> stepIds(PlanSpec plan) {
        if (plan == null || plan.steps().isEmpty()) {
            return List.of();
        }
        var ids = plan.steps().stream().map(PlanStep::id).toList();
        var idSet = new LinkedHashSet<>(ids);
        var outgoing = new LinkedHashMap<String, LinkedHashSet<String>>();
        var indegree = new LinkedHashMap<String, Integer>();
        ids.forEach(id -> {
            outgoing.put(id, new LinkedHashSet<>());
            indegree.put(id, 0);
        });

        for (var step : plan.steps()) {
            step.dependsOn().forEach(dependency -> addEdge(dependency, step.id(), idSet, outgoing, indegree));
            step.inputBindings().values().forEach(binding -> addEdge(binding.fromStep(), step.id(), idSet, outgoing, indegree));
        }
        addDynamicParameterEdges(plan, idSet, outgoing, indegree);

        var remaining = new LinkedHashSet<>(ids);
        var ordered = new ArrayList<String>();
        while (!remaining.isEmpty()) {
            var next = remaining.stream()
                    .filter(id -> indegree.getOrDefault(id, 0) == 0)
                    .findFirst();
            if (next.isEmpty()) {
                return List.copyOf(ids);
            }
            var id = next.get();
            ordered.add(id);
            remaining.remove(id);
            for (var target : outgoing.getOrDefault(id, new LinkedHashSet<>())) {
                if (remaining.contains(target)) {
                    indegree.put(target, indegree.get(target) - 1);
                }
            }
        }
        return List.copyOf(ordered);
    }

    public static List<PlanStep> steps(PlanSpec plan, List<String> stepIds) {
        if (plan == null || plan.steps().isEmpty()) {
            return List.of();
        }
        var byId = new LinkedHashMap<String, PlanStep>();
        plan.steps().forEach(step -> byId.put(step.id(), step));
        var ordered = new ArrayList<PlanStep>();
        for (var id : stepIds == null || stepIds.isEmpty() ? stepIds(plan) : stepIds) {
            var step = byId.remove(id);
            if (step != null) {
                ordered.add(step);
            }
        }
        ordered.addAll(byId.values());
        return List.copyOf(ordered);
    }

    private static void addDynamicParameterEdges(PlanSpec plan, LinkedHashSet<String> idSet, Map<String, LinkedHashSet<String>> outgoing, Map<String, Integer> indegree) {
        if (plan.definition() == null || plan.definition().states() == null) {
            return;
        }
        plan.definition().states().forEach((stateName, state) -> {
            if (state == null || state.parameters() == null) {
                return;
            }
            state.parameters().forEach((key, value) -> {
                if (!String.valueOf(key).endsWith(".$")) {
                    return;
                }
                var source = sourceStepFromPath(String.valueOf(value));
                addEdge(source, stateName, idSet, outgoing, indegree);
            });
        });
    }

    private static String sourceStepFromPath(String path) {
        if (path == null || !path.startsWith("$.")) {
            return "";
        }
        var remainder = path.substring(2);
        var split = remainder.split("\\.", 2);
        return split.length == 0 ? "" : split[0];
    }

    private static void addEdge(String from, String to, LinkedHashSet<String> idSet, Map<String, LinkedHashSet<String>> outgoing, Map<String, Integer> indegree) {
        if (from == null || from.isBlank() || to == null || to.isBlank() || from.equals(to) || !idSet.contains(from) || !idSet.contains(to)) {
            return;
        }
        if (outgoing.computeIfAbsent(from, ignored -> new LinkedHashSet<>()).add(to)) {
            indegree.put(to, indegree.getOrDefault(to, 0) + 1);
        }
    }
}
