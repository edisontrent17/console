// Seed the Data360 Goal Agent demo with mock Dormant Revenue Recovery data.
// Run after deploying the metadata in salesforce/force-app.

delete [SELECT Id FROM D360_Approval__c WHERE Proposed_Action__r.Name LIKE 'D360 Demo%'];
delete [SELECT Id FROM D360_Proposed_Action__c WHERE Name LIKE 'D360 Demo%'];
delete [SELECT Id FROM D360_Evidence__c WHERE Name LIKE 'D360 Demo%'];
delete [SELECT Id FROM D360_Impact_Metric__c WHERE Name LIKE 'D360 Demo%'];
delete [SELECT Id FROM D360_Recovery_Account__c WHERE Name LIKE 'D360 Demo%'];
delete [SELECT Id FROM D360_Goal_Run__c WHERE Goal_Pack__r.Name LIKE 'D360 Demo%'];
delete [SELECT Id FROM D360_Goal_Pack__c WHERE Name LIKE 'D360 Demo%'];

D360_Goal_Pack__c goalPack = new D360_Goal_Pack__c(
    Name = 'D360 Demo - Dormant Revenue Recovery',
    Status__c = 'Active',
    Persona__c = 'RevOps, Sales Leadership, Customer Success',
    Business_Outcome__c = 'Recover signed or high-intent customers who have not activated before quarter end.',
    Required_Signals__c = 'Account, Opportunity, Contract, Product Usage, Service Cases, Marketing Engagement, Sales Activity',
    Allowed_Actions__c = 'Preview audience, create governed segment, publish segment, create sales tasks, draft emails, escalate blockers, create activation draft, publish impact snapshot',
    Success_Metrics__c = 'Recoverable revenue identified, accounts contacted, meetings booked, blockers escalated, accounts activated, revenue recovered'
);
insert goalPack;

D360_Goal_Run__c goalRun = new D360_Goal_Run__c(
    Goal_Pack__c = goalPack.Id,
    Run_Status__c = 'Waiting Approval',
    Environment__c = 'Sandbox',
    Dataspace__c = 'default',
    Total_Recoverable_Revenue__c = 940000,
    Accounts_Identified__c = 5,
    Started_At__c = System.now().addMinutes(-32),
    Summary__c = 'Agent found five signed or high-intent accounts with activation gaps, service blockers, or stale sales motion. Writes are waiting for human approval.'
);
insert goalRun;

List<D360_Recovery_Account__c> accounts = new List<D360_Recovery_Account__c>{
    new D360_Recovery_Account__c(
        Name = 'D360 Demo - Acme Manufacturing',
        Account_Name__c = 'Acme Manufacturing',
        Unified_Account_Id__c = 'ua_acme_mfg',
        Goal_Run__c = goalRun.Id,
        Segment__c = 'Enterprise',
        Contract_Value__c = 240000,
        Activation_Gap_Days__c = 42,
        Intent_Score__c = 0.81,
        Support_Blocker_Score__c = 0.92,
        AE_Inactivity_Days__c = 14,
        Recovery_Priority_Score__c = 0.94,
        Priority__c = 'Critical',
        Recommended_Motion__c = 'Escalate onboarding case, create AE follow-up task, and send executive sponsor recovery email.'
    ),
    new D360_Recovery_Account__c(
        Name = 'D360 Demo - Northstar Retail',
        Account_Name__c = 'Northstar Retail',
        Unified_Account_Id__c = 'ua_northstar_retail',
        Goal_Run__c = goalRun.Id,
        Segment__c = 'Commercial',
        Contract_Value__c = 180000,
        Activation_Gap_Days__c = 31,
        Intent_Score__c = 0.88,
        Support_Blocker_Score__c = 0.20,
        AE_Inactivity_Days__c = 21,
        Recovery_Priority_Score__c = 0.87,
        Priority__c = 'High',
        Recommended_Motion__c = 'Create rep task and draft follow-up anchored on recent pricing and implementation-page visits.'
    ),
    new D360_Recovery_Account__c(
        Name = 'D360 Demo - Apex Logistics',
        Account_Name__c = 'Apex Logistics',
        Unified_Account_Id__c = 'ua_apex_logistics',
        Goal_Run__c = goalRun.Id,
        Segment__c = 'Mid-Market',
        Contract_Value__c = 90000,
        Activation_Gap_Days__c = 27,
        Intent_Score__c = 0.66,
        Support_Blocker_Score__c = 0.45,
        AE_Inactivity_Days__c = 10,
        Recovery_Priority_Score__c = 0.73,
        Priority__c = 'High',
        Recommended_Motion__c = 'Create adoption-health task and route enablement resources before usage drops further.'
    ),
    new D360_Recovery_Account__c(
        Name = 'D360 Demo - BrightPath Health',
        Account_Name__c = 'BrightPath Health',
        Unified_Account_Id__c = 'ua_brightpath_health',
        Goal_Run__c = goalRun.Id,
        Segment__c = 'Enterprise',
        Contract_Value__c = 300000,
        Activation_Gap_Days__c = 55,
        Intent_Score__c = 0.74,
        Support_Blocker_Score__c = 0.85,
        AE_Inactivity_Days__c = 7,
        Recovery_Priority_Score__c = 0.91,
        Priority__c = 'Critical',
        Recommended_Motion__c = 'Escalate support blocker and schedule joint CS plus AE recovery call.'
    ),
    new D360_Recovery_Account__c(
        Name = 'D360 Demo - Meridian Robotics',
        Account_Name__c = 'Meridian Robotics',
        Unified_Account_Id__c = 'ua_meridian_robotics',
        Goal_Run__c = goalRun.Id,
        Segment__c = 'Commercial',
        Contract_Value__c = 130000,
        Activation_Gap_Days__c = 36,
        Intent_Score__c = 0.79,
        Support_Blocker_Score__c = 0.25,
        AE_Inactivity_Days__c = 18,
        Recovery_Priority_Score__c = 0.82,
        Priority__c = 'High',
        Recommended_Motion__c = 'Draft partner-led activation email and create AE task for manufacturing use-case workshop.'
    )
};
insert accounts;

D360_Recovery_Account__c acme = accounts[0];
D360_Recovery_Account__c northstar = accounts[1];
D360_Recovery_Account__c apex = accounts[2];
D360_Recovery_Account__c brightpath = accounts[3];
D360_Recovery_Account__c meridian = accounts[4];

List<D360_Evidence__c> evidence = new List<D360_Evidence__c>{
    new D360_Evidence__c(Name='D360 Demo - Acme contract signal', Recovery_Account__c=acme.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Contract', Signal__c='Contract signed 42 days ago', Detail__c='Closed-won contract has no corresponding activation event in product telemetry.', Confidence__c='High', Source_System__c='Sales Cloud', Observed_At__c=System.now().addDays(-42)),
    new D360_Evidence__c(Name='D360 Demo - Acme usage gap', Recovery_Account__c=acme.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Product Usage', Signal__c='No product usage events', Detail__c='Unified usage stream shows zero active users after contract start.', Confidence__c='High', Source_System__c='Product Telemetry', Observed_At__c=System.now().addDays(-1)),
    new D360_Evidence__c(Name='D360 Demo - Acme onboarding blocker', Recovery_Account__c=acme.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Service Case', Signal__c='Open P1 onboarding case', Detail__c='Service Cloud case indicates SSO provisioning issue blocking launch.', Confidence__c='High', Source_System__c='Service Cloud', Observed_At__c=System.now().addDays(-3)),
    new D360_Evidence__c(Name='D360 Demo - Northstar intent', Recovery_Account__c=northstar.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Marketing Engagement', Signal__c='Pricing page visits increased', Detail__c='Buying committee visited pricing and implementation content seven times in the last week.', Confidence__c='High', Source_System__c='Marketing Cloud', Observed_At__c=System.now().addDays(-2)),
    new D360_Evidence__c(Name='D360 Demo - Northstar sales inactivity', Recovery_Account__c=northstar.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Sales Activity', Signal__c='No AE activity in 21 days', Detail__c='No logged call, email, meeting, or task completion since purchase confirmation.', Confidence__c='High', Source_System__c='Sales Cloud', Observed_At__c=System.now().addDays(-21)),
    new D360_Evidence__c(Name='D360 Demo - Northstar clean service posture', Recovery_Account__c=northstar.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Service Case', Signal__c='No active blockers', Detail__c='No open high-severity support cases, so recommended motion is sales-led.', Confidence__c='Medium', Source_System__c='Service Cloud', Observed_At__c=System.now().addDays(-1)),
    new D360_Evidence__c(Name='D360 Demo - Apex usage drop', Recovery_Account__c=apex.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Product Usage', Signal__c='Usage dropped to zero', Detail__c='Initial pilot users stopped logging in after implementation workshop.', Confidence__c='High', Source_System__c='Product Telemetry', Observed_At__c=System.now().addDays(-5)),
    new D360_Evidence__c(Name='D360 Demo - Apex enablement signal', Recovery_Account__c=apex.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Marketing Engagement', Signal__c='Enablement content opened', Detail__c='Operations lead opened implementation guide twice but no sales follow-up was logged.', Confidence__c='Medium', Source_System__c='Marketing Cloud', Observed_At__c=System.now().addDays(-4)),
    new D360_Evidence__c(Name='D360 Demo - Apex case friction', Recovery_Account__c=apex.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Service Case', Signal__c='One medium-priority case', Detail__c='Data import question remains open, but not marked launch-blocking.', Confidence__c='Medium', Source_System__c='Service Cloud', Observed_At__c=System.now().addDays(-6)),
    new D360_Evidence__c(Name='D360 Demo - BrightPath contract aging', Recovery_Account__c=brightpath.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Contract', Signal__c='Contract signed 55 days ago', Detail__c='Enterprise contract is nearing internal activation SLA breach.', Confidence__c='High', Source_System__c='Sales Cloud', Observed_At__c=System.now().addDays(-55)),
    new D360_Evidence__c(Name='D360 Demo - BrightPath support blocker', Recovery_Account__c=brightpath.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Service Case', Signal__c='Security review case blocking rollout', Detail__c='Open security questionnaire case has no owner update in four business days.', Confidence__c='High', Source_System__c='Service Cloud', Observed_At__c=System.now().addDays(-4)),
    new D360_Evidence__c(Name='D360 Demo - BrightPath declining engagement', Recovery_Account__c=brightpath.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Marketing Engagement', Signal__c='Champion engagement declined', Detail__c='Primary champion engagement score dropped from 82 to 41 over two weeks.', Confidence__c='Medium', Source_System__c='Data360', Observed_At__c=System.now().addDays(-2)),
    new D360_Evidence__c(Name='D360 Demo - Meridian partner intent', Recovery_Account__c=meridian.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Marketing Engagement', Signal__c='Partner implementation interest', Detail__c='Partner manager clicked manufacturing implementation workshop invitation.', Confidence__c='Medium', Source_System__c='Marketing Cloud', Observed_At__c=System.now().addDays(-3)),
    new D360_Evidence__c(Name='D360 Demo - Meridian usage gap', Recovery_Account__c=meridian.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Product Usage', Signal__c='License assigned, no activation', Detail__c='Licenses provisioned but no admin setup event has been received.', Confidence__c='High', Source_System__c='Product Telemetry', Observed_At__c=System.now().addDays(-1)),
    new D360_Evidence__c(Name='D360 Demo - Meridian AE gap', Recovery_Account__c=meridian.Id, Goal_Run__c=goalRun.Id, Evidence_Type__c='Sales Activity', Signal__c='No AE activity in 18 days', Detail__c='The account owner has no completed recovery task after purchase handoff.', Confidence__c='High', Source_System__c='Sales Cloud', Observed_At__c=System.now().addDays(-18))
};
insert evidence;

List<D360_Proposed_Action__c> actions = new List<D360_Proposed_Action__c>{
    new D360_Proposed_Action__c(Name='D360 Demo - Create dormant revenue segment', Goal_Run__c=goalRun.Id, Action_Type__c='Create Segment', Target_System__c='Data360', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Medium', Rationale__c='Create a governed audience of recovery accounts using activation gap, revenue at risk, intent, service blockers, and AE inactivity.', Draft_Content__c='Segment: Dormant Revenue Recovery - Q2. Criteria: activation_gap_days > 21 AND recovery_priority_score >= 0.70.', Expected_Impact__c='Reusable governed audience for sales and lifecycle activation.'),
    new D360_Proposed_Action__c(Name='D360 Demo - Publish segment for activation review', Goal_Run__c=goalRun.Id, Action_Type__c='Publish Segment', Target_System__c='Data360', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='High', Rationale__c='Publishing makes the audience available for downstream Salesforce and Marketing activation.', Draft_Content__c='Publish only after RevOps approval; do not run external activation yet.', Expected_Impact__c='Audience becomes available to approved destinations.'),
    new D360_Proposed_Action__c(Name='D360 Demo - Create activation draft', Goal_Run__c=goalRun.Id, Action_Type__c='Create Activation Draft', Target_System__c='Marketing Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='High', Rationale__c='Prepare a journey draft without sending customer communications.', Draft_Content__c='Journey draft: Dormant Revenue Recovery. Entry source: Data360 segment. Suppress accounts with open legal or privacy flags.', Expected_Impact__c='Lifecycle team can review messaging before launch.'),
    new D360_Proposed_Action__c(Name='D360 Demo - Acme AE task', Goal_Run__c=goalRun.Id, Recovery_Account__c=acme.Id, Action_Type__c='Create Task', Target_System__c='Sales Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Medium', Rationale__c='High value account has no activation and open onboarding blocker.', Draft_Content__c='Task: Call Acme executive sponsor and align on SSO unblock plan within 24 hours.', Expected_Impact__c='Recover $240k activation path.'),
    new D360_Proposed_Action__c(Name='D360 Demo - Acme support escalation', Goal_Run__c=goalRun.Id, Recovery_Account__c=acme.Id, Action_Type__c='Escalate Case', Target_System__c='Service Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Medium', Rationale__c='P1 onboarding case is blocking product launch.', Draft_Content__c='Escalate SSO provisioning case to onboarding war room.', Expected_Impact__c='Remove launch blocker.'),
    new D360_Proposed_Action__c(Name='D360 Demo - Northstar AE task', Goal_Run__c=goalRun.Id, Recovery_Account__c=northstar.Id, Action_Type__c='Create Task', Target_System__c='Sales Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Low', Rationale__c='High buying intent with stale AE activity.', Draft_Content__c='Task: Follow up with Northstar on implementation timeline and success criteria.', Expected_Impact__c='Book activation call this week.'),
    new D360_Proposed_Action__c(Name='D360 Demo - Northstar draft email', Goal_Run__c=goalRun.Id, Recovery_Account__c=northstar.Id, Action_Type__c='Draft Email', Target_System__c='Sales Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Low', Rationale__c='Recent pricing and implementation engagement gives a relevant outreach hook.', Draft_Content__c='Subject: Turning your rollout plan into a launch date. Body: I noticed your team is reviewing implementation options; here are three paths we can use to get live quickly...', Expected_Impact__c='Increase response likelihood.'),
    new D360_Proposed_Action__c(Name='D360 Demo - Apex adoption task', Goal_Run__c=goalRun.Id, Recovery_Account__c=apex.Id, Action_Type__c='Create Task', Target_System__c='Sales Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Low', Rationale__c='Usage dropped after initial workshop.', Draft_Content__c='Task: Schedule adoption reset call with operations lead and share import checklist.', Expected_Impact__c='Restore usage before account goes cold.'),
    new D360_Proposed_Action__c(Name='D360 Demo - BrightPath case escalation', Goal_Run__c=goalRun.Id, Recovery_Account__c=brightpath.Id, Action_Type__c='Escalate Case', Target_System__c='Service Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Medium', Rationale__c='Security review is blocking enterprise activation.', Draft_Content__c='Escalate security questionnaire case and request owner update before Friday.', Expected_Impact__c='Protect $300k activation.'),
    new D360_Proposed_Action__c(Name='D360 Demo - BrightPath exec email', Goal_Run__c=goalRun.Id, Recovery_Account__c=brightpath.Id, Action_Type__c='Draft Email', Target_System__c='Sales Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Medium', Rationale__c='Champion engagement is declining while blocker remains open.', Draft_Content__c='Subject: Clearing the security path for launch. Body: We have a focused plan to close the security item and confirm your launch timeline...', Expected_Impact__c='Re-engage champion and executive sponsor.'),
    new D360_Proposed_Action__c(Name='D360 Demo - Meridian workshop task', Goal_Run__c=goalRun.Id, Recovery_Account__c=meridian.Id, Action_Type__c='Create Task', Target_System__c='Sales Cloud', Action_Status__c='Waiting Approval', Needs_Approval__c=true, Risk_Level__c='Low', Rationale__c='Partner-led workshop is the fastest activation path.', Draft_Content__c='Task: Invite Meridian and partner manager to manufacturing activation workshop.', Expected_Impact__c='Convert partner intent into activation motion.')
};
insert actions;

List<D360_Approval__c> approvals = new List<D360_Approval__c>();
for (D360_Proposed_Action__c actionRecord : actions) {
    approvals.add(new D360_Approval__c(
        Proposed_Action__c = actionRecord.Id,
        Approval_Status__c = 'Waiting Approval',
        Approver_Role__c = actionRecord.Risk_Level__c == 'High' ? 'RevOps Director' : 'Sales Manager',
        Approval_Reason__c = 'Demo approval gate: review evidence and approve before any write, publish, or activation step executes.',
        Requested_At__c = System.now().addMinutes(-20)
    ));
}
insert approvals;

List<D360_Impact_Metric__c> metrics = new List<D360_Impact_Metric__c>{
    new D360_Impact_Metric__c(Name='Recoverable Revenue Identified', Goal_Run__c=goalRun.Id, Metric_Key__c='recoverable_revenue_identified', Metric_Unit__c='Currency', Baseline_Value__c=0, Current_Value__c=940000, Target_Value__c=1000000, Narrative__c='Revenue from five dormant signed or high-intent accounts.', Last_Updated__c=System.now()),
    new D360_Impact_Metric__c(Name='Accounts Identified', Goal_Run__c=goalRun.Id, Metric_Key__c='accounts_identified', Metric_Unit__c='Count', Baseline_Value__c=0, Current_Value__c=5, Target_Value__c=10, Narrative__c='Mock accounts selected using Data360 context and recovery priority score.', Last_Updated__c=System.now()),
    new D360_Impact_Metric__c(Name='Actions Waiting Approval', Goal_Run__c=goalRun.Id, Metric_Key__c='actions_waiting_approval', Metric_Unit__c='Count', Baseline_Value__c=0, Current_Value__c=11, Target_Value__c=0, Narrative__c='Every write, publish, and activation action is gated.', Last_Updated__c=System.now()),
    new D360_Impact_Metric__c(Name='Support Blockers Found', Goal_Run__c=goalRun.Id, Metric_Key__c='support_blockers_found', Metric_Unit__c='Count', Baseline_Value__c=0, Current_Value__c=3, Target_Value__c=0, Narrative__c='Service Cloud blockers that require escalation before activation.', Last_Updated__c=System.now()),
    new D360_Impact_Metric__c(Name='Expected Meetings Booked', Goal_Run__c=goalRun.Id, Metric_Key__c='expected_meetings_booked', Metric_Unit__c='Count', Baseline_Value__c=0, Current_Value__c=0, Target_Value__c=5, Narrative__c='Demo target after approved Sales Cloud tasks are executed.', Last_Updated__c=System.now()),
    new D360_Impact_Metric__c(Name='Activation Rate', Goal_Run__c=goalRun.Id, Metric_Key__c='activation_rate', Metric_Unit__c='Percent', Baseline_Value__c=0, Current_Value__c=0, Target_Value__c=40, Narrative__c='Measured after activation motions complete.', Last_Updated__c=System.now())
};
insert metrics;

System.debug('Seeded Data360 Goal Agent demo: ' + goalPack.Name + ' with ' + accounts.size() + ' recovery accounts, ' + evidence.size() + ' evidence signals, ' + actions.size() + ' proposed actions, and ' + metrics.size() + ' impact metrics.');
