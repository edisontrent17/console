package com.acme.data360agent.mcp;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public record McpToolRegistrySnapshot(
        Instant discoveredAt,
        String registryHash,
        List<McpToolDescriptor> tools,
        List<McpServerRegistryStatus> servers
) implements Serializable {
    public McpToolRegistrySnapshot {
        discoveredAt = discoveredAt == null ? Instant.now() : discoveredAt;
        tools = tools == null ? List.of() : List.copyOf(tools);
        servers = servers == null ? List.of() : List.copyOf(servers);
        registryHash = registryHash == null || registryHash.isBlank()
                ? stableHash(tools, servers)
                : registryHash;
    }

    public McpToolRegistrySnapshot(Instant discoveredAt, List<McpToolDescriptor> tools, List<McpServerRegistryStatus> servers) {
        this(discoveredAt, null, tools, servers);
    }

    public Optional<McpToolDescriptor> findTool(String serverId, String toolName) {
        return tools.stream()
                .filter(tool -> equal(tool.serverId(), serverId) && equal(tool.name(), toolName))
                .findFirst();
    }

    public Optional<McpServerRegistryStatus> serverStatus(String serverId) {
        return servers.stream()
                .filter(server -> equal(server.serverId(), serverId))
                .findFirst();
    }

    public record McpServerRegistryStatus(
            String serverId,
            String status,
            String message,
            int toolCount,
            boolean executionServer,
            String connectorDefinitionHash
    ) implements Serializable {
        public McpServerRegistryStatus {
            connectorDefinitionHash = connectorDefinitionHash == null ? "" : connectorDefinitionHash;
        }

        public McpServerRegistryStatus(String serverId, String status, String message, int toolCount) {
            this(serverId, status, message, toolCount, true, "");
        }

        public McpServerRegistryStatus(String serverId, String status, String message, int toolCount, boolean executionServer) {
            this(serverId, status, message, toolCount, executionServer, "");
        }
    }

    private static boolean equal(String left, String right) {
        return left != null && left.equals(right);
    }

    private static String stableHash(List<McpToolDescriptor> tools, List<McpServerRegistryStatus> servers) {
        var canonical = new StringBuilder();
        tools.stream()
                .sorted(Comparator.comparing(McpToolDescriptor::serverId, Comparator.nullsFirst(String::compareTo))
                        .thenComparing(McpToolDescriptor::name, Comparator.nullsFirst(String::compareTo)))
                .forEach(tool -> canonical
                        .append("tool|").append(tool.serverId()).append('|')
                        .append(tool.name()).append('|')
                        .append(tool.effect()).append('|')
                        .append(canonical(tool.inputSchema()))
                        .append('|').append(canonical(tool.outputSchema()))
                        .append('|').append(canonical(tool.selectorContracts()))
                        .append('\n'));
        servers.stream()
                .sorted(Comparator.comparing(McpServerRegistryStatus::serverId, Comparator.nullsFirst(String::compareTo)))
                .forEach(server -> canonical
                        .append("server|").append(server.serverId()).append('|')
                        .append(server.status()).append('|')
                        .append(server.executionServer()).append('|')
                        .append(server.connectorDefinitionHash()).append('|')
                        .append(server.toolCount())
                        .append('\n'));
        try {
            var digest = MessageDigest.getInstance("SHA-256").digest(canonical.toString().getBytes(StandardCharsets.UTF_8));
            var hex = new StringBuilder(digest.length * 2);
            for (var b : digest) {
                hex.append(String.format("%02x", b));
            }
            return "sha256:" + hex;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable.", e);
        }
    }

    private static String canonical(Object value) {
        if (value instanceof Map<?, ?> map) {
            var sorted = new TreeMap<String, Object>();
            map.forEach((key, child) -> sorted.put(String.valueOf(key), child));
            var builder = new StringBuilder("{");
            var first = true;
            for (var entry : sorted.entrySet()) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                builder.append(entry.getKey()).append(':').append(canonical(entry.getValue()));
            }
            return builder.append('}').toString();
        }
        if (value instanceof Iterable<?> iterable) {
            var builder = new StringBuilder("[");
            var first = true;
            for (var child : iterable) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                builder.append(canonical(child));
            }
            return builder.append(']').toString();
        }
        return String.valueOf(value);
    }
}
