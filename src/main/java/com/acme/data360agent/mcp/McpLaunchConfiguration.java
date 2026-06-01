package com.acme.data360agent.mcp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record McpLaunchConfiguration(
        String command,
        List<String> arguments,
        Map<String, String> environment,
        List<String> environmentPassthrough,
        String workingDirectory
) {
    public McpLaunchConfiguration {
        arguments = arguments == null ? List.of() : List.copyOf(arguments);
        environment = environment == null ? Map.of() : Map.copyOf(environment);
        environmentPassthrough = environmentPassthrough == null ? List.of() : List.copyOf(environmentPassthrough);
        workingDirectory = workingDirectory == null || workingDirectory.isBlank() ? null : workingDirectory.trim();
    }

    public List<String> commandLine() {
        var commandLine = new java.util.ArrayList<>(McpCommandParser.parse(command));
        commandLine.addAll(arguments);
        return List.copyOf(commandLine);
    }

    public static Map<String, String> environmentMap(List<McpEnvironmentVariable> variables) {
        var map = new LinkedHashMap<String, String>();
        if (variables == null) {
            return map;
        }
        for (var variable : variables) {
            if (variable == null || variable.key() == null || variable.key().isBlank()) {
                continue;
            }
            map.put(variable.key().trim(), variable.value() == null ? "" : variable.value());
        }
        return map;
    }
}
