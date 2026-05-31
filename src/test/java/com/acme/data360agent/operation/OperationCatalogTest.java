package com.acme.data360agent.operation;

import com.acme.data360agent.plan.Data360Action;
import org.junit.jupiter.api.Test;

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
}
