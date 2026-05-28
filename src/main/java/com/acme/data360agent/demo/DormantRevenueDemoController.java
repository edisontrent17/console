package com.acme.data360agent.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/demo/dormant-revenue-recovery")
public class DormantRevenueDemoController {
    private static final int DORMANT_USERS_FOUND = 18400;
    private static final int ELIGIBLE_USERS = 12650;
    private static final int HOLDOUT_USERS = 1265;
    private static final int ACTIVATION_USERS = ELIGIBLE_USERS - HOLDOUT_USERS;
    private static final double REVENUE_AT_RISK = 1850000;

    private final Map<String, String> actionStatuses = new ConcurrentHashMap<>();

    public DormantRevenueDemoController() {
        seedStatuses();
    }

    @GetMapping
    public DormantRevenueDemoResponse demo() {
        seedStatuses();
        return buildResponse();
    }

    @PostMapping("/actions/{actionId}/approve")
    public DormantRevenueDemoResponse approve(@PathVariable String actionId) {
        requireAction(actionId);
        actionStatuses.put(actionId, "Approved");
        return buildResponse();
    }

    @PostMapping("/actions/{actionId}/execute")
    public DormantRevenueDemoResponse execute(@PathVariable String actionId) {
        requireAction(actionId);
        actionStatuses.put(actionId, "Executed");
        return buildResponse();
    }

    @PostMapping("/actions/{actionId}/reject")
    public DormantRevenueDemoResponse reject(@PathVariable String actionId) {
        requireAction(actionId);
        actionStatuses.put(actionId, "Rejected");
        return buildResponse();
    }

    @PostMapping("/reset")
    public DormantRevenueDemoResponse reset() {
        actionStatuses.clear();
        seedStatuses();
        return buildResponse();
    }

    private void seedStatuses() {
        for (var action : baseActions()) {
            actionStatuses.putIfAbsent(action.id(), "Waiting Approval");
        }
    }

    private void requireAction(String actionId) {
        seedStatuses();
        if (!actionStatuses.containsKey(actionId)) {
            throw new IllegalArgumentException("Unknown demo action: " + actionId);
        }
    }

    private DormantRevenueDemoResponse buildResponse() {
        var accounts = accounts();
        var actions = actionsWithStatuses(baseActions());
        var waiting = actions.stream().filter(action -> "Waiting Approval".equals(action.status())).count();
        var approved = actions.stream().filter(action -> "Approved".equals(action.status())).count();
        var executed = actions.stream().filter(action -> "Executed".equals(action.status())).count();
        var emailActions = actions.stream().filter(action -> "Draft Email".equals(action.type())).toList();
        var approvedEmails = emailActions.stream().filter(action -> "Approved".equals(action.status()) || "Executed".equals(action.status())).count();
        var sentEmails = emailActions.stream().filter(action -> "Executed".equals(action.status())).count();
        var approvedAudience = approvedEmails == 0 ? 0 : ACTIVATION_USERS;
        var sentAudience = sentEmails == 0 ? 0 : ACTIVATION_USERS;
        var deliveredUsers = sentEmails == 0 ? 0 : 11043;
        var openedUsers = sentEmails == 0 ? 0 : 3644;
        var clickedUsers = sentEmails == 0 ? 0 : 626;
        var activatedUsers = sentEmails == 0 ? 0 : 615;
        var retainedUsers = sentEmails == 0 ? 0 : 188;
        var recoveredRevenue = sentEmails == 0 ? 0 : 275000;

        var summary = new GoalSummary(
                "dormant-revenue-recovery",
                "Dormant Revenue Recovery",
                "Recover 12,650 eligible dormant users with approved Salesforce and SendGrid activation, then measure recovery and retention in Data360.",
                "Waiting Approval",
                "default",
                REVENUE_AT_RISK,
                ELIGIBLE_USERS,
                accounts.stream().filter(account -> "Critical".equals(account.priority())).count(),
                waiting,
                "SendGrid audience of 11,385 users plus Salesforce recovery work queue"
        );

        var impact = new ArrayList<ImpactMetric>();
        impact.add(new ImpactMetric("Recoverable revenue", REVENUE_AT_RISK, REVENUE_AT_RISK, "currency", "Revenue at risk across the eligible dormant audience; the account cards are explainable samples."));
        impact.add(new ImpactMetric("Dormant users found", DORMANT_USERS_FOUND, DORMANT_USERS_FOUND, "count", "Unified users with inactivity or activation gaps detected by Data360."));
        impact.add(new ImpactMetric("Eligible audience", ELIGIBLE_USERS, DORMANT_USERS_FOUND, "count", "Users remaining after consent, suppression, freshness, and recent-activity checks."));
        impact.add(new ImpactMetric("Holdout users", HOLDOUT_USERS, ELIGIBLE_USERS, "count", "A 10% holdout gives the demo a credible retention and recovery measurement story."));
        impact.add(new ImpactMetric("Actions waiting approval", waiting, 0, "count", "Every write, publish, and activation action remains human-gated."));
        impact.add(new ImpactMetric("Approved actions", approved, actions.size(), "count", "Actions reviewed by the revenue operator."));
        impact.add(new ImpactMetric("Executed actions", executed, actions.size(), "count", "Actions that would write back to Salesforce, Data360, Service Cloud, Marketing Cloud, or Slack."));
        impact.add(new ImpactMetric("Support blockers found", 3, 0, "count", "Service Cloud signals that explain why revenue is dormant."));
        impact.add(new ImpactMetric("Recovery rate", sentEmails == 0 ? 0 : 5.4, 8, "percent", "Recovered users completed a post-send activation or purchase milestone."));
        impact.add(new ImpactMetric("Retained users", retainedUsers, activatedUsers == 0 ? 1 : activatedUsers, "count", "Retained users remain active after the measurement window."));
        impact.add(new ImpactMetric("Revenue recovered", recoveredRevenue, REVENUE_AT_RISK, "currency", "Revenue is counted only after Data360 sees activation or retention outcome signals."));

        return new DormantRevenueDemoResponse(
                summary,
                dataSources(),
                accounts,
                actions,
                impact,
                planSteps(),
                emailActivation(approvedAudience, sentAudience),
                recoveryFunnel(approvedAudience, sentAudience, deliveredUsers, openedUsers, clickedUsers, activatedUsers, retainedUsers, recoveredRevenue),
                goalRunSpec(actions, approvedAudience, sentAudience, deliveredUsers, openedUsers, clickedUsers, activatedUsers, retainedUsers, recoveredRevenue)
        );
    }

    private List<SignalSource> dataSources() {
        return List.of(
                new SignalSource("Data360", "Unified account profile, identity resolution, calculated recovery score", "Trusted"),
                new SignalSource("Sales Cloud", "Opportunity, contract, owner, activity, and task context", "Connected"),
                new SignalSource("Product Telemetry", "Usage events, active users, activation milestones", "Mock"),
                new SignalSource("Service Cloud", "Open onboarding cases and support blockers", "Connected"),
                new SignalSource("Marketing Cloud", "Engagement, pricing-page visits, campaign response", "Mock"),
                new SignalSource("SendGrid", "Approved recovery emails, delivery events, opens, clicks, bounces, and unsubscribes", "Draft"),
                new SignalSource("Slack", "Approval and revenue-room notification target", "Draft")
        );
    }

    private List<RecoveryAccount> accounts() {
        return List.of(
                new RecoveryAccount(
                        "acme",
                        "Acme Manufacturing",
                        "Enterprise",
                        "Critical",
                        240000,
                        42,
                        0.81,
                        0.92,
                        14,
                        0.94,
                        "Escalate onboarding case, create AE follow-up task, and send executive sponsor recovery email.",
                        List.of(
                                new EvidenceSignal("acme-contract", "Contract", "Sales Cloud", "Contract signed 42 days ago", "Closed-won contract has no corresponding activation event in product telemetry.", "High"),
                                new EvidenceSignal("acme-usage", "Product Usage", "Product Telemetry", "No product usage events", "Unified usage stream shows zero active users after contract start.", "High"),
                                new EvidenceSignal("acme-case", "Service Case", "Service Cloud", "Open P1 onboarding case", "SSO provisioning issue is blocking launch.", "High")
                        )
                ),
                new RecoveryAccount(
                        "northstar",
                        "Northstar Retail",
                        "Commercial",
                        "High",
                        180000,
                        31,
                        0.88,
                        0.20,
                        21,
                        0.87,
                        "Create rep task and draft follow-up anchored on recent pricing and implementation-page visits.",
                        List.of(
                                new EvidenceSignal("northstar-intent", "Marketing Engagement", "Marketing Cloud", "Pricing page visits increased", "Buying committee visited pricing and implementation content seven times in the last week.", "High"),
                                new EvidenceSignal("northstar-activity", "Sales Activity", "Sales Cloud", "No AE activity in 21 days", "No logged call, email, meeting, or task completion since purchase confirmation.", "High"),
                                new EvidenceSignal("northstar-service", "Service Case", "Service Cloud", "No active blockers", "No open high-severity support cases, so recommended motion is sales-led.", "Medium")
                        )
                ),
                new RecoveryAccount(
                        "apex",
                        "Apex Logistics",
                        "Mid-Market",
                        "High",
                        90000,
                        27,
                        0.66,
                        0.45,
                        10,
                        0.73,
                        "Create adoption-health task and route enablement resources before usage drops further.",
                        List.of(
                                new EvidenceSignal("apex-usage", "Product Usage", "Product Telemetry", "Usage dropped to zero", "Initial pilot users stopped logging in after implementation workshop.", "High"),
                                new EvidenceSignal("apex-guide", "Marketing Engagement", "Marketing Cloud", "Enablement content opened", "Operations lead opened implementation guide twice with no sales follow-up.", "Medium"),
                                new EvidenceSignal("apex-case", "Service Case", "Service Cloud", "One medium-priority case", "Data import question remains open, but not marked launch-blocking.", "Medium")
                        )
                ),
                new RecoveryAccount(
                        "brightpath",
                        "BrightPath Health",
                        "Enterprise",
                        "Critical",
                        300000,
                        55,
                        0.74,
                        0.85,
                        7,
                        0.91,
                        "Escalate support blocker and schedule joint CS plus AE recovery call.",
                        List.of(
                                new EvidenceSignal("brightpath-contract", "Contract", "Sales Cloud", "Contract signed 55 days ago", "Enterprise contract is nearing internal activation SLA breach.", "High"),
                                new EvidenceSignal("brightpath-case", "Service Case", "Service Cloud", "Security review case blocking rollout", "Open questionnaire case has no owner update in four business days.", "High"),
                                new EvidenceSignal("brightpath-engagement", "Marketing Engagement", "Data360", "Champion engagement declined", "Primary champion engagement score dropped from 82 to 41 over two weeks.", "Medium")
                        )
                ),
                new RecoveryAccount(
                        "meridian",
                        "Meridian Robotics",
                        "Commercial",
                        "High",
                        130000,
                        36,
                        0.79,
                        0.25,
                        18,
                        0.82,
                        "Draft partner-led activation email and create AE task for manufacturing use-case workshop.",
                        List.of(
                                new EvidenceSignal("meridian-partner", "Marketing Engagement", "Marketing Cloud", "Partner implementation interest", "Partner manager clicked manufacturing implementation workshop invitation.", "Medium"),
                                new EvidenceSignal("meridian-usage", "Product Usage", "Product Telemetry", "License assigned, no activation", "Licenses provisioned but no admin setup event has been received.", "High"),
                                new EvidenceSignal("meridian-activity", "Sales Activity", "Sales Cloud", "No AE activity in 18 days", "The account owner has no completed recovery task after purchase handoff.", "High")
                        )
                )
        );
    }

    private List<ProposedAction> baseActions() {
        return List.of(
                action("create-segment", "Create dormant revenue segment", null, null, "Create Segment", "Data360", "Medium", "Create a governed audience using activation gap, revenue at risk, intent, service blockers, and AE inactivity.", "Segment: Dormant Revenue Recovery - Q2. Criteria: activation_gap_days > 21 AND recovery_priority_score >= 0.70.", "Reusable governed audience for activation."),
                action("publish-segment", "Publish segment for activation review", null, null, "Publish Segment", "Data360", "High", "Publishing makes the audience available for downstream Salesforce and Marketing activation.", "Publish only after RevOps approval; do not run external activation yet.", "Audience available to approved destinations."),
                action("activation-draft", "Create activation draft", null, null, "Create Activation Draft", "Marketing Cloud", "High", "Prepare a journey draft without sending customer communications.", "Journey draft: Dormant Revenue Recovery. Entry source: Data360 segment. Suppress accounts with open legal or privacy flags.", "Lifecycle team can review messaging before launch."),
                action("acme-task", "Acme AE task", "acme", "Acme Manufacturing", "Create Task", "Sales Cloud", "Medium", "High value account has no activation and an open onboarding blocker.", "Task: Call Acme executive sponsor and align on SSO unblock plan within 24 hours.", "Recover $240k activation path."),
                action("acme-escalation", "Acme support escalation", "acme", "Acme Manufacturing", "Escalate Case", "Service Cloud", "Medium", "P1 onboarding case is blocking product launch.", "Escalate SSO provisioning case to onboarding war room.", "Remove launch blocker."),
                action("northstar-task", "Northstar AE task", "northstar", "Northstar Retail", "Create Task", "Sales Cloud", "Low", "High buying intent with stale AE activity.", "Task: Follow up with Northstar on implementation timeline and success criteria.", "Book activation call this week."),
                action("northstar-email", "Northstar draft email", "northstar", "Northstar Retail", "Draft Email", "Sales Cloud", "Low", "Recent pricing and implementation engagement gives a relevant outreach hook.", "Subject: Turning your rollout plan into a launch date. Body: I noticed your team is reviewing implementation options; here are three paths we can use to get live quickly...", "Increase response likelihood."),
                action("apex-task", "Apex adoption task", "apex", "Apex Logistics", "Create Task", "Sales Cloud", "Low", "Usage dropped after initial workshop.", "Task: Schedule adoption reset call with operations lead and share import checklist.", "Restore usage before account goes cold."),
                action("brightpath-escalation", "BrightPath case escalation", "brightpath", "BrightPath Health", "Escalate Case", "Service Cloud", "Medium", "Security review is blocking enterprise activation.", "Escalate security questionnaire case and request owner update before Friday.", "Protect $300k activation."),
                action("brightpath-email", "BrightPath exec email", "brightpath", "BrightPath Health", "Draft Email", "Sales Cloud", "Medium", "Champion engagement is declining while blocker remains open.", "Subject: Clearing the security path for launch. Body: We have a focused plan to close the security item and confirm your launch timeline...", "Re-engage champion and executive sponsor."),
                action("meridian-task", "Meridian workshop task", "meridian", "Meridian Robotics", "Create Task", "Sales Cloud", "Low", "Partner-led workshop is the fastest activation path.", "Task: Invite Meridian and partner manager to manufacturing activation workshop.", "Convert partner intent into activation motion.")
        );
    }

    private ProposedAction action(String id, String title, String accountId, String accountName, String type, String targetSystem, String risk, String rationale, String draftContent, String expectedImpact) {
        return new ProposedAction(id, title, accountId, accountName, type, targetSystem, "Waiting Approval", true, risk, rationale, draftContent, expectedImpact);
    }

    private List<ProposedAction> actionsWithStatuses(List<ProposedAction> actions) {
        return actions.stream()
                .map(action -> new ProposedAction(
                        action.id(),
                        action.title(),
                        action.accountId(),
                        action.accountName(),
                        action.type(),
                        action.targetSystem(),
                        actionStatuses.getOrDefault(action.id(), action.status()),
                        action.needsApproval(),
                        action.risk(),
                        action.rationale(),
                        action.draftContent(),
                        action.expectedImpact()
                ))
                .toList();
    }

    private List<PlanStage> planSteps() {
        return List.of(
                new PlanStage("Discover", "Query Data360 unified account context and calculate dormant revenue candidates.", "Read"),
                new PlanStage("Diagnose", "Explain each account with contract, usage, support, marketing, and activity evidence.", "Read"),
                new PlanStage("Decide", "Rank accounts by recoverable revenue, blocker severity, and recovery priority.", "Read"),
                new PlanStage("Draft", "Prepare segment, task, email, case-escalation, and activation draft actions.", "Draft"),
                new PlanStage("Approve", "Route write, publish, and activation steps through human approval.", "Approval"),
                new PlanStage("Activate", "Send approved email drafts through SendGrid and write approved operational actions to downstream systems.", "Write"),
                new PlanStage("Measure", "Join SendGrid events with Data360 usage, support, and contract signals to calculate recovery and retention.", "Monitor")
        );
    }

    private EmailActivation emailActivation(long approvedEmails, long sentEmails) {
        return new EmailActivation(
                "SendGrid",
                "Demo mode",
                "recovery@your-verified-domain.example",
                "Routes to a safe test inbox until production sending is enabled.",
                "11,385 eligible users after consent, suppression, and 10% holdout controls",
                approvedEmails,
                sentEmails,
                List.of(
                        new EmailStep("Day 0", "Executive unblock email", "Personalized recovery note using account evidence and recommended motion.", "Send after approval"),
                        new EmailStep("Day 2", "Proof and next step", "Case study or implementation checklist based on preferred product category/use case.", "Send if no click or reply"),
                        new EmailStep("Day 7", "Final activation offer", "Time-boxed activation workshop invitation with owner handoff.", "Send if no meeting booked")
                ),
                List.of("processed", "delivered", "open", "click", "bounce", "dropped", "spam_report", "unsubscribe"),
                "SendGrid events are keyed by contact/account IDs and joined in Data360 with product usage, support resolution, meeting, and contract activation signals."
        );
    }

    private List<FunnelStage> recoveryFunnel(long approvedEmails, long sentEmails, long deliveredUsers, long openedEmails, long clickedEmails, long activatedAccounts, long retainedAccounts, double recoveredRevenue) {
        return List.of(
                new FunnelStage("Identified", ELIGIBLE_USERS, ELIGIBLE_USERS, "Data360 selected eligible dormant users from unified customer context."),
                new FunnelStage("Holdout", HOLDOUT_USERS, ELIGIBLE_USERS, "10% of eligible users are reserved for incrementality measurement."),
                new FunnelStage("Email approved", approvedEmails, ACTIVATION_USERS, "Human approved the SendGrid recovery activation."),
                new FunnelStage("Sent", sentEmails, ACTIVATION_USERS, "Approved email actions were executed through SendGrid in demo mode."),
                new FunnelStage("Delivered", deliveredUsers, ACTIVATION_USERS, "SendGrid delivery events confirm the message reached the mailbox provider."),
                new FunnelStage("Opened", openedEmails, ACTIVATION_USERS, "Open events are engagement signals, not recovery outcomes."),
                new FunnelStage("Clicked", clickedEmails, ACTIVATION_USERS, "Click events indicate intent and feed back into the recovery score."),
                new FunnelStage("Activated", activatedAccounts, ACTIVATION_USERS, "Data360 sees product usage, purchase, or activation milestone completion."),
                new FunnelStage("Retained", retainedAccounts, activatedAccounts == 0 ? 1 : activatedAccounts, "Recovered users remain active after the measurement window."),
                new FunnelStage("Revenue recovered", recoveredRevenue, REVENUE_AT_RISK, "currency", "Recognized only when activation or retention outcome is observed.")
        );
    }

    private GoalRunSpec goalRunSpec(List<ProposedAction> actions, long approvedAudience, long sentAudience, long deliveredUsers, long openedUsers, long clickedUsers, long activatedUsers, long retainedUsers, double recoveredRevenue) {
        return new GoalRunSpec(
                "goal-run-dormant-recovery-q2",
                "2026-05-28.demo",
                planContract(),
                executeContract(actions),
                monitorContract(approvedAudience, sentAudience, deliveredUsers, openedUsers, clickedUsers, activatedUsers, retainedUsers, recoveredRevenue),
                replanPolicy()
        );
    }

    private PlanContract planContract() {
        return new PlanContract(
                "Recover dormant users and accounts with governed Salesforce and SendGrid activation.",
                new AudienceContract(
                        DORMANT_USERS_FOUND,
                        ELIGIBLE_USERS,
                        HOLDOUT_USERS,
                        ACTIVATION_USERS,
                        "Unified users with activation_gap_days > 21, contactable status, no recent activation, and no suppression event.",
                        "10% stratified holdout by value band, priority score, and segment to measure incrementality."
                ),
                List.of(
                        new SpecStep("plan-query", "plan", "Preview dormant audience", "data360.query", Map.of(
                                "dmo", "UnifiedIndividual",
                                "criteria", "activation_gap_days > 21 AND recovery_priority_score >= 0.70",
                                "limit", 12650
                        ), List.of(), false),
                        new SpecStep("plan-holdout", "plan", "Assign holdout", "data360.createSegment", Map.of(
                                "segmentName", "Dormant Revenue Recovery - Holdout",
                                "size", HOLDOUT_USERS,
                                "method", "stratified_random"
                        ), List.of("plan-query"), true),
                        new SpecStep("plan-activation-segment", "plan", "Create activation segment", "data360.createSegment", Map.of(
                                "segmentName", "Dormant Revenue Recovery - Activation",
                                "eligibleUsers", ACTIVATION_USERS,
                                "excludeSegment", "Dormant Revenue Recovery - Holdout"
                        ), List.of("plan-holdout"), true)
                )
        );
    }

    private ExecuteContract executeContract(List<ProposedAction> actions) {
        return new ExecuteContract(
                "human_gated",
                "Read-only planning can run automatically. Data360 publish, Salesforce writes, Service Cloud escalations, and SendGrid activation require approval.",
                "Mock mode records action state only. A production connector would write to Data360, Salesforce, Service Cloud, or SendGrid after approval.",
                actions.stream()
                        .map(action -> new ExecuteAction(
                                action.id(),
                                action.title(),
                                action.targetSystem(),
                                action.type(),
                                action.status(),
                                action.needsApproval(),
                                writesTo(action.targetSystem(), action.type())
                        ))
                        .toList()
        );
    }

    private String writesTo(String targetSystem, String actionType) {
        if ("Data360".equals(targetSystem)) {
            return switch (actionType) {
                case "Create Segment" -> "Segment metadata and audience definition";
                case "Publish Segment" -> "Activation target publication metadata";
                default -> "Data360 goal metadata";
            };
        }
        if ("Sales Cloud".equals(targetSystem)) {
            return "Task, draft email, or account-owner work queue";
        }
        if ("Service Cloud".equals(targetSystem)) {
            return "Case escalation or service work queue";
        }
        if ("Marketing Cloud".equals(targetSystem)) {
            return "Activation or journey draft";
        }
        return "Demo action state";
    }

    private MonitorContract monitorContract(long approvedAudience, long sentAudience, long deliveredUsers, long openedUsers, long clickedUsers, long activatedUsers, long retainedUsers, double recoveredRevenue) {
        return new MonitorContract(
                "14-day activation window plus 30-day retention window",
                List.of(
                        new MonitorSource("SendGrid Event Webhook", "processed, delivered, open, click, bounce, dropped, spam_report, unsubscribe", "SendGrid_Email_Event__dlm"),
                        new MonitorSource("Salesforce outcomes", "task completion, case escalation resolution, meeting booked", "Salesforce_Recovery_Outcome__dlm"),
                        new MonitorSource("Product and commerce outcomes", "activation milestone, product usage, purchase, retained status", "D360_Goal_Outcome__dlm")
                ),
                List.of("goal_run_id", "activation_id", "unified_customer_id", "cohort_id", "content_variant_id", "holdout_flag"),
                List.of(
                        new MonitorMetric("approved_audience", "Approved audience", approvedAudience, ACTIVATION_USERS, "count", "Users approved for SendGrid activation after holdout and suppression."),
                        new MonitorMetric("sent", "Sent", sentAudience, ACTIVATION_USERS, "count", "Approved SendGrid activation events emitted."),
                        new MonitorMetric("delivered", "Delivered", deliveredUsers, ACTIVATION_USERS, "count", "SendGrid delivered events ingested into Data360."),
                        new MonitorMetric("opened", "Opened", openedUsers, ACTIVATION_USERS, "count", "Open is engagement evidence, not a recovery outcome."),
                        new MonitorMetric("clicked", "Clicked", clickedUsers, ACTIVATION_USERS, "count", "Click removes the user from generic reminder segments and routes to high-intent follow-up."),
                        new MonitorMetric("activated", "Activated", activatedUsers, ACTIVATION_USERS, "count", "Data360 observed a product usage, purchase, or activation milestone."),
                        new MonitorMetric("retained", "Retained", retainedUsers, activatedUsers == 0 ? 1 : activatedUsers, "count", "Recovered users still active after the retention window."),
                        new MonitorMetric("revenue_recovered", "Revenue recovered", recoveredRevenue, REVENUE_AT_RISK, "currency", "Revenue counted only after outcome DMOs confirm recovery.")
                ),
                List.of(
                        new SegmentRule("Day 2 reminder exclusion", "Exclude users with clicked_day0 = true, purchased_after_send = true, activated_after_send = true, unsubscribe = true, spam_report = true, or hard_bounce = true."),
                        new SegmentRule("High-intent follow-up", "Users with clicked_day0 = true and no activation are routed to a sales or CS follow-up draft instead of a generic email reminder."),
                        new SegmentRule("Suppression", "Bounce, unsubscribe, spam_report, legal hold, privacy suppression, or open service dispute removes the user from activation.")
                ),
                new MonitorState(
                        sentAudience > 0 ? "monitoring" : "waiting_for_activation",
                        sentAudience > 0,
                        sentAudience > 0 ? "SendGrid events have landed in Data360 DMOs and updated recovery metrics." : "Monitor spec is ready; no SendGrid activation events have been ingested yet."
                )
        );
    }

    private ReplanPolicy replanPolicy() {
        return new ReplanPolicy(
                "draft_only_until_approved",
                List.of(
                        "delivery_rate_below_95_percent",
                        "click_rate_below_3_percent_after_day_2",
                        "activation_rate_below_holdout_adjusted_target",
                        "support_blocker_unresolved_after_48_hours"
                ),
                List.of(
                        "create_high_intent_sales_followup",
                        "suppress_low_quality_cohort",
                        "adjust_sendgrid_template_variant",
                        "escalate_service_blocker"
                )
        );
    }

    public record DormantRevenueDemoResponse(
            GoalSummary summary,
            List<SignalSource> signalSources,
            List<RecoveryAccount> accounts,
            List<ProposedAction> actions,
            List<ImpactMetric> impact,
            List<PlanStage> plan,
            EmailActivation emailActivation,
            List<FunnelStage> recoveryFunnel,
            GoalRunSpec goalRunSpec
    ) {
    }

    public record GoalSummary(
            String id,
            String title,
            String command,
            String status,
            String dataspace,
            double recoverableRevenue,
            int accountsIdentified,
            long criticalAccounts,
            long actionsWaitingApproval,
            String activationTarget
    ) {
    }

    public record SignalSource(String name, String detail, String status) {
    }

    public record RecoveryAccount(
            String id,
            String name,
            String segment,
            String priority,
            double contractValue,
            int activationGapDays,
            double intentScore,
            double supportBlockerScore,
            int aeInactivityDays,
            double recoveryPriorityScore,
            String recommendedMotion,
            List<EvidenceSignal> evidence
    ) {
    }

    public record EvidenceSignal(String id, String type, String source, String signal, String detail, String confidence) {
    }

    public record ProposedAction(
            String id,
            String title,
            String accountId,
            String accountName,
            String type,
            String targetSystem,
            String status,
            boolean needsApproval,
            String risk,
            String rationale,
            String draftContent,
            String expectedImpact
    ) {
    }

    public record ImpactMetric(String label, double current, double target, String unit, String narrative) {
    }

    public record PlanStage(String title, String detail, String effect) {
    }

    public record EmailActivation(
            String provider,
            String mode,
            String fromAddress,
            String safetyRule,
            String audience,
            long approvedEmails,
            long sentEmails,
            List<EmailStep> sequence,
            List<String> trackingEvents,
            String outcomeJoin
    ) {
    }

    public record EmailStep(String timing, String title, String content, String rule) {
    }

    public record FunnelStage(String label, double current, double target, String unit, String narrative) {
        public FunnelStage(String label, double current, double target, String narrative) {
            this(label, current, target, "count", narrative);
        }
    }

    public record GoalRunSpec(
            String id,
            String version,
            PlanContract plan,
            ExecuteContract execute,
            MonitorContract monitor,
            ReplanPolicy replan
    ) {
    }

    public record PlanContract(
            String goal,
            AudienceContract audience,
            List<SpecStep> steps
    ) {
    }

    public record AudienceContract(
            int dormantUsersFound,
            int eligibleUsers,
            int holdoutUsers,
            int activationUsers,
            String eligibilityRule,
            String holdoutRule
    ) {
    }

    public record SpecStep(
            String id,
            String phase,
            String title,
            String action,
            Map<String, Object> input,
            List<String> dependsOn,
            boolean needsApproval
    ) {
    }

    public record ExecuteContract(
            String mode,
            String approvalPolicy,
            String sideEffectBoundary,
            List<ExecuteAction> actions
    ) {
    }

    public record ExecuteAction(
            String id,
            String title,
            String targetSystem,
            String operation,
            String status,
            boolean needsApproval,
            String writesTo
    ) {
    }

    public record MonitorContract(
            String measurementWindow,
            List<MonitorSource> sources,
            List<String> joinKeys,
            List<MonitorMetric> metrics,
            List<SegmentRule> segmentRules,
            MonitorState state
    ) {
    }

    public record MonitorSource(String name, String events, String writesToDmo) {
    }

    public record MonitorMetric(String key, String label, double current, double target, String unit, String formula) {
    }

    public record SegmentRule(String name, String rule) {
    }

    public record MonitorState(String status, boolean eventsIngested, String narrative) {
    }

    public record ReplanPolicy(String mode, List<String> triggers, List<String> allowedDrafts) {
    }
}
