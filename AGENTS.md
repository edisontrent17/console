# Agent Handoff Guide

This repository is a Java/Spring Boot prototype for a first-class Data 360/Data Cloud
plan-review-execute agent. It is meant to help a user describe a business goal,
review a small plan, approve gated side effects, and then execute Data 360/Salesforce
operations through a narrow tool boundary.

Current clean baseline: `c652942 Initial Data 360 agent console`.

## Where To Start

- Root: `/home/manoj/Projects/data360-agent-console-capability-execution`
- App entry point: `src/main/java/com/acme/data360agent/Data360AgentApplication.java`
- Browser UI source: LWC OSS components under `src/main/frontend/modules/`
- Browser UI static shell: `src/main/resources/static/index.html`, `styles.css`, and generated `app.js`
- Primary README: `README.md`
- Salesforce demo package: `salesforce/README.md`
- Current demo narrative docs: `docs/`

Always check `git status --short --branch` before editing. Other agents may have
changed files. Work with those changes; do not revert them unless the user explicitly
asks.

## Run And Test

Use Java 21, Maven, Node, and npm.

```bash
npm install
npm run build
mvn test
```

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

If port `8080` is busy, use another port:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8082'
```

Open:

```text
http://localhost:8082
```

Desktop packaging commands:

```bash
npm run desktop:dev
npm run desktop:pack
npm run desktop:dist
```

Electron must remain a local shell around the Spring/LWC app. Do not move
planner, PlanSpec validation, execution, MCP, Temporal, or audit logic into
Electron. Keep desktop behavior behind the Electron launcher plus the
`desktop` Spring profile so normal web/server startup is unchanged.

## Architecture Map

The current boundary is:

```text
Goal -> Planner -> PlanSpec -> Human review -> Executor -> Data 360/MCP tools
```

The MCP-executable roadmap target is:

```text
Goal -> Planner -> DraftPlan -> Validator/Plan DAG -> Human review -> ApprovedExecutablePlan -> Deterministic executor -> MCP tools
```

Keep these responsibilities separate:

- Planner: drafts a small `DraftPlan`, using LangGraph4j plus Anthropic,
  OpenRouter, or deterministic fallback. Planner output is never executable by
  itself.
- PlanSpec: the reviewable contract. It should stay small and serializable.
- Plan DAG compiler: derives a deterministic DAG artifact from the PlanSpec for
  review, approval, import/export, and execution. Do not hand-author or mutate
  this artifact at runtime.
- ApprovedExecutablePlan: the frozen, validated, approved execution contract. It
  contains resolved MCP task snapshots and the generated Plan DAG.
- Operation bindings: immutable call-boundary snapshots resolved from PlanSpec
  capability URIs and frozen onto a run before execution.
- MCP task registry: owns server/tool discovery, effect metadata, parameter
  schemas, registry hashes, and selector contracts before approval.
- Executor: runs only approved executable DAG nodes and stores runtime outputs
  outside the plan. `LocalPlanExecutor` is default; `TemporalPlanExecutor` is the
  durable orchestration path.
- Monitor service: registers monitor-phase steps and evaluates goal health after setup.
- MCP/Data360 client: the only place that translates approved actions into tool calls.
- State stores: keep drafts, runs, approvals, audit events, monitor state, and Connect idempotency durable.
- Temporal: production orchestration boundary for durable setup execution.
- Security: production defaults are fail-closed. Keep OAuth/JWT auth, scope checks,
  and actor propagation intact when adding APIs.

## Key Java Packages

- `plan/`: `PlanSpec`, `PlanStep`, `PlanContext`, `Data360Action`, validation.
- `planner/`: LangGraph4j planner and Anthropic-backed plan drafting.
- `execution/`: local ordered executor, run state, step state, `PlanStore` implementations.
- `data360/`: mock and MCP-backed Data 360 clients.
- `monitor/`: monitor definitions, due-monitor leasing, run-now execution, recommendation review.
- `operation/`: allowed operation registry and effect metadata.
- `state/`: shared JSON/timestamp state codec for JDBC stores.
- `support/`: small cross-cutting helpers such as ID generation.
- `security/`: JWT resource-server config, current-user extraction, and audience validation.
- `temporal/`: workflow/activity interfaces, workflow implementation, executor adapter, worker lifecycle, and tests.
- `library/`: reusable solution templates.
- `scenario/`: publicly grounded customer scenario packs.
- `demo/`: Dormant Revenue Recovery demo API.
- `web/`: REST controllers and API exception handling.

## PlanSpec Rules

Be critical about PlanSpec changes. The current shape is intentionally simpler than
a workflow engine:

- Treat planner output as `DraftPlan` until validation, deterministic DAG
  generation, human review, approval, and execution snapshot freezing produce an
  `ApprovedExecutablePlan`.
- `DraftPlan` may be LLM-generated or repaired. `ApprovedExecutablePlan` must be
  deterministic and LLM-free: it is built from validated schema, registry metadata,
  explicit approvals, and immutable execution snapshots.
- Each node is a `PlanStep`.
- Each step has a phase: `discover`, `setup`, or `monitor`.
- Step input is the API/action parameters for that step, not arbitrary hidden state.
- Step output belongs in runtime execution records such as `StepRun`, not in the
  static plan.
- Tool names, MCP facade calls, Connect paths, schema hashes, and binding
  versions belong in `OperationBindingSnapshot`, not in PlanSpec.
- MCP-executable roadmap work should compile typed actions or capability URIs into
  a canonical generic MCP task snapshot:

```json
{
  "serverId": "data360-prod",
  "toolName": "d360_segment_create",
  "params": {},
  "effect": "write",
  "approvalRequired": true,
  "outputSelectors": {
    "segmentId": "$.id"
  }
}
```

- `serverId` must resolve to an enabled MCP server setting that is explicitly
  marked as an execution server. Discovery-only MCPs such as warehouse or CRM
  inspection servers may help the planner, but they must not execute PlanSpec
  setup tasks.
- `toolName` must exist in the registry for that server. For the Data 360 MCP,
  `toolName` is the underlying `d360_*` tool reached through the `execute`
  facade. PlanSpec authors must not override the facade or call raw MCP tools.
- `params` must validate against the tool schema when available. SQL-like generic
  MCP tools must still pass the same read-only and bounded-query checks as typed
  query steps.
- Approval should freeze the current tenant-scoped MCP registry hash and tool schema
  hash into `OperationBindingSnapshot`. Execution must fail closed on registry or
  tool-schema drift until the plan is revalidated and reapproved.
- MCP registry snapshots should carry each tool's input schema, output schema,
  curated payload examples, effect classification, and selector contracts. Payload
  examples are planner/review hints; schema hashes should cover executable schema
  and selector contracts, not example text.
- `effect` must come from `read`, `write`, `publish`, `activate`, or
  `destructive`. The declared effect may be stricter than registry metadata, but
  must never understate registry risk. `approvalRequired` must be true for every
  non-read side effect.
- `outputSelectors` are named, deterministic selectors over the tool result. They
  are the only way later steps may consume prior MCP output. Keep selectors
  declarative and bounded; do not add script execution, eval, network calls, or
  model calls to selector evaluation.
- Selector evaluation must reject missing paths, sensitive values, and oversized
  selected values. A selector is for stable runtime IDs and small object names, not
  raw MCP frames, tokens, cookies, bearer strings, or bulk payload export.
- Generic MCP selectors must be declared for every executing generic MCP step.
  Reject `$.raw`, `$.text`, whole `$.output`, and selector aliases/paths that do
  not match a frozen tool selector contract when one is available.
- When an MCP step declares selectors, persisted step output is selector-bounded:
  top-level selector aliases plus `selected`. Later bindings may use
  `$.selectorName` or `$.selected.selectorName`, but must not bind `$.raw`,
  `$.text`, or arbitrary `$.output.*`.
- Every validated PlanSpec must emit a mandatory deterministic Plan DAG artifact.
  The DAG should use stable node IDs, explicit edges, topological order, effect and
  approval metadata, task snapshot hashes, input bindings, and output selector
  names. Reject cycles, missing dependencies, unresolved selectors, and hidden
  runtime dependencies.
- The DAG artifact must include `topologicalOrder`. Execution order should come
  from `PlanExecutionOrder`, which reads the frozen `ApprovedExecutablePlan`
  artifact first and falls back to `PlanTopology` only for legacy/test paths.
- Scenario packs should also live as JSON fixtures under
  `src/test/resources/scenarios/`. Each fixture should include the mirrored
  `CustomerScenario` metadata and a canonical ASL `goldenPlan` that validates.
  Treat these fixtures as the golden set for planner, validator, and demo
  regression coverage.
- If a step uses `Parameters` keys ending in `.$`, the referenced output must be
  declared on the source capability contract and the target input must be declared
  on the consuming capability contract.
- Dependencies should stay simple and explicit.
- Use only simple `inputBindings` with `fromStep` and `$.field` paths when a later
  step needs a prior output.
- Approval-gated steps must pause before write, publish, or activation side effects.
- Do not add a general expression language, loops, retries, queues, compensation,
  or scheduling into PlanSpec unless the user explicitly chooses to build a workflow
  engine. Temporal should own durable workflow concerns.

The plan is a contract humans and systems can inspect. It is not an autonomous
scratchpad for the LLM.

## Execution Rules

The execute path should not be broadly agentic by default.

- The LLM may propose or repair a plan.
- The executor should run only validated, approved, known operations from an
  `ApprovedExecutablePlan`.
- Execution must be deterministic and LLM-free. Do not call the planner, model
  gateway, MCP discovery, or registry refresh from inside the execution loop.
- Never expose a generic unrestricted `d360.execute` from the plan surface.
- Route side effects through named operations such as query, create segment, publish
  segment, create activation, or run activation.
- Keep execution deterministic and auditable.
- For MCP-executable work, invoke only frozen task snapshots with canonical
  `serverId`, `toolName`, validated `params`, `effect`, `approvalRequired`, and
  `outputSelectors`. The executor may resolve input bindings and evaluate output
  selectors, but it must not invent calls, rewrite params, or pick alternate tools.
- Generic MCP execution must use the `execute` facade only. Do not allow
  PlanSpec-supplied `facadeTool` overrides.
- MCP child processes must receive only explicitly configured environment values
  and explicitly allowed passthrough variables. Never let stdio child processes
  inherit the backend process environment wholesale.
- `PlanController` must store a tenant-scoped `ApprovedExecutablePlan` through
  `POST /api/plans/{planId}/approve` before a run can start. `POST
  /api/plans/{planId}/runs` must consume that stored artifact, not a live draft,
  and must require the exact approved `artifactId` and `planHash` in the request.
- Approval may freeze operation bindings from registry metadata. Execution must
  use the approved artifact as-is and must not silently re-resolve tools after
  approval.
- Typed Data 360 actions should freeze live Data 360 MCP descriptor metadata when
  it is available. The static local catalog is the fallback shape, not permission
  to skip registry hash/tool schema hash capture during approval.
- Approval gates must honor both `PlanStep.needsApproval()` and the frozen
  `OperationBindingSnapshot.requiresApproval()` value. The binding is the
  authoritative execution risk boundary.
- `LocalPlanExecutor` and `TemporalPlanExecutor` should call the configured
  `Data360Client` with the frozen binding snapshot from the run.
- `LocalPlanExecutor` and `TemporalPlanExecutor` should traverse the frozen Plan
  DAG order. Do not re-sort or infer a different order inside activities.
- `RunContext.organizationId` is the authoritative tenant for background
  Data360/MCP calls. Do not resolve MCP launch settings from `CurrentUserService`
  inside local executor workers, Temporal activities, monitor runs, or other
  non-request execution paths.
- MCP registry discovery and cache entries must be tenant-scoped. Never validate
  or execute a PlanSpec against a registry snapshot discovered for another
  organization.
- Runtime data dependencies should use ASL dynamic parameters plus contract
  validation. For IR-to-CI flows, bind identity resolution outputs such as
  `unifiedProfileObjectApiName` into calculated insight inputs; do not hard-code
  runtime unified model names in generated SQL.
- Every executing step gets a deterministic idempotency key derived from tenant,
  run, plan, step, binding hash, and resolved input. Connect uses it as an
  `Idempotency-Key`; MCP execution may pass it only when the frozen tool schema
  declares a compatible idempotency/request field.
- Run cancellation must be supported by both local and Temporal executors and
  must not let a late tool result overwrite a canceled run or step.
- Use `PlanStore.withRunLock` for run mutations. Do not synchronize on a freshly loaded JDBC `PlanRun`.
- Keep secrets, org credentials, and bearer tokens out of source files and logs.
- API responses for MCP settings must redact environment values. When a user saves
  masked values, preserve the previous stored value rather than replacing it with
  the mask.
- Import/export paths must redact traces through `SensitiveData`. Exports may carry
  PlanSpec, Plan DAG, approval metadata, task hashes, status, and selected outputs,
  but must not include bearer tokens, environment values, private keys, raw MCP
  stdio frames, unredacted tool payloads, or PII-heavy traces. Imports must
  revalidate schema, DAG, registry references, effects, selectors, and approvals
  before any execution can resume.
- Execution-log exports must use archive DTOs such as `ExecutionRunArchive`, not
  raw mutable `PlanRun` objects. Audit and approval payloads must be redacted before
  persistence as well as before API export.

## Multi-Agent Workstreams

This branch may have multiple agents editing at once. Keep roadmap work sliced by
tracked checkbox in `RoadMap.md` and claim the smallest coherent workstream.

- Schema/validator changes should land before executor behavior that depends on
  those fields.
- MCP registry work should define stable `serverId`, `toolName`, schema, effect,
  and selector metadata before UI or planner features depend on it.
- Executor work should consume generated DAG artifacts and frozen task snapshots,
  not planner internals.
- Planner harness and scenario work should produce `DraftPlan` fixtures that pass
  validator checks without weakening the execution contract.
- UI import/export work should use redacted artifacts and should not expose raw
  traces or secrets for debugging convenience.
- Keep docs updated with any contract changes, and avoid broad refactors across
  another agent's active files.

## Temporal Mode

Temporal mode is enabled with:

```properties
app.executor=temporal
app.temporal.target=127.0.0.1:7233
app.temporal.namespace=default
app.temporal.task-queue=data360-plan-task-queue
```

Design rules:

- `TemporalPlanExecutor` should only create runs, start workflows, and signal approvals.
- `Data360PlanWorkflowImpl` owns deterministic orchestration: step order, approval waits, retryable activities, cancellation, and monitor-step skipping.
- `Data360PlanWorkflowImpl` receives frozen `OperationBindingSnapshot` values at
  workflow start. Workflow code must not construct `OperationRegistry` or perform
  MCP discovery during replay.
- Workflow code must not inject Spring beans, call wall-clock APIs, or mutate JDBC directly.
- `Data360ActivitiesImpl` is the only Temporal activity that calls the configured Data 360 client.
- `PlanRunActivitiesImpl` is the only Temporal activity that persists run state, approvals, audit events, and monitor registration.
- Keep input binding logic in `PlanInputResolver`; do not reimplement it in Temporal or Local executors.
- Use `app.temporal.worker-enabled=false` for web-only processes that should start/signal workflows without polling tasks.

Test Temporal changes with:

```bash
mvn -Dtest=Data360PlanWorkflowImplTest test
```

## State Store

Default app state is JDBC-backed H2:

```properties
app.state.store=jdbc
spring.datasource.url=jdbc:h2:file:./data/data360-agent-console;AUTO_SERVER=TRUE
```

Tests override this to in-memory H2 under `src/test/resources/application.properties`.
Use `app.state.store=memory` only for quick local experiments.

When adding a durable store:

- depend on the interface (`PlanStore`, `MonitorStore`, `AuditService`, `ConnectApiIdempotencyStore`) from services/controllers
- reuse `JsonStateCodec` for JSON and timestamp conversion
- keep generated H2 files under `data/` out of git
- do not duplicate store-specific serialization helpers unless there is a real reason

## MCP And Data 360

Default mode is mock:

```properties
app.data360.client=mock
```

To use the Salesforce Data 360 MCP stdio server, build
`forcedotcom/d360-mcp-server` separately and start this app with:

```bash
mvn spring-boot:run \
  -Dspring-boot.run.arguments='--app.data360.client=mcp --app.data360.mcp.command="java -jar /absolute/path/to/data360-mcp-server-1.0.0.jar"'
```

Current adapter mapping:

- `data360.search` -> MCP `search`
- `data360.query` -> MCP `execute(d360_query_sql)`
- `data360.createSegment` -> MCP `execute(d360_segment_create)`
- `data360.publishSegment` -> MCP `execute(d360_segment_publish)`
- `data360.createActivation` -> MCP `execute(d360_activation_create)`
- `data360.runActivation` -> mock-only until a real target-specific operation is mapped

If new Salesforce MCPs are added, wrap them behind typed `Data360Action` or operation
definitions. Do not let template authors call raw tools directly.

The generic MCP package under `mcp/` is discovery/read-only infrastructure for
`tools/list`, `search`, and `payload_examples`. Execution should still enter
through `Data360Client` and an approved `OperationBindingSnapshot`.

## Connect API Mode

`app.data360.client=connect` routes approved PlanSpec actions through
`HttpData360ConnectClient`. It supports three auth shapes:

- direct Data 360 token: `DATA360_CONNECT_INSTANCE_URL` + `DATA360_CONNECT_ACCESS_TOKEN`
- Salesforce token exchange: `SALESFORCE_INSTANCE_URL` + `SALESFORCE_ACCESS_TOKEN`
- JWT bearer: `SALESFORCE_CLIENT_ID`, `SALESFORCE_USERNAME`, and a PKCS#8 private key

The token provider exchanges Salesforce tokens through `/services/a360/token`.
The HTTP client never accepts raw URLs or raw tool names from PlanSpec. Keep it that
way: add new Data 360 capabilities as typed `Data360Action` values plus explicit
payload builders.

Current Connect endpoint families:

- metadata: `/api/v1/metadata/`
- query: `/services/data/{version}/ssot/query-sql`
- calculated insights: `/services/data/{version}/ssot/calculated-insights`
- segments: `/services/data/{version}/ssot/segments`
- activations: `/services/data/{version}/ssot/activations`
- identity rulesets: `/services/data/{version}/ssot/identity-resolutions`

`ConnectApiIdempotencyStore` has memory and JDBC implementations. JDBC mode is the
default and prevents duplicate mutation replay across restarts for the same run,
step, and resolved input.

Use diagnostics before real setup execution:

- `GET /api/data360/diagnostics` reports mode/configuration without external calls.
- `POST /api/data360/diagnostics/smoke` performs one read-only metadata call.

Do not make diagnostics perform writes. They exist to validate auth and reachability
before a user approves setup mutations.

## Production Auth And DB

Production startup should provide:

- `APP_SECURITY_ENABLED=true`
- `OAUTH2_ISSUER_URI`
- `APP_SECURITY_REQUIRED_AUDIENCE`
- `DATA360_AGENT_DB_URL`, `DATA360_AGENT_DB_USERNAME`, and `DATA360_AGENT_DB_PASSWORD`

The `dev` profile may disable auth and use H2 for local demos only:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Do not add new write endpoints without assigning a narrow authority in
`SecurityConfig`. Current scopes are `data360.read`, `data360.plan`,
`data360.execute`, `data360.approve`, `data360.monitor`, `data360.demo`, and
`data360.admin`. Any operation that approves, starts, runs, diagnoses, mutates, or
exposes audit/raw details should not share the generic read scope.

Desktop mode still protects `/api/**` with a per-launch
`X-Data360-Desktop-Token`, even when OAuth is disabled for local use. Do not
remove that token gate or pass real API keys through command-line arguments,
URLs, localStorage, IndexedDB, plan JSON, or execution exports. Export payloads
must be redacted through `SensitiveData`.

See `docs/security-threat-model.md` before changing MCP settings, plan import,
approved artifacts, registry validation, or archive export. In particular,
tenant-editable MCP stdio commands are only acceptable for local desktop or
single-tenant developer deployments. Shared hosted deployments must use
server-owned allowlisted connector definitions where tenants can only enable a
connector and provide declared credentials or secret references. Keep
`app.mcp.managed-connectors.enabled=true` for shared deployments, and do not add
a hosted path where tenant admins can set command, arguments, working directory,
transport, arbitrary environment passthrough, or process launch details.

Plan imports are drafts, never execution authority. Saving or replacing a draft
must invalidate any stored approved artifact for the same tenant and plan ID, and
future archive-import work must not import approvals, runs, idempotency records,
monitor leases, or approved executable artifacts without a fresh local approval.

Database schema changes belong in Flyway migrations under
`src/main/resources/db/migration/`. Do not reintroduce `schema.sql`. Keep
production `baseline-on-migrate=false`; only use baselining for an explicit
one-time existing-schema adoption.

## Browser UI Modules

The browser UI is LWC OSS with SLDS, not hand-written DOM modules. Keep source
logic under `src/main/frontend/modules/` and build the bundle with:

```bash
npm run build
```

Current module boundaries:

- `c/data360Console`: root shell, session, tabs, API orchestration, shared state
- `c/planWorkspace`: PlanSpec drafting, review, approval, and step inspector
- `c/executionWorkspace`: dormant revenue account cockpit and approval queue
- `c/monitorWorkspace`: monitors, monitor runs, and recommendations
- `c/auditWorkspace`: trusted context and raw PlanSpec/run diagnostics
- `c/api`: fetch wrapper and API error handling
- `c/format`: formatting helpers

Do not edit generated `src/main/resources/static/app.js` by hand. Change LWC
source, run `npm run build`, and commit the source plus generated static bundle
when the app needs to run directly from Spring Boot. Shared code belongs in
small LWC service modules rather than being copy-pasted between workspaces.

## LLM Providers

The planner uses a deterministic fallback unless a provider is configured.

Anthropic direct:

```bash
export APP_LLM_PROVIDER="anthropic"
export ANTHROPIC_API_KEY="..."
export ANTHROPIC_MODEL="claude-sonnet-4-5"
```

OpenRouter:

```bash
export APP_LLM_PROVIDER="openrouter"
export OPENROUTER_API_KEY="..."
export OPENROUTER_MODEL="anthropic/claude-sonnet-4.5"
```

Do not commit `.env` files or real API keys.

Provider-specific quirks belong inside `llm/`. The planner should consume only
`LlmGateway` and PlanSpec JSON.

## Scenario Library

`ScenarioLibrary` contains publicly grounded scenario packs for FedEx-style
dormant account reactivation, UChicago Medicine-style patient access, Pacers
fan engagement, Salesforce event-to-pipeline, and PepsiCo retailer engagement.
Treat them as demo goal packs grounded in public Salesforce stories, not private
customer implementations.

## Solution Library

`SolutionLibrary` contains representative, reusable cross-cloud Data 360 templates:

- Retail loyalty winback
- Financial household expansion
- Healthcare care-gap outreach
- Manufacturing service-to-sales
- Communications churn deflection
- Automotive connected service
- B2B product-led expansion
- Nonprofit donor stewardship

Treat these as synthesized implementation patterns, not verified real customer
case studies. If the user asks for real customer examples, browse and cite public
sources before making claims.

## Dormant Revenue Recovery Demo

The external browser demo is the current primary product surface. Its API lives at:

```text
GET  /api/demo/dormant-revenue-recovery
POST /api/demo/dormant-revenue-recovery/actions/{actionId}/approve
POST /api/demo/dormant-revenue-recovery/actions/{actionId}/execute
POST /api/demo/dormant-revenue-recovery/actions/{actionId}/reject
POST /api/demo/dormant-revenue-recovery/reset
```

It shows Data360, Sales Cloud, Service Cloud, product telemetry, Marketing Cloud,
Slack, approval gates, and impact metrics for a leadership-style demo.

## Salesforce Package

The `salesforce/` folder is a Salesforce DX demo package. It creates sandbox-safe
custom `D360_*` objects, a Lightning app, a permission set, and seed data. It does
not modify standard Account, Opportunity, Case, Campaign, or Data Cloud records.

Deploy instructions are in `salesforce/README.md`.

Be careful when changing metadata: preserve the mock-demo guarantee unless the user
explicitly asks to wire real org behavior.

## Coding Notes

- Follow the existing Spring Boot style.
- Keep the UI operational and dense; this is a work console, not a landing page.
- Prefer focused tests for plan validation, execution, library templates, and demo APIs.
- Keep generated build output such as `target/` out of git.
- Do not add custom workflow-engine behavior when Temporal or LangGraph already owns
  that concern.
- Use the README as the user-facing entry point; use this file for agent handoff.
