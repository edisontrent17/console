package com.acme.data360agent.mcp;

import java.util.List;
import java.util.Map;

public record McpToolCallResult(
        String toolName,
        List<Map<String, Object>> content,
        Map<String, Object> rawResult
) {
    public String text() {
        if (content == null || content.isEmpty()) {
            return "";
        }
        var first = content.getFirst();
        var text = first.get("text");
        return text == null ? "" : String.valueOf(text);
    }
}
