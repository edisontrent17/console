package com.acme.data360agent.plan;

import com.acme.data360agent.mcp.McpToolDescriptor;
import com.acme.data360agent.mcp.McpToolRegistryService;
import com.acme.data360agent.mcp.McpToolRegistrySnapshot;
import com.acme.data360agent.operation.OperationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PlanValidatorTest {
    private final PlanValidator validator = new PlanValidator(new OperationRegistry());

    @Test
    void rejectsUnsupportedSchemaVersion() {
        var plan = new PlanSpec(
                "1900-01-01",
                "plan_test",
                null,
                "Preview records",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview records",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Unsupported PlanSpec schemaVersion"));
    }

    @Test
    void rejectsRawToolOrUrlResourcesInAslDefinition() {
        var plan = new PlanSpec(
                PlanSpec.CURRENT_SCHEMA_VERSION,
                "plan_bad_resource",
                null,
                "Bad resource",
                new PlanContext("org", "default", "sandbox"),
                new AslStateMachine(
                        "1.0",
                        "JSONPath",
                        "call_tool",
                        Map.of("call_tool", AslState.task(
                                "Call a raw tool",
                                "https://example.com/services/data",
                                Map.of("body", Map.of()),
                                "$.call_tool",
                                null,
                                true
                        ))
                ),
                List.of()
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Task Resource must be a Data 360 capability URI"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "search",
            "execute",
            "d360_segment_create",
            "arn:aws:lambda:us-east-1:123456789012:function:data360"
    })
    void rejectsRawToolNamesAndArnsAsAslResources(String resource) {
        var plan = new PlanSpec(
                PlanSpec.CURRENT_SCHEMA_VERSION,
                "plan_bad_resource",
                null,
                "Bad resource",
                new PlanContext("org", "default", "sandbox"),
                new AslStateMachine(
                        "1.0",
                        "JSONPath",
                        "raw_call",
                        Map.of("raw_call", AslState.task(
                                "Call raw operation",
                                resource,
                                Map.of("query", "UnifiedIndividual"),
                                "$.raw_call",
                                null,
                                true
                        ))
                ),
                List.of()
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Task Resource must be a Data 360 capability URI"));
    }

    @Test
    void acceptsGovernedMcpExecuteTaskWithSelectors() {
        var plan = new PlanSpec(
                PlanSpec.CURRENT_SCHEMA_VERSION,
                "plan_mcp_execute",
                null,
                "List dataspaces",
                new PlanContext("org", "default", "sandbox"),
                new AslStateMachine(
                        "1.0",
                        "JSONPath",
                        "list_dataspaces",
                        Map.of("list_dataspaces", AslState.task(
                                "List dataspaces",
                                Data360Action.MCP_EXECUTE.resource(),
                                Map.of(
                                        "serverId", "data360",
                                        "toolName", "d360_dataspace_list",
                                        "effect", "read",
                                        "params", Map.of(),
                                        "outputSelectors", Map.of("dataspaces", "$.output.items")
                                ),
                                "$.list_dataspaces",
                                null,
                                true
                        ))
                ),
                List.of()
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).as(result.issues().toString()).isTrue();
    }

    @Test
    void rejectsRawMcpResource() {
        var plan = new PlanSpec(
                PlanSpec.CURRENT_SCHEMA_VERSION,
                "plan_raw_mcp",
                null,
                "Bad MCP resource",
                new PlanContext("org", "default", "sandbox"),
                new AslStateMachine(
                        "1.0",
                        "JSONPath",
                        "raw_mcp",
                        Map.of("raw_mcp", AslState.task(
                                "Raw MCP",
                                "mcp://data360/execute",
                                Map.of("toolName", "d360_dataspace_list"),
                                "$.raw_mcp",
                                null,
                                true
                        ))
                ),
                List.of()
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("not raw mcp://"));
    }

    @Test
    void requiresApprovalForMutatingMcpExecuteTask() {
        var plan = new PlanSpec(
                "plan_mcp_write",
                "Create segment via MCP",
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
                                "params", Map.of("displayName", "High LTV Travelers")
                        ),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("require needsApproval=true"));
    }

    @Test
    void rejectsMcpExecuteToolMissingFromRegistry() {
        var validator = validatorWithRegistry(new McpToolDescriptor(
                "data360",
                "d360_dataspace_list",
                "List dataspaces",
                "read",
                Map.of("type", "object", "properties", Map.of())
        ));
        var plan = new PlanSpec(
                "plan_mcp_unknown",
                "Unknown MCP tool",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "unknown_tool",
                        "Unknown tool",
                        PlanPhase.DISCOVER,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "data360",
                                "toolName", "d360_not_real",
                                "effect", "read",
                                "params", Map.of()
                        ),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("MCP tool is not available"));
    }

    @Test
    void validatesMcpExecuteEffectAndRequiredParamsAgainstRegistry() {
        var validator = validatorWithRegistry(new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of(
                        "type", "object",
                        "required", List.of("displayName"),
                        "properties", Map.of("displayName", Map.of("type", "string"))
                )
        ));
        var plan = new PlanSpec(
                "plan_mcp_registry",
                "Create segment via registry",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        PlanPhase.SETUP,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "data360",
                                "toolName", "d360_segment_create",
                                "effect", "read",
                                "params", Map.of()
                        ),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("effect understates registry effect"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("missing required tool field"));
    }

    @Test
    void pureValidationSkipsLiveRegistryChecksButKeepsPlanSpecRules() {
        var validator = validatorWithRegistry(new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of(
                        "type", "object",
                        "required", List.of("displayName"),
                        "properties", Map.of("displayName", Map.of("type", "string"))
                )
        ));
        var plan = new PlanSpec(
                "plan_mcp_pure",
                "Create segment via registry",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        PlanPhase.SETUP,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "data360",
                                "toolName", "d360_segment_create",
                                "effect", "read",
                                "params", Map.of(),
                                "outputSelectors", Map.of("segmentId", "$.output.id")
                        ),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var pure = validator.validatePure(plan);
        var registryBacked = validator.validate(plan);

        assertThat(pure.issues()).noneMatch(issue -> issue.message().contains("registry effect"));
        assertThat(pure.issues()).noneMatch(issue -> issue.message().contains("missing required tool field"));
        assertThat(registryBacked.issues()).anyMatch(issue -> issue.message().contains("registry effect"));
        assertThat(registryBacked.issues()).anyMatch(issue -> issue.message().contains("missing required tool field"));
    }

    @Test
    void rejectsGenericMcpFacadeOverridesAndMutatingSql() {
        var plan = new PlanSpec(
                "plan_mcp_sql",
                "Bad generic SQL",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "query_sql",
                        "Query through MCP",
                        PlanPhase.DISCOVER,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "data360",
                                "toolName", "d360_query_sql",
                                "facadeTool", "search",
                                "effect", "read",
                                "params", Map.of("sql", "SELECT Id FROM UnifiedIndividual UNION DROP TABLE UnifiedIndividual")
                        ),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("facadeTool must be execute"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("read-only"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("LIMIT"));
    }

    @Test
    void rejectsMcpExecuteAgainstDiscoveryOnlyServer() {
        var validator = validatorWithRegistryStatus(
                new McpToolRegistrySnapshot.McpServerRegistryStatus("snowflake", "passed", "ok", 1, false),
                new McpToolDescriptor(
                        "snowflake",
                        "run_snowflake_query",
                        "Run Snowflake query",
                        "read",
                        Map.of("type", "object", "properties", Map.of())
                )
        );
        var plan = new PlanSpec(
                "plan_mcp_discovery_only",
                "Use discovery-only server",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "list_tables",
                        "List tables",
                        PlanPhase.DISCOVER,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "snowflake",
                                "toolName", "run_snowflake_query",
                                "effect", "read",
                                "params", Map.of("query", "SELECT table_name FROM information_schema.tables LIMIT 10")
                        ),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("discovery-only"));
    }

    @Test
    void rejectsMcpExecutePlaceholdersAndBadSelectors() {
        var plan = new PlanSpec(
                "plan_mcp_bad",
                "Bad MCP",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_ci",
                        "Create CI",
                        PlanPhase.SETUP,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "data360",
                                "toolName", "d360_ci_create",
                                "effect", "write",
                                "params", Map.of("expression", "${approvedSql}"),
                                "outputSelectors", Map.of("bad/selector", "id")
                        ),
                        List.of(),
                        Map.of(),
                        true
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Unresolved placeholder"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Output selector names"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Output selector path must start with $."));
    }

    @Test
    void rejectsMcpSelectorsOutsideDeclaredToolContract() {
        var validator = validatorWithRegistry(new McpToolDescriptor(
                "data360",
                "d360_segment_create",
                "Create segment",
                "write",
                Map.of("type", "object", "properties", Map.of("displayName", Map.of("type", "string"))),
                Map.of("type", "object", "properties", Map.of("segmentId", "string")),
                List.of(),
                Map.of("segmentId", "$.output.id")
        ));
        var plan = new PlanSpec(
                "plan_mcp_bad_selector_contract",
                "Create segment",
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
                                "outputSelectors", Map.of(
                                        "segmentId", "$.output.segmentId",
                                        "rawPayload", "$.raw"
                                )
                        ),
                        List.of(),
                        Map.of(),
                        true
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("too broad or unsafe"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("must match the MCP tool contract"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("not declared by the MCP tool contract"));
    }

    @Test
    void allowsMcpInputBindingsOnlyThroughDeclaredSelectors() {
        var plan = mcpSelectorBindingPlan("$.selected.segmentId");

        var result = validator.validate(plan);

        assertThat(result.ok()).as(result.issues().toString()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"$.raw", "$.raw.id", "$.text", "$.output.id", "$.selected.segmentId.id"})
    void rejectsMcpInputBindingsToRawTextOutputOrNestedSelectorPaths(String path) {
        var plan = mcpSelectorBindingPlan(path);

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Input binding references output not produced"));
    }

    @Test
    void rejectsSubmittedStepsThatConflictWithAslDefinition() {
        var plan = new PlanSpec(
                PlanSpec.CURRENT_SCHEMA_VERSION,
                "plan_conflict",
                null,
                "Conflicting ASL and steps",
                new PlanContext("org", "default", "sandbox"),
                new AslStateMachine(
                        "1.0",
                        "JSONPath",
                        "preview",
                        Map.of("preview", AslState.task(
                                "Preview records",
                                Data360Action.QUERY.resource(),
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                "$.preview",
                                null,
                                true
                        ))
                ),
                List.of(new PlanStep(
                        "preview",
                        "Preview records",
                        Data360Action.CREATE_SEGMENT,
                        Map.of("name", "Should Not Win", "criteria", Map.of()),
                        List.of(),
                        true
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Submitted steps must match the ASL definition-derived steps"));
    }

    @Test
    void acceptsIdentityResolutionOutputsFeedingCalculatedInsightInputs() {
        var plan = identityResolutionToCalculatedInsightPlan(
                "unifiedProfileObjectApiName.$",
                "$.run_identity_resolution.unifiedProfileObjectApiName"
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).as(result.issues().toString()).isTrue();
    }

    @Test
    void rejectsBindingToOutputMissingFromSourceCapabilityContract() {
        var plan = identityResolutionToCalculatedInsightPlan(
                "unifiedProfileObjectApiName.$",
                "$.run_identity_resolution.notARealOutput"
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("not produced by data360.identityResolution.run"));
    }

    @Test
    void rejectsBindingToNestedPathBelowScalarSourceOutput() {
        var plan = identityResolutionToCalculatedInsightPlan(
                "unifiedProfileObjectApiName.$",
                "$.run_identity_resolution.unifiedProfileObjectApiName.notARealNestedField"
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("not produced by data360.identityResolution.run"));
    }

    @Test
    void rejectsBindingToInputMissingFromTargetCapabilityContract() {
        var plan = identityResolutionToCalculatedInsightPlan(
                "unknownCiInput.$",
                "$.run_identity_resolution.unifiedProfileObjectApiName"
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Input binding target is not declared for data360.calculatedInsight.create"));
    }

    @Test
    void rejectsQueryWithoutLimit() {
        var plan = new PlanSpec(
                "plan_test",
                "Preview records",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview records",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual"),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("LIMIT"));
    }

    @Test
    void requiresApprovalForPublish() {
        var plan = new PlanSpec(
                "plan_test",
                "Publish segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "publish",
                        "Publish segment",
                        Data360Action.PUBLISH_SEGMENT,
                        Map.of("segmentId", "seg_123"),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("needsApproval=true"));
    }

    @Test
    void rejectsMonitorMetricOutsideMonitorPhase() {
        var plan = new PlanSpec(
                "plan_test",
                "Monitor metric",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "monitor",
                        "Monitor metric",
                        PlanPhase.SETUP,
                        Data360Action.MONITOR_METRIC,
                        Map.of("metric", "activation_rate", "cadence", "daily", "threshold", Map.of("operator", "<", "value", 0.13)),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("phase=monitor"));
    }

    @Test
    void validatesSimpleInputBindingsOnly() {
        var plan = new PlanSpec(
                "plan_test",
                "Bind inputs",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "preview",
                                "Preview records",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                false
                        ),
                        new PlanStep(
                                "publish",
                                "Publish segment",
                                PlanPhase.SETUP,
                                Data360Action.PUBLISH_SEGMENT,
                                Map.of(),
                                List.of("preview"),
                                Map.of("segmentId", new InputBinding("preview", "segmentId")),
                                true
                        )
                )
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("path must start with $."));
    }

    @Test
    void rejectsMutationActionsInMonitorPhase() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad monitor",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment later",
                        PlanPhase.MONITOR,
                        Data360Action.CREATE_SEGMENT,
                        Map.of("name", "Bad Segment", "criteria", Map.of()),
                        List.of(),
                        Map.of(),
                        true
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Monitor phase currently allows only"));
    }

    @Test
    void rejectsMalformedMonitorThresholds() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad threshold",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "monitor",
                        "Monitor metric",
                        PlanPhase.MONITOR,
                        Data360Action.MONITOR_METRIC,
                        Map.of("metric", "activation_rate", "cadence", "daily", "threshold", Map.of("operator", "around", "value", "0.13")),
                        List.of(),
                        Map.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("operator must be one of"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("value must be numeric"));
    }

    @Test
    void rejectsTooManySteps() {
        var steps = java.util.stream.IntStream.rangeClosed(1, 21)
                .mapToObj(index -> new PlanStep(
                        "preview_" + index,
                        "Preview records",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                        List.of(),
                        false
                ))
                .toList();
        var plan = new PlanSpec(
                "plan_test",
                "Too many previews",
                new PlanContext("org", "default", "sandbox"),
                steps
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("more than 20 steps"));
    }

    @Test
    void rejectsBlankDuplicateAndMalformedStepIds() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad ids",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "1_bad",
                                "Preview records",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                false
                        ),
                        new PlanStep(
                                "1_bad",
                                "Preview again",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                false
                        ),
                        new PlanStep(
                                "   ",
                                "Blank id",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                false
                        )
                )
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("start with a letter"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Duplicate step id"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Step id is required"));
    }

    @Test
    void rejectsOversizedInputAndRawUrls() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad input",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "search",
                        "Search",
                        Data360Action.SEARCH,
                        Map.of("query", "https://example.com/" + "x".repeat(8_200)),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("cannot exceed 8192"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Raw URL-like input"));
    }

    @Test
    void rejectsMutationKeywordsInQuerySql() {
        var plan = new PlanSpec(
                "plan_test",
                "Mutating query",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview records",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT id FROM UnifiedIndividual UNION DROP TABLE UnifiedIndividual LIMIT 10"),
                        List.of(),
                        false
                ))
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("read-only"));
    }

    @Test
    void rejectsBadSegmentNameAndBlankActivationDestination() {
        var plan = new PlanSpec(
                "plan_test",
                "Bad names",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "create_segment",
                                "Create segment",
                                Data360Action.CREATE_SEGMENT,
                                Map.of("name", "Bad/Segment", "criteria", Map.of()),
                                List.of(),
                                true
                        ),
                        new PlanStep(
                                "create_activation",
                                "Create activation",
                                Data360Action.CREATE_ACTIVATION,
                                Map.of("name", "Activation", "segmentId", "seg_123", "destination", "   "),
                                List.of(),
                                true
                        )
                )
        );

        var result = validator.validate(plan);

        assertThat(result.ok()).isFalse();
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Segment name must be 1-80"));
        assertThat(result.issues()).anyMatch(issue -> issue.message().contains("Activation destination must be nonblank"));
    }

    private PlanSpec identityResolutionToCalculatedInsightPlan(String dynamicInputKey, String dynamicPath) {
        return new PlanSpec(
                PlanSpec.CURRENT_SCHEMA_VERSION,
                "plan_ir_to_ci",
                null,
                "Create LTV on unified data",
                new PlanContext("org", "default", "sandbox"),
                new AslStateMachine(
                        "1.0",
                        "JSONPath",
                        "create_identity_ruleset",
                        Map.of(
                                "create_identity_ruleset", AslState.task(
                                        "Create identity ruleset",
                                        Data360Action.CREATE_IDENTITY_RULESET.resource(),
                                        Map.of(
                                                "name", "Travel Customer Identity Ruleset",
                                                "rules", List.of(Map.of("name", "Exact external id", "fields", List.of("externalCustomerId")))
                                        ),
                                        "$.create_identity_ruleset",
                                        "run_identity_resolution",
                                        false
                                ),
                                "run_identity_resolution", AslState.task(
                                        "Run identity resolution",
                                        Data360Action.RUN_IDENTITY_RESOLUTION.resource(),
                                        Map.of("rulesetId.$", "$.create_identity_ruleset.rulesetId"),
                                        "$.run_identity_resolution",
                                        "create_lifetime_value_insight",
                                        false
                                ),
                                "create_lifetime_value_insight", AslState.task(
                                        "Create LTV calculated insight",
                                        Data360Action.CREATE_CALCULATED_INSIGHT.resource(),
                                        Map.of(
                                                "name", "Travel Customer Lifetime Value",
                                                dynamicInputKey, dynamicPath,
                                                "unifiedProfileIdField.$", "$.run_identity_resolution.unifiedProfileIdField",
                                                "transactionObjectApiName", "TravelItinerary",
                                                "transactionCustomerKeyField", "externalCustomerId",
                                                "measure", Map.of("type", "SUM", "field", "transactionAmount", "alias", "lifetime_value")
                                        ),
                                        "$.create_lifetime_value_insight",
                                        null,
                                        true
                                )
                        )
                ),
                List.of()
        );
    }

    private PlanSpec mcpSelectorBindingPlan(String bindingPath) {
        return new PlanSpec(
                "plan_mcp_selector_binding",
                "Publish selected MCP segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
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
                        ),
                        new PlanStep(
                                "publish_segment",
                                "Publish segment",
                                PlanPhase.SETUP,
                                Data360Action.PUBLISH_SEGMENT,
                                Map.of(),
                                List.of("create_segment"),
                                Map.of("segmentId", new InputBinding("create_segment", bindingPath)),
                                true
                        )
                )
        );
    }

    private PlanValidator validatorWithRegistry(McpToolDescriptor... tools) {
        return validatorWithRegistryStatus(
                new McpToolRegistrySnapshot.McpServerRegistryStatus("data360", "passed", "ok", tools.length, true),
                tools
        );
    }

    private PlanValidator validatorWithRegistryStatus(McpToolRegistrySnapshot.McpServerRegistryStatus status, McpToolDescriptor... tools) {
        return new PlanValidator(new OperationRegistry(), new McpToolRegistryService(null, null) {
            @Override
            public McpToolRegistrySnapshot current() {
                return new McpToolRegistrySnapshot(
                        Instant.parse("2026-06-01T00:00:00Z"),
                        List.of(tools),
                        List.of(status)
                );
            }
        });
    }
}
