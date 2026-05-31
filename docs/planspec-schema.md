# PlanSpec Schema

PlanSpec is a versioned Data 360 governance contract. The canonical wire format
is JSON and the canonical schema is:

```text
schemas/planspec.schema.json
```

The current schema version is:

```text
2026-05-31
```

Every generated plan must include `schemaVersion`. The runtime defaults missing
versions to the current version for backward compatibility with older drafts, but
new planners and examples should always emit the field explicitly.

## Why JSON Schema Is Canonical

PlanSpec is consumed by the Java executor, the LWC UI, the LLM planner, and Data
360 Connect API adapters. Those surfaces already use JSON. JSON Schema also fits
the PlanSpec shape well because `steps[*].input` is action-specific: the schema
can say that `data360.createSegment` requires `name` plus `criteria` or
`criteriaFromStep`, while `data360.monitor.metric` requires a threshold.

XML is possible as an import/export representation, but it should not be the
canonical execution contract yet. XSD is strong for fixed trees and Salesforce
Metadata API-style documents, but PlanSpec has discriminated action inputs and
JSON-path bindings. In XML, we would either use generic `<param>` elements and
lose useful XSD validation, or create a large action-specific XSD that is harder
for the LLM, browser UI, and Connect API layer to work with.

Use XML only if we need a Salesforce package-style artifact or administrator
handoff format. In that case, convert XML into the canonical JSON PlanSpec before
validation and execution.

## Minimal Example

```json
{
  "schemaVersion": "2026-05-31",
  "id": "plan_reactivate_dormant_accounts",
  "scenarioId": "fedex_dormant_reactivation",
  "goal": "Recover dormant high-value accounts",
  "context": {
    "org": "demo-org",
    "dataspace": "default",
    "environment": "sandbox"
  },
  "steps": [
    {
      "id": "preview_audience",
      "title": "Preview the candidate audience",
      "phase": "discover",
      "action": "data360.query",
      "input": {
        "sql": "SELECT unified_account_id, account_name FROM UnifiedAccount LIMIT 100",
        "limit": 100
      },
      "dependsOn": [],
      "inputBindings": {},
      "needsApproval": false
    },
    {
      "id": "create_segment",
      "title": "Create dormant account segment",
      "phase": "setup",
      "action": "data360.createSegment",
      "input": {
        "name": "Dormant High Value Accounts",
        "criteriaFromStep": "preview_audience"
      },
      "dependsOn": [
        "preview_audience"
      ],
      "inputBindings": {},
      "needsApproval": true
    },
    {
      "id": "monitor_goal",
      "title": "Monitor recovered revenue",
      "phase": "monitor",
      "action": "data360.monitor.metric",
      "input": {
        "metric": "recovered_revenue",
        "cadence": "daily",
        "threshold": {
          "operator": "<",
          "value": 100000
        },
        "queryFromStep": "preview_audience"
      },
      "dependsOn": [
        "create_segment"
      ],
      "inputBindings": {},
      "needsApproval": false
    }
  ]
}
```
