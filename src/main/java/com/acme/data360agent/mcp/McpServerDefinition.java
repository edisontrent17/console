package com.acme.data360agent.mcp;

public record McpServerDefinition(
        String id,
        String label,
        String description,
        boolean executionServer
) {
}
