# Data360 Goal Agent Salesforce Demo

This folder contains a Salesforce DX demo package for the **Dormant Revenue Recovery** goal pack.

It creates a mock Salesforce-native version of the Data360 goal-agent loop:

1. Goal pack
2. Goal run
3. Recovery accounts selected from trusted customer context
4. Evidence signals explaining each account
5. Proposed actions waiting for approval
6. Approval queue
7. Impact metrics

The package is intentionally sandbox-safe. It creates custom `D360_*` objects and mock records only; it does not modify standard Account, Opportunity, Case, Campaign, or Data Cloud records.

## Contents

- `force-app/main/default/applications/D360_Goal_Agent_Demo.app-meta.xml`  
  Lightning app for the demo.

- `force-app/main/default/objects/*`  
  Custom objects, fields, tabs, and list views.

- `force-app/main/default/permissionsets/D360_Goal_Agent_Demo_Admin.permissionset-meta.xml`  
  Permission set for demo access.

- `scripts/seedDormantRevenueRecovery.apex`  
  Idempotent mock-data seed script. It deletes only records whose names start with `D360 Demo`.

## Deploy

From this folder:

```bash
sf project deploy start --source-dir force-app --target-org <your-org-alias>
```

Assign the permission set:

```bash
sf org assign permset --name D360_Goal_Agent_Demo_Admin --target-org <your-org-alias>
```

Seed mock data:

```bash
sf apex run --file scripts/seedDormantRevenueRecovery.apex --target-org <your-org-alias>
```

Open the app:

```bash
sf org open --target-org <your-org-alias> --path lightning/app/D360_Goal_Agent_Demo
```

## Demo Talk Track

Open **Data360 Goal Agent Demo** and walk through:

1. **D360 Goal Packs**  
   Show `D360 Demo - Dormant Revenue Recovery` as a reusable business outcome, not a one-off prompt.

2. **D360 Goal Runs**  
   Show the current run with `$940k` recoverable revenue, five accounts identified, and status `Waiting Approval`.

3. **D360 Recovery Accounts**  
   Open accounts like `Acme Manufacturing` and `BrightPath Health`. These are mock unified accounts selected from Data360-style signals.

4. **D360 Evidence**  
   Show why the agent selected the account: contract age, usage gaps, support blockers, engagement, and sales inactivity.

5. **D360 Proposed Actions**  
   Show the governed action plan: create tasks, draft emails, escalate cases, create/publish a Data360 segment, and prepare a Marketing Cloud activation draft.

6. **D360 Approvals**  
   Show that writes and activation steps are not autonomous by default. They are waiting for human review.

7. **D360 Impact Metrics**  
   Close with business measurement: recoverable revenue, accounts identified, actions waiting approval, blockers found, expected meetings, and activation-rate target.

## Leadership Story

The point of the demo is not that a model can write a task or email. The point is:

> Data360 can turn fragmented customer signals into a trusted, governed, measurable business goal loop.

This mock package creates the Salesforce surface area needed to tell that story before wiring real Data360, Sales Cloud, Flow, Slack, Marketing Cloud, and Tableau actions.
