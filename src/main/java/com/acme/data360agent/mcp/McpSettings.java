package com.acme.data360agent.mcp;

import java.util.List;

public record McpSettings(
        String organizationId,
        List<McpServerSetting> servers
) {
    public McpSettings {
        servers = servers == null ? List.of() : List.copyOf(servers);
    }

    public McpSettings redacted() {
        return new McpSettings(
                organizationId,
                servers.stream().map(McpServerSetting::redacted).toList()
        );
    }
}
