package com.acme.data360agent.planner;

import com.acme.data360agent.llm.LlmGateway;
import com.acme.data360agent.llm.LlmCompletion;
import com.acme.data360agent.mcp.McpServerSetting;
import com.acme.data360agent.mcp.McpSettings;
import com.acme.data360agent.mcp.McpSettingsService;
import com.acme.data360agent.operation.OperationBindingCatalog;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanValidator;
import com.acme.data360agent.scenario.ScenarioLibrary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LlmPlanGeneratorTest {
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
}
