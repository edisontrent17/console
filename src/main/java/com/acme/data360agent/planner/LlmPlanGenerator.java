package com.acme.data360agent.planner;

import com.acme.data360agent.llm.LlmGateway;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
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

                PlanSpec is the governance contract. It is not a workflow engine.

                Use only the allowed actions listed in the prompt.

                Rules:
                - Keep the plan ordered.
                - Use phases exactly as: discover, setup, monitor.
                - Discover steps inspect or preview current Data 360 state.
                - Setup steps create or publish Data 360 assets through governed Connect API operations.
                - Monitor steps define goal-health checks after setup.
                - Every query must be SELECT-only and include LIMIT.
                - Any create, update, publish, calculated insight run, or activation step must set needsApproval=true.
                - Use data360.runActivation only if the user explicitly asks to run, send, or execute an activation.
                - Do not include raw MCP tool names, API URLs, prompts, loops, code, retries, timers, or arbitrary expressions.
                - Step inputs must be human-reviewable domain parameters, not raw HTTP payloads.
                - Prefer dependsOn. If step output is needed, use inputBindings with simple JSON paths like {"segmentId":{"fromStep":"create_segment","path":"$.segmentId"}}.
                - Keep monitors read-only. A monitor can recommend follow-up later, but it must not silently mutate Data 360.
                """;
        var user = """
                Draft a PlanSpec for this request.

                Required JSON shape:
                {
                  "id": "plan_<short id>",
                  "scenarioId": "...",
                  "goal": "...",
                  "context": {
                    "org": "...",
                    "dataspace": "...",
                    "environment": "sandbox|production"
                  },
                  "steps": [
                    {
                      "id": "snake_case",
                      "title": "...",
                      "phase": "discover|setup|monitor",
                      "action": "data360.query",
                      "input": {},
                      "dependsOn": [],
                      "inputBindings": {},
                      "needsApproval": false
                    }
                  ]
                }

                Allowed actions:
                %s

                Ground this plan in the selected public customer scenario:
                %s

                User goal: %s
                Context: %s
                """.formatted(allowedActions(), toJson(scenario), request.goal(), request.context());

        var completion = llm.completeJson(system, user);
        try {
            return objectMapper.readValue(extractJson(completion.text()), PlanSpec.class);
        } catch (Exception e) {
            throw new IllegalStateException("%s returned invalid PlanSpec JSON: %s".formatted(completion.provider(), completion.text()), e);
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

    private List<String> allowedActions() {
        return operations.all().keySet().stream()
                .map(Data360Action::value)
                .sorted()
                .toList();
    }

    private String toJson(CustomerScenario scenario) {
        try {
            return objectMapper.writeValueAsString(scenario);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize scenario.", e);
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
