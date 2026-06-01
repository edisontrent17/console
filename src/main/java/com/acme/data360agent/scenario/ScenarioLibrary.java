package com.acme.data360agent.scenario;

import com.acme.data360agent.plan.Data360Action;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class ScenarioLibrary {
    private final List<CustomerScenario> scenarios = List.of(
            new CustomerScenario(
                    "fedex_dormant_reactivation",
                    "Dormant Account Reactivation",
                    "Logistics",
                    "https://www.salesforce.com/customer-stories/fedex",
                    "FedEx publicly describes using Data 360 to reactivate dormant accounts and improve activation.",
                    List.of(
                            "Recover dormant high-value accounts",
                            "Find inactive customers with shipping intent and launch reactivation"
                    ),
                    List.of("activation_rate", "recovered_revenue", "email_engagement"),
                    List.of(
                            Data360Action.METADATA_DESCRIBE,
                            Data360Action.QUERY,
                            Data360Action.CREATE_SEGMENT,
                            Data360Action.PUBLISH_SEGMENT,
                            Data360Action.CREATE_ACTIVATION,
                            Data360Action.MONITOR_METRIC
                    ),
                    List.of("dormant", "reactivation", "inactive", "account", "revenue", "fedex", "shipping"),
                    "UnifiedAccount",
                    "SELECT unified_account_id, account_name, annual_revenue, last_engagement_date, intent_score FROM UnifiedAccount WHERE annual_revenue > 100000 AND days_since_last_engagement > 90 LIMIT 100",
                    "Dormant High Value Accounts",
                    "Dormant Account Reactivation Draft",
                    "MarketingCloud",
                    "activation_rate",
                    0.13
            ),
            new CustomerScenario(
                    "uchicago_patient_access",
                    "Patient Access and Conversion",
                    "Healthcare",
                    "https://www.salesforce.com/customer-stories/uchicago-medicine",
                    "UChicago Medicine publicly describes Data 360, Health Cloud, Marketing Cloud, Agentforce, and Tableau for patient engagement.",
                    List.of(
                            "Increase patient access and conversion with personalized outreach",
                            "Identify patients who need follow-up and route them into the right care journey"
                    ),
                    List.of("appointment_conversion_rate", "journey_engagement", "care_gap_closure"),
                    List.of(
                            Data360Action.METADATA_DESCRIBE,
                            Data360Action.QUERY,
                            Data360Action.CREATE_SEGMENT,
                            Data360Action.PUBLISH_SEGMENT,
                            Data360Action.CREATE_ACTIVATION,
                            Data360Action.MONITOR_METRIC
                    ),
                    List.of("patient", "health", "appointment", "care", "journey", "uchicago", "conversion"),
                    "UnifiedIndividual",
                    "SELECT unified_individual_id, preferred_channel, care_gap_count, last_appointment_date FROM UnifiedIndividual WHERE care_gap_count > 0 LIMIT 100",
                    "Patients Needing Follow Up",
                    "Patient Access Outreach Draft",
                    "MarketingCloud",
                    "appointment_conversion_rate",
                    0.60
            ),
            new CustomerScenario(
                    "pacers_fan_engagement",
                    "Fan Loyalty and Revenue",
                    "Sports and Entertainment",
                    "https://www.salesforce.com/customer-stories/pacers-sports-entertainment-data-360",
                    "Pacers Sports & Entertainment publicly describes unifying fan data sources with Data 360 for personalized journeys.",
                    List.of(
                            "Grow fan loyalty and revenue through personalized journeys",
                            "Find high-intent fans and activate a game-day offer journey"
                    ),
                    List.of("offer_conversion_rate", "fan_engagement_score", "ticket_revenue"),
                    List.of(
                            Data360Action.METADATA_DESCRIBE,
                            Data360Action.QUERY,
                            Data360Action.CREATE_SEGMENT,
                            Data360Action.PUBLISH_SEGMENT,
                            Data360Action.CREATE_ACTIVATION,
                            Data360Action.MONITOR_METRIC
                    ),
                    List.of("fan", "ticket", "sports", "pacers", "loyalty", "offer", "game"),
                    "UnifiedIndividual",
                    "SELECT unified_individual_id, favorite_team, ticket_purchase_score, app_engagement_score FROM UnifiedIndividual WHERE ticket_purchase_score > 70 LIMIT 100",
                    "High Intent Fans",
                    "Fan Journey Activation Draft",
                    "MarketingCloud",
                    "offer_conversion_rate",
                    0.18
            ),
            new CustomerScenario(
                    "salesforce_event_pipeline",
                    "Event-to-Pipeline Acceleration",
                    "Technology",
                    "https://www.salesforce.com/customer-stories/agentforce-for-events-agent/unified-data/",
                    "Salesforce publicly describes using Data 360 event profiles and Agentforce to personalize event engagement and pipeline follow-up.",
                    List.of(
                            "Turn event engagement into pipeline",
                            "Use session attendance and engagement to prioritize sales follow-up"
                    ),
                    List.of("meeting_conversion_rate", "pipeline_created", "followup_sla"),
                    List.of(
                            Data360Action.METADATA_DESCRIBE,
                            Data360Action.QUERY,
                            Data360Action.CREATE_CALCULATED_INSIGHT,
                            Data360Action.CREATE_SEGMENT,
                            Data360Action.PUBLISH_SEGMENT,
                            Data360Action.CREATE_ACTIVATION,
                            Data360Action.MONITOR_METRIC
                    ),
                    List.of("event", "pipeline", "attendee", "session", "salesforce", "meeting", "followup"),
                    "UnifiedIndividual",
                    "SELECT unified_individual_id, company_name, session_count, product_interest_score FROM UnifiedIndividual WHERE session_count > 1 AND product_interest_score > 70 LIMIT 100",
                    "High Intent Event Attendees",
                    "Event Follow Up Activation Draft",
                    "SalesCloud",
                    "meeting_conversion_rate",
                    0.25
            ),
            new CustomerScenario(
                    "pepsico_retailer_engagement",
                    "Retailer Engagement and Store Execution",
                    "Consumer Goods",
                    "https://www.salesforce.com/customer-stories/pepsico-data-360",
                    "PepsiCo publicly describes using Data 360 with Agentforce Marketing, MuleSoft, retail execution, service, and loyalty data.",
                    List.of(
                            "Improve store execution and personalized retailer engagement",
                            "Find retailers with execution gaps and launch the right next action"
                    ),
                    List.of("retailer_engagement_rate", "execution_gap_closure", "promotion_uplift"),
                    List.of(
                            Data360Action.METADATA_DESCRIBE,
                            Data360Action.QUERY,
                            Data360Action.CREATE_SEGMENT,
                            Data360Action.PUBLISH_SEGMENT,
                            Data360Action.CREATE_ACTIVATION,
                            Data360Action.MONITOR_METRIC
                    ),
                    List.of("retail", "retailer", "store", "pepsico", "execution", "promotion", "loyalty"),
                    "UnifiedAccount",
                    "SELECT unified_account_id, store_name, execution_gap_score, promotion_response_score FROM UnifiedAccount WHERE execution_gap_score > 60 LIMIT 100",
                    "Retailers With Execution Gaps",
                    "Retailer Execution Activation Draft",
                    "MarketingCloud",
                    "execution_gap_closure",
                    0.20
            ),
            travelLtvFromSnowflakeAndCrm()
    );

    public List<CustomerScenario> all() {
        return scenarios;
    }

    public Optional<CustomerScenario> byId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return scenarios.stream()
                .filter(scenario -> scenario.id().equals(id))
                .findFirst();
    }

    public CustomerScenario resolve(String scenarioId, String utterance) {
        if (scenarioId != null && !scenarioId.isBlank()) {
            return byId(scenarioId).orElseThrow(() -> new IllegalArgumentException("Unknown scenario: " + scenarioId));
        }
        return bestMatch(utterance);
    }

    private CustomerScenario bestMatch(String utterance) {
        var lower = utterance == null ? "" : utterance.toLowerCase(Locale.ROOT);
        return scenarios.stream()
                .max(Comparator.comparingInt(scenario -> score(scenario, lower)))
                .orElseThrow();
    }

    private int score(CustomerScenario scenario, String utterance) {
        var score = 0;
        for (var keyword : scenario.keywords()) {
            if (utterance.contains(keyword.toLowerCase(Locale.ROOT))) {
                score++;
            }
        }
        return score;
    }

    private static CustomerScenario travelLtvFromSnowflakeAndCrm() {
        return new CustomerScenario(
                "travel_ltv_snowflake_crm",
                "Travel Customer LTV",
                "Travel and Hospitality",
                "snowflake://DCBOOTCAMP/PUBLIC",
                "Prompt-sourced travel scenario joining Snowflake DCBOOTCAMP.PUBLIC CUSTOMER, ITINERARY, and ITINERARY_ORDER data with CRM Contact profiles for Data 360 LTV activation.",
                List.of(
                        "Create a travel LTV audience from Snowflake bookings and CRM contacts",
                        "Use CUSTOMER, ITINERARY, ITINERARY_ORDER, and Contact to find high-value travelers"
                ),
                List.of("travel_lifetime_value", "booking_frequency", "activation_conversion_rate"),
                List.of(
                        Data360Action.METADATA_DESCRIBE,
                        Data360Action.CREATE_SNOWFLAKE_DATA_STREAM,
                        Data360Action.CREATE_CRM_DATA_STREAM,
                        Data360Action.CREATE_MAPPING,
                        Data360Action.CREATE_IDENTITY_RULESET,
                        Data360Action.RUN_IDENTITY_RESOLUTION,
                        Data360Action.CREATE_CALCULATED_INSIGHT,
                        Data360Action.RUN_CALCULATED_INSIGHT,
                        Data360Action.QUERY,
                        Data360Action.CREATE_SEGMENT,
                        Data360Action.PUBLISH_SEGMENT,
                        Data360Action.CREATE_ACTIVATION,
                        Data360Action.MONITOR_METRIC
                ),
                List.of(
                        "travel",
                        "traveler",
                        "ltv",
                        "lifetime value",
                        "snowflake",
                        "dcbootcamp",
                        "customer",
                        "itinerary",
                        "itinerary_order",
                        "crm",
                        "contact",
                        "booking"
                ),
                "UnifiedIndividual",
                "SELECT unified_individual_id, email, loyalty_tier, lifetime_value, itinerary_count, last_booking_date FROM UnifiedIndividual WHERE lifetime_value > 10000 AND marketing_consent = 'OptIn' LIMIT 100",
                "High LTV Travelers",
                "Travel LTV Activation Draft",
                "MarketingCloud",
                "high_ltv_activation_rate",
                0.20,
                List.of(
                        sourceSystem("Snowflake DCBOOTCAMP.PUBLIC", "Snowflake", "CUSTOMER", "ITINERARY", "ITINERARY_ORDER"),
                        sourceSystem("Salesforce CRM", "CRM", "Contact")
                ),
                List.of(
                        modelNote("TravelCustomerDLO", "DLO", "Ingest DCBOOTCAMP.PUBLIC.CUSTOMER with stable externalCustomerId, email, loyalty tier, and home market fields."),
                        modelNote("TravelItineraryDLO", "DLO", "Ingest DCBOOTCAMP.PUBLIC.ITINERARY as the booking header source with itinerary id, customer key, booking date, and trip status."),
                        modelNote("TravelItineraryOrderDLO", "DLO", "Ingest DCBOOTCAMP.PUBLIC.ITINERARY_ORDER as order-level spend that rolls into transactionAmount for LTV."),
                        modelNote("Individual", "DMO", "Map CRM Contact and Snowflake customer attributes into the profile model, using Contact email and externalCustomerId for identity rules."),
                        modelNote("TravelItinerary", "DMO", "Model itinerary spend as the transaction object used by the LTV calculated insight.")
                ),
                new CustomerScenario.CalculatedInsight(
                        "Travel Customer Lifetime Value",
                        "SUM TravelItinerary.transactionAmount by unified individual after identity resolution links CRM Contact to Snowflake customer records.",
                        "TravelItinerary",
                        "lifetime_value"
                ),
                new CustomerScenario.SegmentMetadata(
                        "High LTV Travelers",
                        "Unified individuals with lifetime_value greater than 10000, opt-in marketing consent, and recent itinerary activity."
                ),
                List.of(
                        monitor("high_ltv_activation_rate", "daily", "Alert below 0.20", "Review activation performance and refresh segment criteria before expanding spend."),
                        monitor("travel_ltv_refresh_rate", "daily", "Alert when calculated insight has not refreshed in 24 hours", "Rerun the LTV calculated insight after Snowflake data stream refresh."),
                        monitor("snowflake_travel_stream_health", "hourly", "Alert on failed CUSTOMER, ITINERARY, or ITINERARY_ORDER ingestion", "Inspect Snowflake data stream failures before publishing the segment.")
                )
        );
    }

    private static CustomerScenario.SourceSystem sourceSystem(String name, String systemType, String... tables) {
        return new CustomerScenario.SourceSystem(name, systemType, List.of(tables));
    }

    private static CustomerScenario.TargetModelNote modelNote(String targetObject, String objectType, String note) {
        return new CustomerScenario.TargetModelNote(targetObject, objectType, note);
    }

    private static CustomerScenario.MonitorMetadata monitor(String metric, String cadence, String threshold, String recommendation) {
        return new CustomerScenario.MonitorMetadata(metric, cadence, threshold, recommendation);
    }
}
