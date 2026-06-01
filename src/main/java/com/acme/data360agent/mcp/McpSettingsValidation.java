package com.acme.data360agent.mcp;

import java.util.List;

public record McpSettingsValidation(
        String status,
        String message,
        List<McpServerValidation> servers
) {
    public McpSettingsValidation {
        servers = servers == null ? List.of() : List.copyOf(servers);
    }
}
