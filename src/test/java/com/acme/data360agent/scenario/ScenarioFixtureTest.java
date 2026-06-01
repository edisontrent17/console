package com.acme.data360agent.scenario;

import com.acme.data360agent.data360.Data360CallResult;
import com.acme.data360agent.data360.Data360Client;
import com.acme.data360agent.execution.InMemoryPlanStore;
import com.acme.data360agent.execution.LocalPlanExecutor;
import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.execution.PlanRunSupport;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.execution.RunStatus;
import com.acme.data360agent.execution.StepStatus;
import com.acme.data360agent.mcp.McpToolDescriptor;
import com.acme.data360agent.mcp.McpToolRegistrySnapshot;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationBindingResolver;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.operation.OperationTransport;
import com.acme.data360agent.plan.AslStateMachine;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.plan.PlanValidator;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.support.SensitiveData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

class ScenarioFixtureTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String FIXTURE_RESOURCE = "/scenarios/customer-scenarios.json";

    private final ScenarioLibrary library = new ScenarioLibrary();
    private final PlanValidator validator = new PlanValidator(new OperationRegistry());

    @Test
    void fixturesMirrorScenarioLibraryMetadata() throws Exception {
        var fixtures = fixtures();
        var libraryIds = library.all().stream().map(CustomerScenario::id).toList();

        assertThat(fixtures)
                .extracting(fixture -> fixture.scenario().id())
                .containsExactlyElementsOf(libraryIds);
        for (var fixture : fixtures) {
            assertThat(fixture.scenario())
                    .as(fixture.scenario().id())
                    .isEqualTo(library.byId(fixture.scenario().id()).orElseThrow());
        }
    }

    @Test
    void goldenPlansUseCanonicalAslWireShape() throws Exception {
        for (var fixture : rawFixtures()) {
            var plan = fixture.path("goldenPlan");
            var definition = plan.path("definition");
            var states = definition.path("States");
            var scenarioId = fixture.path("scenario").path("id").asText();

            assertThat(plan.has("steps")).as(scenarioId).isFalse();
            assertThat(plan.path("schemaVersion").asText()).as(scenarioId).isEqualTo(PlanSpec.CURRENT_SCHEMA_VERSION);
            assertThat(plan.path("scenarioId").asText()).as(scenarioId).isEqualTo(scenarioId);
            assertThat(definition.path("Version").asText()).as(scenarioId).isEqualTo(AslStateMachine.VERSION);
            assertThat(definition.path("QueryLanguage").asText()).as(scenarioId).isEqualTo(AslStateMachine.QUERY_LANGUAGE);
            assertThat(states.has(definition.path("StartAt").asText())).as(scenarioId).isTrue();

            states.fields().forEachRemaining(entry -> {
                var state = entry.getValue();
                var transitionCount = (state.hasNonNull("Next") ? 1 : 0) + (state.path("End").asBoolean(false) ? 1 : 0);

                assertThat(state.path("Type").asText()).as(entry.getKey()).isEqualTo("Task");
                assertThat(state.path("Resource").asText()).as(entry.getKey()).startsWith("urn:salesforce:data360:capability:");
                assertThat(state.path("ResultPath").asText()).as(entry.getKey()).isEqualTo("$." + entry.getKey());
                assertThat(state.path("Parameters").isObject()).as(entry.getKey()).isTrue();
                assertThat(transitionCount).as(entry.getKey()).isEqualTo(1);
            });
        }
    }

    @Test
    void everyGoldenPlanValidatesAndCoversScenarioRequiredActions() throws Exception {
        for (var fixture : fixtures()) {
            var plan = fixture.goldenPlan();
            var result = validator.validate(plan);
            var plannedActions = plan.steps().stream().map(PlanStep::action).toList();

            assertThat(result.ok())
                    .as("%s validation issues: %s", fixture.scenario().id(), result.issues())
                    .isTrue();
            assertThat(plan.id()).as(fixture.scenario().id()).isEqualTo("plan_" + fixture.scenario().id());
            assertThat(plan.scenarioId()).as(fixture.scenario().id()).isEqualTo(fixture.scenario().id());
            assertThat(plan.goal()).as(fixture.scenario().id()).isIn(fixture.scenario().defaultUtterances());
            assertThat(plan.definition().states()).as(fixture.scenario().id()).hasSize(plan.steps().size());
            assertThat(plannedActions).as(fixture.scenario().id()).containsAll(fixture.scenario().requiredActions());
            assertThat(plan.steps())
                    .filteredOn(step -> step.action().requiresApproval())
                    .as(fixture.scenario().id())
                    .allSatisfy(step -> assertThat(step.needsApproval()).as(step.id()).isTrue());
        }
    }

    @Test
    void travelLtvFixturePreservesPromptThresholdAndRuntimeBindings() throws Exception {
        var fixture = fixtures().stream()
                .filter(item -> item.scenario().id().equals("travel_ltv_snowflake_crm"))
                .findFirst()
                .orElseThrow();
        var plan = fixture.goldenPlan();

        assertThat(fixture.scenario().previewSql()).contains("lifetime_value > 10000");
        assertThat(fixture.scenario().sourceSystems())
                .anySatisfy(source -> assertThat(source.tables()).contains("CUSTOMER", "ITINERARY", "ITINERARY_ORDER"))
                .anySatisfy(source -> assertThat(source.tables()).contains("Contact"));
        assertThat(plan.steps())
                .filteredOn(step -> step.id().equals("create_lifetime_value_insight"))
                .singleElement()
                .satisfies(step -> assertThat(step.inputBindings())
                        .containsKeys("unifiedProfileObjectApiName", "unifiedProfileIdField"));
        assertThat(plan.steps())
                .filteredOn(step -> step.id().equals("preview_audience"))
                .singleElement()
                .satisfies(step -> assertThat(String.valueOf(step.input().get("sql"))).contains("lifetime_value > 10000"));
    }

    @Test
    void travelLtvFixtureProvidesExpectedSelectedOutputsForRuntimeBindings() throws Exception {
        var fixture = fixtures().stream()
                .filter(item -> item.scenario().id().equals("travel_ltv_snowflake_crm"))
                .findFirst()
                .orElseThrow();
        var outputs = fixture.expectedSelectedOutputs();

        assertThat(outputs).containsKeys(
                "create_identity_ruleset",
                "run_identity_resolution",
                "create_lifetime_value_insight",
                "preview_audience",
                "create_segment",
                "create_activation"
        );
        assertThat(outputs.get("run_identity_resolution"))
                .containsEntry("unifiedProfileObjectApiName", "UnifiedIndividual")
                .containsEntry("unifiedProfileIdField", "UnifiedIndividualId");
        assertThat(outputs.get("preview_audience"))
                .containsEntry("rowCount", 2);
        assertThat((List<?>) outputs.get("preview_audience").get("rows"))
                .allSatisfy(row -> assertThat(((Number) ((Map<?, ?>) row).get("lifetime_value")).intValue() > 10_000).isTrue());

        fixture.goldenPlan().steps().forEach(step -> step.inputBindings().values().forEach(binding -> {
            var field = binding.path().substring(2);

            assertThat(outputs)
                    .as("fixture output for %s -> %s", step.id(), binding.fromStep())
                    .containsKey(binding.fromStep());
            assertThat(outputs.get(binding.fromStep()))
                    .as("fixture selected field for %s -> %s.%s", step.id(), binding.fromStep(), field)
                    .containsKey(field);
        }));
    }

    @Test
    void travelLtvApprovedMcpSnapshotsCoverDiscoverySetupActivationAndMonitorTools() throws Exception {
        var fixture = fixtures().stream()
                .filter(item -> item.scenario().id().equals("travel_ltv_snowflake_crm"))
                .findFirst()
                .orElseThrow();
        var operations = new OperationRegistry();
        var registrySnapshot = registrySnapshotFor(fixture.goldenPlan(), operations);

        var bindings = OperationBindingResolver.snapshotsFor(operations, fixture.goldenPlan(), registrySnapshot);

        assertThat(bindings)
                .allSatisfy(binding -> {
                    assertThat(binding.transport()).isEqualTo(OperationTransport.MCP);
                    assertThat(binding.registryHash()).startsWith("sha256:");
                    assertThat(binding.toolSchemaHash()).startsWith("sha256:");
                    assertThat(binding.connectorDefinitionHash()).startsWith("sha256:");
                });
        assertThat(bindings)
                .extracting("underlyingTool")
                .contains(
                        "d360_metadata_describe",
                        "d360_query_sql",
                        "d360_datastream_create_snowflake",
                        "d360_datastream_create_sfdc",
                        "d360_dmo_mapping_create",
                        "d360_ir_create",
                        "d360_ir_run",
                        "d360_ci_create",
                        "d360_segment_create",
                        "d360_segment_publish",
                        "d360_activation_create",
                        "d360_monitor_metric"
                );
        assertThat(bindings)
                .filteredOn(binding -> List.of("d360_segment_create", "d360_segment_publish", "d360_activation_create").contains(binding.underlyingTool()))
                .allSatisfy(binding -> assertThat(binding.requiresApproval()).isTrue());
    }

    @Test
    void travelLtvSelectedOutputsReplayThroughDeterministicLocalExecutor() throws Exception {
        var fixture = fixtures().stream()
                .filter(item -> item.scenario().id().equals("travel_ltv_snowflake_crm"))
                .findFirst()
                .orElseThrow();
        var operations = new OperationRegistry();
        var validation = new PlanValidationResult(List.of());
        var bindings = OperationBindingResolver.snapshotsFor(operations, fixture.goldenPlan(), registrySnapshotFor(fixture.goldenPlan(), operations));
        var approved = ApprovedExecutablePlan.approve(
                new PlanDraft(fixture.goldenPlan(), validation, List.of("fixture_replay"), bindings),
                validation,
                "scenario-test"
        );
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, operations, replayClient(fixture.expectedSelectedOutputs()));

        var run = executor.start("org_fixture", approved);
        var completedRun = approveUntilFinished(store, executor, run);

        assertThat(completedRun.getStatus()).isEqualTo(RunStatus.SUCCEEDED);
        fixture.expectedSelectedOutputs().forEach((stepId, expectedOutput) -> {
            var stepRun = PlanRunSupport.stepRun(completedRun, stepId);
            assertThat(stepRun.getStatus()).as(stepId).isEqualTo(StepStatus.SUCCEEDED);
            assertThat(stepRun.getOutput()).as(stepId).containsAllEntriesOf(SensitiveData.redactMap(expectedOutput));
        });
    }

    private static List<ScenarioFixture> fixtures() throws IOException {
        try (var stream = ScenarioFixtureTest.class.getResourceAsStream(FIXTURE_RESOURCE)) {
            assertThat(stream).isNotNull();
            return OBJECT_MAPPER.readValue(stream, new TypeReference<>() {
            });
        }
    }

    private static List<JsonNode> rawFixtures() throws IOException {
        try (var stream = ScenarioFixtureTest.class.getResourceAsStream(FIXTURE_RESOURCE)) {
            assertThat(stream).isNotNull();
            return StreamSupport.stream(OBJECT_MAPPER.readTree(Objects.requireNonNull(stream)).spliterator(), false)
                    .toList();
        }
    }

    private McpToolRegistrySnapshot registrySnapshotFor(PlanSpec plan, OperationRegistry operations) {
        var tools = plan.steps().stream()
                .map(PlanStep::action)
                .distinct()
                .map(operations::require)
                .map(definition -> new McpToolDescriptor(
                        "data360",
                        definition.mcpOperation(),
                        definition.label(),
                        definition.effect().name().toLowerCase(Locale.ROOT),
                        Map.of(
                                "type", "object",
                                "properties", definition.inputFields(),
                                "required", definition.requiredAllOf()
                        ),
                        Map.of(
                                "type", "object",
                                "properties", definition.outputFields()
                        ),
                        List.of(),
                        Map.of()
                ))
                .toList();
        return new McpToolRegistrySnapshot(
                Instant.parse("2026-06-01T00:00:00Z"),
                tools,
                List.of(new McpToolRegistrySnapshot.McpServerRegistryStatus("data360", "passed", "ok", tools.size(), true, "sha256:fixture-data360-connector"))
        );
    }

    private Data360Client replayClient(Map<String, Map<String, Object>> expectedSelectedOutputs) {
        return new Data360Client() {
            @Override
            public Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
                return new Data360CallResult(
                        expectedSelectedOutputs.getOrDefault(step.id(), Map.of("status", "SUCCEEDED")),
                        Map.of("stepId", step.id(), "operation", operation.mcpOperation())
                );
            }

            @Override
            public Data360CallResult call(OperationDefinition operation, OperationBindingSnapshot binding, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
                return call(operation, step, resolvedInput, context);
            }
        };
    }

    private PlanRun approveUntilFinished(InMemoryPlanStore store, LocalPlanExecutor executor, PlanRun startedRun) throws InterruptedException {
        var deadline = System.nanoTime() + java.time.Duration.ofSeconds(8).toNanos();
        PlanRun run = startedRun;
        while (System.nanoTime() < deadline) {
            run = store.run("org_fixture", startedRun.getId()).orElseThrow();
            if (run.getStatus() == RunStatus.SUCCEEDED || run.getStatus() == RunStatus.FAILED || run.getStatus() == RunStatus.CANCELED) {
                return run;
            }
            if (run.getStatus() == RunStatus.WAITING_APPROVAL) {
                var waitingStep = run.getSteps().stream()
                        .filter(step -> step.getStatus() == StepStatus.WAITING_APPROVAL)
                        .findFirst()
                        .orElseThrow();
                executor.approveStep("org_fixture", run.getId(), waitingStep.getStepId(), "scenario-test");
            }
            Thread.sleep(25);
        }
        return store.run("org_fixture", startedRun.getId()).orElseThrow();
    }

    private record ScenarioFixture(
            CustomerScenario scenario,
            PlanSpec goldenPlan,
            Map<String, Map<String, Object>> expectedSelectedOutputs
    ) {
        @Override
        public Map<String, Map<String, Object>> expectedSelectedOutputs() {
            return expectedSelectedOutputs == null ? Map.of() : expectedSelectedOutputs;
        }
    }
}
