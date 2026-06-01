package com.acme.data360agent.mcp;

public record McpEnvironmentVariable(
        String key,
        String value
) {
    public static final String REDACTED_VALUE = "********";

    public McpEnvironmentVariable redacted() {
        if (value == null || value.isBlank()) {
            return this;
        }
        return new McpEnvironmentVariable(key, REDACTED_VALUE);
    }

    public boolean hasRedactedValue() {
        return REDACTED_VALUE.equals(value);
    }
}
