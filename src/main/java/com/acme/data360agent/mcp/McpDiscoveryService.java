package com.acme.data360agent.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class McpDiscoveryService {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final McpStdioClient client;
    private final ObjectMapper objectMapper;

    public McpDiscoveryService(McpStdioClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public List<McpTool> list() {
        return client.listTools();
    }

    public Map<String, Object> search(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Discovery search query must not be blank.");
        }
        return callJsonTool("search", Map.of("query", query));
    }

    public Map<String, Object> payloadExamples(String toolName) {
        Map<String, Object> arguments = toolName == null || toolName.isBlank()
                ? Map.of()
                : Map.of("toolName", toolName);
        return callJsonTool("payload_examples", arguments);
    }

    private Map<String, Object> callJsonTool(String toolName, Map<String, Object> arguments) {
        var result = client.callTool(toolName, arguments);
        var text = result.text();
        if (text == null || text.isBlank()) {
            return result.rawResult();
        }
        try {
            return objectMapper.readValue(text, MAP);
        } catch (Exception e) {
            return Map.of("text", text);
        }
    }
}
