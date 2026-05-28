# Data360 Goal Agent Leadership Demo Story

## Demo Title

From dormant revenue to measured recovery.

## One-Line Pitch

Data360 Goal Agent turns trusted customer data into governed activation plans, routes risky actions through human approval, activates through Salesforce and SendGrid, and measures whether the business outcome actually happened.

## The Leadership Hook

Every company has revenue that is technically won but operationally asleep: customers signed, licenses provisioned, onboarding started, but no activation milestone ever happens. The data to find these accounts exists across Sales Cloud, Service Cloud, telemetry, marketing engagement, SendGrid events, and Data360. The problem is that no single operator can continuously inspect every signal, decide the best recovery motion, safely activate it, and then prove recovery.

The demo shows a goal-based agent that does exactly that.

It does not just answer a question. It runs a governed business loop:

```text
Business goal
-> Data360 trusted context
-> agent plan
-> human approval
-> Salesforce / SendGrid activation
-> Data360 outcome measurement
-> reusable goal template
```

## Demo Characters

**Priya, RevOps leader**

Priya owns post-sale revenue health for a B2B business. Her team has a quarter-end problem: signed customers are not activating quickly enough, and nobody trusts a spreadsheet assembled from stale CRM exports.

**Sam, lifecycle operations manager**

Sam is responsible for activation campaigns. Sam wants to send recovery emails but needs confidence that the audience is eligible, the message is relevant, and the company can measure revenue recovery beyond opens and clicks.

**Maya, Data360 platform owner**

Maya wants Data360 to become the governed decision and activation layer, not just a data store. She cares about identity, consent, calculated insights, explainability, destinations, and outcome measurement.

## What The Demo Proves

1. Data360 can identify a revenue problem from unified customer context.
2. The agent can turn the goal into a plan, evidence, and proposed actions.
3. Risky actions are approval-gated before anything writes downstream.
4. Activation can happen through Salesforce work items and SendGrid emails.
5. Data360 measures recovery and retention using outcome signals, not vanity engagement metrics.
6. The same pattern generalizes into a goal template library.

## Products In The Story

| Product or system | Demo role |
| --- | --- |
| Data360 | Unified account context, identity, calculated insights, audience selection, segment publication, event joins, outcome measurement. |
| External Goal Cockpit | The operator surface for goal runs, evidence, approvals, activation, and impact. |
| Salesforce Sales Cloud | Account, opportunity, owner, task, and follow-up context. |
| Salesforce Service Cloud | Onboarding blockers and support escalations. |
| SendGrid | Email activation, dynamic email sequence, delivery and engagement event stream. |
| Marketing Cloud | Future or optional journey destination for enterprise activation. |
| Anthropic model | Strong planner / orchestrator for decomposing the goal and evaluating the recovery plan. |
| OpenAI model | Strong worker / drafter / analyzer for content, evidence summarization, and action generation. |
| Goal Template Library | Packaged playbooks such as Dormant Revenue Recovery, E-Commerce Re-Engagement, Renewal Risk Rescue, and Loyalty Recovery. |

## Model Story

The demo should describe the model architecture without making the demo feel like a model benchmark.

**Anthropic as orchestrator**

The orchestrator reads the business goal and builds the plan:

- What data is needed?
- Which systems need to be queried?
- What actions are allowed?
- Which actions require approval?
- What measurement proves success?

**OpenAI as worker**

Workers execute bounded tasks:

- Summarize account evidence.
- Draft recovery emails.
- Generate Salesforce task language.
- Explain why a customer belongs in a segment.
- Create measurement narratives.

**Data360 as ground truth**

Neither model is the source of truth. Data360 supplies trusted context, calculated insights, identity joins, consent, suppression state, activation metadata, and outcome measurement.

## Live Demo Setup

Open the external app:

```text
http://localhost:8082/
```

Start from a clean state by pressing `Reset`.

The mock run should show:

| Metric | Value |
| --- | ---: |
| Recoverable revenue | $1,850,000 |
| Dormant users found | 18,400 |
| Eligible audience | 12,650 |
| Activation audience after 10% holdout | 11,385 |
| Actions waiting | 11 |
| Critical accounts | 2 |

The cockpit keeps five priority cards on screen as explainable samples from the larger audience:

| Account | Story |
| --- | --- |
| Acme Manufacturing | High-value contract, zero usage, P1 onboarding blocker. |
| BrightPath Health | Largest revenue risk, security review blocker, declining champion engagement. |
| Northstar Retail | Recent pricing intent, no AE activity, good candidate for email recovery. |
| Apex Logistics | Usage fell to zero after implementation workshop. |
| Meridian Robotics | Partner-led activation path exists but no owner action happened. |

## 12-Minute Demo Flow

### 1. Open With The Business Problem

Talk track:

```text
I am not going to start with a chatbot. I am going to start with a business goal:
recover signed customers with no activation, and prove whether we recovered revenue.
```

Show the goal strip:

- `$1,850,000` recoverable revenue
- `12,650` eligible users
- `11` actions waiting

Point:

The first screen is not a prompt box. It is an operating console for a measurable goal. The audience is large enough to measure retention and incrementality, while the sample cards make the evidence human-readable.

### 2. Show Data360 As The Decision Layer

Click `Acme Manufacturing`.

Talk track:

```text
Data360 is not just giving the agent account fields. It is producing a trusted, explainable view:
contract age, product usage, support blockers, intent, AE inactivity, and a recovery priority score.
```

Call out the evidence:

- Contract signed 42 days ago.
- No product usage events.
- Open P1 onboarding case.
- Recovery priority: 94%.
- Support risk: 92%.

Point:

The agent is grounded in evidence. It can show why Acme is prioritized before it asks anyone to approve an action.

### 3. Show The Goal Plan

Open `Data360 context and plan`.

Talk track:

```text
The plan follows a governed loop: discover, diagnose, decide, draft, approve, activate, measure.
The important product idea is that every goal template uses the same operating model.
```

Call out:

- Discover and diagnose are read-only.
- Draft creates candidate actions.
- Approve gates risk.
- Activate writes to downstream systems.
- Measure joins activation events with business outcomes.

Point:

This is how a goal template becomes reusable and governable.

### 4. Approve The Data360 Audience Gates

In `Next Actions`, approve and execute the global actions in order:

1. Create dormant revenue segment.
2. Publish segment for activation review.
3. Create activation draft.

Talk track:

```text
The agent can recommend a segment and activation plan, but it cannot publish or activate without approval.
That is the difference between an impressive demo and a product that enterprise admins could trust.
```

Point:

This is human-in-the-loop autonomy: fast, but bounded.

### 5. Show Salesforce Operational Recovery

Return to `Acme Manufacturing` or `BrightPath Health`.

Approve one operational action:

- Acme AE task
- Acme support escalation
- BrightPath case escalation

Talk track:

```text
Not every recovery motion is an email. Sometimes the right action is a Salesforce task or a Service Cloud escalation.
The agent chooses the motion based on the evidence.
```

Point:

The product is not "send more messages." The product is "pick the next best governed recovery action."

### 6. Show SendGrid Activation

Switch to `Northstar Retail` or `BrightPath Health`.

Progress the queue until the draft email action is visible. Approve and execute the email action.

Talk track:

```text
SendGrid is the activation channel. Data360 selected the audience and evidence, the agent drafted the message, a human approved it, and SendGrid emits delivery and engagement events back into the loop.
```

Call out the SendGrid panel:

- Demo mode uses a safe test inbox.
- Day 0: executive unblock email.
- Day 2: proof and next step.
- Day 7: final activation offer.
- Events: delivered, open, click, bounce, dropped, spam report, unsubscribe.

Point:

SendGrid makes activation real without pretending email engagement is the same thing as revenue recovery.

### 7. Show Measurement

After executing one or more email actions, point to:

- Recovery Funnel
- Impact

Talk track:

```text
Here is the key measurement line: opens and clicks are engagement signals.
Recovery is only counted when Data360 sees an activation or retention outcome.
```

Call out:

- Identified accounts.
- Email approved.
- Sent.
- Opened and clicked.
- Meeting booked.
- Activated.
- Retained.
- Revenue recovered.

Point:

This closes the loop from Data360 context to business outcome.

### 8. Expand To The MeshMesh Pattern

Transition:

```text
What we just saw for dormant B2B revenue is the same pattern described in the MeshMesh campaign workflow:
identify inactive customers, analyze preferences, create personalized content, set up automation, monitor performance.
Data360 makes that pattern trusted, governed, and measurable.
```

Then describe the second template:

**E-Commerce Customer Re-Engagement**

- Find dormant shoppers.
- Check consent and suppressions.
- Build cohorts: lapsed loyalists, category repeaters, cart abandoners, seasonal browsers.
- Draft SendGrid dynamic templates.
- Publish only approved non-holdout contacts.
- Measure recovered purchases, retention lift, and holdout-adjusted incrementality.

Point:

Dormant Revenue Recovery is the enterprise wedge. E-Commerce Re-Engagement shows the template library can scale to high-volume lifecycle marketing.

## Scale Requirement

The demo should never imply that five records are enough to prove recovery or retention. Five records are only the explainability surface. The meaningful measurement unit is the eligible audience:

| Layer | Demo scale | Why it matters |
| --- | ---: | --- |
| Dormant users found | 18,400 | Shows the agent is operating on a real activation population. |
| Eligible audience | 12,650 | Clears consent, suppression, freshness, and recent-activity rules. |
| Holdout | 1,265 | Enables incrementality and retention measurement. |
| Activation audience | 11,385 | Large enough for SendGrid campaign metrics and cohort comparison. |
| Priority samples | 5 | Lets leadership inspect why the agent made decisions. |

## The Compelling Leadership Story

### Before

RevOps, CS, and lifecycle teams manually assemble lists from CRM, support, marketing, and product data. They either move too slowly or they over-automate with weak context. Leadership sees activity metrics but cannot trust whether the work recovered revenue.

### After

Data360 Goal Agent continuously turns trusted customer context into measurable recovery motions. Operators approve the right actions, SendGrid and Salesforce activate them, and Data360 proves recovery and retention.

### Why Salesforce Wins

Salesforce already owns the systems where customer truth and customer action live:

- Sales Cloud knows the opportunity and owner.
- Service Cloud knows blockers.
- Data360 unifies the customer and calculates the audience.
- Marketing Cloud and SendGrid activate.
- Agentforce-style reasoning can plan and coordinate.
- The platform can govern approvals, audit trails, and measurement.

The winning product is not another agent UI. It is the governed business-goal layer across Salesforce customer data and activation.

## Demo Close

Use this close:

```text
This started as a single goal: recover signed customers with no activation.
But what we actually built is a repeatable goal loop.

Data360 decides who matters and why.
The agent proposes the plan and drafts the work.
Humans approve risky actions.
Salesforce and SendGrid activate.
Data360 measures whether the customer recovered.

That is the product: a governed goal template library for measurable customer growth.
```

## Objections And Answers

| Objection | Answer |
| --- | --- |
| Is this just a chatbot? | No. The main object is a goal run with evidence, approvals, actions, activation state, and outcome metrics. |
| Why Data360? | Because identity, consent, calculated insights, segmentation, activation metadata, and outcome joins must be trusted. |
| Why SendGrid? | SendGrid is a practical activation path for verified-domain email, dynamic templates, automations, and event webhooks. |
| What does Salesforce add? | Salesforce owns the operational systems: account owners, tasks, opportunities, cases, approvals, and enterprise governance. |
| What makes it safe? | Read-only diagnosis comes first; write, publish, and send actions require approval; demo mode routes email safely. |
| How do we prove value? | Recovery and retention are measured from Data360 outcome signals, with engagement treated only as supporting evidence. |
| Why use both Anthropic and OpenAI? | Use the strongest model for the job: one for orchestration and plan critique, another for bounded worker tasks like drafting and summarization. |
| What is the wedge? | Dormant Revenue Recovery: clear pain, clear data, clear activation, clear revenue measurement. |

## Product Market Fit Hypothesis

**Target buyer**

- Data360 leaders who need activation use cases with measurable ROI.
- RevOps and lifecycle leaders with dormant revenue, renewal risk, onboarding delays, or retention problems.
- Marketing operations teams that want governed audiences and measurable activation.

**Initial wedge**

Dormant Revenue Recovery for B2B customers with signed revenue, delayed activation, and scattered evidence across CRM, service, product telemetry, and engagement systems.

**Why the wedge is strong**

- The revenue number is easy to understand.
- The customer pain is common.
- The workflow is cross-cloud, which favors Salesforce.
- Human approval is expected, not a limitation.
- Measurement can be tied to activation and retention, not just content generation.

**Expansion path**

1. Dormant Revenue Recovery.
2. Renewal Risk Rescue.
3. Onboarding SLA Rescue.
4. E-Commerce Re-Engagement.
5. Loyalty Points Expiry Rescue.
6. Product-Led Expansion.
7. Winback and Churn Save.

## Minimum Artifacts For A Leadership Pitch

| Artifact | Status | Purpose |
| --- | --- | --- |
| External Goal Cockpit | Built | Live demo surface for the goal loop. |
| Dormant Revenue mock API | Built | Deterministic mock Data360/Salesforce/SendGrid scenario. |
| Salesforce metadata package | Built | Shows the Salesforce-side operating model for goal runs, actions, approvals, evidence, and impact. |
| SendGrid demo story | Built | Explains email activation and event measurement. |
| MeshMesh campaign workflow spec | Built | Shows expansion to high-volume customer lifecycle campaigns. |
| Leadership demo story | This document | The narrative to pitch product direction. |
| Optional next artifact | Not yet built | A short slide deck for exec review. |

## Optional Slide Outline

1. The problem: revenue is won but not activated.
2. The insight: Data360 can turn fragmented signals into governed goals.
3. The demo: Dormant Revenue Recovery.
4. The product: Goal Template Library.
5. The architecture: orchestrator, workers, Data360, Salesforce, SendGrid.
6. The moat: trusted data, governance, activation, and outcome measurement.
7. The roadmap: from B2B dormant revenue to lifecycle and loyalty templates.
8. The ask: sponsor a pilot with real Data360 signals and one approved SendGrid destination.
