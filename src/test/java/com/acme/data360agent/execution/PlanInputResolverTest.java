package com.acme.data360agent.execution;

import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.InputBinding;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationTransport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanInputResolverTest {
    @Test
    void resolvesIdentityResolutionOutputsIntoCalculatedInsightInputs() {
        var step = new PlanStep(
                "create_lifetime_value_insight",
                "Create LTV calculated insight",
                PlanPhase.SETUP,
                Data360Action.CREATE_CALCULATED_INSIGHT,
                Map.of(
                        "name", "Travel Customer Lifetime Value",
                        "transactionObjectApiName", "TravelItinerary",
                        "measure", Map.of("type", "SUM", "field", "transactionAmount")
                ),
                List.of("run_identity_resolution"),
                Map.of(
                        "unifiedProfileObjectApiName", new InputBinding("run_identity_resolution", "$.unifiedProfileObjectApiName"),
                        "unifiedProfileIdField", new InputBinding("run_identity_resolution", "$.unifiedProfileIdField")
                ),
                true
        );

        var resolved = PlanInputResolver.resolve(step, source -> Map.of(
                "unifiedProfileObjectApiName", "UnifiedIndividual",
                "unifiedProfileIdField", "UnifiedIndividualId"
        ));

        assertThat(resolved)
                .containsEntry("unifiedProfileObjectApiName", "UnifiedIndividual")
                .containsEntry("unifiedProfileIdField", "UnifiedIndividualId")
                .containsEntry("transactionObjectApiName", "TravelItinerary");
    }

    @Test
    void outputSelectorsExposeStableTopLevelOutputs() {
        var step = new PlanStep(
                "create_identity_resolution",
                "Create identity resolution",
                PlanPhase.SETUP,
                Data360Action.MCP_EXECUTE,
                Map.of(
                        "serverId", "data360",
                        "toolName", "d360_ir_create",
                        "effect", "write",
                        "params", Map.of(),
                        "outputSelectors", Map.of("rulesetId", "$.output.id")
                ),
                List.of(),
                Map.of(),
                true
        );

        var selected = StepOutputSelector.apply(step, Map.of("output", Map.of("id", "irs_123")));

        assertThat(selected)
                .containsEntry("rulesetId", "irs_123")
                .containsEntry("selected", Map.of("rulesetId", "irs_123"));
        assertThat(selected.get("selectorMetadata")).isInstanceOf(Map.class);
        assertThat(selected).doesNotContainKey("output");
    }

    @Test
    void outputSelectorsExposeRedactionMetadataForAuditAndUi() {
        var step = mcpStep("create_segment", Map.of("segmentId", "$.output.id"));
        var selected = StepOutputSelector.apply(step, Map.of("output", Map.of("id", "seg_123")));

        assertThat(selected.get("selectorMetadata"))
                .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.map(String.class, Object.class))
                .containsKey("segmentId");
        var selectorMetadata = (Map<?, ?>) ((Map<?, ?>) selected.get("selectorMetadata")).get("segmentId");
        assertThat(selectorMetadata.get("path")).isEqualTo("$.output.id");
        assertThat(selectorMetadata.get("redactionPolicy")).isEqualTo("reject-sensitive-and-oversized");
        assertThat(selectorMetadata.get("redacted")).isEqualTo(false);
    }

    @Test
    void outputSelectorsFailOnMissingPath() {
        var step = new PlanStep(
                "create_ci",
                "Create CI",
                PlanPhase.SETUP,
                Data360Action.MCP_EXECUTE,
                Map.of(
                        "serverId", "data360",
                        "toolName", "d360_ci_create",
                        "effect", "write",
                        "params", Map.of(),
                        "outputSelectors", Map.of("insightId", "$.output.id")
                ),
                List.of(),
                Map.of(),
                true
        );

        assertThatThrownBy(() -> StepOutputSelector.apply(step, Map.of("output", Map.of("apiName", "Travel_LTV__cio"))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Output selector path not found");
    }

    @Test
    void outputSelectorsRejectSensitiveResults() {
        var step = mcpStep("create_segment", Map.of("authorization", "$.output.authorization"));

        assertThatThrownBy(() -> StepOutputSelector.apply(step, Map.of("output", Map.of("authorization", "Bearer secret-token"))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sensitive data")
                .hasMessageContaining("authorization");
    }

    @Test
    void outputSelectorsRejectOversizedResults() {
        var step = mcpStep("create_segment", Map.of("payload", "$.output.payload"));

        assertThatThrownBy(() -> StepOutputSelector.apply(step, Map.of("output", Map.of("payload", "x".repeat(9000)))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("too large")
                .hasMessageContaining("payload");
    }

    @Test
    void outputSelectorsRejectContractMismatchAtExecution() {
        var step = mcpStep("create_segment", Map.of("segmentId", "$.output.segmentId"));
        var binding = new OperationBindingSnapshot(
                Data360Action.MCP_EXECUTE.resource() + "#create_segment",
                OperationTransport.MCP,
                "data360",
                "execute",
                "d360_segment_create",
                Effect.WRITE,
                true,
                Map.of(),
                Map.of("selectorContracts", Map.of("segmentId", "$.output.id")),
                null,
                "test"
        );

        assertThatThrownBy(() -> StepOutputSelector.apply(step, Map.of("output", Map.of("segmentId", "seg_1")), binding))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not match the tool contract");
    }

    private PlanStep mcpStep(String id, Map<String, String> outputSelectors) {
        return new PlanStep(
                id,
                "MCP step",
                PlanPhase.SETUP,
                Data360Action.MCP_EXECUTE,
                Map.of(
                        "serverId", "data360",
                        "toolName", "d360_segment_create",
                        "effect", "write",
                        "params", Map.of(),
                        "outputSelectors", outputSelectors
                ),
                List.of(),
                Map.of(),
                true
        );
    }
}
