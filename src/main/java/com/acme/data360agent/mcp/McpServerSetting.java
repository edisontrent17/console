package com.acme.data360agent.mcp;

public record McpServerSetting(
        String id,
        String label,
        String description,
        boolean executionServer,
        boolean enabled,
        boolean commandConfigured,
        String command,
        String commandPreview
) {
}
