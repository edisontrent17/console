package com.acme.data360agent.mcp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record McpSettingsRequest(
        @Valid List<Server> servers
) {
    public McpSettingsRequest {
        servers = servers == null ? List.of() : List.copyOf(servers);
    }

    public record Server(
            @NotBlank @Size(max = 128) String id,
            boolean enabled,
            @Size(max = 4000) String command
    ) {
    }
}
