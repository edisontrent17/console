package com.acme.data360agent.execution;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationCatalogHash;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.support.SensitiveData;

import java.util.LinkedHashMap;
import java.util.Map;

public final class StepOutputSelector {
    private static final int MAX_SELECTED_VALUE_CHARS = 8192;
    private static final int MAX_SELECTED_TOTAL_CHARS = 32768;

    private StepOutputSelector() {
    }

    public static Map<String, Object> apply(PlanStep step, Map<String, Object> output) {
        return apply(step, output, null);
    }

    public static Map<String, Object> apply(PlanStep step, Map<String, Object> output, OperationBindingSnapshot binding) {
        var safeOutput = output == null ? Map.<String, Object>of() : output;
        var selectors = selectors(step);
        if (selectors.isEmpty()) {
            if (step != null && step.action() == Data360Action.MCP_EXECUTE) {
                throw new IllegalStateException("MCP execute steps must declare outputSelectors to persist runtime output: " + step.id());
            }
            return safeOutput;
        }
        validateSelectorContracts(step, selectors, binding);
        var selected = new LinkedHashMap<String, Object>();
        var metadata = new LinkedHashMap<String, Object>();
        var selectedChars = 0;
        for (var entry : selectors.entrySet()) {
            var value = readJsonPath(safeOutput, entry.getValue());
            var valueChars = validateSelectedValue(entry.getKey(), value);
            selectedChars += valueChars;
            if (selectedChars > MAX_SELECTED_TOTAL_CHARS) {
                throw new IllegalStateException("Output selector results are too large for step: " + step.id());
            }
            selected.put(entry.getKey(), value);
            metadata.put(entry.getKey(), selectorMetadata(entry.getValue(), value, valueChars, binding));
        }
        var bounded = new LinkedHashMap<String, Object>(selected);
        bounded.put("selected", Map.copyOf(selected));
        bounded.put("selectorMetadata", Map.copyOf(metadata));
        return Map.copyOf(bounded);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> selectors(PlanStep step) {
        if (step == null || !(step.input().get("outputSelectors") instanceof Map<?, ?> raw)) {
            return Map.of();
        }
        var selectors = new LinkedHashMap<String, String>();
        for (var entry : raw.entrySet()) {
            var key = String.valueOf(entry.getKey());
            var path = String.valueOf(entry.getValue());
            if (!key.isBlank() && !path.isBlank()) {
                selectors.put(key, path);
            }
        }
        return Map.copyOf(selectors);
    }

    @SuppressWarnings("unchecked")
    private static Object readJsonPath(Map<String, Object> source, String path) {
        if (path == null || !path.startsWith("$.")) {
            throw new IllegalArgumentException("Output selector paths must start with $.: " + path);
        }
        Object current = source;
        for (var part : path.substring(2).split("\\.")) {
            if (!(current instanceof Map<?, ?> map) || !map.containsKey(part)) {
                throw new IllegalStateException("Output selector path not found: " + path);
            }
            current = ((Map<String, Object>) map).get(part);
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    private static void validateSelectorContracts(PlanStep step, Map<String, String> selectors, OperationBindingSnapshot binding) {
        selectors.forEach((name, path) -> {
            if ("$.raw".equals(path) || path.startsWith("$.raw.") || "$.text".equals(path) || path.startsWith("$.text.")
                    || "$.output".equals(path) || "$".equals(path)) {
                throw new IllegalStateException("Output selector path is too broad or unsafe: " + name);
            }
        });
        if (binding == null || !(binding.outputSchema().get("selectorContracts") instanceof Map<?, ?> rawContracts) || rawContracts.isEmpty()) {
            return;
        }
        var contracts = new LinkedHashMap<String, String>();
        rawContracts.forEach((key, value) -> contracts.put(String.valueOf(key), String.valueOf(value)));
        selectors.forEach((name, path) -> {
            var expected = contracts.get(name);
            if (expected == null) {
                throw new IllegalStateException("Output selector is not declared by the tool contract for step " + step.id() + ": " + name);
            }
            if (!expected.equals(path)) {
                throw new IllegalStateException("Output selector path does not match the tool contract for step " + step.id() + ": " + name);
            }
        });
    }

    private static int validateSelectedValue(String name, Object value) {
        if (SensitiveData.containsSensitiveData(name, value)) {
            throw new IllegalStateException("Output selector result contains sensitive data: " + name);
        }
        var chars = OperationCatalogHash.canonicalJson(value).length();
        if (chars > MAX_SELECTED_VALUE_CHARS) {
            throw new IllegalStateException("Output selector result is too large: " + name);
        }
        return chars;
    }

    private static Map<String, Object> selectorMetadata(String path, Object value, int valueChars, OperationBindingSnapshot binding) {
        return Map.of(
                "path", path,
                "jsonType", jsonType(value),
                "estimatedChars", valueChars,
                "redactionPolicy", "reject-sensitive-and-oversized",
                "redacted", false,
                "contractEnforced", binding != null && binding.outputSchema().containsKey("selectorContracts")
        );
    }

    private static String jsonType(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Map<?, ?>) {
            return "object";
        }
        if (value instanceof Iterable<?>) {
            return "array";
        }
        if (value instanceof Number) {
            return "number";
        }
        if (value instanceof Boolean) {
            return "boolean";
        }
        return "string";
    }
}
