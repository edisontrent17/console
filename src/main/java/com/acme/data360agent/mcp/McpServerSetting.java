package com.acme.data360agent.mcp;

public record McpServerSetting(
        String id,
        String name,
        String label,
        String description,
        boolean executionServer,
        boolean enabled,
        boolean commandConfigured,
        String transport,
        String command,
        String endpoint,
        java.util.List<String> arguments,
        java.util.List<McpEnvironmentVariable> environment,
        java.util.List<String> environmentPassthrough,
        String workingDirectory,
        String commandPreview
) {
    public McpServerSetting redacted() {
        return new McpServerSetting(
                id,
                name,
                label,
                description,
                executionServer,
                enabled,
                commandConfigured,
                transport,
                command,
                endpoint,
                arguments,
                environment.stream().map(McpEnvironmentVariable::redacted).toList(),
                environmentPassthrough,
                workingDirectory,
                commandPreview
        );
    }

    public McpServerSetting(
            String id,
            String label,
            String description,
            boolean executionServer,
            boolean enabled,
            boolean commandConfigured,
            String command,
            String commandPreview
    ) {
        this(
                id,
                label,
                label,
                description,
                executionServer,
                enabled,
                commandConfigured,
                "stdio",
                command,
                "",
                java.util.List.of(),
                java.util.List.of(),
                java.util.List.of(),
                "",
                commandPreview
        );
    }
}
