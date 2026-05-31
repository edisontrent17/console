package com.acme.data360agent.data360;

import com.acme.data360agent.config.AppProperties;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.mcp.McpStdioClient;
import com.acme.data360agent.mcp.McpToolCallResult;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanStep;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.LinkedHashMap;

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
        return call(operation, OperationBindingSnapshot.data360Mcp(operation), step, resolvedInput, context);
    }

    @Override
    public Data360CallResult call(OperationDefinition operation, OperationBindingSnapshot binding, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
        var command = properties.data360() == null || properties.data360().mcp() == null
                ? null
                : properties.data360().mcp().command();
        if (command == null || command.isBlank()) {
            throw new IllegalStateException("app.data360.mcp.command must point to the Data 360 MCP stdio server command.");
        }

        var facadeTool = facadeTool(binding, step);
        var arguments = compileArguments(binding, step, resolvedInput);
        var raw = new McpStdioClient(command, objectMapper).callTool(facadeTool, arguments);
        var audit = new LinkedHashMap<String, Object>();
        audit.put("mode", "mcp");
        audit.put("calledAt", java.time.Instant.now().toString());
        audit.put("resource", binding.resource());
        audit.put("mcpServerId", nullToEmpty(binding.mcpServerId()));
        audit.put("facadeTool", facadeTool);
        audit.put("underlyingTool", nullToEmpty(binding.underlyingTool()));
        audit.put("schemaHash", binding.schemaHash());
        audit.put("bindingVersion", binding.bindingVersion());
        audit.put("arguments", Map.of(
                "toolName", arguments.getOrDefault("toolName", facadeTool),
                "params", resolvedInput
        ));
        audit.put("argumentKeys", arguments.keySet().stream().sorted().toList());
        audit.put("rawKeys", raw.rawResult().keySet().stream().sorted().toList());
        audit.put("rawResult", raw.rawResult());
        audit.put("text", raw.text() == null ? "" : raw.text());
        return new Data360CallResult(normalizeOutput(raw), audit);
    }

    private Map<String, Object> compileArguments(OperationBindingSnapshot binding, PlanStep step, Map<String, Object> input) {
        if ("search".equals(facadeTool(binding, step))) {
            return Map.of("query", input.get("query"));
        }
        if (step.action() == Data360Action.RUN_ACTIVATION) {
            throw new IllegalArgumentException("The current Data 360 MCP catalog does not expose a generic run activation tool. Use createActivation for a draft activation or add a target-specific operation mapping.");
        }
        if (binding.underlyingTool() == null || binding.underlyingTool().isBlank()) {
            throw new IllegalArgumentException("MCP execute binding requires an underlying tool name for " + binding.resource());
        }
        try {
            return Map.of(
                    "toolName", binding.underlyingTool(),
                    "paramsJson", objectMapper.writeValueAsString(input)
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compile MCP execute arguments.", e);
        }
    }

    private String facadeTool(OperationBindingSnapshot binding, PlanStep step) {
        if (binding.facadeTool() != null && !binding.facadeTool().isBlank()) {
            return binding.facadeTool();
        }
        return step.action() == Data360Action.SEARCH ? "search" : "execute";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> normalizeOutput(McpToolCallResult raw) {
        var text = raw.text();
        if (text != null && !text.isBlank()) {
            try {
                return objectMapper.readValue(text, MAP);
            } catch (Exception ignored) {
                return Map.of("text", text);
            }
        }
        if (raw.rawResult() instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of("rawResult", raw.rawResult());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
