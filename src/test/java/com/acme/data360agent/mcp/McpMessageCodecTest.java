package com.acme.data360agent.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class McpMessageCodecTest {
    private final McpMessageCodec codec = new McpMessageCodec(new ObjectMapper());

    @Test
    void readsLineDelimitedJsonRpc() throws Exception {
        var input = new ByteArrayInputStream("{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":{\"ok\":true}}\n"
                .getBytes(StandardCharsets.UTF_8));

        var message = codec.read(input);

        assertThat(message).containsEntry("id", 1);
        assertThat(message.get("result")).asString().contains("ok=true");
    }

    @Test
    void readsContentLengthFramedJsonRpc() throws Exception {
        var body = "{\"jsonrpc\":\"2.0\",\"id\":2,\"result\":{\"text\":\"cafe\"}}";
        var framed = "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" + body;

        var message = codec.read(new ByteArrayInputStream(framed.getBytes(StandardCharsets.UTF_8)));

        assertThat(message).containsEntry("id", 2);
        assertThat(message.get("result")).asString().contains("text=cafe");
    }
}
