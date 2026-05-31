package com.acme.data360agent.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class McpStdioClientTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void initializesThenListsTools() {
        var process = new FakeProcess("""
                {"jsonrpc":"2.0","id":1,"result":{"protocolVersion":"2024-11-05"}}
                {"jsonrpc":"2.0","id":2,"result":{"tools":[{"name":"search","description":"Search tools","inputSchema":{"type":"object"}},{"name":"payload_examples","description":"Examples","inputSchema":{"type":"object"}}]}}
                """);
        var client = new McpStdioClient(List.of("fake-mcp"), Duration.ofSeconds(2), objectMapper, ignored -> process);

        var tools = client.listTools();

        assertThat(tools).extracting(McpTool::name).containsExactly("search", "payload_examples");
        assertThat(process.writtenText())
                .contains("\"method\":\"initialize\"")
                .contains("\"method\":\"notifications/initialized\"")
                .contains("\"method\":\"tools/list\"");
    }

    @Test
    void callsToolAndReturnsTextContent() {
        var process = new FakeProcess("""
                {"jsonrpc":"2.0","id":1,"result":{"protocolVersion":"2024-11-05"}}
                {"jsonrpc":"2.0","id":2,"result":{"content":[{"type":"text","text":"{\\"results\\":[\\"Query\\"]}"}]}}
                """);
        var client = new McpStdioClient(List.of("fake-mcp"), Duration.ofSeconds(2), objectMapper, ignored -> process);

        var result = client.callTool("search", Map.of("query", "metadata"));

        assertThat(result.text()).isEqualTo("{\"results\":[\"Query\"]}");
        assertThat(process.writtenText())
                .contains("\"name\":\"search\"")
                .contains("\"query\":\"metadata\"");
    }

    @Test
    void convertsJsonRpcErrorsToMcpException() {
        var process = new FakeProcess("""
                {"jsonrpc":"2.0","id":1,"result":{"protocolVersion":"2024-11-05"}}
                {"jsonrpc":"2.0","id":2,"error":{"code":-32601,"message":"missing"}}
                """);
        var client = new McpStdioClient(List.of("fake-mcp"), Duration.ofSeconds(2), objectMapper, ignored -> process);

        assertThatThrownBy(client::listTools)
                .isInstanceOf(McpException.class)
                .hasMessageContaining("missing");
    }

    private static final class FakeProcess extends Process {
        private final ByteArrayInputStream inputStream;
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private boolean alive = true;

        private FakeProcess(String stdout) {
            this.inputStream = new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public OutputStream getOutputStream() {
            return outputStream;
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public InputStream getErrorStream() {
            return InputStream.nullInputStream();
        }

        @Override
        public int waitFor() {
            alive = false;
            return 0;
        }

        @Override
        public boolean waitFor(long timeout, TimeUnit unit) {
            alive = false;
            return true;
        }

        @Override
        public int exitValue() {
            alive = false;
            return 0;
        }

        @Override
        public void destroy() {
            alive = false;
        }

        @Override
        public Process destroyForcibly() {
            alive = false;
            return this;
        }

        @Override
        public boolean isAlive() {
            return alive;
        }

        private String writtenText() {
            return outputStream.toString(StandardCharsets.UTF_8);
        }
    }
}
