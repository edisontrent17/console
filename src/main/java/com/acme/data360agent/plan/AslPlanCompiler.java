package com.acme.data360agent.plan;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class AslPlanCompiler {
    private AslPlanCompiler() {
    }

    public static AslStateMachine fromSteps(List<PlanStep> steps) {
        var safeSteps = steps == null ? List.<PlanStep>of() : steps;
        var states = new LinkedHashMap<String, AslState>();
        for (var index = 0; index < safeSteps.size(); index++) {
            var step = safeSteps.get(index);
            var next = index + 1 < safeSteps.size() ? safeSteps.get(index + 1).id() : null;
            states.put(step.id(), AslState.task(
                    step.title(),
                    step.action().resource(),
                    parametersFromStep(step),
                    "$." + step.id(),
                    next,
                    next == null
            ));
        }
        var startAt = safeSteps.isEmpty() ? null : safeSteps.getFirst().id();
        return new AslStateMachine(AslStateMachine.VERSION, AslStateMachine.QUERY_LANGUAGE, startAt, states);
    }

    public static List<PlanStep> toSteps(AslStateMachine definition) {
        if (definition == null || definition.startAt() == null || definition.startAt().isBlank()) {
            return List.of();
        }
        var steps = new ArrayList<PlanStep>();
        var visited = new LinkedHashSet<String>();
        var current = definition.startAt();
        String previous = null;
        while (current != null && !current.isBlank()) {
            if (!visited.add(current)) {
                throw new IllegalArgumentException("ASL definition contains a cycle at state: " + current);
            }
            var state = definition.states().get(current);
            if (state == null) {
                throw new IllegalArgumentException("ASL transition references unknown state: " + current);
            }
            if (!"Task".equals(state.type())) {
                throw new IllegalArgumentException("Data 360 ASL profile currently supports only Task states: " + current);
            }
            var action = Data360Action.fromResource(state.resource());
            var parameters = splitParameters(state.parameters());
            var dependencies = new LinkedHashSet<String>();
            if (previous != null) {
                dependencies.add(previous);
            }
            parameters.bindings().values().forEach(binding -> dependencies.add(binding.fromStep()));
            steps.add(new PlanStep(
                    current,
                    state.comment() == null || state.comment().isBlank() ? current : state.comment(),
                    phaseFor(action, parameters.input()),
                    action,
                    parameters.input(),
                    new ArrayList<>(dependencies),
                    parameters.bindings(),
                    needsApprovalFor(action, parameters.input())
            ));
            previous = current;
            current = Boolean.TRUE.equals(state.end()) ? null : state.next();
        }
        return List.copyOf(steps);
    }

    private static Map<String, Object> parametersFromStep(PlanStep step) {
        var parameters = new LinkedHashMap<String, Object>(step.input());
        for (var binding : step.inputBindings().entrySet()) {
            parameters.put(binding.getKey() + ".$", "$." + binding.getValue().fromStep() + bindingPath(binding.getValue().path()));
        }
        return Map.copyOf(parameters);
    }

    private static String bindingPath(String path) {
        if (path == null || path.isBlank() || "$".equals(path)) {
            return "";
        }
        if (!path.startsWith("$.")) {
            return "." + path;
        }
        return path.substring(1);
    }

    private static SplitParameters splitParameters(Map<String, Object> parameters) {
        var input = new LinkedHashMap<String, Object>();
        var bindings = new LinkedHashMap<String, InputBinding>();
        for (var entry : parameters.entrySet()) {
            var key = entry.getKey();
            if (key.endsWith(".$")) {
                var bindingKey = key.substring(0, key.length() - 2);
                bindings.put(bindingKey, bindingFromPath(String.valueOf(entry.getValue())));
            } else {
                input.put(key, entry.getValue());
            }
        }
        return new SplitParameters(Map.copyOf(input), Map.copyOf(bindings));
    }

    private static InputBinding bindingFromPath(String path) {
        if (path == null || !path.startsWith("$.")) {
            throw new IllegalArgumentException("ASL Parameters dynamic values must use simple $.state.field paths.");
        }
        var parts = path.substring(2).split("\\.", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("ASL Parameters dynamic values must include a source state and field path: " + path);
        }
        return new InputBinding(parts[0], "$." + parts[1]);
    }

    private static PlanPhase phaseFor(Data360Action action, Map<String, Object> input) {
        if (action != Data360Action.MCP_EXECUTE) {
            return action.defaultPhase();
        }
        var effect = String.valueOf(input.getOrDefault("effect", "read")).toLowerCase(Locale.ROOT);
        if ("read".equals(effect)) {
            return PlanPhase.DISCOVER;
        }
        return PlanPhase.SETUP;
    }

    private static boolean needsApprovalFor(Data360Action action, Map<String, Object> input) {
        if (action != Data360Action.MCP_EXECUTE) {
            return action.requiresApproval();
        }
        if (Boolean.TRUE.equals(input.get("approvalRequired"))) {
            return true;
        }
        var effect = String.valueOf(input.getOrDefault("effect", "read")).toLowerCase(Locale.ROOT);
        return !"read".equals(effect);
    }

    private record SplitParameters(Map<String, Object> input, Map<String, InputBinding> bindings) {
    }
}
