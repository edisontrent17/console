package com.acme.data360agent.support;

import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class SensitiveData {
    private static final String REDACTED = "***";
    private static final int MAX_STRING_LENGTH = 4096;
    private static final int MAX_MAP_ENTRIES = 100;
    private static final int MAX_COLLECTION_ITEMS = 100;
    private static final Pattern BEARER_TOKEN = Pattern.compile("(?i)(bearer\\s+)([^\\s,;\\}\\]\"]+)");
    private static final Pattern INLINE_SECRET = Pattern.compile("(?i)((?:access|refresh)[_-]?token|client[_-]?secret|api[_-]?key|password|private[_-]?key|authorization|cookie|session)(\"?\\s*[:=]\\s*\"?)([^\\s,;\\}\\]\"]+)");

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

    public static Object redactValue(Object value) {
        return redact("", value);
    }

    public static String redactText(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        var redacted = BEARER_TOKEN.matcher(value).replaceAll("$1" + REDACTED);
        redacted = INLINE_SECRET.matcher(redacted).replaceAll("$1$2" + REDACTED);
        if (redacted.length() > MAX_STRING_LENGTH) {
            return redacted.substring(0, MAX_STRING_LENGTH) + "...[truncated]";
        }
        return redacted;
    }

    public static boolean containsSensitiveData(String key, Object value) {
        if (isSensitiveKey(key)) {
            return true;
        }
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .anyMatch(entry -> containsSensitiveData(String.valueOf(entry.getKey()), entry.getValue()));
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().anyMatch(item -> containsSensitiveData(key, item));
        }
        if (value instanceof Object[] array) {
            return Arrays.stream(array).anyMatch(item -> containsSensitiveData(key, item));
        }
        return value instanceof String text && containsSensitiveText(text);
    }

    @SuppressWarnings("unchecked")
    private static Object redact(String key, Object value) {
        if (isSensitiveKey(key)) {
            return REDACTED;
        }
        if (value instanceof Map<?, ?> map) {
            var nested = new LinkedHashMap<String, Object>();
            map.forEach((nestedKey, nestedValue) -> nested.put(String.valueOf(nestedKey), redact(String.valueOf(nestedKey), nestedValue)));
            if (nested.size() > MAX_MAP_ENTRIES) {
                var bounded = new LinkedHashMap<String, Object>();
                nested.entrySet().stream().limit(MAX_MAP_ENTRIES).forEach(entry -> bounded.put(entry.getKey(), entry.getValue()));
                bounded.put("_truncated", nested.size() - MAX_MAP_ENTRIES + " entries omitted");
                return Map.copyOf(bounded);
            }
            return Map.copyOf(nested);
        }
        if (value instanceof Collection<?> collection) {
            var redacted = collection.stream().limit(MAX_COLLECTION_ITEMS).map(item -> redact(key, item)).toList();
            if (collection.size() <= MAX_COLLECTION_ITEMS) {
                return redacted;
            }
            return java.util.stream.Stream.concat(redacted.stream(), java.util.stream.Stream.of(Map.of("_truncated", collection.size() - MAX_COLLECTION_ITEMS + " items omitted"))).toList();
        }
        if (value instanceof Object[] array) {
            var redacted = Arrays.stream(array).limit(MAX_COLLECTION_ITEMS).map(item -> redact(key, item)).toList();
            if (array.length <= MAX_COLLECTION_ITEMS) {
                return redacted;
            }
            return java.util.stream.Stream.concat(redacted.stream(), java.util.stream.Stream.of(Map.of("_truncated", array.length - MAX_COLLECTION_ITEMS + " items omitted"))).toList();
        }
        if (value instanceof String text) {
            return redactText(text);
        }
        return value;
    }

    public static boolean isSensitiveKey(String key) {
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
                || normalized.contains("session")
                || normalized.equals("email")
                || normalized.endsWith("email")
                || normalized.equals("phone")
                || normalized.endsWith("phone")
                || normalized.contains("ssn")
                || normalized.contains("socialsecurity")
                || normalized.contains("creditcard");
    }

    private static boolean containsSensitiveText(String value) {
        return value != null && (BEARER_TOKEN.matcher(value).find() || INLINE_SECRET.matcher(value).find());
    }
}
