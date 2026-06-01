package com.acme.data360agent.chat;

import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.mcp.McpStdioClient;
import com.acme.data360agent.mcp.McpToolCallResult;
import com.acme.data360agent.support.SensitiveData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

@Service
public class McpExplorationService {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };
    private static final List<String> LIST_KEYS = List.of(
            "data",
            "dataSpaces",
            "dataStreams",
            "dataStreamDefinitions",
            "items",
            "records",
            "results"
    );
    private static final int MAX_ITEMS = 20;

    private final McpSettingsService mcpSettings;
    private final ObjectMapper objectMapper;

    public McpExplorationService(McpSettingsService mcpSettings, ObjectMapper objectMapper) {
        this.mcpSettings = mcpSettings;
        this.objectMapper = objectMapper;
    }

    public ExplorationResult answer(String message) {
        var intent = (message == null ? "" : message).toLowerCase(Locale.ROOT);
        if (isDataStreamList(intent)) {
            return listDataStreams();
        }
        if (isDataspaceList(intent)) {
            return listDataspaces();
        }
        return ExplorationResult.unhandled();
    }

    private boolean isDataspaceList(String intent) {
        var normalized = intent
                .replace("data spaces", "dataspaces")
                .replace("dataspces", "dataspaces")
                .replace("data spces", "dataspaces");
        return (normalized.contains("dataspace") || normalized.contains("dataspaces"))
                && (normalized.contains("list") || normalized.contains("show") || normalized.contains("what") || normalized.contains("which"));
    }

    private boolean isDataStreamList(String intent) {
        var normalized = intent
                .replace("data streams", "datastreams")
                .replace("data stream", "datastream")
                .replace("data-streams", "datastreams")
                .replace("data-stream", "datastream");
        return (normalized.contains("datastream") || normalized.contains("datastreams"))
                && (normalized.contains("list") || normalized.contains("show") || normalized.contains("what") || normalized.contains("which"));
    }

    private ExplorationResult listDataspaces() {
        var trace = new ArrayList<ChatTraceEvent>();
        trace.add(ChatTraceEvent.step("router", "Matched dataspace list intent", "completed", "Routed to read-only Data 360 MCP exploration."));
        try {
            var result = callData360Tool("d360_dataspace_list", "{}", trace);
            var payload = parseJsonObject(result.text());
            if (payload.containsKey("error")) {
                var error = SensitiveData.redactText(value(payload.get("error")));
                trace.add(ChatTraceEvent.step("response", "Data 360 returned an error payload", "failed", error));
                return ExplorationResult.handled("I tried to list dataspaces, but Data 360 returned: " + error, trace);
            }
            trace.add(ChatTraceEvent.step("response", "Formatted dataspace list", "completed", itemCount(payload) + " item(s) returned."));
            return ExplorationResult.handled(formatDataspaces(payload), trace);
        } catch (Exception e) {
            return ExplorationResult.handled("I tried to list dataspaces through the Data 360 MCP server, but it failed: " + concise(e), trace);
        }
    }

    private ExplorationResult listDataStreams() {
        var trace = new ArrayList<ChatTraceEvent>();
        trace.add(ChatTraceEvent.step("router", "Matched data stream list intent", "completed", "Routed to read-only Data 360 MCP exploration."));
        try {
            var result = callData360Tool("d360_datastream_list", "{}", trace);
            var payload = parseJsonObject(result.text());
            if (payload.containsKey("error")) {
                var error = SensitiveData.redactText(value(payload.get("error")));
                trace.add(ChatTraceEvent.step("response", "Data 360 returned an error payload", "failed", error));
                return ExplorationResult.handled("I tried to list data streams, but Data 360 returned: " + error, trace);
            }
            trace.add(ChatTraceEvent.step("response", "Formatted data stream list", "completed", itemCount(payload) + " item(s) returned."));
            return ExplorationResult.handled(formatDataStreams(payload), trace);
        } catch (Exception e) {
            return ExplorationResult.handled("I tried to list data streams through the Data 360 MCP server, but it failed: " + concise(e), trace);
        }
    }

    private McpToolCallResult callData360Tool(String toolName, String paramsJson, List<ChatTraceEvent> trace) {
        var launch = mcpSettings.launchConfigurationFor("data360")
                .orElseThrow(() -> new IllegalStateException("Data 360 MCP is not enabled or configured."));
        var arguments = Map.<String, Object>of(
                "toolName", toolName,
                "paramsJson", paramsJson
        );
        var started = Instant.now();
        try {
            var result = new McpStdioClient(launch, objectMapper).callTool("execute", arguments);
            trace.add(toolTrace(toolName, paramsJson, "completed", "Data 360 MCP tool completed.", started));
            return result;
        } catch (RuntimeException e) {
            trace.add(toolTrace(toolName, paramsJson, "failed", concise(e), started));
            throw e;
        }
    }

    private Map<String, Object> parseJsonObject(String text) throws Exception {
        if (text == null || text.isBlank()) {
            return Map.of();
        }
        return objectMapper.readValue(text, MAP);
    }

    private String formatDataspaces(Map<String, Object> payload) {
        var dataspaces = listItems(payload);
        if (dataspaces.isEmpty()) {
            return "I did not find any dataspaces in the connected Data 360 org.";
        }
        var builder = new StringBuilder();
        builder.append("I found ").append(dataspaces.size()).append(dataspaces.size() == 1 ? " dataspace:" : " dataspaces:");
        for (var item : limited(dataspaces)) {
            if (item instanceof Map<?, ?> dataspace) {
                var name = value(dataspace.get("name"));
                var label = value(dataspace.get("label"));
                var status = value(dataspace.get("status"));
                var id = value(dataspace.get("id"));
                builder.append("\n- ").append(!label.isBlank() ? label : name);
                if (!name.isBlank() && !name.equals(label)) {
                    builder.append(" (`").append(name).append("`)");
                }
                if (!status.isBlank()) {
                    builder.append(" - ").append(status);
                }
                if (!id.isBlank()) {
                    builder.append(" - ").append(id);
                }
            }
        }
        appendRemainder(builder, dataspaces);
        return builder.toString();
    }

    private String formatDataStreams(Map<String, Object> payload) {
        var streams = listItems(payload);
        if (streams.isEmpty()) {
            return "I did not find any data streams in the connected Data 360 org.";
        }
        var builder = new StringBuilder();
        builder.append("I found ").append(streams.size()).append(streams.size() == 1 ? " data stream:" : " data streams:");
        for (var item : limited(streams)) {
            if (item instanceof Map<?, ?> stream) {
                appendDataStream(builder, stream);
            }
        }
        appendRemainder(builder, streams);
        return builder.toString();
    }

    private void appendDataStream(StringBuilder builder, Map<?, ?> stream) {
        var name = firstValue(stream, "name", "label", "displayName", "developerName");
        var id = firstValue(stream, "id", "dataStreamId", "dataStreamDefinitionId");
        var status = firstValue(stream, "status", "state");
        var category = firstValue(stream, "category", "dataCategory", "type", "dataStreamType", "datastreamType");
        var source = firstValue(stream, "sourceObjectName", "sourceObject", "objectName", "sourceName");
        var connector = connectorType(stream);
        builder.append("\n- ").append(!name.isBlank() ? name : !id.isBlank() ? id : "Unnamed data stream");
        appendDetail(builder, category);
        appendDetail(builder, connector);
        appendDetail(builder, source);
        appendDetail(builder, status);
        if (!id.isBlank() && !id.equals(name)) {
            builder.append(" - ").append(id);
        }
    }

    private List<?> listItems(Map<String, Object> payload) {
        for (var key : LIST_KEYS) {
            if (payload.get(key) instanceof List<?> items) {
                return items;
            }
        }
        return List.of();
    }

    private int itemCount(Map<String, Object> payload) {
        return listItems(payload).size();
    }

    private ChatTraceEvent toolTrace(String toolName, String paramsJson, String status, String detail, Instant started) {
        return ChatTraceEvent.timed("tool", "Called Data 360 MCP", status, detail, Duration.between(started, Instant.now()).toMillis(), Map.of(
                "server", "data360",
                "mcpTool", "execute",
                "data360Tool", toolName,
                "paramsJson", SensitiveData.redactText(paramsJson)
        ));
    }

    private List<?> limited(List<?> items) {
        return items.size() <= MAX_ITEMS ? items : items.subList(0, MAX_ITEMS);
    }

    private void appendRemainder(StringBuilder builder, List<?> items) {
        if (items.size() > MAX_ITEMS) {
            builder.append("\n- ...and ").append(items.size() - MAX_ITEMS).append(" more.");
        }
    }

    private void appendDetail(StringBuilder builder, String value) {
        if (!value.isBlank()) {
            builder.append(" - ").append(value);
        }
    }

    private String firstValue(Map<?, ?> values, String... keys) {
        for (var key : keys) {
            var value = value(values.get(key));
            if (!value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private String connectorType(Map<?, ?> stream) {
        var connectorInfo = stream.get("connectorInfo");
        if (connectorInfo instanceof Map<?, ?> connector) {
            return firstValue(connector, "connectorType", "type", "name");
        }
        return firstValue(stream, "connectorType", "sourceType");
    }

    private String value(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String concise(Exception e) {
        var current = e;
        while (current.getCause() instanceof Exception nested) {
            current = nested;
        }
        return current.getMessage() == null || current.getMessage().isBlank()
                ? e.getClass().getSimpleName()
                : SensitiveData.redactText(current.getMessage());
    }

    private String concise(RuntimeException e) {
        return concise((Exception) e);
    }
}
