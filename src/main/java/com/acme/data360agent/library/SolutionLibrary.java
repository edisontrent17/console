package com.acme.data360agent.library;

import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SolutionLibrary {
    private final Map<String, SolutionTemplate> templates;
    private final List<SolutionTemplate> orderedTemplates;

    public SolutionLibrary() {
        var entries = List.of(
                retailLoyaltyWinback(),
                financialHouseholdExpansion(),
                healthcareCareGapOutreach(),
                manufacturingServiceToSales(),
                communicationsChurnDeflection(),
                automotiveConnectedService(),
                b2bProductLedExpansion(),
                nonprofitDonorStewardship()
        );
        var map = new LinkedHashMap<String, SolutionTemplate>();
        for (var entry : entries) {
            map.put(entry.id(), entry);
        }
        this.orderedTemplates = List.copyOf(entries);
        this.templates = Map.copyOf(map);
    }

    public List<SolutionTemplate> all() {
        return orderedTemplates;
    }

    public SolutionTemplate get(String id) {
        var template = templates.get(id);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + id);
        }
        return template;
    }

    public PlanSpec instantiate(String id, PlanContext context) {
        var template = get(id);
        return new PlanSpec(
                "plan_" + UUID.randomUUID().toString().substring(0, 8),
                template.title() + ": " + template.outcome(),
                context,
                template.steps()
        );
    }

    private SolutionTemplate retailLoyaltyWinback() {
        return template(
                "retail-loyalty-winback",
                "Retail loyalty winback",
                "Retail and Consumer Goods",
                "Complex",
                "Unify Commerce, Loyalty, Service, and Marketing signals to recover high-value customers showing purchase lapse and recent service friction.",
                "Create a governed churn-risk audience and prepare a Marketing Cloud winback activation.",
                List.of("Data 360", "Marketing Cloud", "Commerce Cloud", "Service Cloud", "Loyalty Management"),
                List.of(
                        "Uses Data 360 as the profile and segmentation layer.",
                        "Commerce and Loyalty attributes shape value and lapse logic.",
                        "Service Cloud case friction is used as suppression or personalization context.",
                        "Activation is prepared for Marketing Cloud, but execution remains approval-gated."
                ),
                "Retail High Value Lapse Risk",
                "Marketing Cloud Engagement - Winback Journey",
                """
                        SELECT unified_individual_id, lifetime_value, loyalty_tier, days_since_last_purchase, open_case_count
                        FROM UnifiedIndividual
                        WHERE lifetime_value > 7500
                          AND days_since_last_purchase > 60
                          AND loyalty_tier IN ('Gold', 'Platinum')
                          AND marketing_consent = 'OptIn'
                        LIMIT 100
                        """
        );
    }

    private SolutionTemplate financialHouseholdExpansion() {
        return template(
                "financial-household-expansion",
                "Financial household expansion",
                "Financial Services",
                "Complex",
                "Find profitable households with product gaps by combining Data 360 unified profiles, Financial Services Cloud householding, Sales Cloud pipeline, and engagement signals.",
                "Create a relationship-expansion segment for advisor review and Sales Cloud campaign handoff.",
                List.of("Data 360", "Financial Services Cloud", "Sales Cloud", "Marketing Cloud"),
                List.of(
                        "Household-level logic belongs in calculated insights or source DMOs before segmentation.",
                        "Open opportunities are used as suppression to avoid duplicate sales motions.",
                        "Activation target should be advisor-owned, not mass marketing, unless consent and suitability rules pass."
                ),
                "Financial Household Expansion",
                "Sales Cloud Campaign - Advisor Handoff",
                """
                        SELECT unified_household_id, primary_contact_id, total_assets, product_gap_score, open_opportunity_count
                        FROM UnifiedHousehold
                        WHERE total_assets > 250000
                          AND product_gap_score > 0.72
                          AND open_opportunity_count = 0
                          AND contactable = true
                        LIMIT 100
                        """
        );
    }

    private SolutionTemplate healthcareCareGapOutreach() {
        return template(
                "healthcare-care-gap-outreach",
                "Healthcare care-gap outreach",
                "Healthcare and Life Sciences",
                "Regulated",
                "Coordinate Data 360, Health Cloud, Service Cloud, and Marketing Cloud around patients with consented preventive-care gaps.",
                "Create a consent-filtered care-gap audience for care-team review before outreach.",
                List.of("Data 360", "Health Cloud", "Service Cloud", "Marketing Cloud"),
                List.of(
                        "Consent and minimum-necessary data controls are part of the segment criteria.",
                        "PHI-bearing columns should be masked in previews and not stored in workflow history.",
                        "Activation should route to a care management queue or approved journey, not ad platforms."
                ),
                "Consented Preventive Care Gap",
                "Service Cloud Care Queue",
                """
                        SELECT unified_individual_id, care_gap_type, primary_provider_id, days_overdue, consent_status
                        FROM UnifiedPatient
                        WHERE days_overdue > 30
                          AND care_gap_type IN ('Annual Wellness', 'Screening')
                          AND consent_status = 'OptIn'
                          AND deceased_flag = false
                        LIMIT 100
                        """
        );
    }

    private SolutionTemplate manufacturingServiceToSales() {
        return template(
                "manufacturing-service-to-sales",
                "Manufacturing service-to-sales",
                "Manufacturing",
                "Complex",
                "Use Data 360 to connect asset telemetry, Service Cloud case history, Manufacturing Cloud account plans, and Sales Cloud whitespace.",
                "Create an account audience for proactive renewal, replacement, or attach motions.",
                List.of("Data 360", "Service Cloud", "Manufacturing Cloud", "Sales Cloud"),
                List.of(
                        "Asset and case signals should be aggregated before segmentation.",
                        "Warranty expiry and repeat incidents indicate service-led commercial opportunity.",
                        "Activation should hand off to named account teams rather than broad campaigns."
                ),
                "Service Led Expansion Accounts",
                "Sales Cloud Account Team Queue",
                """
                        SELECT unified_account_id, asset_count, high_priority_case_count, warranty_expiring_assets, whitespace_score
                        FROM UnifiedAccount
                        WHERE high_priority_case_count >= 2
                          AND warranty_expiring_assets > 0
                          AND whitespace_score > 0.65
                        LIMIT 100
                        """
        );
    }

    private SolutionTemplate communicationsChurnDeflection() {
        return template(
                "communications-churn-deflection",
                "Communications churn deflection",
                "Communications",
                "Complex",
                "Blend usage, billing, NPS, Service Cloud cases, and Marketing Cloud engagement to identify subscribers needing save treatment.",
                "Create a churn-deflection audience and prepare an owned-channel activation.",
                List.of("Data 360", "Service Cloud", "Marketing Cloud", "Experience Cloud", "Revenue Cloud"),
                List.of(
                        "Billing and service interactions shape churn risk.",
                        "Suppression should exclude active disputes and do-not-contact subscribers.",
                        "Experience Cloud personalization can use the same segment after publication."
                ),
                "Subscriber Churn Deflection",
                "Marketing Cloud Engagement - Save Journey",
                """
                        SELECT unified_individual_id, subscription_id, churn_score, nps_score, recent_case_count, billing_issue_flag
                        FROM UnifiedSubscriber
                        WHERE churn_score > 0.78
                          AND recent_case_count > 0
                          AND billing_issue_flag = false
                          AND marketing_consent = 'OptIn'
                        LIMIT 100
                        """
        );
    }

    private SolutionTemplate automotiveConnectedService() {
        return template(
                "automotive-connected-service",
                "Automotive connected service",
                "Automotive",
                "Complex",
                "Combine connected-vehicle signals, owner profiles, Service Cloud appointments, and Marketing Cloud preferences for proactive maintenance.",
                "Create a service-due audience for dealer or owner outreach.",
                List.of("Data 360", "Automotive Cloud", "Service Cloud", "Marketing Cloud"),
                List.of(
                        "Vehicle telemetry should be mapped to owner consent and preferred dealer.",
                        "Open service appointments suppress duplicate outreach.",
                        "Activation can be dealer-assigned or consumer journey-based."
                ),
                "Connected Vehicle Service Due",
                "Service Cloud Dealer Queue",
                """
                        SELECT unified_individual_id, vehicle_id, preferred_dealer_id, service_due_score, open_appointment_count
                        FROM UnifiedVehicleOwner
                        WHERE service_due_score > 0.7
                          AND open_appointment_count = 0
                          AND contactable = true
                        LIMIT 100
                        """
        );
    }

    private SolutionTemplate b2bProductLedExpansion() {
        return template(
                "b2b-product-led-expansion",
                "B2B product-led expansion",
                "Technology",
                "Complex",
                "Use product usage, support health, Sales Cloud opportunities, and account hierarchy to find expansion-ready accounts.",
                "Create an expansion-ready account segment for sales and customer success orchestration.",
                List.of("Data 360", "Sales Cloud", "Service Cloud", "Slack", "Agentforce"),
                List.of(
                        "Usage and support signals should be aggregated to account level.",
                        "Open renewal risk can suppress or change the sales motion.",
                        "Slack or Agentforce handoff should summarize the evidence behind the recommendation."
                ),
                "Product Led Expansion Accounts",
                "Sales Cloud Campaign - Expansion Plays",
                """
                        SELECT unified_account_id, active_users, usage_growth_rate, support_health_score, open_expansion_opportunity_count
                        FROM UnifiedAccountUsage
                        WHERE active_users > 50
                          AND usage_growth_rate > 0.25
                          AND support_health_score > 0.7
                          AND open_expansion_opportunity_count = 0
                        LIMIT 100
                        """
        );
    }

    private SolutionTemplate nonprofitDonorStewardship() {
        return template(
                "nonprofit-donor-stewardship",
                "Nonprofit donor stewardship",
                "Nonprofit",
                "Moderate",
                "Combine gift history, program engagement, case interactions, and Marketing Cloud preferences to identify donors needing personalized stewardship.",
                "Create a donor stewardship audience for relationship-manager follow-up.",
                List.of("Data 360", "Nonprofit Cloud", "Marketing Cloud", "Service Cloud"),
                List.of(
                        "Gift history and engagement should be unified before segmentation.",
                        "Recent service requests can indicate relationship risk or special handling.",
                        "Activation should be relationship-manager led for major donors."
                ),
                "Major Donor Stewardship",
                "Marketing Cloud Engagement - Stewardship Journey",
                """
                        SELECT unified_individual_id, lifetime_giving, days_since_last_gift, engagement_score, assigned_relationship_manager_id
                        FROM UnifiedDonor
                        WHERE lifetime_giving > 10000
                          AND days_since_last_gift > 180
                          AND engagement_score > 0.55
                          AND contactable = true
                        LIMIT 100
                        """
        );
    }

    private SolutionTemplate template(
            String id,
            String title,
            String industry,
            String complexity,
            String summary,
            String outcome,
            List<String> clouds,
            List<String> architectureNotes,
            String segmentName,
            String destination,
            String sql
    ) {
        var previewId = "preview";
        var createSegmentId = "create_segment";
        var publishSegmentId = "publish_segment";
        var createActivationId = "create_activation";
        return new SolutionTemplate(
                id,
                title,
                industry,
                complexity,
                summary,
                outcome,
                clouds,
                architectureNotes,
                List.of(
                        new PlanStep(
                                previewId,
                                "Preview audience candidates",
                                Data360Action.QUERY,
                                Map.of("sql", cleanSql(sql), "limit", 100),
                                List.of(),
                                false
                        ),
                        new PlanStep(
                                createSegmentId,
                                "Create governed Data 360 segment",
                                Data360Action.CREATE_SEGMENT,
                                Map.of(
                                        "name", segmentName,
                                        "description", outcome,
                                        "criteriaFromStep", previewId
                                ),
                                List.of(previewId),
                                true
                        ),
                        new PlanStep(
                                publishSegmentId,
                                "Publish segment",
                                Data360Action.PUBLISH_SEGMENT,
                                Map.of("segmentIdFromStep", createSegmentId),
                                List.of(createSegmentId),
                                true
                        ),
                        new PlanStep(
                                createActivationId,
                                "Create activation draft",
                                Data360Action.CREATE_ACTIVATION,
                                Map.of(
                                        "name", segmentName + " Activation",
                                        "destination", destination,
                                        "segmentIdFromStep", createSegmentId
                                ),
                                List.of(publishSegmentId),
                                true
                        )
                )
        );
    }

    private String cleanSql(String sql) {
        return sql.strip().replaceAll("\\s+", " ");
    }
}
