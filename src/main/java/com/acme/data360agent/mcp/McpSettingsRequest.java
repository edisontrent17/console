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
            @Size(max = 255) String name,
            boolean enabled,
            @Size(max = 64) String transport,
            @Size(max = 4000) String command,
            @Size(max = 4000) String endpoint,
            List<@Size(max = 1000) String> arguments,
            @Valid List<McpEnvironmentVariable> environment,
            List<@Size(max = 255) String> environmentPassthrough,
            @Size(max = 4000) String workingDirectory
    ) {
        public Server(String id, boolean enabled, String command) {
            this(id, null, enabled, "stdio", command, null, List.of(), List.of(), List.of(), null);
        }

        public Server {
            arguments = arguments == null ? List.of() : List.copyOf(arguments);
            environment = environment == null ? List.of() : List.copyOf(environment);
            environmentPassthrough = environmentPassthrough == null ? List.of() : List.copyOf(environmentPassthrough);
        }
    }
}
