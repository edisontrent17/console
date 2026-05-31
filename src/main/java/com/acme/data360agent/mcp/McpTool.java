package com.acme.data360agent.mcp;

import java.util.Map;

public record McpTool(
        String name,
        String description,
        Map<String, Object> inputSchema
) {
}
