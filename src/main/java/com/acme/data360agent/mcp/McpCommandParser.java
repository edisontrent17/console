package com.acme.data360agent.mcp;

import java.util.ArrayList;
import java.util.List;

public final class McpCommandParser {
    private McpCommandParser() {
    }

    public static List<String> parse(String command) {
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("MCP command must not be blank.");
        }

        var args = new ArrayList<String>();
        var current = new StringBuilder();
        char quote = 0;
        var escaping = false;

        for (int i = 0; i < command.length(); i++) {
            var ch = command.charAt(i);
            if (escaping) {
                current.append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if ((ch == '\'' || ch == '"') && quote == 0) {
                quote = ch;
            } else if (ch == quote) {
                quote = 0;
            } else if (Character.isWhitespace(ch) && quote == 0) {
                addArg(args, current);
            } else {
                current.append(ch);
            }
        }

        if (escaping) {
            current.append('\\');
        }
        if (quote != 0) {
            throw new IllegalArgumentException("Unclosed quote in MCP command.");
        }
        addArg(args, current);
        return List.copyOf(args);
    }

    private static void addArg(List<String> args, StringBuilder current) {
        if (!current.isEmpty()) {
            args.add(current.toString());
            current.setLength(0);
        }
    }
}
