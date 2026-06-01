package com.acme.data360agent.planner;

import com.acme.data360agent.llm.LlmGateway;
import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.InputBinding;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.scenario.CustomerScenario;
import com.acme.data360agent.scenario.ScenarioLibrary;
import com.acme.data360agent.support.Ids;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

@Component
public class LlmPlanGenerator {
    private final LlmGateway llm;
    private final ObjectMapper objectMapper;
    private final ScenarioLibrary scenarios;
    private final OperationRegistry operations;
    private final McpSettingsService mcpSettings;

    public LlmPlanGenerator(LlmGateway llm, ObjectMapper objectMapper, ScenarioLibrary scenarios, OperationRegistry operations, McpSettingsService mcpSettings) {
        this.llm = llm;
        this.objectMapper = objectMapper;
        this.scenarios = scenarios;
        this.operations = operations;
        this.mcpSettings = mcpSettings;
    }

    public PlanSpec generate(PlanRequest request) {
        var scenario = scenarios.resolve(request.scenarioId(), request.goal());
        if (!llm.configured()) {
            return fallback(request, scenario);
        }

        var system = """
                You draft small, auditable Data 360 PlanSpec JSON.
                Return only JSON. No markdown. No commentary.

                PlanSpec is the governance contract. It is a governed Amazon States Language profile that Temporal executes.

                Use only the allowed Data 360 capability resources listed in the prompt.

                Rules:
                - Emit definition.Version="1.0" and definition.QueryLanguage="JSONPath".
                - Use only Task states.
                - Every state name must be snake_case or lower camel case and start with a letter.
                - Use Resource as a stable Data 360 capability URI, never a tool name.
                - Use Parameters for human-reviewable domain parameters, not raw HTTP payloads.
                - Use ResultPath exactly as "$.<stateName>".
                - Use Next for ordered flow and End=true on the final state.
                - Every query must be SELECT-only and include LIMIT.
                - Use urn:salesforce:data360:capability:activation.run only if the user explicitly asks to run, send, or execute an activation.
                - Use dynamic Parameters such as "rulesetId.$": "$.create_identity_ruleset.rulesetId" when a step needs prior output.
                - If identity resolution output is needed by calculated insights, bind "unifiedProfileObjectApiName.$" and "unifiedProfileIdField.$" from the identityResolution.run result.
                - Prefer semantic calculated insight parameters for runtime model outputs. Do not concatenate SQL with runtime object names.
                - Do not include raw MCP tool names, API URLs, AWS ARNs, Credentials, prompts, loops, code, Retry, Catch, Map, Parallel, timers, or arbitrary expressions.
                - If step output is needed, prefer domain references like segmentIdFromStep, activationIdFromStep, insightIdFromStep, criteriaFromStep, or queryFromStep.
                - Keep monitors read-only. A monitor can recommend follow-up later, but it must not silently mutate Data 360.
                """;
        var user = """
                Draft a PlanSpec for this request.

                Required JSON shape:
                {
                  "schemaVersion": "data360-asl-profile-2026-05-31",
                  "id": "plan_<short id>",
                  "scenarioId": "...",
                  "goal": "...",
                  "context": {
                    "org": "...",
                    "dataspace": "...",
                    "environment": "sandbox|production"
                  },
                  "definition": {
                    "Version": "1.0",
                    "QueryLanguage": "JSONPath",
                    "StartAt": "inspect_model",
                    "States": {
                      "inspect_model": {
                        "Type": "Task",
                        "Comment": "Inspect Data 360 metadata",
                        "Resource": "urn:salesforce:data360:capability:metadata.describe",
                        "Parameters": {"objects": ["UnifiedIndividual", "Segment", "Activation"]},
                        "ResultPath": "$.inspect_model",
                        "Next": "preview_audience"
                      },
                      "preview_audience": {
                        "Type": "Task",
                        "Comment": "Preview the candidate audience",
                        "Resource": "urn:salesforce:data360:capability:query",
                        "Parameters": {"sql": "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100", "limit": 100},
                        "ResultPath": "$.preview_audience",
                        "End": true
                      }
                    }
                  }
                }

                Allowed capability resources:
                %s

                Enabled MCP servers:
                %s

                Ground this plan in the selected public customer scenario:
                %s

                User goal: %s
                Context: %s
                """.formatted(allowedResources(), enabledMcpServers(), toJson(scenario), request.goal(), request.context());

        var completion = llm.completeJson(system, user);
        try {
            return objectMapper.readValue(extractJson(completion.text()), PlanSpec.class);
        } catch (Exception e) {
            throw new IllegalStateException("%s returned invalid PlanSpec JSON: %s".formatted(completion.provider(), completion.text()), e);
        }
    }

    public boolean canRepair() {
        return llm.configured();
    }

    public PlanSpec repair(PlanRequest request, PlanSpec invalidPlan, PlanValidationResult validation) {
        if (!llm.configured()) {
            return invalidPlan;
        }
        var system = """
                You repair Data 360 PlanSpec JSON.
                Return only JSON. No markdown. No commentary.

                The corrected PlanSpec must use the governed Amazon States Language profile:
                - schemaVersion must be data360-asl-profile-2026-05-31.
                - definition.Version must be "1.0".
                - definition.QueryLanguage must be "JSONPath".
                - Use only Task states.
                - Use only allowed Data 360 capability Resource URIs.
                - ResultPath must be "$.<stateName>".
                - Use exactly one of Next or End=true per Task state.
                - Dynamic Parameters ending in ".$" must reference declared outputs from earlier steps.
                - When calculated insights depend on identity resolution runtime output, bind the IR output fields instead of hard-coding unified object names.
                - Do not include raw MCP tool names, API URLs, AWS ARNs, Credentials, Retry, Catch, Map, Parallel, code, or arbitrary expressions.
                - Preserve the user's goal and scenario unless they are clearly malformed.
                """;
        var user = """
                Repair this PlanSpec so every validator passes.

                Validation issues:
                %s

                Allowed capability resources:
                %s

                Enabled MCP servers:
                %s

                Original request:
                %s

                Invalid PlanSpec:
                %s
                """.formatted(toJson(validation), allowedResources(), enabledMcpServers(), toJson(request), toJson(invalidPlan));

        var completion = llm.completeJson(system, user);
        try {
            return objectMapper.readValue(extractJson(completion.text()), PlanSpec.class);
        } catch (Exception e) {
            throw new IllegalStateException("%s returned invalid repaired PlanSpec JSON: %s".formatted(completion.provider(), completion.text()), e);
        }
    }

    private PlanSpec fallback(PlanRequest request, CustomerScenario scenario) {
        var lowerGoal = request.goal().toLowerCase();
        var wantsRunActivation = lowerGoal.contains("run activation")
                || lowerGoal.contains("execute activation")
                || lowerGoal.contains("send activation");
        var steps = new ArrayList<PlanStep>();
        steps.add(new PlanStep(
                "inspect_model",
                "Inspect Data 360 metadata for the goal",
                PlanPhase.DISCOVER,
                Data360Action.METADATA_DESCRIBE,
                Map.of("objects", List.of(scenario.audienceEntity(), "Engagement", "Segment", "Activation")),
                List.of(),
                Map.of(),
                false
        ));
        var previewDependency = "inspect_model";
        if ("travel_ltv_snowflake_crm".equals(scenario.id())) {
            previewDependency = addTravelLtvSetupSteps(steps, request, scenario);
        }
        steps.add(new PlanStep(
                "preview_audience",
                "Preview the candidate audience",
                PlanPhase.DISCOVER,
                Data360Action.QUERY,
                Map.of("sql", scenario.previewSql(), "limit", 100),
                List.of(previewDependency),
                Map.of(),
                false
        ));
        if (scenario.requiredActions().contains(Data360Action.CREATE_CALCULATED_INSIGHT) && !stepsContain(steps, "create_ltv_insight")) {
            steps.add(new PlanStep(
                    "create_goal_insight",
                    "Create goal scoring calculated insight",
                    PlanPhase.SETUP,
                    Data360Action.CREATE_CALCULATED_INSIGHT,
                    Map.of(
                            "name", scenario.name().replaceAll("[^A-Za-z0-9]+", " ").trim() + " Score",
                            "definition", "Score " + scenario.audienceEntity() + " records for " + scenario.name().toLowerCase() + " using trusted engagement signals."
                    ),
                    List.of("preview_audience"),
                    Map.of(),
                    true
            ));
        }
        steps.add(new PlanStep(
                "create_segment",
                "Create the Data 360 audience segment",
                PlanPhase.SETUP,
                Data360Action.CREATE_SEGMENT,
                Map.of(
                        "name", scenario.segmentName(),
                        "description", scenario.sourceSummary(),
                        "criteriaFromStep", "preview_audience"
                ),
                List.of("preview_audience"),
                Map.of(),
                true
        ));
        steps.add(new PlanStep(
                "publish_segment",
                "Publish the segment",
                PlanPhase.SETUP,
                Data360Action.PUBLISH_SEGMENT,
                Map.of("segmentIdFromStep", "create_segment"),
                List.of("create_segment"),
                Map.of(),
                true
        ));
        steps.add(new PlanStep(
                "create_activation",
                "Create activation draft",
                PlanPhase.SETUP,
                Data360Action.CREATE_ACTIVATION,
                Map.of(
                        "name", scenario.activationName(),
                        "destination", scenario.activationDestination(),
                        "segmentIdFromStep", "create_segment"
                ),
                List.of("publish_segment"),
                Map.of(),
                true
        ));
        if (wantsRunActivation) {
            steps.add(new PlanStep(
                    "run_activation",
                    "Run activation",
                    PlanPhase.SETUP,
                    Data360Action.RUN_ACTIVATION,
                    Map.of("activationIdFromStep", "create_activation"),
                    List.of("create_activation"),
                    Map.of(),
                    true
            ));
        }
        steps.add(new PlanStep(
                "monitor_goal",
                "Monitor goal health",
                PlanPhase.MONITOR,
                Data360Action.MONITOR_METRIC,
                Map.of(
                        "metric", scenario.monitorMetric(),
                        "cadence", "daily",
                        "threshold", Map.of("operator", "<", "value", scenario.monitorThreshold()),
                        "queryFromStep", "preview_audience"
                ),
                List.of("create_activation"),
                Map.of(),
                false
        ));
        return new PlanSpec(Ids.prefixed("plan"), scenario.id(), request.goal(), request.context(), steps);
    }

    private String addTravelLtvSetupSteps(ArrayList<PlanStep> steps, PlanRequest request, CustomerScenario scenario) {
        var dataspace = request.context() == null || request.context().dataspace() == null || request.context().dataspace().isBlank()
                ? "default"
                : request.context().dataspace();
        steps.add(snowflakeStreamStep(
                "create_customer_stream",
                "Create Snowflake CUSTOMER data stream",
                "CUSTOMER",
                "TravelCustomerDLO",
                "Travel Customer DLO",
                dataspace,
                List.of(
                        field("CUSTOMER_ID", "text"),
                        field("CUSTOMER_FIRST_NAME", "text"),
                        field("CUSTOMER_LAST_NAME", "text"),
                        field("CUSTOMER_EMAIL_ADDRESS", "email"),
                        field("CUSTOMER_CHILDREN_COUNT", "number"),
                        field("CUSTOMER_COUNTRY", "text")
                ),
                "inspect_model"
        ));
        steps.add(snowflakeStreamStep(
                "create_itinerary_stream",
                "Create Snowflake ITINERARY data stream",
                "ITINERARY",
                "TravelItineraryDLO",
                "Travel Itinerary DLO",
                dataspace,
                List.of(
                        field("ITINERARY_ID", "text"),
                        field("CUSTOMER_ID", "text"),
                        field("ITINERARY_START_DATE", "date"),
                        field("ITINERARY_END_DATE", "date"),
                        field("DURATION_IN_DAYS", "number"),
                        field("BUDGET", "number"),
                        field("ITINERARY_SOURCE_LOCATION", "text"),
                        field("ITINERARY_DESTINATION", "text")
                ),
                "create_customer_stream"
        ));
        steps.add(snowflakeStreamStep(
                "create_itinerary_order_stream",
                "Create Snowflake ITINERARY_ORDER data stream",
                "ITINERARY_ORDER",
                "TravelItineraryOrderDLO",
                "Travel Itinerary Order DLO",
                dataspace,
                List.of(
                        field("ORDER_ID", "text"),
                        field("ITINERARY_ID", "text"),
                        field("ORDER_TYPE", "text"),
                        field("DESCRIPTION", "text"),
                        field("ORDER_COST", "number"),
                        field("ORDER_DATE", "date")
                ),
                "create_itinerary_stream"
        ));
        steps.add(new PlanStep(
                "create_crm_contact_stream",
                "Create CRM Contact data stream",
                PlanPhase.SETUP,
                Data360Action.CREATE_CRM_DATA_STREAM,
                Map.of(
                        "streamName", "CRM_Contact_Profile_Stream",
                        "label", "CRM Contact Profile Stream",
                        "sourceObject", "Contact",
                        "dataSpaceName", dataspace,
                        "dloName", "CrmContactDLO",
                        "dloLabel", "CRM Contact DLO",
                        "category", "Profile",
                        "fields", List.of(
                                field("Id", "text"),
                                field("FirstName", "text"),
                                field("LastName", "text"),
                                field("Email", "email")
                        )
                ),
                List.of("create_itinerary_order_stream"),
                Map.of(),
                true
        ));
        steps.add(mappingStep(
                "map_customer_to_individual",
                "Map Snowflake CUSTOMER to Individual",
                "TravelCustomerDLO",
                "Individual",
                Map.of(
                        "CUSTOMER_ID", "externalCustomerId",
                        "CUSTOMER_FIRST_NAME", "firstName",
                        "CUSTOMER_LAST_NAME", "lastName",
                        "CUSTOMER_EMAIL_ADDRESS", "email",
                        "CUSTOMER_COUNTRY", "country"
                ),
                "create_crm_contact_stream",
                dataspace
        ));
        steps.add(mappingStep(
                "map_contact_to_individual",
                "Map CRM Contact to Individual",
                "CrmContactDLO",
                "Individual",
                Map.of(
                        "Id", "crmContactId",
                        "FirstName", "firstName",
                        "LastName", "lastName",
                        "Email", "email"
                ),
                "map_customer_to_individual",
                dataspace
        ));
        steps.add(mappingStep(
                "map_itinerary_to_travel_itinerary",
                "Map Snowflake ITINERARY to TravelItinerary",
                "TravelItineraryDLO",
                "TravelItinerary",
                Map.of(
                        "ITINERARY_ID", "itineraryId",
                        "CUSTOMER_ID", "externalCustomerId",
                        "ITINERARY_START_DATE", "itineraryStartDate",
                        "ITINERARY_END_DATE", "itineraryEndDate",
                        "DURATION_IN_DAYS", "durationInDays",
                        "BUDGET", "budget",
                        "ITINERARY_DESTINATION", "destination"
                ),
                "map_contact_to_individual",
                dataspace
        ));
        steps.add(mappingStep(
                "map_order_to_travel_itinerary",
                "Map Snowflake ITINERARY_ORDER spend to TravelItinerary",
                "TravelItineraryOrderDLO",
                "TravelItinerary",
                Map.of(
                        "ORDER_ID", "orderId",
                        "ITINERARY_ID", "itineraryId",
                        "ORDER_TYPE", "orderType",
                        "ORDER_COST", "transactionAmount",
                        "ORDER_DATE", "transactionDate"
                ),
                "map_itinerary_to_travel_itinerary",
                dataspace
        ));
        steps.add(new PlanStep(
                "create_identity_ruleset",
                "Create identity ruleset for CRM Contact and Snowflake customer",
                PlanPhase.SETUP,
                Data360Action.CREATE_IDENTITY_RULESET,
                Map.of(
                        "name", "Travel Contact Customer Identity",
                        "description", "Unify CRM Contact profiles with Snowflake CUSTOMER records using email and external customer id.",
                        "profileObject", "Individual",
                        "rules", List.of(
                                Map.of("sourceField", "email", "matchMethod", "exact", "confidence", "high"),
                                Map.of("sourceField", "externalCustomerId", "matchMethod", "exact", "confidence", "high")
                        )
                ),
                List.of("map_order_to_travel_itinerary"),
                Map.of(),
                true
        ));
        steps.add(new PlanStep(
                "run_identity_resolution",
                "Run identity resolution",
                PlanPhase.SETUP,
                Data360Action.RUN_IDENTITY_RESOLUTION,
                Map.of("rulesetIdFromStep", "create_identity_ruleset"),
                List.of("create_identity_ruleset"),
                Map.of(),
                true
        ));
        steps.add(new PlanStep(
                "create_ltv_insight",
                "Create lifetime value calculated insight",
                PlanPhase.SETUP,
                Data360Action.CREATE_CALCULATED_INSIGHT,
                Map.of(
                        "name", scenario.calculatedInsight().name(),
                        "apiName", "Travel_Customer_Lifetime_Value",
                        "description", scenario.calculatedInsight().description(),
                        "transactionObjectApiName", scenario.calculatedInsight().transactionObjectApiName(),
                        "transactionCustomerKeyField", "externalCustomerId",
                        "measure", Map.of(
                                "alias", scenario.calculatedInsight().measureAlias(),
                                "expression", "SUM(transactionAmount)"
                        ),
                        "groupBy", List.of("unifiedProfileId")
                ),
                List.of("run_identity_resolution", "map_order_to_travel_itinerary"),
                Map.of(
                        "unifiedProfileObjectApiName", new InputBinding("run_identity_resolution", "$.unifiedProfileObjectApiName"),
                        "unifiedProfileIdField", new InputBinding("run_identity_resolution", "$.unifiedProfileIdField")
                ),
                true
        ));
        steps.add(new PlanStep(
                "run_ltv_insight",
                "Run lifetime value calculated insight",
                PlanPhase.SETUP,
                Data360Action.RUN_CALCULATED_INSIGHT,
                Map.of("insightIdFromStep", "create_ltv_insight"),
                List.of("create_ltv_insight"),
                Map.of(),
                true
        ));
        return "run_ltv_insight";
    }

    private PlanStep snowflakeStreamStep(String id, String title, String objectName, String dloName, String dloLabel, String dataspace, List<Map<String, String>> fields, String dependsOn) {
        return new PlanStep(
                id,
                title,
                PlanPhase.SETUP,
                Data360Action.CREATE_SNOWFLAKE_DATA_STREAM,
                Map.ofEntries(
                        entry("streamName", objectName + "_Snowflake_Stream"),
                        entry("label", title.replace("Create ", "")),
                        entry("connectionName", "Snowflake Data 360 Connection"),
                        entry("warehouse", "dcdemo"),
                        entry("database", "dcbootcamp"),
                        entry("schema", "PUBLIC"),
                        entry("objectName", objectName),
                        entry("dataSpaceName", dataspace),
                        entry("dloName", dloName),
                        entry("dloLabel", dloLabel),
                        entry("category", "Engagement"),
                        entry("fields", fields)
                ),
                List.of(dependsOn),
                Map.of(),
                true
        );
    }

    private PlanStep mappingStep(String id, String title, String sourceDloName, String targetDmoName, Map<String, String> fieldMappings, String dependsOn, String dataspace) {
        return new PlanStep(
                id,
                title,
                PlanPhase.SETUP,
                Data360Action.CREATE_MAPPING,
                Map.of(
                        "sourceDloName", sourceDloName,
                        "targetDmoName", targetDmoName,
                        "fieldMappings", fieldMappings,
                        "dataspace", dataspace
                ),
                List.of(dependsOn),
                Map.of(),
                true
        );
    }

    private Map<String, String> field(String name, String type) {
        return Map.of("name", name, "type", type);
    }

    private boolean stepsContain(List<PlanStep> steps, String id) {
        return steps.stream().anyMatch(step -> step.id().equals(id));
    }

    private List<String> allowedResources() {
        return operations.all().keySet().stream()
                .map(Data360Action::resource)
                .sorted()
                .toList();
    }

    private List<String> enabledMcpServers() {
        return mcpSettings.current().servers().stream()
                .filter(server -> server.enabled() && server.commandConfigured())
                .map(server -> "%s (%s)%s".formatted(server.id(), server.label(), server.executionServer() ? " [execution]" : " [discovery]"))
                .sorted()
                .toList();
    }

    private String toJson(CustomerScenario scenario) {
        return toJson((Object) scenario);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize planner payload.", e);
        }
    }

    private String extractJson(String text) {
        var start = text.indexOf('{');
        var end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalArgumentException("No JSON object found.");
        }
        return text.substring(start, end + 1);
    }
}
