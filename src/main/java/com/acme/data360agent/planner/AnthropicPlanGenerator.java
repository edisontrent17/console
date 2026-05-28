package com.acme.data360agent.planner;

import com.acme.data360agent.llm.AnthropicClient;
import com.acme.data360agent.plan.PlanSpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class AnthropicPlanGenerator {
    private final AnthropicClient anthropic;
    private final ObjectMapper objectMapper;

    public AnthropicPlanGenerator(AnthropicClient anthropic, ObjectMapper objectMapper) {
        this.anthropic = anthropic;
        this.objectMapper = objectMapper;
    }

    public PlanSpec generate(PlanRequest request) {
        if (!anthropic.configured()) {
            return fallback(request);
        }

        var system = """
                You draft small, auditable Data 360 PlanSpec JSON.
                Return only JSON. No markdown. No commentary.
                Use only these actions:
                - data360.search
                - data360.query
                - data360.createSegment
                - data360.publishSegment
                - data360.createActivation
                - data360.runActivation

                Rules:
                - Keep the plan ordered.
                - Every query must be SELECT-only and include LIMIT.
                - Any create, publish, or activation step must set needsApproval=true.
                - Use data360.runActivation only if the user explicitly asks to run, send, or execute an activation.
                - Do not include raw MCP tool names, API URLs, prompts, loops, or code.
                - Step inputs must be human-reviewable domain parameters, not raw API payloads.
                - Use criteriaFromStep, segmentIdFromStep, or activationIdFromStep to reference earlier outputs.
                """;
        var user = """
                Draft a PlanSpec for this request.

                Required JSON shape:
                {
                  "id": "plan_<short id>",
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
                      "action": "data360.query",
                      "input": {},
                      "dependsOn": [],
                      "needsApproval": false
                    }
                  ]
                }

                User goal: %s
                Context: %s
                """.formatted(request.goal(), request.context());

        var text = anthropic.completeJson(system, user);
        try {
            return objectMapper.readValue(extractJson(text), PlanSpec.class);
        } catch (Exception e) {
            throw new IllegalStateException("Anthropic returned invalid PlanSpec JSON: " + text, e);
        }
    }

    private PlanSpec fallback(PlanRequest request) {
        var lowerGoal = request.goal().toLowerCase();
        var wantsActivation = lowerGoal.contains("activat");
        var wantsRunActivation = lowerGoal.contains("run activation")
                || lowerGoal.contains("execute activation")
                || lowerGoal.contains("send activation");
        var steps = new java.util.ArrayList<com.acme.data360agent.plan.PlanStep>();
        steps.add(new com.acme.data360agent.plan.PlanStep(
                "preview",
                "Preview candidate audience",
                com.acme.data360agent.plan.Data360Action.QUERY,
                Map.of(
                        "sql", "SELECT unified_individual_id, lifetime_value, churn_score FROM UnifiedIndividual WHERE lifetime_value > 10000 AND churn_score > 0.7 LIMIT 100",
                        "limit", 100
                ),
                java.util.List.of(),
                false
        ));
        steps.add(new com.acme.data360agent.plan.PlanStep(
                "create_segment",
                "Create Data 360 segment",
                com.acme.data360agent.plan.Data360Action.CREATE_SEGMENT,
                Map.of(
                        "name", "High Value Churn Risk",
                        "description", "Customers with high lifetime value and elevated churn risk.",
                        "criteriaFromStep", "preview"
                ),
                java.util.List.of("preview"),
                true
        ));
        steps.add(new com.acme.data360agent.plan.PlanStep(
                "publish_segment",
                "Publish segment",
                com.acme.data360agent.plan.Data360Action.PUBLISH_SEGMENT,
                Map.of("segmentIdFromStep", "create_segment"),
                java.util.List.of("create_segment"),
                true
        ));
        if (wantsActivation) {
            steps.add(new com.acme.data360agent.plan.PlanStep(
                    "create_activation",
                    "Create activation",
                    com.acme.data360agent.plan.Data360Action.CREATE_ACTIVATION,
                    Map.of(
                            "name", "High Value Churn Risk Activation",
                            "destination", "default_destination",
                            "segmentIdFromStep", "create_segment"
                    ),
                    java.util.List.of("publish_segment"),
                    true
            ));
        }
        if (wantsRunActivation) {
            steps.add(new com.acme.data360agent.plan.PlanStep(
                    "run_activation",
                    "Run activation",
                    com.acme.data360agent.plan.Data360Action.RUN_ACTIVATION,
                    Map.of("activationIdFromStep", "create_activation"),
                    java.util.List.of("create_activation"),
                    true
            ));
        }
        return new PlanSpec("plan_" + UUID.randomUUID().toString().substring(0, 8), request.goal(), request.context(), steps);
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
