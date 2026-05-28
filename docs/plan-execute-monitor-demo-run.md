# Plan / Execute / Monitor Demo Run

Base URL:

```text
http://localhost:8082
```

This is a compact transcript of the dormant revenue recovery scenario after adding the first-class `goalRunSpec` object to the mock API response.

The full API response includes `summary`, `signalSources`, `accounts`, `actions`, `impact`, `plan`, `emailActivation`, `recoveryFunnel`, and `goalRunSpec`. The response snippets below focus on the `plan`, `execute`, and `monitor` state changes.

## Step 1: Reset Goal Run

Request:

```http
POST /api/demo/dormant-revenue-recovery/reset
Body: none
```

Response:

```json
{
  "goalRunSpec": {
    "id": "goal-run-dormant-recovery-q2",
    "plan": {
      "eligibleUsers": 12650,
      "holdoutUsers": 1265,
      "activationUsers": 11385,
      "steps": [
        {"id": "plan-query", "action": "data360.query", "needsApproval": false},
        {"id": "plan-holdout", "action": "data360.createSegment", "needsApproval": true},
        {"id": "plan-activation-segment", "action": "data360.createSegment", "needsApproval": true}
      ]
    },
    "execute": {
      "mode": "human_gated",
      "watchedActions": {
        "create-segment": "Waiting Approval",
        "publish-segment": "Waiting Approval",
        "acme-escalation": "Waiting Approval",
        "northstar-email": "Waiting Approval"
      }
    },
    "monitor": {
      "status": "waiting_for_activation",
      "eventsIngested": false,
      "dmOs": [
        "SendGrid_Email_Event__dlm",
        "Salesforce_Recovery_Outcome__dlm",
        "D360_Goal_Outcome__dlm"
      ],
      "metrics": {
        "approved_audience": 0,
        "sent": 0,
        "delivered": 0,
        "clicked": 0,
        "activated": 0,
        "retained": 0,
        "revenue_recovered": 0
      }
    }
  }
}
```

## Step 2: Inspect Starting Spec

Request:

```http
GET /api/demo/dormant-revenue-recovery
Body: none
```

Response:

```json
{
  "plan": "12650 eligible users, 1265 holdout users, 11385 activation users",
  "execute": "all watched actions waiting for approval",
  "monitor": "waiting_for_activation",
  "replan": {
    "mode": "draft_only_until_approved",
    "triggers": [
      "delivery_rate_below_95_percent",
      "click_rate_below_3_percent_after_day_2",
      "activation_rate_below_holdout_adjusted_target",
      "support_blocker_unresolved_after_48_hours"
    ],
    "allowedDrafts": [
      "create_high_intent_sales_followup",
      "suppress_low_quality_cohort",
      "adjust_sendgrid_template_variant",
      "escalate_service_blocker"
    ]
  }
}
```

## Step 3: Approve Data360 Segment Creation

Request:

```http
POST /api/demo/dormant-revenue-recovery/actions/create-segment/approve
Body: none
```

Response:

```json
{
  "execute": {
    "create-segment": "Approved",
    "publish-segment": "Waiting Approval",
    "acme-escalation": "Waiting Approval",
    "northstar-email": "Waiting Approval"
  },
  "actionsWaitingApproval": 10,
  "monitor": {
    "status": "waiting_for_activation",
    "eventsIngested": false
  }
}
```

## Step 4: Execute Data360 Segment Creation

Request:

```http
POST /api/demo/dormant-revenue-recovery/actions/create-segment/execute
Body: none
```

Response:

```json
{
  "execute": {
    "create-segment": "Executed",
    "writesTo": "Segment metadata and audience definition"
  },
  "monitor": {
    "status": "waiting_for_activation",
    "eventsIngested": false
  }
}
```

## Step 5: Approve Segment Publish

Request:

```http
POST /api/demo/dormant-revenue-recovery/actions/publish-segment/approve
Body: none
```

Response:

```json
{
  "execute": {
    "create-segment": "Executed",
    "publish-segment": "Approved"
  },
  "actionsWaitingApproval": 9
}
```

## Step 6: Execute Segment Publish

Request:

```http
POST /api/demo/dormant-revenue-recovery/actions/publish-segment/execute
Body: none
```

Response:

```json
{
  "execute": {
    "publish-segment": "Executed",
    "writesTo": "Activation target publication metadata"
  },
  "monitor": {
    "status": "waiting_for_activation"
  }
}
```

## Step 7: Approve Service Cloud Escalation

Request:

```http
POST /api/demo/dormant-revenue-recovery/actions/acme-escalation/approve
Body: none
```

Response:

```json
{
  "execute": {
    "acme-escalation": "Approved",
    "target": "Service Cloud",
    "writesTo": "Case escalation or service work queue"
  },
  "actionsWaitingApproval": 8
}
```

## Step 8: Execute Service Cloud Escalation

Request:

```http
POST /api/demo/dormant-revenue-recovery/actions/acme-escalation/execute
Body: none
```

Response:

```json
{
  "execute": {
    "acme-escalation": "Executed",
    "target": "Service Cloud",
    "writesTo": "Case escalation or service work queue"
  },
  "monitor": {
    "status": "waiting_for_activation"
  }
}
```

## Step 9: Approve SendGrid Recovery Email

Request:

```http
POST /api/demo/dormant-revenue-recovery/actions/northstar-email/approve
Body: none
```

Response:

```json
{
  "execute": {
    "northstar-email": "Approved"
  },
  "monitor": {
    "status": "waiting_for_activation",
    "metrics": {
      "approved_audience": 11385,
      "sent": 0,
      "delivered": 0,
      "clicked": 0,
      "activated": 0,
      "revenue_recovered": 0
    }
  },
  "actionsWaitingApproval": 7
}
```

## Step 10: Execute SendGrid Recovery Email

Request:

```http
POST /api/demo/dormant-revenue-recovery/actions/northstar-email/execute
Body: none
```

Response:

```json
{
  "execute": {
    "northstar-email": "Executed"
  },
  "monitor": {
    "status": "monitoring",
    "eventsIngested": true,
    "dmOs": [
      "SendGrid_Email_Event__dlm",
      "Salesforce_Recovery_Outcome__dlm",
      "D360_Goal_Outcome__dlm"
    ],
    "joinKeys": [
      "goal_run_id",
      "activation_id",
      "unified_customer_id",
      "cohort_id",
      "content_variant_id",
      "holdout_flag"
    ],
    "metrics": {
      "approved_audience": 11385,
      "sent": 11385,
      "delivered": 11043,
      "clicked": 626,
      "activated": 615,
      "retained": 188,
      "revenue_recovered": 275000
    },
    "segmentRules": [
      "Day 2 reminder exclusion",
      "High-intent follow-up",
      "Suppression"
    ]
  },
  "summary": {
    "recoveryRate": 5.4,
    "revenueRecovered": 275000
  }
}
```

## Step 11: Inspect Final Monitor And Replan State

Request:

```http
GET /api/demo/dormant-revenue-recovery
Body: none
```

Response:

```json
{
  "execute": {
    "create-segment": "Executed",
    "publish-segment": "Executed",
    "acme-escalation": "Executed",
    "northstar-email": "Executed"
  },
  "monitor": {
    "status": "monitoring",
    "eventsIngested": true,
    "metrics": {
      "sent": 11385,
      "delivered": 11043,
      "clicked": 626,
      "activated": 615,
      "retained": 188,
      "revenue_recovered": 275000
    }
  },
  "replan": {
    "mode": "draft_only_until_approved",
    "triggers": [
      "delivery_rate_below_95_percent",
      "click_rate_below_3_percent_after_day_2",
      "activation_rate_below_holdout_adjusted_target",
      "support_blocker_unresolved_after_48_hours"
    ],
    "allowedDrafts": [
      "create_high_intent_sales_followup",
      "suppress_low_quality_cohort",
      "adjust_sendgrid_template_variant",
      "escalate_service_blocker"
    ]
  }
}
```

## Demo Interpretation

The API now demonstrates:

```text
Plan:
  define eligible audience, holdout, activation audience, and plan steps

Execute:
  approve and execute side-effecting Data360, Salesforce, Service Cloud, and SendGrid actions

Monitor:
  ingest SendGrid and outcome events into DMOs, join by goal_run_id and unified_customer_id, update metrics

Replan:
  draft next actions only when triggers fire; execution still requires approval
```

