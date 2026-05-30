package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanStep;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ConditionalOnProperty(name = "app.data360.client", havingValue = "mcp")
public class McpData360Client implements Data360Client {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final AppProperties properties;
    private final ObjectMapper objectMapper;

    public McpData360Client(AppProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
        var command = properties.data360() == null || properties.data360().mcp() == null
                ? null
                : properties.data360().mcp().command();
        if (command == null || command.isBlank()) {
            throw new IllegalStateException("app.data360.mcp.command must point to the Data 360 MCP stdio server command.");
        }

        var toolName = step.action() == Data360Action.SEARCH ? "search" : "execute";
        var arguments = compileArguments(operation, step, resolvedInput);
        var raw = callTool(command, toolName, arguments);
        return new Data360CallResult(normalizeOutput(step, raw), Map.of(
                "mode", "mcp",
                "tool", toolName,
                "argumentKeys", arguments.keySet().stream().sorted().toList(),
                "rawKeys", raw.keySet().stream().sorted().toList()
        ));
    }

    private Map<String, Object> compileArguments(OperationDefinition operation, PlanStep step, Map<String, Object> input) {
        if (step.action() == Data360Action.SEARCH) {
            return Map.of("query", input.get("query"));
        }
        if (step.action() == Data360Action.RUN_ACTIVATION) {
            throw new IllegalArgumentException("The current Data 360 MCP catalog does not expose a generic run activation tool. Use createActivation for a draft activation or add a target-specific operation mapping.");
        }
        try {
            return Map.of(
                    "toolName", operation.mcpOperation(),
                    "paramsJson", objectMapper.writeValueAsString(input)
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compile MCP execute arguments.", e);
        }
    }

    private Map<String, Object> callTool(String command, String toolName, Map<String, Object> arguments) {
        Process process = null;
        try {
            process = new ProcessBuilder(parseCommand(command))
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                 var writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                var ids = new AtomicInteger(1);
                send(writer, ids.getAndIncrement(), "initialize", Map.of(
                        "protocolVersion", "2024-11-05",
                        "capabilities", Map.of(),
                        "clientInfo", Map.of("name", "data360-agent-console", "version", "0.0.1")
                ));
                readResponse(reader);
                sendNotification(writer, "notifications/initialized", Map.of());

                send(writer, ids.getAndIncrement(), "tools/call", Map.of(
                        "name", toolName,
                        "arguments", arguments
                ));
                var result = readResponse(reader);
                process.destroy();
                process.waitFor(Duration.ofSeconds(2).toMillis(), TimeUnit.MILLISECONDS);
                return result;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Data 360 MCP call failed.", e);
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private List<String> parseCommand(String command) {
        var args = new java.util.ArrayList<String>();
        var current = new StringBuilder();
        var quote = '\0';
        for (int i = 0; i < command.length(); i++) {
            var ch = command.charAt(i);
            if ((ch == '\'' || ch == '"') && quote == '\0') {
                quote = ch;
            } else if (ch == quote) {
                quote = '\0';
            } else if (Character.isWhitespace(ch) && quote == '\0') {
                addArg(args, current);
            } else {
                current.append(ch);
            }
        }
        if (quote != '\0') {
            throw new IllegalArgumentException("Unclosed quote in app.data360.mcp.command.");
        }
        addArg(args, current);
        if (args.isEmpty()) {
            throw new IllegalArgumentException("app.data360.mcp.command must not be blank.");
        }
        return List.copyOf(args);
    }

    private void addArg(List<String> args, StringBuilder current) {
        if (!current.isEmpty()) {
            args.add(current.toString());
            current.setLength(0);
        }
    }

    private void send(BufferedWriter writer, int id, String method, Map<String, Object> params) throws Exception {
        var message = new LinkedHashMap<String, Object>();
        message.put("jsonrpc", "2.0");
        message.put("id", id);
        message.put("method", method);
        message.put("params", params);
        writer.write(objectMapper.writeValueAsString(message));
        writer.newLine();
        writer.flush();
    }

    private void sendNotification(BufferedWriter writer, String method, Map<String, Object> params) throws Exception {
        var message = new LinkedHashMap<String, Object>();
        message.put("jsonrpc", "2.0");
        message.put("method", method);
        message.put("params", params);
        writer.write(objectMapper.writeValueAsString(message));
        writer.newLine();
        writer.flush();
    }

    private Map<String, Object> readResponse(BufferedReader reader) throws Exception {
        var line = reader.readLine();
        if (line == null) {
            throw new IllegalStateException("MCP server closed stdout before responding.");
        }
        var response = objectMapper.readValue(line, MAP);
        if (response.containsKey("error")) {
            throw new IllegalStateException("MCP error: " + response.get("error"));
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> normalizeOutput(PlanStep step, Map<String, Object> raw) {
        var result = raw.get("result");
        if (result instanceof Map<?, ?> map) {
            var content = map.get("content");
            if (content instanceof List<?> list && !list.isEmpty() && list.getFirst() instanceof Map<?, ?> first) {
                var text = first.get("text");
                if (text != null) {
                    try {
                        return objectMapper.readValue(String.valueOf(text), MAP);
                    } catch (Exception ignored) {
                        return Map.of("text", String.valueOf(text));
                    }
                }
            }
            return (Map<String, Object>) map;
        }
        return Map.of("rawResult", raw);
    }
}
