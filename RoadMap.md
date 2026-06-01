# MCP-Executable PlanSpec Roadmap

Track implementation here. Keep each checkbox small enough for one agent to own
without crossing into unrelated work.

## Phase 1: Schema And Validator

- [x] Document `DraftPlan` and `ApprovedExecutablePlan` contract boundaries.
- [x] Add explicit `ApprovedExecutablePlan` wire model for validated execution
  artifacts.
- [x] Add canonical MCP task schema with `serverId`, `toolName`, `params`,
  `effect`, `approvalRequired`, and `outputSelectors`.
- [x] Validate effect enum, side-effect approval rules, input bindings, selector
  shape, and plan versioning.
- [x] Validate MCP params against live registry schemas when available.
- [x] Reject raw URLs, raw MCP resources, hidden tool names, cycles, missing
  dependencies, and unresolved runtime references.
- [x] Reject facade overrides, discovery-only execution servers, and unsafe SQL
  routed through generic MCP query tools.

## Phase 2: MCP Registry

- [x] Model enabled MCP servers by stable `serverId`.
- [x] Add read-only registry discovery endpoint for enabled MCP server tools.
- [x] Classify MCP tools as read, write, or destructive from tool names.
- [x] Cache discovered MCP facade and underlying tool metadata per organization
  with a stable registry hash.
- [x] Cache full payload schema, examples, effect metadata, and selector
  contracts per server/tool.
- [x] Freeze generic MCP task snapshots per step, including server/tool/effect.
- [x] Compile typed Data 360 capabilities into frozen MCP snapshots when live
  Data 360 registry descriptors are available.
- [x] Block validation/execution for disabled servers, unavailable servers, unknown
  tools, and registry effects that are riskier than the approved snapshot.
- [x] Block execution for schema drift or registry hashes that differ from the
  approved snapshot.

## Phase 3: Deterministic Executor

- [x] Start API execution from an `ApprovedExecutablePlan` artifact.
- [x] Store plan-level approval as a tenant-scoped executable artifact before
  starting a run.
- [x] Remove legacy executor shims so every non-test execution path requires an
  `ApprovedExecutablePlan`.
- [x] Traverse the generated DAG in deterministic topological order.
- [x] Resolve input bindings and output selectors without LLM calls, discovery, or
  tool choice during execution.
- [x] Enforce approval gates, run locks, audit events, and deterministic local and
  Temporal execution with frozen bindings.
- [x] Carry organization context through local execution, Temporal activities,
  monitor execution, and MCP launch lookup without ambient web authentication.
- [x] Add generic MCP idempotency keys and cancellation semantics across local and
  Temporal executors.

## Phase 4: Output Selectors

- [x] Define a small JSON selector profile and result type rules.
- [x] Validate selectors against declared tool output contracts when available.
- [x] Persist selected outputs on step run records.
- [x] Add explicit selector redaction metadata.
- [x] Fail fast on missing selector outputs.
- [x] Fail fast on oversized or sensitive selector outputs.
- [x] Fail fast on ambiguous selector outputs once tool output contracts are
  available.

## Phase 5: Mandatory Plan DAG

- [x] Generate a deterministic Plan DAG from every PlanDraft.
- [x] Include stable node IDs, explicit control/data edges, phase, effect,
  approval metadata, input bindings, and validation warnings.
- [x] Add task snapshot hashes and output selector names to DAG nodes.
- [x] Store the DAG with drafts and PlanSpec exports.
- [x] Store the DAG with runs through the persisted `ApprovedExecutablePlan`.
- [x] Store approved DAG nodes with hashes from the actual frozen binding list.
- [x] Store DAG summaries and task hashes with approval/import audit events.
- [x] Add DAG diffing so plan repair and reapproval are easy to review.

## Phase 6: Planner Harness

- [x] Add golden DraftPlan fixtures for deterministic fallback and LLM providers.
- [x] Verify planner output compiles without execution-only fields.
- [x] Add repair tests for invalid draft output.
- [x] Add repair tests for missing approvals, invalid selectors, and dependency errors.
- [x] Keep prompts focused on DraftPlan generation, not execution.

## Phase 7: Travel LTV Scenario

- [x] Add a travel lifetime-value scenario pack with realistic source signals,
  audience logic, activation steps, and monitor outcomes.
- [x] Save scenario fixtures as JSON for golden validation.
- [x] Validate every golden scenario PlanSpec through the ASL profile validator.
- [x] Assert the travel LTV scenario preserves Snowflake/CRM sources,
  IR-to-CI runtime bindings, and the `lifetime_value > 10000` prompt threshold.
- [x] Cover read-only discovery, segment creation, publish, activation, and monitor
  steps through approved MCP task snapshots.
- [x] Include demo data and expected selected outputs for planner regression
  tests.
- [x] Wire demo selected outputs into deterministic executor replay tests.

## Phase 8: Import/Export UI

- [x] Add PlanSpec import endpoint with validation.
- [x] Allow `data360.plan` scope to call PlanSpec import while keeping read-only
  users blocked.
- [x] Split PlanSpec approval from run start in the API and review UI.
- [x] Add Plan DAG panel to the Plan workspace.
- [x] Export redacted PlanSpec, Plan DAG, approvals, task hashes, status, selected
  outputs, and audit summary.
- [x] Redact MCP settings environment values in API responses and preserve masked
  values on save.
- [x] Redact bearer tokens, raw MCP frames, raw tool
  payloads, and PII-heavy traces through `SensitiveData`.
- [x] Revalidate imported PlanSpec before display.
- [x] Reject duplicate imported plan IDs unless replacement is explicit.
- [x] Revalidate approved artifacts before execution and require artifact hash on
  run start.
- [x] Show DAG, approvals, registry drift, and redaction warnings in the UI.

## Phase 9: Tests

- [x] Unit test schema validation, DAG generation, selector evaluation, registry
  snapshots, and redaction.
- [x] Integration test local executor, Temporal executor, and approval gates.
- [x] Integration test local and Temporal cancellation plus deterministic
  idempotency-key propagation.
- [x] Integration test import/export round-trips.
- [x] Integration test MCP error handling and redaction.
- [x] Add e2e coverage for draft review, approval, execution, trace export/import,
  and the travel LTV scenario.

## Phase 10: Maintainability And Security Reviews

- [x] Review package boundaries during implementation so planner, registry,
  validator, executor, UI, and
  audit code stay separable.
- [x] Complete parallel security review and patch P0/P1 MCP secret, process
  environment, facade override, and discovery-only execution findings.
- [x] Patch reviewed selector exfiltration, trace leakage, approval-bypass, and
  duplicate import overwrite findings.
- [x] Threat-model remaining import replay, registry drift, and shared-deployment
  MCP launch risks.
- [x] Add query-level tenant scoping for plan drafts, runs, approvals, audit
  events, exports, async execution, monitor definitions, and recommendations.
- [x] Migrate global JDBC keys and foreign keys to composite tenant keys for
  plans, runs, approvals, audit, monitors, and recommendations.
- [x] Add managed connector mode that rejects tenant-supplied stdio command
  configuration for shared deployments.
- [x] Add connector definition version or digest to approved MCP bindings.
- [x] Replace raw execution-log archives with redacted DTOs and redaction
  metadata.
- [x] Redact/cap raw MCP text, raw MCP errors, and unstructured trace strings
  before persistence, audit, export, chat traces, or Temporal history.
- [x] Validate JWT organization claims against an issuer/tenant allowlist or local
  organization registry.
- [x] Harden PlanSpec import and run start against approval replay and artifact
  reuse.
- [x] Centralize operation binding resource and snapshot resolution.
- [x] Split pure PlanSpec validation from registry-backed validation.
- [x] Confirm docs, examples, and comments match the final contract.
- [x] Run targeted tests plus `npm run build` and `mvn test` before merging.
