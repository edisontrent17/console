package com.acme.data360agent.plan;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AslPlanCompilerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void compilesLegacyStepsIntoAslDefinition() {
        var plan = new PlanSpec(
                "plan_asl",
                "Create governed segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "preview",
                                "Preview audience",
                                PlanPhase.DISCOVER,
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                Map.of(),
                                false
                        ),
                        new PlanStep(
                                "create_segment",
                                "Create segment",
                                PlanPhase.SETUP,
                                Data360Action.CREATE_SEGMENT,
                                Map.of("name", "Dormant Accounts", "criteriaFromStep", "preview"),
                                List.of("preview"),
                                Map.of(),
                                true
                        )
                )
        );

        assertThat(plan.definition().startAt()).isEqualTo("preview");
        assertThat(plan.definition().states().get("preview").resource()).isEqualTo(Data360Action.QUERY.resource());
        assertThat(plan.definition().states().get("preview").next()).isEqualTo("create_segment");
        assertThat(plan.definition().states().get("create_segment").end()).isTrue();
    }

    @Test
    void derivesExecutableStepsFromAslDefinition() throws Exception {
        var json = """
                {
                  "schemaVersion": "data360-asl-profile-2026-05-31",
                  "id": "plan_asl_only",
                  "goal": "Publish a governed audience",
                  "context": {"org": "org", "dataspace": "default", "environment": "sandbox"},
                  "definition": {
                    "Version": "1.0",
                    "QueryLanguage": "JSONPath",
                    "StartAt": "preview",
                    "States": {
                      "preview": {
                        "Type": "Task",
                        "Comment": "Preview audience",
                        "Resource": "urn:salesforce:data360:capability:query",
                        "Parameters": {"sql": "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"},
                        "ResultPath": "$.preview",
                        "Next": "publish_segment"
                      },
                      "publish_segment": {
                        "Type": "Task",
                        "Comment": "Publish segment",
                        "Resource": "urn:salesforce:data360:capability:segment.publish",
                        "Parameters": {"segmentId.$": "$.preview.segmentId"},
                        "ResultPath": "$.publish_segment",
                        "End": true
                      }
                    }
                  }
                }
                """;

        var plan = objectMapper.readValue(json, PlanSpec.class);

        assertThat(plan.steps()).hasSize(2);
        assertThat(plan.steps().get(1).action()).isEqualTo(Data360Action.PUBLISH_SEGMENT);
        assertThat(plan.steps().get(1).needsApproval()).isTrue();
        assertThat(plan.steps().get(1).inputBindings())
                .containsEntry("segmentId", new InputBinding("preview", "$.segmentId"));
    }
}
