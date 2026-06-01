package com.acme.data360agent.data360;

import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.mcp.McpLaunchConfiguration;
import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.mcp.McpToolDescriptor;
import com.acme.data360agent.mcp.McpToolRegistryService;
import com.acme.data360agent.mcp.McpToolRegistrySnapshot;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.operation.OperationTransport;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanStep;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class McpData360ClientTest {
    @Test
    void resolvesLaunchConfigurationFromRunOrganization() {
        var settings = mock(McpSettingsService.class);
        when(settings.launchConfigurationFor("org_run", "data360")).thenReturn(Optional.empty());
        var client = new McpData360Client(settings, new ObjectMapper());
        var operation = new OperationRegistry().require(Data360Action.QUERY);
        var step = new PlanStep(
                "query_preview",
                "Preview records",
                PlanPhase.DISCOVER,
                Data360Action.QUERY,
                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                List.of(),
                Map.of(),
                false
        );

        assertThatThrownBy(() -> client.call(
                operation,
                step,
                step.input(),
                new RunContext("org_run", "run_1", "plan_1", new PlanContext("org", "default", "sandbox"))
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("this organization");

        verify(settings).launchConfigurationFor("org_run", "data360");
        verify(settings, never()).launchConfigurationFor("data360");
    }

    @Test
    void blocksGenericMcpExecutionWhenApprovedRegistryHashDrifts() {
        var settings = mock(McpSettingsService.class);
        when(settings.launchConfigurationFor("org_run", "data360"))
                .thenReturn(Optional.of(new McpLaunchConfiguration("node", List.of("--version"), Map.of(), List.of(), null)));
        var registry = mock(McpToolRegistryService.class);
        var approvedSnapshot = snapshot(new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object", "required", List.of("displayName"), "properties", Map.of("displayName", Map.of("type", "string")))
        ));
        var currentSnapshot = snapshot(new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object", "required", List.of("displayName", "segmentType"), "properties", Map.of("displayName", Map.of("type", "string"), "segmentType", Map.of("type", "string")))
        ));
        when(registry.current("org_run")).thenReturn(currentSnapshot);
        var client = new McpData360Client(settings, new ObjectMapper(), registry);
        var step = new PlanStep(
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
        );
        var binding = OperationBindingSnapshot.mcpExecute(step, approvedSnapshot);

        assertThatThrownBy(() -> client.call(
                new OperationRegistry().require(Data360Action.MCP_EXECUTE),
                binding,
                step,
                step.input(),
                new RunContext("org_run", "run_1", "plan_1", new PlanContext("org", "default", "sandbox"))
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("registry drift");
    }

    @Test
    void blocksTypedData360ExecutionWhenApprovedRegistryHashDrifts() {
        var settings = mock(McpSettingsService.class);
        when(settings.launchConfigurationFor("org_run", "data360"))
                .thenReturn(Optional.of(new McpLaunchConfiguration("node", List.of("--version"), Map.of(), List.of(), null)));
        var registry = mock(McpToolRegistryService.class);
        var approvedSnapshot = snapshot(new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object", "required", List.of("displayName"), "properties", Map.of("displayName", Map.of("type", "string")))
        ));
        var currentSnapshot = snapshot(new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object", "required", List.of("displayName", "segmentType"), "properties", Map.of("displayName", Map.of("type", "string"), "segmentType", Map.of("type", "string")))
        ));
        when(registry.current("org_run")).thenReturn(currentSnapshot);
        var client = new McpData360Client(settings, new ObjectMapper(), registry);
        var operation = new OperationRegistry().require(Data360Action.CREATE_SEGMENT);
        var binding = OperationBindingSnapshot.data360Mcp(operation, approvedSnapshot);
        var step = new PlanStep(
                "create_segment",
                "Create segment",
                PlanPhase.SETUP,
                Data360Action.CREATE_SEGMENT,
                Map.of("name", "High LTV Travelers", "criteria", Map.of()),
                List.of(),
                Map.of(),
                true
        );

        assertThatThrownBy(() -> client.call(
                operation,
                binding,
                step,
                step.input(),
                new RunContext("org_run", "run_1", "plan_1", new PlanContext("org", "default", "sandbox"))
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("registry drift");
    }

    @Test
    void blocksExecutionWhenApprovedConnectorDefinitionDrifts() {
        var settings = mock(McpSettingsService.class);
        when(settings.launchConfigurationFor("org_run", "data360"))
                .thenReturn(Optional.of(new McpLaunchConfiguration("node", List.of("--version"), Map.of(), List.of(), null)));
        var registry = mock(McpToolRegistryService.class);
        when(registry.current("org_run")).thenReturn(snapshot(new McpToolDescriptor(
                "data360",
                "d360_dataspace_list",
                "List dataspaces",
                "read",
                Map.of("type", "object")
        ), "sha256:current-connector"));
        var client = new McpData360Client(settings, new ObjectMapper(), registry);
        var step = new PlanStep(
                "list_dataspaces",
                "List dataspaces",
                PlanPhase.DISCOVER,
                Data360Action.MCP_EXECUTE,
                Map.of(
                        "serverId", "data360",
                        "toolName", "d360_dataspace_list",
                        "effect", "read",
                        "params", Map.of()
                ),
                List.of(),
                Map.of(),
                false
        );
        var binding = new OperationBindingSnapshot(
                Data360Action.MCP_EXECUTE.resource() + "#list_dataspaces",
                OperationTransport.MCP,
                "data360",
                "execute",
                "d360_dataspace_list",
                Effect.READ,
                false,
                Map.of("type", "object"),
                Map.of("type", "object"),
                null,
                OperationBindingSnapshot.DEFAULT_BINDING_VERSION,
                "",
                "",
                "sha256:approved-connector"
        );

        assertThatThrownBy(() -> client.call(
                new OperationRegistry().require(Data360Action.MCP_EXECUTE),
                binding,
                step,
                step.input(),
                new RunContext("org_run", "run_1", "plan_1", new PlanContext("org", "default", "sandbox"))
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("connector definition drift");
    }

    private McpToolRegistrySnapshot snapshot(McpToolDescriptor tool) {
        return snapshot(tool, "");
    }

    private McpToolRegistrySnapshot snapshot(McpToolDescriptor tool, String connectorDefinitionHash) {
        return new McpToolRegistrySnapshot(
                Instant.parse("2026-06-01T00:00:00Z"),
                List.of(tool),
                List.of(new McpToolRegistrySnapshot.McpServerRegistryStatus(tool.serverId(), "passed", "ok", 1, true, connectorDefinitionHash))
        );
    }
}
