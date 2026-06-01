package com.acme.data360agent.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

final class McpMessageCodec {
    static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    McpMessageCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    void writeLineDelimited(BufferedWriter writer, Map<String, Object> message) throws IOException {
        writer.write(objectMapper.writeValueAsString(message));
        writer.newLine();
        writer.flush();
    }

    Map<String, Object> read(InputStream inputStream) throws IOException {
        var firstLine = readLine(inputStream);
        if (firstLine == null) {
            throw new McpException("MCP server closed stdout before responding.");
        }
        if (firstLine.isBlank()) {
            return read(inputStream);
        }
        if (firstLine.startsWith("{")) {
            return objectMapper.readValue(firstLine, MAP);
        }

        var headers = new LinkedHashMap<String, String>();
        var line = firstLine;
        while (line != null && !line.isBlank()) {
            var separator = line.indexOf(':');
            if (separator > 0) {
                headers.put(line.substring(0, separator).trim().toLowerCase(Locale.ROOT),
                        line.substring(separator + 1).trim());
            }
            line = readLine(inputStream);
        }

        var lengthHeader = headers.get("content-length");
        if (lengthHeader == null) {
            throw new McpException("MCP response did not include JSON or Content-Length framing.");
        }

        int byteLength;
        try {
            byteLength = Integer.parseInt(lengthHeader);
        } catch (NumberFormatException e) {
            throw new McpException("Invalid MCP Content-Length: " + lengthHeader, e);
        }

        var body = inputStream.readNBytes(byteLength);
        if (body.length != byteLength) {
            throw new McpException("MCP server closed stdout while sending a framed response.");
        }
        return objectMapper.readValue(body, MAP);
    }

    private String readLine(InputStream inputStream) throws IOException {
        var bytes = new ByteArrayOutputStream();
        while (true) {
            var read = inputStream.read();
            if (read < 0) {
                return bytes.size() == 0 ? null : bytes.toString(StandardCharsets.UTF_8);
            }
            if (read == '\n') {
                var line = bytes.toString(StandardCharsets.UTF_8);
                return line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
            }
            bytes.write(read);
        }
    }
}
