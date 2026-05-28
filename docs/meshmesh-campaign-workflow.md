# MeshMesh Campaign Workflow Demo Spec

## Purpose

This artifact defines a self-contained demo story for an e-commerce re-engagement campaign workflow adapted to the Data360 Goal Cockpit. It follows the MeshMesh Complete Workflow Example pattern:

1. Identify inactive customers.
2. Analyze customer preferences.
3. Create personalized content.
4. Set up marketing automation.
5. Monitor performance.

In this version, Data360 owns the trusted audience, signal explanation, governance, and recovery measurement. SendGrid owns email activation, dynamic content delivery, and engagement event generation. The cockpit turns the workflow into an approval-gated goal loop that product and engineering can demo without sending real customer communications.

## Demo Goal

**Goal name:** E-Commerce Customer Re-Engagement

**Business outcome:** Recover dormant shoppers before they lapse permanently by launching a personalized SendGrid email sequence and measuring retention impact in Data360.

**Demo headline metrics:**

| Metric | Mock value | Demo meaning |
| --- | ---: | --- |
| Dormant customers found | 18,400 | Unified customer profiles matching inactivity rules. |
| Eligible for activation | 12,650 | Customers remaining after consent, suppression, and freshness checks. |
| Revenue at risk | $1.85M | Estimated 90-day recoverable revenue from inactive customers. |
| Target recovered revenue | $275K | Incremental purchase revenue target during the campaign window. |
| Holdout size | 10% | Control group retained for incrementality measurement. |
| Approval status | Waiting Approval | All write, publish, and activation steps are human-gated. |

## Product Fit

The existing Dormant Revenue Recovery cockpit proves that Data360 can turn fragmented account signals into governed recovery actions. This campaign workflow complements that story by showing the same operating model for high-volume customer lifecycle marketing:

| Dormant Revenue Recovery cockpit | E-Commerce Re-Engagement workflow |
| --- | --- |
| B2B account-level recovery. | B2C customer-level reactivation. |
| Sales Cloud, Service Cloud, product telemetry, Marketing Cloud, and Slack context. | Commerce, web/app behavior, loyalty, product catalog, consent, SendGrid engagement, and support context. |
| Sales tasks, case escalations, and activation drafts. | Data360 segments, SendGrid dynamic templates, automations, suppressions, and performance tracking. |
| Measures recoverable signed revenue and activation progress. | Measures recovered purchases, retention lift, incremental revenue, and deliverability health. |

The product message is: **Data360 Goal Cockpit can manage any governed recovery loop, from strategic account rescue to automated lifecycle campaigns.**

## Mock Scenario

**Retail brand:** Northstar Outfitters

**Season:** Early summer reactivation push

**Campaign premise:** Northstar has a large customer population that bought in the last two years but has not purchased recently. Some customers are still browsing, some have abandoned replenishable products, and some are high-value loyalty members who stopped engaging. The lifecycle team asks the cockpit to find inactive customers, explain why they qualify, draft a SendGrid campaign, and monitor recovery.

## Signal Sources

| Source | Example signals | Role in workflow |
| --- | --- | --- |
| Data360 unified profile | Identity graph, customer ID, consent, lifecycle score, preference summary | Creates trusted customer view and eligibility rules. |
| Commerce orders | Last purchase date, lifetime orders, average order value, category spend | Defines inactivity and revenue-at-risk estimates. |
| Web/app events | Product views, cart events, search terms, wishlist saves | Explains current intent and content personalization. |
| Product catalog | Category, margin band, inventory availability, recommended bundles | Selects content and offer guardrails. |
| Loyalty | Tier, points balance, reward expiration, member tenure | Adds loyalty-specific motivation. |
| Support/refunds | Open disputes, recent returns, unresolved cases | Suppresses risky contacts or routes service recovery. |
| SendGrid | Delivered, open, click, bounce, unsubscribe, spam report | Feeds activation health and engagement back into Data360. |

## Audience Definition

**Segment name:** `D360 Demo - E-Commerce Re-Engagement - Summer`

**Base criteria:**

| Rule | Mock value |
| --- | --- |
| Last purchase | More than 90 days ago and less than 730 days ago. |
| Email consent | Opted in to promotional email. |
| SendGrid status | Not globally suppressed, not hard bounced, no spam report. |
| Recent purchase exclusion | No completed order in the last 14 days. |
| Recent campaign exclusion | No lifecycle reactivation email in the last 21 days. |
| Support exclusion | No unresolved refund dispute or high-severity support case. |
| Data freshness | Unified profile updated in the last 48 hours. |

**Audience scoring:**

| Score | Inputs | Output |
| --- | --- | --- |
| `dormancy_score` | Days since purchase, days since site visit, days since SendGrid engagement | Measures likelihood of lapse. |
| `preference_confidence` | Category affinity, recent product views, repeat purchase pattern | Determines whether content can be personalized. |
| `recovery_value_score` | Predicted order value, margin, loyalty tier, churn risk | Ranks commercial impact. |
| `send_risk_score` | Bounce history, complaint risk, unsubscribe proximity, low engagement | Protects deliverability and consent posture. |
| `reengagement_priority_score` | Weighted blend of value, intent, preference confidence, and send risk | Drives cohort ordering in the cockpit. |

## Mock Cohorts

| Cohort | Count | Qualification | Primary personalization | SendGrid treatment | Measurement goal |
| --- | ---: | --- | --- | --- | --- |
| Lapsed loyalists | 3,200 | 3+ historical purchases, no purchase in 120+ days, loyalty tier Gold or Platinum | Points balance, member-only picks, free shipping threshold | Higher-touch 3-email sequence with loyalty framing | Recover high-value repeat buyers. |
| Category repeaters | 4,500 | Repeated purchases in one category, replenishment window exceeded | Favorite category, replenishable product, complementary bundle | Product recommendation sequence | Drive category-specific purchase. |
| Cart abandoners gone cold | 2,150 | Cart created 30-90 days ago, no purchase since | Abandoned category, price-drop or back-in-stock hook | Short urgency sequence | Convert existing intent. |
| Seasonal browsers | 2,800 | Recent browsing but no purchase in 180+ days | Seasonal collection, location/weather-aware category | Inspiration-led sequence | Reactivate interest without heavy discounting. |
| At-risk high send risk | 5,750 | Dormant and valuable but low email engagement or prior soft bounces | Preference center, low-frequency content | Suppressed from promo automation; optional one-time preference reset | Protect deliverability and regain consent signal. |

## Example Customer Cards

| Customer | Evidence | Preference readout | Recommended motion |
| --- | --- | --- | --- |
| Ana Rivera | Last purchase 146 days ago; viewed trail shoes 3 times this week; Gold loyalty member with 1,200 points expiring. | Trail running, women's footwear, size 8. | Send loyalty-framed trail kit email with points reminder. |
| Marcus Chen | Bought filters every 60 days, now 130 days since purchase; opened last how-to guide. | Gear maintenance, replenishment, practical content. | Send replenishment reminder with bundle CTA. |
| Priya Shah | Added insulated bottle and daypack to cart 42 days ago; no purchase; clicked a sale email once. | Hiking accessories, sale responsive. | Send cart-recovery variant with limited-time bundle. |
| Jordan Blake | No purchase in 220 days; viewed camping collection after a site search; no email engagement in 90 days. | Camping, family travel, low email confidence. | Send low-frequency preference reset before promotion. |
| Elena Gomez | Platinum member; returned last order and has unresolved refund case. | High value but active service blocker. | Suppress from campaign and create service follow-up evidence. |

## Cockpit Workflow

The workflow should reuse the existing cockpit mental model: Discover, Diagnose, Decide, Draft, Approve, Activate, Measure.

| Stage | Cockpit behavior | Data360 role | SendGrid role | Approval posture |
| --- | --- | --- | --- | --- |
| Discover | Preview dormant customer population and signal sources. | Query unified profiles, consent, commerce history, and engagement signals. | None. | Read-only. |
| Diagnose | Explain each cohort and sample customer with evidence. | Calculate scores, suppression reasons, and preference summaries. | Provide previous email engagement status if available. | Read-only. |
| Decide | Rank cohorts by recoverable revenue, send risk, and confidence. | Recommend eligible, holdout, and suppressed audiences. | None. | Read-only. |
| Draft | Prepare segment, activation draft, email variants, and measurement plan. | Create Data360 segment draft and holdout criteria. | Draft templates, dynamic data mapping, and automation branches. | Draft only. |
| Approve | Human reviews audience, content, suppressions, and launch rules. | Records approval evidence and immutable run metadata. | Receives no send command yet. | Required for publish and activation. |
| Activate | Publish segment and activate SendGrid workflow. | Publishes segment to approved SendGrid destination. | Syncs contacts, assigns template variants, starts automation. | Required before live send. |
| Measure | Track performance and retention impact. | Joins SendGrid events to orders, holdout, and lifecycle scores. | Emits engagement and deliverability events. | Read-only after launch. |

## Proposed Actions

| Action | Target system | Risk | Draft content | Expected impact |
| --- | --- | --- | --- | --- |
| Create re-engagement segment | Data360 | Medium | Segment criteria from audience rules above, including consent and suppression filters. | Reusable governed audience for lifecycle activation. |
| Create holdout segment | Data360 | Medium | Random 10% of eligible audience stratified by cohort and value band. | Enables incrementality measurement. |
| Publish segment for activation | Data360 | High | Publish eligible contacts and cohort attributes to SendGrid destination. | Makes approved audience available for email activation. |
| Create SendGrid activation draft | Data360 / SendGrid | High | Activation destination: SendGrid. Include `goal_run_id`, `cohort_id`, `content_variant_id`, and `holdout_flag`. | Lifecycle team can review before send. |
| Draft dynamic email templates | SendGrid | Medium | Templates use approved product/category modules and personalization fields. | Allows content to vary without separate campaigns per cohort. |
| Configure automation branches | SendGrid | High | Entry source: Data360 eligible segment. Exit on purchase, unsubscribe, bounce, or suppression update. | Starts governed multi-step sequence. |
| Configure Event Webhook ingest | SendGrid / Data360 | Medium | Capture delivered, open, click, bounce, unsubscribe, spam report, and deferred events with campaign metadata. | Feeds measurement and deliverability monitoring. |
| Publish performance dashboard | Data360 Goal Cockpit | Low | Impact cards for recovered revenue, conversion, retention lift, holdout delta, and send health. | Shows whether the campaign is recovering revenue safely. |

## Email Sequence

The sequence is intentionally short for demo clarity. Timing can be accelerated in the mock cockpit.

| Step | Timing | Audience | Subject pattern | Content logic | Exit criteria |
| --- | --- | --- | --- | --- | --- |
| Email 1: Relevance hook | Day 0 | Eligible non-holdout contacts | `Still thinking about {{preferred_category}}?` | Lead with recently viewed or historically purchased category. Loyalty members see points balance. | Purchase, unsubscribe, hard bounce, spam report. |
| Email 2: Personalized proof | Day 3 | Contacts with no purchase after Email 1 | `Picked for your next {{activity_theme}}` | Show 3 product modules selected from category affinity, margin guardrails, and inventory. | Purchase, unsubscribe, hard bounce, spam report. |
| Email 3: Offer nudge | Day 7 | Contacts who clicked or opened but did not purchase | `A member offer to get you back outside` | Offer level depends on value band and discount sensitivity. High-margin products prefer free shipping. | Purchase, unsubscribe, hard bounce, spam report. |
| Email 4: Preference reset | Day 14 | Non-engaged contacts with send risk below threshold | `What should we send you next?` | Preference-center CTA, low-frequency opt-down, and category selection. | Preference update, unsubscribe, hard bounce, spam report. |

**Cohort-specific copy examples:**

| Cohort | Email 1 angle | Email 2 angle | Email 3 angle | Email 4 angle |
| --- | --- | --- | --- | --- |
| Lapsed loyalists | "Your points can still unlock summer picks." | "New arrivals based on your past favorites." | "Free shipping for Gold members this week." | "Choose the member updates you still want." |
| Category repeaters | "Time to refresh your usual kit?" | "Recommended add-ons for your last category." | "Bundle and save on your replenishment list." | "Tell us what category you are shopping next." |
| Cart abandoners gone cold | "Your saved picks are still available." | "Customers also bought these with your cart item." | "A short-window incentive on your saved category." | "Want fewer sale reminders?" |
| Seasonal browsers | "Summer-ready gear from your recent browsing." | "Top-rated picks for your next trip." | "Free shipping over the seasonal threshold." | "Pick your favorite outdoor interests." |

## Personalization Fields

| Field | Source | Example | Use |
| --- | --- | --- | --- |
| `goal_run_id` | Data360 Goal Cockpit | `goal-run-ecomm-reengagement-q2` | Joins activation events to cockpit run. |
| `unified_customer_id` | Data360 | `uc_100245` | Stable measurement key. |
| `cohort_id` | Data360 | `lapsed_loyalist` | Branching and KPI grouping. |
| `content_variant_id` | Data360 / SendGrid | `trail_loyalty_points_v1` | Attribution by creative. |
| `preferred_category` | Data360 | `Trail running` | Subject and hero module. |
| `recommended_products` | Data360 | SKU list | Dynamic product modules. |
| `loyalty_points_balance` | Loyalty | `1200` | Loyalty message block. |
| `offer_code` | Commerce / Data360 | `SUMMERBACK10` | Controlled offer exposure. |
| `suppression_reason` | Data360 | `open_refund_case` | Exclusion explanation in cockpit. |

## SendGrid Activation Design

The demo should frame SendGrid as the execution destination, not the system of record.

**Contact sync:**

- Data360 publishes only approved non-holdout eligible contacts.
- Each contact includes cohort, value band, preference fields, and campaign metadata.
- Contacts with consent or suppression changes are removed from the active automation on the next sync.

**Template design:**

- Use one dynamic template family with cohort-aware modules.
- Use SendGrid dynamic data for subject, hero category, product recommendations, offer code, and preference-center URL.
- Include unsubscribe group and preference center links in every message.
- Include `goal_run_id`, `cohort_id`, `content_variant_id`, and `unified_customer_id` as custom arguments or equivalent metadata for event attribution.

**Automation rules:**

- Entry: contact enters approved Data360 segment and is not in holdout.
- Delay: 3 days between first and second message, 4 days between second and third, 7 days before preference reset.
- Branch: clicked or opened but no purchase receives the offer nudge; no engagement receives the preference reset.
- Exit: purchase, unsubscribe, hard bounce, spam report, consent withdrawal, support dispute, or manual pause.
- Safety: daily send cap and cohort-level throttling protect deliverability during demo launches.

**Event return path:**

- SendGrid Event Webhook events flow into the Data360 ingestion mock.
- Data360 normalizes engagement events into the goal run using `goal_run_id` and `unified_customer_id`.
- The cockpit displays raw delivery health separately from business recovery to avoid treating opens as revenue impact.

## Measurement Plan

Data360 measures whether the campaign recovered customers, not just whether email was sent.

| KPI family | KPI | Formula or interpretation | Demo target |
| --- | --- | --- | --- |
| Audience quality | Eligible activation rate | Eligible contacts / dormant customers found | 65%+ |
| Audience quality | Suppression rate | Suppressed contacts / dormant customers found | Explainable, not minimized blindly |
| Deliverability | Delivery rate | Delivered / accepted by SendGrid | 97%+ |
| Deliverability | Bounce rate | Bounces / accepted | Under 2% |
| Deliverability | Spam complaint rate | Spam reports / delivered | Under 0.1% |
| Engagement | Unique click rate | Unique clickers / delivered | 4%+ |
| Engagement | Preference update rate | Preference updates / delivered | 1%+ |
| Recovery | Recovered customers | Customers with qualifying purchase after exposure | 1,150 |
| Recovery | Recovered revenue | Qualifying order revenue attributed to campaign | $275K |
| Recovery | Revenue per delivered email | Recovered revenue / delivered emails | Used for cohort ranking |
| Retention | 30-day repeat purchase rate | Recovered customers with second purchase in 30 days | 18%+ |
| Retention | Lifecycle score lift | Post-campaign lifecycle score minus baseline | +8 points |
| Incrementality | Holdout-adjusted lift | Conversion rate exposed cohort minus matched holdout | +2.5 percentage points |

**Attribution tiers:**

| Tier | Definition | Confidence |
| --- | --- | --- |
| Direct click recovery | Customer clicked campaign email and purchased within 7 days. | High |
| Engaged recovery | Customer opened or clicked and purchased within 14 days. | Medium |
| Holdout-adjusted recovery | Exposed cohort outperformed stratified holdout. | High for aggregate incrementality |
| Organic recovery | Customer purchased but had no SendGrid engagement. | Low; shown separately |

## Cockpit Views

### Goal Summary

Show the same executive structure as Dormant Revenue Recovery:

- Goal title: `E-Commerce Customer Re-Engagement`
- Goal summary: `Recover dormant shoppers with Data360-governed SendGrid activation.`
- Status: `Waiting Approval`
- Business metric: `$1.85M revenue at risk`
- Activation target: `SendGrid automation draft`
- Approval count: number of write, publish, and activation actions waiting approval

### Audience And Evidence

The account list from Dormant Revenue Recovery becomes a cohort and customer sample browser:

- Cohort cards show count, revenue at risk, send risk, preference confidence, and recommended motion.
- Customer cards show identity, inactivity reason, preference summary, suppression status, and evidence signals.
- Evidence should always answer: "Why this customer, why this message, why now, and why is it safe to send?"

### Action Queue

The proposed action queue should look familiar to the existing demo:

- Data360 segment creation is medium risk.
- Segment publishing is high risk.
- SendGrid activation creation is high risk.
- Template drafting is medium risk.
- Event webhook setup is medium risk.
- Performance dashboard publishing is low risk.

### Email Preview

Product needs a lightweight preview panel for:

- Selected cohort.
- Subject line variant.
- Personalization fields.
- Example product modules.
- Suppression and exit rules.
- Approval status.

### Performance

After activation, the cockpit should separate operational email health from recovery impact:

- Send health: accepted, delivered, bounced, unsubscribed, spam reports.
- Engagement: opens, clicks, preference updates.
- Recovery: purchases, recovered revenue, revenue per delivered email.
- Retention: repeat purchase rate, lifecycle score lift, churn-risk movement.
- Incrementality: exposed versus holdout conversion and revenue.

## Demo Script

1. **Open Dormant Revenue Recovery first.** Show that the existing cockpit already handles governed account recovery with evidence, approvals, and impact metrics.
2. **Switch to E-Commerce Customer Re-Engagement.** Explain that the same goal loop now runs for high-volume customer marketing.
3. **Discover the audience.** Show 18,400 dormant customers, 12,650 eligible contacts, and 10% holdout.
4. **Inspect evidence.** Open Ana Rivera and Elena Gomez to contrast a good activation candidate with a suppressed high-value customer.
5. **Review campaign draft.** Show cohort-specific SendGrid template logic and automation branches.
6. **Approve safely.** Approve segment creation, holdout creation, SendGrid draft creation, and webhook ingest, then leave live activation gated unless the demo calls for execution.
7. **Measure recovery.** Switch to mock post-launch state and show delivered emails, recovered customers, recovered revenue, holdout lift, and retention score lift.
8. **Tie back to Data360.** Close with Data360 as the system that knows who to contact, what to say, whether it is safe, and whether it worked.

## Engineering Notes

**Mock-first behavior:**

- The first implementation should not send real email.
- SendGrid actions can be represented as draft objects with deterministic IDs.
- Event Webhook payloads can be seeded from mock events tied to `goal_run_id`.
- The cockpit can offer a `post-launch demo state` toggle that swaps in performance metrics.

**PlanSpec mapping:**

| Existing action | Campaign equivalent |
| --- | --- |
| `data360.query` | Preview dormant customer candidates and cohort counts. |
| `data360.createSegment` | Create eligible audience and holdout segments. |
| `data360.publishSegment` | Publish approved audience to SendGrid destination. |
| `data360.createActivation` | Create SendGrid campaign or automation draft. |
| `data360.runActivation` | Start mock SendGrid activation after approval. |

**Suggested mock IDs:**

| Object | ID |
| --- | --- |
| Goal pack | `ecommerce-customer-reengagement` |
| Goal run | `goal-run-ecomm-reengagement-q2` |
| Data360 segment | `seg-ecomm-reengagement-summer` |
| Holdout segment | `seg-ecomm-reengagement-holdout` |
| SendGrid activation | `sg-activation-reengagement-summer` |
| SendGrid template family | `sg-template-reengagement-dynamic-v1` |

**Minimum demo data entities:**

- `CampaignGoalSummary`
- `CustomerCohort`
- `CustomerSample`
- `EvidenceSignal`
- `ProposedAction`
- `EmailSequenceStep`
- `PersonalizationField`
- `ImpactMetric`
- `SendGridEventSummary`

## Acceptance Criteria

- The workflow can be explained as a direct adaptation of the MeshMesh inactive customer re-engagement flow.
- Data360 is clearly responsible for identity, segmentation, consent, explanation, governance, and recovery measurement.
- SendGrid is clearly responsible for email activation, templates, automation, and engagement events.
- The mock audience includes cohorts, sample customers, preference evidence, suppression logic, and value estimates.
- The campaign includes a concrete multi-step email sequence with branch and exit rules.
- KPIs include deliverability, engagement, recovered revenue, retention, and holdout-adjusted incrementality.
- The story explicitly complements the existing Dormant Revenue Recovery cockpit rather than replacing it.
- No real customer data or real email send is required for the demo.

## Reference Points

- SendGrid contacts and custom fields: https://www.twilio.com/docs/sendgrid/ui/managing-contacts
- SendGrid segmentation: https://www.twilio.com/docs/sendgrid/ui/managing-contacts/segmenting-your-contacts
- SendGrid automations: https://www.twilio.com/docs/sendgrid/ui/sending-email/getting-started-with-automation
- SendGrid dynamic templates and Handlebars: https://www.twilio.com/docs/sendgrid/for-developers/sending-email/using-handlebars
- SendGrid Event Webhook: https://www.twilio.com/docs/sendgrid/for-developers/tracking-events/event
