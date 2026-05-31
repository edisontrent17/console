# PlanSpec Schema

PlanSpec is now a governed Amazon States Language profile. The canonical wire
format is JSON, and the canonical schema lives at:

```text
schemas/planspec.schema.json
```

The current schema version is:

```text
data360-asl-profile-2026-05-31
```

## Contract Layers

PlanSpec uses ASL shape for the executable graph:

- `definition.Version`
- `definition.QueryLanguage`
- `definition.StartAt`
- `definition.States`
- `Task` states
- `Resource`
- `Parameters`
- `ResultPath`
- `Next` / `End`

The Data 360 profile deliberately narrows ASL:

- only `Task` states are executable in this version
- `Resource` must be a Salesforce Data 360 capability URI
- raw AWS ARNs, HTTP URLs, MCP tool names, and arbitrary callouts are rejected
- `QueryLanguage` must be `JSONPath`
- `ResultPath` must be `$.stateName`
- `Retry`, `Catch`, `InputPath`, `OutputPath`, `Map`, and `Parallel` are reserved
- Temporal owns durable execution, retries, approval waits, and audit history

The API still exposes a derived `steps` view for the current UI and executor.
New planners should emit `definition`, not author `steps` directly.

## Agent Draft And Repair Loop

The planner should use validators as tools:

```text
draft ASL PlanSpec
  -> standard ASL validation
  -> Data 360 ASL profile validation
  -> capability input validation
  -> repair with concrete validator errors when needed
  -> approve
  -> Temporal execution
```

The repo includes the standard `asl-validator` package for local ASL checks. It
validates the embedded `definition` object and disables AWS ARN checks because
this profile uses Salesforce capability URNs:

```bash
npm run validate:asl
npm run validate:asl -- path/to/plan.json
```

## Why ASL Profile, Not Full ASL

Full ASL is a workflow language. It is too broad for model-authored Data 360
setup plans because it can represent execution details that should stay outside
the approval contract. The profile gives us the standard state-machine shape
while keeping governance enforceable.

The key rule remains:

```text
PlanSpec Resource = stable Data 360 capability URI
OperationBindingSnapshot = approved execution binding for that capability URI
Executor binding = Temporal activity/local executor -> MCP or Connect API call
```

Operation bindings are deliberately outside the PlanSpec schema. A PlanSpec state
does not name `search`, `execute`, `d360_segment_create`, or an HTTP endpoint.
When a draft is created, the app resolves each capability URI into an immutable
`OperationBindingSnapshot` with transport, facade tool, underlying tool, effect,
approval requirement, parameter schema hash, and binding version. That binding
list is saved with the draft, copied onto the run at approval/start time, and
persisted in `plan_runs.operation_bindings_json` so Temporal replay and local
execution use the same frozen call boundary.

So this is valid:

```text
urn:salesforce:data360:capability:segment.create
```

This is not valid PlanSpec:

```text
d360_segment_create
https://example.salesforce.com/services/data/...
arn:aws:lambda:...
```

## Minimal Example

```json
{
  "schemaVersion": "data360-asl-profile-2026-05-31",
  "id": "plan_reactivate_dormant_accounts",
  "scenarioId": "fedex_dormant_reactivation",
  "goal": "Recover dormant high-value accounts",
  "context": {
    "org": "demo-org",
    "dataspace": "default",
    "environment": "sandbox"
  },
  "definition": {
    "Version": "1.0",
    "QueryLanguage": "JSONPath",
    "StartAt": "preview_audience",
    "States": {
      "preview_audience": {
        "Type": "Task",
        "Comment": "Preview the candidate audience",
        "Resource": "urn:salesforce:data360:capability:query",
        "Parameters": {
          "sql": "SELECT unified_account_id, account_name FROM UnifiedAccount LIMIT 100",
          "limit": 100
        },
        "ResultPath": "$.preview_audience",
        "Next": "create_segment"
      },
      "create_segment": {
        "Type": "Task",
        "Comment": "Create dormant account segment",
        "Resource": "urn:salesforce:data360:capability:segment.create",
        "Parameters": {
          "name": "Dormant High Value Accounts",
          "criteriaFromStep": "preview_audience"
        },
        "ResultPath": "$.create_segment",
        "Next": "monitor_goal"
      },
      "monitor_goal": {
        "Type": "Task",
        "Comment": "Monitor recovered revenue",
        "Resource": "urn:salesforce:data360:capability:monitor.metric",
        "Parameters": {
          "metric": "recovered_revenue",
          "cadence": "daily",
          "threshold": {
            "operator": "<",
            "value": 100000
          },
          "queryFromStep": "preview_audience"
        },
        "ResultPath": "$.monitor_goal",
        "End": true
      }
    }
  }
}
```

At runtime, Java compiles this ASL profile into internal `PlanStep` objects.
The same local and Temporal executors then run approved setup steps and register
monitor states after setup succeeds.
