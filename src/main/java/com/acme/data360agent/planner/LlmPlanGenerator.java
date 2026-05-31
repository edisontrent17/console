package com.acme.data360agent.planner;

import com.acme.data360agent.llm.LlmGateway;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
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

@Component
public class LlmPlanGenerator {
    private final LlmGateway llm;
    private final ObjectMapper objectMapper;
    private final ScenarioLibrary scenarios;
    private final OperationRegistry operations;

    public LlmPlanGenerator(LlmGateway llm, ObjectMapper objectMapper, ScenarioLibrary scenarios, OperationRegistry operations) {
        this.llm = llm;
        this.objectMapper = objectMapper;
        this.scenarios = scenarios;
        this.operations = operations;
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

                Ground this plan in the selected public customer scenario:
                %s

                User goal: %s
                Context: %s
                """.formatted(allowedResources(), toJson(scenario), request.goal(), request.context());

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
                - Do not include raw MCP tool names, API URLs, AWS ARNs, Credentials, Retry, Catch, Map, Parallel, code, or arbitrary expressions.
                - Preserve the user's goal and scenario unless they are clearly malformed.
                """;
        var user = """
                Repair this PlanSpec so every validator passes.

                Validation issues:
                %s

                Allowed capability resources:
                %s

                Original request:
                %s

                Invalid PlanSpec:
                %s
                """.formatted(toJson(validation), allowedResources(), toJson(request), toJson(invalidPlan));

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
        steps.add(new PlanStep(
                "preview_audience",
                "Preview the candidate audience",
                PlanPhase.DISCOVER,
                Data360Action.QUERY,
                Map.of("sql", scenario.previewSql(), "limit", 100),
                List.of("inspect_model"),
                Map.of(),
                false
        ));
        if (scenario.requiredActions().contains(Data360Action.CREATE_CALCULATED_INSIGHT)) {
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

    private List<String> allowedResources() {
        return operations.all().keySet().stream()
                .map(Data360Action::resource)
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
