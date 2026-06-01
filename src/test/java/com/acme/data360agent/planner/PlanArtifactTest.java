package com.acme.data360agent.planner;

import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.InputBinding;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationTransport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PlanArtifactTest {
    @Test
    void buildsDeterministicDagFromPlanSpec() {
        var plan = new PlanSpec(
                "plan_dag",
                "Create and publish",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "create_segment",
                                "Create better segment",
                                PlanPhase.SETUP,
                                Data360Action.CREATE_SEGMENT,
                                Map.of("name", "High LTV", "criteria", Map.of()),
                                List.of(),
                                Map.of(),
                                true
                        ),
                        new PlanStep(
                                "publish_segment",
                                "Publish segment",
                                PlanPhase.SETUP,
                                Data360Action.PUBLISH_SEGMENT,
                                Map.of(),
                                List.of("create_segment"),
                                Map.of("segmentId", new InputBinding("create_segment", "$.segmentId")),
                                true
                        )
                )
        );

        var artifact = PlanArtifact.planDag(plan, new PlanValidationResult(List.of()));

        assertThat(artifact.type()).isEqualTo("plan_dag");
        assertThat(artifact.data().get("topologicalOrder")).isEqualTo(List.of("create_segment", "publish_segment"));
        assertThat((List<?>) artifact.data().get("nodes")).hasSize(2);
        assertThat((List<?>) artifact.data().get("edges"))
                .anySatisfy(edge -> assertThat(((Map<?, ?>) edge).get("type")).isEqualTo("control"))
                .anySatisfy(edge -> assertThat(((Map<?, ?>) edge).get("type")).isEqualTo("data"));
    }

    @Test
    void includesMcpExecutableMetadataInDagNodes() {
        var plan = new PlanSpec(
                "plan_mcp_dag",
                "Create segment through MCP",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        PlanPhase.SETUP,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "data360",
                                "toolName", "d360_segment_create",
                                "effect", "write",
                                "params", Map.of("displayName", "High LTV Travelers"),
                                "outputSelectors", Map.of("segmentId", "$.output.id")
                        ),
                        List.of(),
                        Map.of(),
                        true
                ))
        );

        var artifact = PlanArtifact.planDag(plan, new PlanValidationResult(List.of()));
        var node = (Map<?, ?>) ((List<?>) artifact.data().get("nodes")).getFirst();

        assertThat(node.get("effect")).isEqualTo("write");
        assertThat(node.get("bindingResource")).isEqualTo(Data360Action.MCP_EXECUTE.resource() + "#create_segment");
        assertThat(node.get("outputSelectors")).isEqualTo(List.of("segmentId"));
    }

    @Test
    void usesApprovedBindingHashesInDagNodesWhenProvided() {
        var plan = new PlanSpec(
                "plan_approved_dag",
                "Create segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        PlanPhase.SETUP,
                        Data360Action.CREATE_SEGMENT,
                        Map.of("name", "High LTV", "criteria", Map.of()),
                        List.of(),
                        Map.of(),
                        true
                ))
        );
        var approvedBinding = new OperationBindingSnapshot(
                Data360Action.CREATE_SEGMENT.resource(),
                OperationTransport.MCP,
                "data360",
                "execute",
                "d360_segment_create",
                Effect.WRITE,
                true,
                Map.of("type", "object", "toolInputSchema", Map.of("type", "object")),
                Map.of("type", "object", "toolOutputSchema", Map.of("type", "object")),
                "sha256:approved-binding-hash",
                "test"
        );

        var artifact = PlanArtifact.planDag(plan, new PlanValidationResult(List.of()), List.of(approvedBinding));
        var node = (Map<?, ?>) ((List<?>) artifact.data().get("nodes")).getFirst();

        assertThat(node.get("taskSnapshotHash")).isEqualTo("sha256:approved-binding-hash");
    }

    @Test
    void diffsDagNodesEdgesAndSnapshotHashChanges() {
        var before = PlanArtifact.planDag(new PlanSpec(
                "plan_dag_diff",
                "Create segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        PlanPhase.SETUP,
                        Data360Action.CREATE_SEGMENT,
                        Map.of("name", "High LTV", "criteria", Map.of()),
                        List.of(),
                        Map.of(),
                        true
                ))
        ), new PlanValidationResult(List.of()));
        var after = PlanArtifact.planDag(new PlanSpec(
                "plan_dag_diff",
                "Create and publish segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "create_segment",
                                "Create better segment",
                                PlanPhase.SETUP,
                                Data360Action.CREATE_SEGMENT,
                                Map.of("name", "Very High LTV", "criteria", Map.of()),
                                List.of(),
                                Map.of(),
                                true
                        ),
                        new PlanStep(
                                "publish_segment",
                                "Publish segment",
                                PlanPhase.SETUP,
                                Data360Action.PUBLISH_SEGMENT,
                                Map.of(),
                                List.of("create_segment"),
                                Map.of("segmentId", new InputBinding("create_segment", "$.segmentId")),
                                true
                        )
                )
        ), new PlanValidationResult(List.of()));

        var diff = PlanDagDiff.between(List.of(before), List.of(after));

        assertThat(diff.empty()).isFalse();
        assertThat(diff.addedNodes()).containsExactly("publish_segment");
        assertThat(diff.changedNodes()).contains("create_segment");
        assertThat(diff.addedEdges()).anyMatch(edge -> edge.contains("create_segment->publish_segment"));
    }
}
