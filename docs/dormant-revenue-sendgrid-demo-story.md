# Dormant Revenue Recovery Demo Story

## One-Line Story

Data360 identifies a 10k+ dormant audience, the external cockpit inspects priority samples and approves a recovery motion, SendGrid activates the email sequence, and Data360 measures whether users actually recovered or retained.

## Why This Demo Exists

Most agent demos stop at drafting content or creating a task. This demo goes one level deeper:

- Data360 decides which accounts are worth acting on.
- Evidence explains why each account is dormant.
- A human approves every outbound or operational action.
- SendGrid executes approved recovery emails from a verified domain.
- Data360 joins email engagement with business outcome signals.
- Recovery and retention are measured from activation milestones, not email opens.

## Demo Persona

Primary persona: RevOps leader or lifecycle growth operator.

Secondary personas:

- Customer Success leader
- Sales manager
- Data360 solution owner
- Marketing operations owner

## Demo Goal

Recover signed customers with no activation before quarter end.

Mock command:

```text
Recover 12,650 eligible dormant users with approved Salesforce and SendGrid activation, then measure recovery and retention in Data360.
```

Scale rule:

```text
The five visible accounts are explainable samples. The measurement audience is 12,650 eligible users, with 1,265 held out for incrementality and 11,385 eligible for activation.
```

## Data360 Inputs

Data360 unifies and scores:

- Account and opportunity context from Sales Cloud
- Contract value and contract age
- Product usage events
- Service Cloud onboarding blockers
- Marketing engagement and intent
- AE activity gaps
- Contact and consent signals

Calculated insights:

- `activation_gap_days`
- `intent_score`
- `support_blocker_score`
- `ae_inactivity_days`
- `recovery_priority_score`
- `recoverable_revenue`

## SendGrid Activation

SendGrid is the first real activation channel because it can be wired before Marketing Cloud.

Safe demo configuration:

```text
SENDGRID_API_KEY=<secret>
SENDGRID_FROM_EMAIL=recovery@verified-domain.example
SENDGRID_DEMO_TO_EMAIL=operator-test-inbox@example.com
```

Safety rule:

All outbound demo emails route to the configured test inbox until production sending is explicitly enabled.

Recommended event webhook events:

- processed
- delivered
- open
- click
- bounce
- dropped
- spam_report
- unsubscribe

## Recovery Email Sequence

Day 0: Executive Unblock

- Uses account-specific evidence.
- Names the blocker or activation gap.
- Offers a concrete recovery meeting.

Day 2: Proof and Next Step

- Sends implementation checklist or relevant case study.
- Personalized by product/use-case preference.
- Sent only if no reply or click is observed.

Day 7: Final Activation Offer

- Offers a time-boxed activation workshop.
- Routes to owner or partner motion.
- Sent only if no meeting is booked.

## Funnel

```text
Identified
Approved
Sent
Delivered
Opened
Clicked
Meeting Booked
Activated
Retained
Revenue Recovered
```

Important measurement rule:

Open and click are engagement signals. Recovery requires a business outcome signal, such as product activation, support blocker resolution, meeting booked, or retained account status.

## Core Metrics

```text
Recovery Rate = activated accounts / contacted dormant accounts
Retention Rate = retained accounts / contacted dormant accounts
Revenue Recovered = contract value for recovered accounts
Email-Assisted Recovery = recovered accounts with SendGrid engagement before outcome
Delivered Recovery Rate = recovered accounts / delivered emails
Click-to-Recovery Rate = recovered accounts / clicked emails
```

## Demo Walkthrough

1. Open the external Data360 Goal Cockpit.
2. Show `$1.85M` recoverable revenue across `12,650` eligible dormant users.
3. Select Acme or BrightPath and inspect evidence.
4. Show the SendGrid Activation panel.
5. Approve an email action.
6. Execute the approved action in demo mode.
7. Show the recovery funnel changing from approval to sent.
8. Explain that SendGrid events measure engagement, while Data360 outcome signals measure recovery and retention.

## Product Point

SendGrid makes the activation real. Data360 makes the outcome trustworthy.

The durable product value is not sending an email. It is connecting:

```text
trusted customer context -> approved activation -> observable engagement -> business recovery outcome
```
