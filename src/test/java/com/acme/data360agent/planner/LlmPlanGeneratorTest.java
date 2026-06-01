package com.acme.data360agent.planner;

import com.acme.data360agent.llm.LlmGateway;
import com.acme.data360agent.llm.LlmCompletion;
import com.acme.data360agent.mcp.McpServerSetting;
import com.acme.data360agent.mcp.McpSettings;
import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.plan.AslState;
import com.acme.data360agent.plan.AslStateMachine;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.InputBinding;
import com.acme.data360agent.operation.OperationBindingCatalog;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidator;
import com.acme.data360agent.scenario.ScenarioLibrary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LlmPlanGeneratorTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String CUSTOMER_SCENARIOS_RESOURCE = "/scenarios/customer-scenarios.json";
    private static final String DRAFT_FIXTURES_RESOURCE = "/scenarios/planner-draft-fixtures.json";

    @Test
    void fallbackGeneratesValidScenarioGroundedPlan() {
        var llm = mock(LlmGateway.class);
        when(llm.configured()).thenReturn(false);
        var operations = new OperationRegistry();
        var generator = new LlmPlanGenerator(llm, new ObjectMapper(), new ScenarioLibrary(), operations, mcpSettings());

        var plan = generator.generate(new PlanRequest(
                null,
                "Turn event engagement into pipeline with session attendance follow up",
                new PlanContext("org", "default", "sandbox")
        ));

        var validation = new PlanValidator(operations).validate(plan);

        assertThat(plan.scenarioId()).isEqualTo("salesforce_event_pipeline");
        assertThat(plan.steps()).anyMatch(step -> step.phase() == PlanPhase.DISCOVER);
        assertThat(plan.steps()).anyMatch(step -> step.phase() == PlanPhase.SETUP && step.needsApproval());
        assertThat(plan.steps()).anyMatch(step -> step.phase() == PlanPhase.MONITOR);
        assertThat(validation.ok()).as(validation.issues().toString()).isTrue();
    }

    @Test
    void fallbackGeneratesTravelDataCloudSetupPlan() {
        var llm = mock(LlmGateway.class);
        when(llm.configured()).thenReturn(false);
        var operations = new OperationRegistry();
        var generator = new LlmPlanGenerator(llm, new ObjectMapper(), new ScenarioLibrary(), operations, mcpSettings());

        var plan = generator.generate(new PlanRequest(
                null,
                "Use Snowflake DCBOOTCAMP CUSTOMER ITINERARY ITINERARY_ORDER and CRM Contact to unify travel customers, calculate lifetime value, and segment lifetime value greater than 10000",
                new PlanContext("org", "default", "sandbox")
        ));

        var validation = new PlanValidator(operations).validate(plan);

        assertThat(plan.scenarioId()).isEqualTo("travel_ltv_snowflake_crm");
        assertThat(plan.steps()).extracting(PlanStep::id).containsSubsequence(
                "create_customer_stream",
                "create_itinerary_stream",
                "create_itinerary_order_stream",
                "create_crm_contact_stream",
                "map_customer_to_individual",
                "map_contact_to_individual",
                "map_itinerary_to_travel_itinerary",
                "map_order_to_travel_itinerary",
                "create_identity_ruleset",
                "run_identity_resolution",
                "create_ltv_insight",
                "run_ltv_insight",
                "preview_audience",
                "create_segment"
        );
        assertThat(plan.steps())
                .filteredOn(step -> step.action() == Data360Action.CREATE_SNOWFLAKE_DATA_STREAM)
                .hasSize(3)
                .allSatisfy(step -> assertThat(step.needsApproval()).isTrue());
        assertThat(plan.definition().states().get("create_ltv_insight").parameters())
                .containsEntry("unifiedProfileObjectApiName.$", "$.run_identity_resolution.unifiedProfileObjectApiName")
                .containsEntry("unifiedProfileIdField.$", "$.run_identity_resolution.unifiedProfileIdField");
        assertThat(validation.ok()).as(validation.issues().toString()).isTrue();
    }

    @Test
    void plannerDraftsMatchGoldenFallbackAndLlmFixtures() throws Exception {
        for (var fixture : draftFixtures()) {
            var llm = mock(LlmGateway.class);
            when(llm.configured()).thenReturn(fixture.provider().equals("llm"));
            if (fixture.provider().equals("llm")) {
                when(llm.completeJson(anyString(), anyString()))
                        .thenReturn(new LlmCompletion("fixture", "deterministic-model", customerGoldenPlanJson(fixture.completionPlanScenarioId())));
            }
            var operations = new OperationRegistry();
            var generator = new LlmPlanGenerator(llm, OBJECT_MAPPER, new ScenarioLibrary(), operations, mcpSettings());
            var planner = new LangGraphData360Planner(generator, new PlanValidator(operations), new OperationBindingCatalog(operations));

            var draft = planner.draft(new PlanRequest(
                    fixture.scenarioId(),
                    fixture.goal(),
                    fixture.context()
            ));

            assertGoldenDraft(fixture, draft);
        }
    }

    @Test
    void plannerRepairsInvalidAslWithValidatorFeedback() {
        var llm = mock(LlmGateway.class);
        when(llm.configured()).thenReturn(true);
        when(llm.completeJson(anyString(), anyString()))
                .thenReturn(new LlmCompletion("mock", "model", invalidPlanJson()))
                .thenReturn(new LlmCompletion("mock", "model", repairedPlanJson()));
        var operations = new OperationRegistry();
        var generator = new LlmPlanGenerator(llm, new ObjectMapper(), new ScenarioLibrary(), operations, mcpSettings());
        var planner = new LangGraphData360Planner(generator, new PlanValidator(operations), new OperationBindingCatalog(operations));

        var draft = planner.draft(new PlanRequest(
                "fedex_dormant_reactivation",
                "Recover dormant high value accounts",
                new PlanContext("org", "default", "sandbox")
        ));

        assertThat(draft.validation().ok()).as(draft.validation().issues().toString()).isTrue();
        assertThat(draft.graphStages()).contains("repair_plan_1", "validate_repair_1");
        assertThat(draft.plan().definition().states().get("preview_audience").parameters())
                .containsEntry("limit", 100);
    }

    @Test
    void plannerRepairsSubmittedStepsMissingApproval() {
        assertPlannerRepair(
                missingApprovalPlan(),
                approvedCreateSegmentPlan(),
                draft -> {
                    assertThat(draft.plan().steps())
                            .singleElement()
                            .satisfies(step -> assertThat(step.needsApproval()).isTrue());
                    assertThat(draft.validation().issues()).isEmpty();
                }
        );
    }

    @Test
    void plannerRepairsInvalidMcpSelectors() {
        assertPlannerRepair(
                invalidMcpSelectorPlan(),
                validMcpSelectorPlan(),
                draft -> {
                    assertThat(draft.plan().steps())
                            .singleElement()
                            .satisfies(step -> assertThat(step.input().get("outputSelectors"))
                                    .isEqualTo(Map.of("dataspaces", "$.output.items")));
                    assertThat(draft.validation().issues()).isEmpty();
                }
        );
    }

    @Test
    void plannerRepairsForwardDependencyErrors() {
        assertPlannerRepair(
                forwardDependencyPlan(),
                orderedDependencyPlan(),
                draft -> {
                    assertThat(draft.plan().steps())
                            .extracting(PlanStep::id)
                            .containsExactly("create_segment", "publish_segment");
                    assertThat(draft.validation().issues()).isEmpty();
                }
        );
    }

    private void assertPlannerRepair(PlanSpec invalidPlan, PlanSpec repairedPlan, Consumer<PlanDraft> assertions) {
        var operations = new OperationRegistry();
        var validator = new PlanValidator(operations);
        assertThat(validator.validate(invalidPlan).ok()).isFalse();

        var llm = mock(LlmGateway.class);
        when(llm.configured()).thenReturn(true);
        when(llm.completeJson(anyString(), anyString()))
                .thenReturn(new LlmCompletion("fixture", "deterministic-model", json(invalidPlan)))
                .thenReturn(new LlmCompletion("fixture", "deterministic-model", json(repairedPlan)));
        var generator = new LlmPlanGenerator(llm, OBJECT_MAPPER, new ScenarioLibrary(), operations, mcpSettings());
        var planner = new LangGraphData360Planner(generator, validator, new OperationBindingCatalog(operations));

        var draft = planner.draft(new PlanRequest(
                "fedex_dormant_reactivation",
                "Repair a governed plan",
                new PlanContext("org", "default", "sandbox")
        ));

        assertThat(draft.graphStages()).contains("repair_plan_1", "validate_repair_1");
        assertThat(draft.validation().ok()).as(draft.validation().issues().toString()).isTrue();
        assertions.accept(draft);
    }

    private void assertGoldenDraft(DraftFixture fixture, PlanDraft draft) {
        assertThat(draft.validation().ok()).as(fixture.name() + " " + draft.validation().issues()).isEqualTo(fixture.expected().validationOk());
        assertThat(draft.plan().scenarioId()).isEqualTo(fixture.expected().scenarioId());
        assertThat(draft.graphStages()).containsExactlyElementsOf(fixture.expected().graphStages());
        assertThat(draft.plan().steps()).extracting(PlanStep::id).containsExactlyElementsOf(fixture.expected().stepIds());
        assertThat(draft.plan().steps()).extracting(step -> step.action().value()).containsExactlyElementsOf(fixture.expected().actions());
        assertThat(draft.plan().steps())
                .filteredOn(PlanStep::needsApproval)
                .extracting(PlanStep::id)
                .containsExactlyElementsOf(fixture.expected().approvalStepIds());
        assertThat(draft.artifacts()).extracting(PlanArtifact::type).containsExactlyElementsOf(fixture.expected().artifactTypes());
        assertThat(draft.artifacts())
                .filteredOn(artifact -> artifact.type().equals("plan_dag"))
                .singleElement()
                .satisfies(artifact -> {
                    assertThat(artifact.data().get("topologicalOrder")).isEqualTo(fixture.expected().stepIds());
                    assertThat(artifact.data().get("nodes")).as(fixture.name()).isInstanceOf(List.class);
                    assertThat((List<?>) artifact.data().get("nodes")).as(fixture.name()).hasSize(fixture.expected().stepIds().size());
                });
    }

    private String invalidPlanJson() {
        return """
                {
                  "schemaVersion": "data360-asl-profile-2026-05-31",
                  "id": "plan_bad_query",
                  "scenarioId": "fedex_dormant_reactivation",
                  "goal": "Recover dormant high value accounts",
                  "context": {"org": "org", "dataspace": "default", "environment": "sandbox"},
                  "definition": {
                    "Version": "1.0",
                    "QueryLanguage": "JSONPath",
                    "StartAt": "preview_audience",
                    "States": {
                      "preview_audience": {
                        "Type": "Task",
                        "Comment": "Preview audience",
                        "Resource": "urn:salesforce:data360:capability:query",
                        "Parameters": {"sql": "SELECT unified_individual_id FROM UnifiedIndividual"},
                        "ResultPath": "$.preview_audience",
                        "End": true
                      }
                    }
                  }
                }
                """;
    }

    private PlanSpec missingApprovalPlan() {
        var input = Map.<String, Object>of("name", "High LTV Travelers", "criteria", Map.of("source", "preview_audience"));
        return new PlanSpec(
                PlanSpec.CURRENT_SCHEMA_VERSION,
                "plan_missing_approval",
                "fedex_dormant_reactivation",
                "Repair missing approval",
                new PlanContext("org", "default", "sandbox"),
                singleState("create_segment", Data360Action.CREATE_SEGMENT, input),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        PlanPhase.SETUP,
                        Data360Action.CREATE_SEGMENT,
                        input,
                        List.of(),
                        Map.of(),
                        false
                ))
        );
    }

    private PlanSpec approvedCreateSegmentPlan() {
        var input = Map.<String, Object>of("name", "High LTV Travelers", "criteria", Map.of("source", "preview_audience"));
        return new PlanSpec(
                "plan_approved_create_segment",
                "fedex_dormant_reactivation",
                "Repair missing approval",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        PlanPhase.SETUP,
                        Data360Action.CREATE_SEGMENT,
                        input,
                        List.of(),
                        Map.of(),
                        true
                ))
        );
    }

    private PlanSpec invalidMcpSelectorPlan() {
        return new PlanSpec(
                "plan_bad_selector",
                "List dataspaces",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "list_dataspaces",
                        "List dataspaces",
                        PlanPhase.DISCOVER,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "data360",
                                "toolName", "d360_dataspace_list",
                                "effect", "read",
                                "params", Map.of(),
                                "outputSelectors", Map.of("rawPayload", "$.raw")
                        ),
                        List.of(),
                        Map.of(),
                        false
                ))
        );
    }

    private PlanSpec validMcpSelectorPlan() {
        return new PlanSpec(
                "plan_good_selector",
                "List dataspaces",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "list_dataspaces",
                        "List dataspaces",
                        PlanPhase.DISCOVER,
                        Data360Action.MCP_EXECUTE,
                        Map.of(
                                "serverId", "data360",
                                "toolName", "d360_dataspace_list",
                                "effect", "read",
                                "params", Map.of(),
                                "outputSelectors", Map.of("dataspaces", "$.output.items")
                        ),
                        List.of(),
                        Map.of(),
                        false
                ))
        );
    }

    private PlanSpec forwardDependencyPlan() {
        return new PlanSpec(
                PlanSpec.CURRENT_SCHEMA_VERSION,
                "plan_forward_dependency",
                "fedex_dormant_reactivation",
                "Publish after create",
                new PlanContext("org", "default", "sandbox"),
                new AslStateMachine(
                        AslStateMachine.VERSION,
                        AslStateMachine.QUERY_LANGUAGE,
                        "publish_segment",
                        Map.of(
                                "publish_segment", AslState.task(
                                        "Publish segment",
                                        Data360Action.PUBLISH_SEGMENT.resource(),
                                        Map.of("segmentId.$", "$.create_segment.segmentId"),
                                        "$.publish_segment",
                                        "create_segment",
                                        false
                                ),
                                "create_segment", AslState.task(
                                        "Create segment",
                                        Data360Action.CREATE_SEGMENT.resource(),
                                        Map.of("name", "High LTV Travelers", "criteria", Map.of("source", "preview_audience")),
                                        "$.create_segment",
                                        null,
                                        true
                                )
                        )
                ),
                List.of()
        );
    }

    private PlanSpec orderedDependencyPlan() {
        return new PlanSpec(
                "plan_ordered_dependency",
                "fedex_dormant_reactivation",
                "Publish after create",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "create_segment",
                                "Create segment",
                                PlanPhase.SETUP,
                                Data360Action.CREATE_SEGMENT,
                                Map.of("name", "High LTV Travelers", "criteria", Map.of("source", "preview_audience")),
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
    }

    private AslStateMachine singleState(String name, Data360Action action, Map<String, Object> input) {
        return new AslStateMachine(
                AslStateMachine.VERSION,
                AslStateMachine.QUERY_LANGUAGE,
                name,
                Map.of(name, AslState.task(name, action.resource(), input, "$." + name, null, true))
        );
    }

    private McpSettingsService mcpSettings() {
        var settings = mock(McpSettingsService.class);
        when(settings.current()).thenReturn(new McpSettings("org", java.util.List.of(
                new McpServerSetting("data360", "Salesforce Data 360 MCP", "Data 360", true, true, true, "d360", "d360")
        )));
        return settings;
    }

    private String repairedPlanJson() {
        return """
                {
                  "schemaVersion": "data360-asl-profile-2026-05-31",
                  "id": "plan_repaired_query",
                  "scenarioId": "fedex_dormant_reactivation",
                  "goal": "Recover dormant high value accounts",
                  "context": {"org": "org", "dataspace": "default", "environment": "sandbox"},
                  "definition": {
                    "Version": "1.0",
                    "QueryLanguage": "JSONPath",
                    "StartAt": "preview_audience",
                    "States": {
                      "preview_audience": {
                        "Type": "Task",
                        "Comment": "Preview audience",
                        "Resource": "urn:salesforce:data360:capability:query",
                        "Parameters": {"sql": "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100", "limit": 100},
                        "ResultPath": "$.preview_audience",
                        "End": true
                      }
                    }
                  }
                }
                """;
    }

    private static List<DraftFixture> draftFixtures() throws IOException {
        try (var stream = LlmPlanGeneratorTest.class.getResourceAsStream(DRAFT_FIXTURES_RESOURCE)) {
            assertThat(stream).isNotNull();
            return OBJECT_MAPPER.readValue(stream, new TypeReference<>() {
            });
        }
    }

    private static String customerGoldenPlanJson(String scenarioId) throws IOException {
        try (var stream = LlmPlanGeneratorTest.class.getResourceAsStream(CUSTOMER_SCENARIOS_RESOURCE)) {
            assertThat(stream).isNotNull();
            return StreamSupport.stream(OBJECT_MAPPER.readTree(Objects.requireNonNull(stream)).spliterator(), false)
                    .filter(node -> scenarioId.equals(node.path("scenario").path("id").asText()))
                    .map(node -> node.path("goldenPlan"))
                    .findFirst()
                    .map(LlmPlanGeneratorTest::json)
                    .orElseThrow();
        }
    }

    private static String json(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize test fixture.", e);
        }
    }

    private record DraftFixture(
            String name,
            String provider,
            String scenarioId,
            String goal,
            PlanContext context,
            String completionPlanScenarioId,
            ExpectedDraft expected
    ) {
    }

    private record ExpectedDraft(
            String scenarioId,
            boolean validationOk,
            List<String> graphStages,
            List<String> stepIds,
            List<String> actions,
            List<String> approvalStepIds,
            List<String> artifactTypes
    ) {
    }
}
