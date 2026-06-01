# ASL PlanSpec Test Plan

## Scope

Protect the governed PlanSpec contract without reaching into operation, MCP, or
executor implementation details.

## Core Invariants

- PlanSpec authors emit ASL `definition`; `steps` remains a derived compatibility
  view.
- ASL `Resource` values are Salesforce Data 360 capability URIs only.
- Raw MCP tool names, HTTP URLs, AWS ARNs, and raw operation names never appear
  in executable PlanSpec resources.
- Discovery/planning context is completed before approved execution; execution
  should not dynamically discover tools or expand the plan.
- Invalid LLM drafts are repaired through concrete validator feedback before a
  draft is marked valid.

## Existing Coverage

- `PlanValidatorTest` covers schema version, ASL profile rules, raw URL
  resources, raw tool-name/ARN resources, approvals, query limits, monitor
  phase constraints, input limits, and safe names.
- `AslPlanCompilerTest` covers step-to-ASL compatibility and ASL-to-derived-step
  compilation, including dynamic parameter bindings.
- `PlanSpecSchemaTest` keeps the JSON Schema version, ASL constants, and
  capability-resource enum aligned with Java.
- `LlmPlanGeneratorTest` covers fallback validity and the validator-feedback
  repair loop.

## Recommended Next Tests

- Add an executor-facing test, in that agent's lane, that a runtime plan executes
  only approved setup/activation states and does not run discovery/tool-search
  states during execution.
- Add an API serialization test that planner responses either omit `steps` from
  canonical wire JSON or prove `steps` is regenerated from `definition` when
  both are present.
- Add JSON Schema validation fixtures for rejected raw resources so CLI schema
  checks fail before Java validation.

## Review Findings

- `LocalPlanExecutor.nextRunnableStep` currently skips only monitor steps, so
  `PlanPhase.DISCOVER` steps from derived ASL are executed as part of local plan
  execution. If discovery is meant to be planner-time only, this needs an
  execution-lane fix.
- `PlanSpec` derives `steps` from `definition` only when `steps` is absent. If a
  client submits both fields with conflicting content, Java preserves the
  submitted `steps`; the validator checks both surfaces but does not assert that
  `steps` equals the ASL-derived view.
