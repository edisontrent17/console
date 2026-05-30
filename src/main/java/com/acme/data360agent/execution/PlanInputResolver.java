package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanStep;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class PlanInputResolver {
    private PlanInputResolver() {
    }

    public static Map<String, Object> resolve(PlanStep step, Function<String, Map<String, Object>> outputForStep) {
        var resolved = new LinkedHashMap<>(step.input());
        for (var binding : step.inputBindings().entrySet()) {
            var sourceOutput = outputForStep.apply(binding.getValue().fromStep());
            resolved.put(binding.getKey(), readJsonPath(sourceOutput, binding.getValue().path()));
        }
        resolveFromStep(resolved, outputForStep, "segmentIdFromStep", "segmentId");
        resolveFromStep(resolved, outputForStep, "activationIdFromStep", "activationId");
        resolveFromStep(resolved, outputForStep, "insightIdFromStep", "insightId");
        if (resolved.containsKey("criteriaFromStep")) {
            var source = String.valueOf(resolved.get("criteriaFromStep"));
            var output = outputForStep.apply(source);
            resolved.put("criteria", Map.of(
                    "sourceStep", source,
                    "rowCount", output.getOrDefault("rowCount", 0),
                    "sql", output.getOrDefault("sql", "")
            ));
        }
        return Map.copyOf(resolved);
    }

    @SuppressWarnings("unchecked")
    private static Object readJsonPath(Map<String, Object> sourceOutput, String path) {
        if (!path.startsWith("$.")) {
            throw new IllegalArgumentException("Only simple $.field paths are supported.");
        }
        Object current = sourceOutput;
        for (var part : path.substring(2).split("\\.")) {
            if (!(current instanceof Map<?, ?> map) || !map.containsKey(part)) {
                throw new IllegalStateException("Output path not found: " + path);
            }
            current = ((Map<String, Object>) map).get(part);
        }
        return current;
    }

    private static void resolveFromStep(Map<String, Object> resolved, Function<String, Map<String, Object>> outputForStep, String refKey, String outputKey) {
        if (!resolved.containsKey(refKey)) {
            return;
        }
        var source = String.valueOf(resolved.get(refKey));
        var output = outputForStep.apply(source);
        var value = output.get(outputKey);
        if (value == null) {
            throw new IllegalStateException("Step " + source + " did not produce " + outputKey);
        }
        resolved.put(outputKey, value);
    }
}
