package com.acme.data360agent.scenario;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScenarioLibraryTest {
    private final ScenarioLibrary scenarios = new ScenarioLibrary();

    @Test
    void includesPubliclyGroundedScenarioPacks() {
        assertThat(scenarios.all()).hasSize(5);
        assertThat(scenarios.all())
                .allMatch(scenario -> scenario.sourceUrl().startsWith("https://www.salesforce.com/customer-stories/"))
                .anyMatch(scenario -> scenario.id().equals("fedex_dormant_reactivation"))
                .anyMatch(scenario -> scenario.id().equals("uchicago_patient_access"))
                .anyMatch(scenario -> scenario.id().equals("pacers_fan_engagement"))
                .anyMatch(scenario -> scenario.id().equals("salesforce_event_pipeline"))
                .anyMatch(scenario -> scenario.id().equals("pepsico_retailer_engagement"));
    }

    @Test
    void resolvesScenarioFromUtteranceKeywords() {
        var scenario = scenarios.resolve(null, "Find dormant inactive accounts and recover revenue");

        assertThat(scenario.id()).isEqualTo("fedex_dormant_reactivation");
    }

    @Test
    void rejectsUnknownExplicitScenarioId() {
        assertThatThrownBy(() -> scenarios.resolve("typo", "Recover dormant accounts"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown scenario");
    }
}
