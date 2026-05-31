package com.acme.data360agent.support;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SensitiveData {
    private static final String REDACTED = "***";

    private SensitiveData() {
    }

    public static Map<String, Object> redactMap(Map<String, Object> input) {
        if (input == null || input.isEmpty()) {
            return Map.of();
        }
        var redacted = new LinkedHashMap<String, Object>();
        input.forEach((key, value) -> redacted.put(key, redact(key, value)));
        return Map.copyOf(redacted);
    }

    @SuppressWarnings("unchecked")
    private static Object redact(String key, Object value) {
        if (isSensitiveKey(key)) {
            return REDACTED;
        }
        if (value instanceof Map<?, ?> map) {
            var nested = new LinkedHashMap<String, Object>();
            map.forEach((nestedKey, nestedValue) -> nested.put(String.valueOf(nestedKey), redact(String.valueOf(nestedKey), nestedValue)));
            return Map.copyOf(nested);
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(item -> redact(key, item)).toList();
        }
        if (value instanceof Object[] array) {
            return List.of(array).stream().map(item -> redact(key, item)).toList();
        }
        return value;
    }

    private static boolean isSensitiveKey(String key) {
        var normalized = key == null ? "" : key.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        return normalized.contains("token")
                || normalized.contains("secret")
                || normalized.contains("password")
                || normalized.contains("apikey")
                || normalized.equals("key")
                || normalized.contains("xapikey")
                || normalized.contains("credential")
                || normalized.contains("bearer")
                || normalized.contains("privatekey")
                || normalized.contains("authorization")
                || normalized.contains("assertion")
                || normalized.contains("cookie")
                || normalized.contains("session");
    }
}
