# Security Threat Model

This document covers the production security model for the Data 360 Agent
Console plan-review-execute flow. It is intentionally implementation-oriented:
each risk should map to code, configuration, tests, or an explicit deployment
constraint.

## Scope

The system turns a business goal into a reviewable PlanSpec, freezes it into an
approved execution artifact, and then executes Data 360 operations through
Connect API or MCP. The primary risks are not generic chat risks; they are
execution authority, tenant isolation, connector process launch, import replay,
registry drift, trace export, and artifact reuse.

## Assets

- Salesforce/Data 360 credentials, MCP environment values, and LLM API keys.
- Tenant-scoped plans, approved executable artifacts, runs, approvals, audit
  events, monitor state, and execution archives.
- MCP registry snapshots: server status, tool schemas, effects, selector
  contracts, schema hashes, and registry hashes.
- Runtime outputs selected from external tools, especially object names and IDs
  used by later steps.
- Data 360 resources created or mutated by setup execution: data streams, DMO
  mappings, identity rulesets, calculated insights, segments, publishes, and
  activations.

## Trust Boundaries

- Browser or Electron renderer to Spring API: guarded by OAuth/JWT, local
  session auth, or the desktop per-launch API token.
- Planner and LLM to DraftPlan: model output is untrusted until schema,
  capability, topology, registry, effect, selector, and approval validation pass.
- DraftPlan to ApprovedExecutablePlan: approval freezes operation bindings,
  task hashes, registry hashes, tool schema hashes, and the generated Plan DAG.
- Executor to Data 360/MCP: execution must consume only the approved artifact;
  it must not ask the LLM to choose tools or rewrite parameters.
- Spring API to MCP stdio process: stdio launch is equivalent to running a local
  program with selected credentials. Treat it as privileged code execution.
- Export/import archive boundary: archives are portability and audit artifacts,
  not execution authority.

## Non-Negotiable Invariants

- A DraftPlan is never executable by itself.
- Starting a run must require a tenant-scoped stored ApprovedExecutablePlan.
- Saving or replacing a draft must invalidate the previous approved artifact for
  that plan ID.
- Execution order comes from the frozen DAG artifact or validated topology, not
  from LLM reasoning.
- Execution must be LLM-free and deterministic. No planner calls, registry
  discovery, or tool selection inside the execution loop.
- Runtime bindings may consume only declared selector outputs from prior steps.
- Non-read effects require approval from the frozen binding risk, even if the
  PlanSpec step forgot to mark itself as approval-gated.
- Exports must use archive DTOs and redaction helpers, not raw run, audit, MCP,
  or process objects.

## Current Controls

- OAuth/JWT mode is fail-closed when enabled and requires an audience. Optional
  organization allowlisting rejects JWT org claims outside configured tenants.
- API scopes separate read, plan, execute, approve, monitor, demo, and admin
  actions.
- `PlanStore.saveDraft` clears the stored approved artifact for the same tenant
  and plan ID, including import replace flows.
- `POST /api/plans/{planId}/runs` loads the approved artifact from the store,
  requires the caller to provide the matching artifact ID and plan hash, and
  does not execute the live draft.
- `ApprovedExecutablePlan` recomputes its hash from plan, validation,
  graph stages, operation bindings, and artifacts. JDBC reads verify persisted
  artifact ID and hash against the serialized artifact.
- MCP execution validates registry hash, tool schema hash, server execution
  status, execution-server classification, facade tool, and effect risk when
  approved bindings contain registry metadata.
- MCP child processes receive a cleared environment plus explicit environment
  values and explicit passthrough variables. They do not inherit the backend
  process environment wholesale.
- Managed connector mode (`app.mcp.managed-connectors.enabled=true`) rejects
  tenant-supplied MCP launch commands, arguments, transports, endpoints, working
  directories, and environment passthrough values.
- Generic MCP output selectors are required, bounded, contract-validated when
  contracts exist, and blocked from broad raw/text/whole-output paths.
- Connect execution uses deterministic idempotency keys for mutations. MCP gets
  the same key in trace metadata and only receives it as a parameter when the
  frozen tool schema declares a compatible field.
- Execution log archives use DTOs that redact inputs, outputs, raw payloads,
  artifacts, and errors through `SensitiveData`.

## P1: Shared-Deployment MCP Launch Risk

Current per-organization MCP settings allow an admin to configure stdio command,
arguments, working directory, explicit environment values, and passthrough
environment keys. This is acceptable for local desktop or single-tenant
developer deployments where the admin owns the machine. It is not acceptable for
a shared hosted deployment because changing MCP settings becomes remote code
execution on the application host.

Required production design:

- Introduce server-owned connector definitions. These are deployed by platform
  operators, not edited by tenant admins.
- A connector definition owns `serverId`, label, transport, command path,
  immutable arguments, working directory, execution-server flag, allowed tool
  names, effect overrides, selector contracts, environment allowlist, timeout,
  resource limits, and optional binary digest or image reference.
- Tenant settings may only enable/disable a connector and provide credential
  values or secret references for fields declared by the connector definition.
  They must not set command, arguments, working directory, arbitrary environment
  passthrough, or arbitrary transport.
- MCP validation and discovery should run in an isolated worker or sidecar with
  CPU, memory, process, file-system, and network egress limits. Validation must
  not run with app database credentials, LLM keys, OAuth signing keys, or the
  backend process environment.
- A hosted deployment should reject existing tenant-edited command fields at API
  save time when managed connector mode is enabled. Do not silently ignore them.
- Default passthrough values should be narrowed to connector-specific names.
  Broad keys such as general LLM provider keys should not be passable to MCP
  child processes unless the connector definition explicitly needs them.

Implemented baseline:

- Set `app.mcp.managed-connectors.enabled=true` for shared deployments.
- `McpSettingsService.save` rejects command, arguments, working directory,
  endpoint, transport changes, and environment passthrough from tenant requests
  in managed mode.
- `McpLaunchConfiguration` resolves from server-owned defaults plus tenant
  credential environment values in managed mode.
- Tests verify managed mode cannot save tenant-supplied commands.

Remaining hardening:

- Add immutable connector definitions from a server-owned table or signed config
  with a connector definition version or digest.
- Include that connector definition version or digest in approved MCP binding
  hashes.

## P1: Registry Drift

The approved artifact is only safe if the execution-time tool registry still
matches the registry and schema observed at approval.

Current controls:

- Approval can freeze registry hash and tool schema hash onto operation
  bindings.
- Execution fails closed when those hashes exist and the current registry or
  tool schema differs.
- MCP settings save invalidates the registry cache.

Remaining requirements:

- In production MCP mode, approving an MCP-backed plan should require a live
  registry snapshot for every executing MCP binding. Static fallback bindings
  are acceptable for tests and mock/local demos, not for shared production.
- Execution should fail closed if a binding uses MCP transport but has no
  registry hash or tool schema hash in production.
- Store the approved registry snapshot summary with the artifact, not only the
  hashes, so reviewers can see the exact server, tool, effect, selector contract,
  and schema summary they approved.
- Include the connector definition version or digest in the binding hash. Tool
  schema stability alone does not prove the launched server binary or container
  is the same.
- Registry refresh must be tenant-scoped and must never satisfy one
  organization's execution from another organization's discovery result.

## P1: Import Replay And Artifact Reuse

Imports are dangerous if an archive can carry execution authority, reuse an old
approved artifact, or replace a plan while keeping stale approval.

Current controls:

- The public import endpoint accepts PlanSpec only, saves it as a draft, and does
  not import ApprovedExecutablePlan objects.
- Duplicate plan IDs are rejected unless `replace=true`.
- Saving the imported draft clears the stored approved artifact for that tenant
  and plan ID.
- Plan import writes an audit event with importer, replace mode, validation
  result, approval invalidation signal, and source archive hash.
- Runs start from the stored approved artifact and verify that the requested
  plan ID, artifact ID, and plan hash match.

Remaining requirements:

- Treat every imported PlanSpec as unapproved. A user must revalidate and approve
  it in the target tenant before execution.
- Consider assigning a new plan ID by default on import. Require an explicit
  replace operation to preserve the incoming ID.
- Never import run state, approvals, idempotency records, monitor leases, or
  approved artifacts through the PlanSpec import endpoint.
- If a future artifact import endpoint is added, require a signature from this
  server or an allowlisted issuer, verify tenant binding, verify artifact hash,
  and still require a fresh local approval before execution.

## P2: Archive Export And Replay

Execution archives are useful for support and review, but they can leak
credentials, PII, or replay hints if raw objects escape.

Current controls:

- PlanSpec and execution exports use archive DTOs.
- Tool call archives redact declared input, resolved input, outputs, raw payloads,
  and errors.
- Raw mutable `PlanRun` objects are not exported directly.

Remaining requirements:

- Add a redaction version and archive schema version to every export.
- Add tests with representative MCP frames containing bearer tokens, client
  secrets, email addresses, phone numbers, and oversized collections.
- Keep raw MCP stdio frames out of support bundles by default. If a break-glass
  raw export is added, require admin scope, short retention, explicit warning,
  and separate encryption.
- Archives should not be accepted as evidence of current execution state. The
  database remains the source of truth for approval, run status, idempotency, and
  monitor leases.

## P2: Tenant Isolation

Tenant isolation applies to every store and every external call.

Required controls:

- Every persisted draft, approved artifact, run, approval, audit event, monitor,
  recommendation, MCP setting, registry snapshot, and idempotency record should
  use a tenant key.
- Background workers must use `RunContext.organizationId`, not request-local user
  state.
- Composite unique constraints and foreign keys should include organization ID
  wherever a child record references a parent record.
- Cross-tenant imports must create new draft records in the target tenant and
  must not reuse source-tenant approval or idempotency state.

## Acceptance Checklist

Before calling the shared production posture ready:

- Managed connector mode exists and blocks tenant-supplied MCP commands.
- MCP execution in production requires registry hash, tool schema hash, and
  connector definition version on every MCP binding.
- Plan import writes an audit event and never preserves approval.
- Execution archive tests cover redaction and collection bounding.
- Approved artifact review shows DAG, task hashes, registry hashes, tool schema
  hashes, effect, approval, and selector contract metadata.
- Threat model changes are reflected in `AGENTS.md`, `README.md`, and focused
  tests.
