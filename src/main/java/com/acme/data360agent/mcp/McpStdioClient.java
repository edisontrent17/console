package com.acme.data360agent.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class McpStdioClient {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final String PROTOCOL_VERSION = "2024-11-05";

    private final List<String> command;
    private final Duration timeout;
    private final ObjectMapper objectMapper;
    private final ProcessFactory processFactory;

    public McpStdioClient(String command, ObjectMapper objectMapper) {
        this(McpCommandParser.parse(command), DEFAULT_TIMEOUT, objectMapper);
    }

    public McpStdioClient(List<String> command, Duration timeout, ObjectMapper objectMapper) {
        this(command, timeout, objectMapper, ProcessBuilder::start);
    }

    McpStdioClient(List<String> command, Duration timeout, ObjectMapper objectMapper, ProcessFactory processFactory) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("MCP command must not be empty.");
        }
        this.command = List.copyOf(command);
        this.timeout = timeout == null || timeout.isNegative() || timeout.isZero() ? DEFAULT_TIMEOUT : timeout;
        this.objectMapper = objectMapper;
        this.processFactory = processFactory;
    }

    public Map<String, Object> initialize() {
        return withSession(session -> {
            session.initialize();
            return Map.copyOf(session.initializeResult());
        });
    }

    public List<McpTool> listTools() {
        return withSession(session -> {
            session.initialize();
            var response = session.request("tools/list", Map.of());
            var result = responseResult(response);
            return readTools(result);
        });
    }

    public McpToolCallResult callTool(String name, Map<String, Object> arguments) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("MCP tool name must not be blank.");
        }
        return withSession(session -> {
            session.initialize();
            var response = session.request("tools/call", Map.of(
                    "name", name,
                    "arguments", arguments == null ? Map.of() : arguments
            ));
            return readToolCallResult(name, responseResult(response));
        });
    }

    private <T> T withSession(SessionCallback<T> callback) {
        Process process = null;
        var executor = Executors.newSingleThreadExecutor();
        try {
            process = processFactory.start(new ProcessBuilder(command).redirectError(ProcessBuilder.Redirect.DISCARD));
            var codec = new McpMessageCodec(objectMapper);
            try (var inputStream = process.getInputStream();
                 var writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                var session = new Session(codec, inputStream, writer);
                var future = executor.submit(() -> callback.run(session));
                return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException e) {
            throw new McpException("MCP request timed out after " + timeout.toSeconds() + " seconds.", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof McpException mcpException) {
                throw mcpException;
            }
            throw new McpException("MCP stdio request failed.", e.getCause());
        } catch (McpException e) {
            throw e;
        } catch (Exception e) {
            throw new McpException("MCP stdio request failed.", e);
        } finally {
            executor.shutdownNow();
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> responseResult(Map<String, Object> response) {
        if (response.containsKey("error")) {
            throw new McpException("MCP error: " + response.get("error"));
        }
        var result = response.get("result");
        if (result instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    private List<McpTool> readTools(Map<String, Object> result) {
        var tools = result.get("tools");
        if (!(tools instanceof List<?> list)) {
            return List.of();
        }
        return list.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(tool -> new McpTool(
                        stringValue(tool.get("name")),
                        stringValue(tool.get("description")),
                        tool.get("inputSchema") instanceof Map<?, ?> schema
                                ? (Map<String, Object>) schema
                                : Map.of()
                ))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private McpToolCallResult readToolCallResult(String name, Map<String, Object> result) {
        var content = result.get("content") instanceof List<?> list
                ? list.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(entry -> (Map<String, Object>) entry)
                .toList()
                : List.<Map<String, Object>>of();
        return new McpToolCallResult(name, content, result);
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    @FunctionalInterface
    interface ProcessFactory {
        Process start(ProcessBuilder processBuilder) throws Exception;
    }

    @FunctionalInterface
    private interface SessionCallback<T> {
        T run(Session session) throws Exception;
    }

    final class Session {
        private final McpMessageCodec codec;
        private final java.io.InputStream inputStream;
        private final BufferedWriter writer;
        private final AtomicInteger ids = new AtomicInteger(1);
        private Map<String, Object> initializeResult = Map.of();
        private boolean initialized;

        Session(McpMessageCodec codec, java.io.InputStream inputStream, BufferedWriter writer) {
            this.codec = codec;
            this.inputStream = inputStream;
            this.writer = writer;
        }

        void initialize() throws Exception {
            if (initialized) {
                return;
            }
            var response = request("initialize", Map.of(
                    "protocolVersion", PROTOCOL_VERSION,
                    "capabilities", Map.of(),
                    "clientInfo", Map.of("name", "data360-agent-console", "version", "0.0.1")
            ));
            initializeResult = responseResult(response);
            notify("notifications/initialized", Map.of());
            initialized = true;
        }

        Map<String, Object> initializeResult() {
            return initializeResult;
        }

        Map<String, Object> request(String method, Map<String, Object> params) throws Exception {
            var message = new LinkedHashMap<String, Object>();
            message.put("jsonrpc", "2.0");
            message.put("id", ids.getAndIncrement());
            message.put("method", method);
            message.put("params", params);
            codec.writeLineDelimited(writer, message);
            return codec.read(inputStream);
        }

        void notify(String method, Map<String, Object> params) throws Exception {
            var message = new LinkedHashMap<String, Object>();
            message.put("jsonrpc", "2.0");
            message.put("method", method);
            message.put("params", params);
            codec.writeLineDelimited(writer, message);
        }
    }
}
