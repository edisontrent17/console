package com.acme.data360agent.scenario;

import com.acme.data360agent.plan.Data360Action;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScenarioLibraryTest {
    private final ScenarioLibrary scenarios = new ScenarioLibrary();

    @Test
    void includesPubliclyGroundedScenarioPacks() {
        assertThat(scenarios.all()).hasSize(6);
        assertThat(scenarios.all())
                .anyMatch(scenario -> scenario.id().equals("fedex_dormant_reactivation"))
                .anyMatch(scenario -> scenario.id().equals("uchicago_patient_access"))
                .anyMatch(scenario -> scenario.id().equals("pacers_fan_engagement"))
                .anyMatch(scenario -> scenario.id().equals("salesforce_event_pipeline"))
                .anyMatch(scenario -> scenario.id().equals("pepsico_retailer_engagement"))
                .anyMatch(scenario -> scenario.id().equals("travel_ltv_snowflake_crm"));
        assertThat(scenarios.all())
                .filteredOn(scenario -> !scenario.id().equals("travel_ltv_snowflake_crm"))
                .allMatch(scenario -> scenario.sourceUrl().startsWith("https://www.salesforce.com/customer-stories/"));
    }

    @Test
    void resolvesScenarioFromUtteranceKeywords() {
        var scenario = scenarios.resolve(null, "Find dormant inactive accounts and recover revenue");

        assertThat(scenario.id()).isEqualTo("fedex_dormant_reactivation");
    }

    @Test
    void resolvesTravelLtvScenarioFromSnowflakeAndCrmPrompt() {
        var scenario = scenarios.resolve(
                null,
                "Use Snowflake DCBOOTCAMP.PUBLIC CUSTOMER, ITINERARY, ITINERARY_ORDER and CRM Contact to create travel LTV"
        );

        assertThat(scenario.id()).isEqualTo("travel_ltv_snowflake_crm");
    }

    @Test
    void includesTravelLtvScenarioMetadata() {
        var scenario = scenarios.byId("travel_ltv_snowflake_crm").orElseThrow();

        assertThat(scenario.requiredActions()).contains(
                Data360Action.CREATE_SNOWFLAKE_DATA_STREAM,
                Data360Action.CREATE_CRM_DATA_STREAM,
                Data360Action.CREATE_MAPPING,
                Data360Action.CREATE_IDENTITY_RULESET,
                Data360Action.RUN_IDENTITY_RESOLUTION,
                Data360Action.CREATE_CALCULATED_INSIGHT,
                Data360Action.RUN_CALCULATED_INSIGHT,
                Data360Action.CREATE_SEGMENT,
                Data360Action.MONITOR_METRIC
        );
        assertThat(scenario.sourceSystems())
                .anySatisfy(source -> {
                    assertThat(source.name()).isEqualTo("Snowflake DCBOOTCAMP.PUBLIC");
                    assertThat(source.tables()).containsExactly("CUSTOMER", "ITINERARY", "ITINERARY_ORDER");
                })
                .anySatisfy(source -> {
                    assertThat(source.name()).isEqualTo("Salesforce CRM");
                    assertThat(source.tables()).containsExactly("Contact");
                });
        assertThat(scenario.targetModelingNotes())
                .anySatisfy(note -> {
                    assertThat(note.objectType()).isEqualTo("DLO");
                    assertThat(note.targetObject()).isEqualTo("TravelItineraryOrderDLO");
                    assertThat(note.note()).contains("ITINERARY_ORDER");
                })
                .anySatisfy(note -> {
                    assertThat(note.objectType()).isEqualTo("DMO");
                    assertThat(note.targetObject()).isEqualTo("TravelItinerary");
                });
        assertThat(scenario.calculatedInsight().name()).isEqualTo("Travel Customer Lifetime Value");
        assertThat(scenario.calculatedInsight().transactionObjectApiName()).isEqualTo("TravelItinerary");
        assertThat(scenario.calculatedInsight().measureAlias()).isEqualTo("lifetime_value");
        assertThat(scenario.segment().name()).isEqualTo("High LTV Travelers");
        assertThat(scenario.previewSql()).contains("lifetime_value > 10000");
        assertThat(scenario.segment().criteria()).contains("lifetime_value greater than 10000");
        assertThat(scenario.monitors())
                .extracting(CustomerScenario.MonitorMetadata::metric)
                .contains("high_ltv_activation_rate", "travel_ltv_refresh_rate", "snowflake_travel_stream_health");
    }

    @Test
    void rejectsUnknownExplicitScenarioId() {
        assertThatThrownBy(() -> scenarios.resolve("typo", "Recover dormant accounts"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown scenario");
    }
}
