package com.acme.data360agent.plan;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record MonitorThreshold(String operator, double value) {
    private static final Set<String> OPERATORS = Set.of("<", "<=", ">", ">=", "==");

    public MonitorThreshold {
        if (!OPERATORS.contains(operator)) {
            throw new IllegalArgumentException("Monitor threshold operator must be one of <, <=, >, >=, ==.");
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Monitor threshold value must be finite.");
        }
    }

    public static MonitorThreshold require(Object value) {
        var issues = validate(value);
        if (!issues.isEmpty()) {
            throw new IllegalArgumentException(issues.getFirst());
        }
        var map = (Map<?, ?>) value;
        return new MonitorThreshold(String.valueOf(map.get("operator")), ((Number) map.get("value")).doubleValue());
    }

    public static List<String> validate(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            return List.of("Monitor threshold must be an object with operator and numeric value.");
        }
        var issues = new java.util.ArrayList<String>();
        var operator = String.valueOf(map.get("operator"));
        if (!OPERATORS.contains(operator)) {
            issues.add("Monitor threshold operator must be one of <, <=, >, >=, ==.");
        }
        if (!(map.get("value") instanceof Number number)) {
            issues.add("Monitor threshold value must be numeric.");
        } else if (Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())) {
            issues.add("Monitor threshold value must be finite.");
        }
        if (map.keySet().stream().anyMatch(key -> !Set.of("operator", "value").contains(String.valueOf(key)))) {
            issues.add("Monitor threshold supports only operator and value.");
        }
        return List.copyOf(issues);
    }

    public boolean isBreached(double observed) {
        return switch (operator) {
            case "<" -> observed < value;
            case "<=" -> observed <= value;
            case ">" -> observed > value;
            case ">=" -> observed >= value;
            case "==" -> Double.compare(observed, value) == 0;
            default -> throw new IllegalStateException("Unexpected threshold operator: " + operator);
        };
    }

    public Map<String, Object> asMap() {
        return Map.of("operator", operator, "value", value);
    }
}
