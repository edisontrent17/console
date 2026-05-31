package com.acme.data360agent.execution;

import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.InputBinding;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanStep;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PlanInputResolverTest {
    @Test
    void resolvesIdentityResolutionOutputsIntoCalculatedInsightInputs() {
        var step = new PlanStep(
                "create_lifetime_value_insight",
                "Create LTV calculated insight",
                PlanPhase.SETUP,
                Data360Action.CREATE_CALCULATED_INSIGHT,
                Map.of(
                        "name", "Travel Customer Lifetime Value",
                        "transactionObjectApiName", "TravelItinerary",
                        "measure", Map.of("type", "SUM", "field", "transactionAmount")
                ),
                List.of("run_identity_resolution"),
                Map.of(
                        "unifiedProfileObjectApiName", new InputBinding("run_identity_resolution", "$.unifiedProfileObjectApiName"),
                        "unifiedProfileIdField", new InputBinding("run_identity_resolution", "$.unifiedProfileIdField")
                ),
                true
        );

        var resolved = PlanInputResolver.resolve(step, source -> Map.of(
                "unifiedProfileObjectApiName", "UnifiedIndividual",
                "unifiedProfileIdField", "UnifiedIndividualId"
        ));

        assertThat(resolved)
                .containsEntry("unifiedProfileObjectApiName", "UnifiedIndividual")
                .containsEntry("unifiedProfileIdField", "UnifiedIndividualId")
                .containsEntry("transactionObjectApiName", "TravelItinerary");
    }
}
