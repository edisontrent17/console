package com.acme.data360agent.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class McpDiscoveryServiceTest {
    @Test
    void searchAndPayloadExamplesParseFacadeJsonText() {
        var client = new StubClient();
        var service = new McpDiscoveryService(client, new ObjectMapper());

        var search = service.search("metadata discovery");
        var examples = service.payloadExamples("d360_metadata_search");

        assertThat(search).containsEntry("query", "metadata discovery");
        assertThat(examples).containsEntry("toolName", "d360_metadata_search");
        assertThat(client.calledTools).containsExactly("search", "payload_examples");
    }

    private static final class StubClient extends McpStdioClient {
        private final java.util.ArrayList<String> calledTools = new java.util.ArrayList<>();

        private StubClient() {
            super(List.of("stub"), Duration.ofSeconds(1), new ObjectMapper());
        }

        @Override
        public McpToolCallResult callTool(String name, Map<String, Object> arguments) {
            calledTools.add(name);
            var text = switch (name) {
                case "search" -> "{\"query\":\"" + arguments.get("query") + "\",\"results\":[]}";
                case "payload_examples" -> "{\"toolName\":\"" + arguments.get("toolName") + "\",\"inputSchema\":{}}";
                default -> "{}";
            };
            return new McpToolCallResult(name, List.of(Map.of("type", "text", "text", text)), Map.of());
        }
    }
}
