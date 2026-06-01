package com.acme.data360agent.mcp;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class McpToolRegistryServiceTest {
    private final McpToolRegistryService registry = new McpToolRegistryService(null, null);

    @Test
    void classifiesToolEffectsFromNames() {
        assertThat(registry.classify("d360_dataspace_list")).isEqualTo("read");
        assertThat(registry.classify("d360_standard_mapping_preview")).isEqualTo("read");
        assertThat(registry.classify("run_snowflake_query")).isEqualTo("read");
        assertThat(registry.classify("d360_ci_create")).isEqualTo("write");
        assertThat(registry.classify("d360_segment_publish")).isEqualTo("write");
        assertThat(registry.classify("drop_table")).isEqualTo("destructive");
    }

    @Test
    void snapshotHashIsStableForSameTools() {
        var tools = List.of(new McpToolDescriptor(
                "data360",
                "d360_dataspace_list",
                "List dataspaces",
                "read",
                Map.of("type", "object", "properties", Map.of())
        ));
        var servers = List.of(new McpToolRegistrySnapshot.McpServerRegistryStatus("data360", "passed", "ok", 1));

        var first = new McpToolRegistrySnapshot(Instant.parse("2026-06-01T00:00:00Z"), tools, servers);
        var second = new McpToolRegistrySnapshot(Instant.parse("2026-06-01T01:00:00Z"), tools, servers);

        assertThat(first.registryHash()).startsWith("sha256:");
        assertThat(first.registryHash()).isEqualTo(second.registryHash());
    }

    @Test
    void snapshotHashChangesWhenConnectorDefinitionChanges() {
        var tools = List.of(new McpToolDescriptor(
                "data360",
                "d360_dataspace_list",
                "List dataspaces",
                "read",
                Map.of("type", "object", "properties", Map.of())
        ));
        var first = new McpToolRegistrySnapshot(
                Instant.parse("2026-06-01T00:00:00Z"),
                tools,
                List.of(new McpToolRegistrySnapshot.McpServerRegistryStatus("data360", "passed", "ok", 1, true, "sha256:connector-v1"))
        );
        var second = new McpToolRegistrySnapshot(
                Instant.parse("2026-06-01T00:00:00Z"),
                tools,
                List.of(new McpToolRegistrySnapshot.McpServerRegistryStatus("data360", "passed", "ok", 1, true, "sha256:connector-v2"))
        );

        assertThat(first.registryHash()).isNotEqualTo(second.registryHash());
        assertThat(first.serverStatus("data360")).get().extracting("connectorDefinitionHash").isEqualTo("sha256:connector-v1");
    }

    @Test
    void descriptorsCarryExamplesAndSelectorContracts() {
        var descriptor = new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object"),
                Map.of("type", "object", "properties", Map.of("segmentId", "string")),
                List.of(Map.of("displayName", "High Value Customers")),
                Map.of("segmentId", "$.output.id")
        );
        var sameContractDifferentExample = new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object"),
                Map.of("type", "object", "properties", Map.of("segmentId", "string")),
                List.of(Map.of("displayName", "Dormant Customers")),
                Map.of("segmentId", "$.output.id")
        );
        var changedContract = new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object"),
                Map.of("type", "object", "properties", Map.of("segmentId", "string")),
                List.of(Map.of("displayName", "High Value Customers")),
                Map.of("segmentApiName", "$.output.apiName")
        );

        assertThat(descriptor.examples()).containsExactly(Map.of("displayName", "High Value Customers"));
        assertThat(descriptor.selectorContracts()).containsEntry("segmentId", "$.output.id");
        assertThat(descriptor.schemaHash()).isEqualTo(sameContractDifferentExample.schemaHash());
        assertThat(descriptor.schemaHash()).isNotEqualTo(changedContract.schemaHash());
    }
}
