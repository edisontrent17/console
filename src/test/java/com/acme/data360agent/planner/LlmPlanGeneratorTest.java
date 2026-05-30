package com.acme.data360agent.planner;

import com.acme.data360agent.llm.LlmGateway;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanValidator;
import com.acme.data360agent.scenario.ScenarioLibrary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LlmPlanGeneratorTest {
    @Test
    void fallbackGeneratesValidScenarioGroundedPlan() {
        var llm = mock(LlmGateway.class);
        when(llm.configured()).thenReturn(false);
        var operations = new OperationRegistry();
        var generator = new LlmPlanGenerator(llm, new ObjectMapper(), new ScenarioLibrary(), operations);

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
}
