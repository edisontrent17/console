package com.acme.data360agent.operation;

import com.acme.data360agent.mcp.McpToolDescriptor;
import com.acme.data360agent.mcp.McpToolRegistrySnapshot;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanStep;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationCatalogTest {
    @Test
    void operationHashesAreDeterministicAcrossEquivalentBindings() {
        var definition = new OperationDefinition(
                Data360Action.QUERY,
                "Run limited Data 360 SQL preview",
                Effect.READ,
                false,
                false,
                "d360_query_sql",
                List.of(),
                List.of("sql")
        );

        var first = definition.binding();
        var second = new OperationBinding(
                new OperationDescriptor(
                        Data360Action.QUERY.value(),
                        Data360Action.QUERY.resource(),
                        "Run limited Data 360 SQL preview",
                        Effect.READ,
                        false,
                        false
                ),
                OperationTransport.MCP,
                "d360_query_sql",
                List.of(),
                List.of("sql")
        );

        assertThat(first.bindingHash()).isEqualTo(second.bindingHash());
        assertThat(OperationCatalogHash.canonicalJson(first)).isEqualTo(OperationCatalogHash.canonicalJson(second));
    }

    @Test
    void snapshotDefensivelyCopiesBindingsAndValidatesHash() {
        var bindings = new java.util.HashMap<String, OperationBinding>();
        bindings.put(Data360Action.QUERY.value(), new OperationRegistry().binding(Data360Action.QUERY));
        var snapshot = new OperationCatalogSnapshot(OperationCatalogSnapshot.CURRENT_SCHEMA_VERSION, bindings, null);
        var originalHash = snapshot.catalogHash();

        bindings.clear();

        assertThat(snapshot.bindings()).containsOnlyKeys(Data360Action.QUERY.value());
        assertThat(snapshot.catalogHash()).isEqualTo(originalHash);
        assertThatThrownBy(() -> snapshot.bindings().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new OperationCatalogSnapshot(snapshot.schemaVersion(), snapshot.bindings(), "bad-hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("catalogHash");
    }

    @Test
    void registryProvidesData360ActionBindingDefaults() {
        var registry = new OperationRegistry();

        for (var action : Data360Action.values()) {
            var definition = registry.require(action);
            var binding = registry.binding(action);

            assertThat(binding.descriptor().id()).isEqualTo(action.value());
            assertThat(binding.descriptor().capabilityResource()).isEqualTo(action.resource());
            assertThat(binding.descriptor().requiresApproval()).isEqualTo(action.requiresApproval());
            assertThat(binding.transport()).isEqualTo(OperationTransport.MCP);
            assertThat(binding.operationName()).isEqualTo(definition.mcpOperation());
            assertThat(binding.requiredAnyOf()).isEqualTo(definition.requiredAnyOf());
            assertThat(binding.requiredAllOf()).isEqualTo(definition.requiredAllOf());
        }

        assertThat(registry.allBindings()).hasSize(Data360Action.values().length);
        assertThat(registry.snapshot().bindings()).hasSize(Data360Action.values().length);
    }

    @Test
    void identityResolutionRunDeclaresOutputsNeededByCalculatedInsights() {
        var registry = new OperationRegistry();

        var irBinding = registry.bindingFor(Data360Action.RUN_IDENTITY_RESOLUTION);
        var ciDefinition = registry.require(Data360Action.CREATE_CALCULATED_INSIGHT);

        @SuppressWarnings("unchecked")
        var outputProperties = (Map<String, Object>) irBinding.outputSchema().get("properties");
        assertThat(outputProperties).containsKey("unifiedProfileObjectApiName");
        assertThat(outputProperties).containsKey("unifiedProfileIdField");
        assertThat(ciDefinition.acceptsInput("unifiedProfileObjectApiName")).isTrue();
        assertThat(ciDefinition.acceptsInput("unifiedProfileIdField")).isTrue();
    }

    @Test
    void genericMcpExecuteBindingsFreezePerStepToolAndEffect() {
        var first = new PlanStep(
                "list_dataspaces",
                "List dataspaces",
                PlanPhase.DISCOVER,
                Data360Action.MCP_EXECUTE,
                Map.of("serverId", "data360", "toolName", "d360_dataspace_list", "effect", "read", "params", Map.of()),
                List.of(),
                Map.of(),
                false
        );
        var second = new PlanStep(
                "create_segment",
                "Create segment",
                PlanPhase.SETUP,
                Data360Action.MCP_EXECUTE,
                Map.of("serverId", "data360", "toolName", "d360_segment_create", "effect", "write", "params", Map.of()),
                List.of(),
                Map.of(),
                true
        );

        var firstBinding = OperationBindingSnapshot.mcpExecute(first);
        var secondBinding = OperationBindingSnapshot.mcpExecute(second);

        assertThat(firstBinding.resource()).isEqualTo(Data360Action.MCP_EXECUTE.resource() + "#list_dataspaces");
        assertThat(secondBinding.resource()).isEqualTo(Data360Action.MCP_EXECUTE.resource() + "#create_segment");
        assertThat(firstBinding.underlyingTool()).isEqualTo("d360_dataspace_list");
        assertThat(secondBinding.underlyingTool()).isEqualTo("d360_segment_create");
        assertThat(firstBinding.effect()).isEqualTo(Effect.READ);
        assertThat(secondBinding.effect()).isEqualTo(Effect.WRITE);
        assertThat(secondBinding.requiresApproval()).isTrue();
    }

    @Test
    void bindingResolverOwnsPerStepMcpResourceKeys() {
        var step = new PlanStep(
                "list_dataspaces",
                "List dataspaces",
                PlanPhase.DISCOVER,
                Data360Action.MCP_EXECUTE,
                Map.of("serverId", "data360", "toolName", "d360_dataspace_list", "effect", "read", "params", Map.of()),
                List.of(),
                Map.of(),
                false
        );

        var binding = OperationBindingResolver.snapshotFor(new OperationRegistry(), step);

        assertThat(OperationBindingResolver.resourceFor(step)).isEqualTo(Data360Action.MCP_EXECUTE.resource() + "#list_dataspaces");
        assertThat(binding.resource()).isEqualTo(OperationBindingResolver.resourceFor(step));
    }

    @Test
    void mcpExecuteBindingsCarryRegistryPayloadMetadata() {
        var step = new PlanStep(
                "create_segment",
                "Create segment",
                PlanPhase.SETUP,
                Data360Action.MCP_EXECUTE,
                Map.of("serverId", "data360", "toolName", "d360_segment_create", "effect", "write", "params", Map.of()),
                List.of(),
                Map.of(),
                true
        );
        var descriptor = new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object", "required", List.of("displayName")),
                Map.of("type", "object", "properties", Map.of("segmentId", "string")),
                List.of(Map.of("displayName", "High Value Customers")),
                Map.of("segmentId", "$.output.id")
        );
        var registry = new McpToolRegistrySnapshot(
                Instant.parse("2026-06-01T00:00:00Z"),
                List.of(descriptor),
                List.of(new McpToolRegistrySnapshot.McpServerRegistryStatus("data360", "passed", "ok", 1, true, "sha256:connector-data360"))
        );

        var binding = OperationBindingSnapshot.mcpExecute(step, registry);

        assertThat(binding.parameterSchema()).containsEntry("toolInputSchema", descriptor.inputSchema());
        assertThat(binding.parameterSchema()).containsEntry("toolExamples", descriptor.examples());
        assertThat(binding.parameterSchema()).containsEntry("selectorContracts", descriptor.selectorContracts());
        assertThat(binding.outputSchema()).containsEntry("toolOutputSchema", descriptor.outputSchema());
        assertThat(binding.outputSchema()).containsEntry("selectorContracts", descriptor.selectorContracts());
        assertThat(binding.toolSchemaHash()).isEqualTo(descriptor.schemaHash());
        assertThat(binding.connectorDefinitionHash()).isEqualTo("sha256:connector-data360");
        assertThat(binding.auditSummary()).containsEntry("connectorDefinitionHash", "sha256:connector-data360");
    }

    @Test
    void typedData360ActionsFreezeLiveMcpDescriptorMetadata() {
        var descriptor = new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object", "required", List.of("displayName")),
                Map.of("type", "object", "properties", Map.of("segmentId", "string")),
                List.of(Map.of("displayName", "High Value Customers")),
                Map.of("segmentId", "$.id")
        );
        var registry = new McpToolRegistrySnapshot(
                Instant.parse("2026-06-01T00:00:00Z"),
                List.of(descriptor),
                List.of(new McpToolRegistrySnapshot.McpServerRegistryStatus("data360", "passed", "ok", 1, true, "sha256:connector-data360"))
        );
        var plan = new com.acme.data360agent.plan.PlanSpec(
                "plan_typed_snapshot",
                "Create segment",
                new com.acme.data360agent.plan.PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        PlanPhase.SETUP,
                        Data360Action.CREATE_SEGMENT,
                        Map.of("name", "High Value Customers", "criteria", Map.of()),
                        List.of(),
                        Map.of(),
                        true
                ))
        );

        var binding = OperationBindingResolver.snapshotsFor(new OperationRegistry(), plan, registry).getFirst();

        assertThat(binding.resource()).isEqualTo(Data360Action.CREATE_SEGMENT.resource());
        assertThat(binding.registryHash()).isEqualTo(registry.registryHash());
        assertThat(binding.toolSchemaHash()).isEqualTo(descriptor.schemaHash());
        assertThat(binding.connectorDefinitionHash()).isEqualTo("sha256:connector-data360");
        assertThat(binding.parameterSchema()).containsEntry("toolInputSchema", descriptor.inputSchema());
        assertThat(binding.outputSchema()).containsEntry("toolOutputSchema", descriptor.outputSchema());
    }
}
