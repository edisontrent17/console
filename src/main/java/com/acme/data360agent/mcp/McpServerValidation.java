package com.acme.data360agent.mcp;

import java.util.List;

public record McpServerValidation(
        String id,
        String name,
        String status,
        String message,
        int toolCount,
        List<String> tools
) {
    public McpServerValidation {
        tools = tools == null ? List.of() : List.copyOf(tools);
    }
}
