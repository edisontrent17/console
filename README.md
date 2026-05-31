# Data 360 Agent Console

Java/Spring Boot prototype for a Data 360 plan-review-execute agent.

The current primary demo surface is an external **Data 360 Agent Console** built
with LWC OSS and SLDS. The web app opens on a chat-first planning surface:
users describe a Data 360 goal, review the generated PlanSpec before execution,
and keep reusable scenario/templates in a separate section.

The app keeps three boundaries separate:

- **LangGraph4j planner** drafts a small `PlanSpec`, using Anthropic, OpenRouter, or deterministic fallback.
- **PlanSpec** is the human-reviewable contract.
- **Executor** runs approved steps, pauses for write/publish/activation approvals, and stores runtime outputs outside the plan. Local mode is the default; Temporal mode is available for durable orchestration.
- **Monitors** track goal health after setup and produce approval-gated recommendations.
- **Durable state** stores drafts, runs, approvals, audit events, monitor leases, recommendations, and Connect idempotency records.

The production default is fail-closed: OAuth JWT auth is enabled and a datasource
must be supplied. The `dev` profile keeps the local product loop mock-backed and
easy to run before wiring real org credentials.

## PlanSpec Lab

The browser includes a chat-first PlanSpec flow. It lets you enter a goal,
generate a PlanSpec, start setup, approve gated setup steps, and run goal
monitors. The detailed review surface remains available as a separate section
for inspecting ASL states, inputs, bindings, outputs, and approvals.

The current PlanSpec phases are:

- `discover`: inspect or preview current Data 360 state
- `setup`: create, update, publish, or activate Data 360 assets through governed operations
- `monitor`: evaluate goal-health metrics after setup

PlanSpec deliberately stays small. It is now a governed Amazon States Language
profile: `definition.StartAt`, `definition.States`, `Task`, `Resource`,
`Parameters`, `ResultPath`, `Next`, and `End`. `Resource` must be a stable Data
360 capability URI such as `urn:salesforce:data360:capability:segment.create`,
not an MCP tool name, raw HTTP URL, or AWS ARN. The canonical PlanSpec schema is
versioned JSON Schema at `schemas/planspec.schema.json`; XML can be added later
as an import/export format that compiles into canonical JSON before validation
and execution.

Tool/API details live outside PlanSpec. Draft creation resolves each capability
URI into an immutable `OperationBindingSnapshot`; starting a run freezes that
binding list onto the run, and local/Temporal execution uses those snapshots
instead of asking the model to choose tools at execution time.
Bindings now include output contracts. That lets validation prove that dynamic
ASL parameters such as `unifiedProfileObjectApiName.$` actually reference a
declared output of an earlier capability like identity resolution.

## Real Scenario Packs

The scenario library contains publicly grounded Data 360/Agentforce goal packs:

- FedEx-style dormant account reactivation
- UChicago Medicine-style patient access and conversion
- Pacers Sports & Entertainment-style fan engagement
- Salesforce event-to-pipeline acceleration
- PepsiCo-style retailer engagement and store execution

These packs are synthesized implementation patterns grounded in public Salesforce
customer stories. They are not claims about private customer implementations.

## Solution Library

The Templates section includes reusable Data 360 implementation templates for complex cross-cloud patterns:

- Retail loyalty winback
- Financial household expansion
- Healthcare care-gap outreach
- Manufacturing service-to-sales
- Communications churn deflection
- Automotive connected service
- B2B product-led expansion
- Nonprofit donor stewardship

Each template creates the same small PlanSpec shape: preview query, create segment, publish segment, and create an activation draft.

## Run

Local demo:

```bash
npm install
npm run build
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Open:

```text
http://localhost:8080
```

On first run in the `dev` profile, create the first organization and owner from
the browser. After login, use **Admin** to save the Anthropic/OpenRouter provider,
model, and API token for the web app. API tokens are stored encrypted and never
returned to the browser.

If port 8080 is busy:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.arguments='--server.port=8082'
```

Open:

```text
http://localhost:8082
```

## Desktop App

The installable local product is an Electron shell around the same Spring/LWC
app. Web/server behavior stays unchanged; Electron starts the backend with the
`desktop` profile on `127.0.0.1:<dynamic-port>` and protects `/api/**` with a
per-launch `X-Data360-Desktop-Token`.

Development run:

```bash
npm run desktop:dev
```

Package an unpacked local app image:

```bash
npm run desktop:pack
```

Create platform installers:

```bash
npm run desktop:dist
```

By default the package uses system Java. To bundle a Java 21 runtime, point
`D360_DESKTOP_JAVA_HOME` at a JRE/JDK before packaging:

```bash
export D360_DESKTOP_JAVA_HOME="$JAVA_HOME"
npm run desktop:dist
```

Desktop settings are stored under the OS app data directory. Non-secret model
settings are persisted in `desktop-settings.json`; API keys are encrypted with
Electron `safeStorage` when available and are injected into the local backend
only at process launch/restart. The renderer receives only masked key metadata.

Desktop-only capabilities:

- choose Anthropic or OpenRouter and model name from the app
- save user-supplied API keys without changing web/server environment variables
- export the current PlanSpec archive as JSON
- export a full execution archive as JSON, including run state, approvals, audit
  events, operation binding snapshots, resolved inputs, raw redacted tool-call
  envelopes, outputs, timings, and errors

## External Goal Cockpit

The browser app calls:

```text
GET /api/demo/dormant-revenue-recovery
POST /api/demo/dormant-revenue-recovery/actions/{actionId}/approve
POST /api/demo/dormant-revenue-recovery/actions/{actionId}/execute
POST /api/demo/dormant-revenue-recovery/actions/{actionId}/reject
POST /api/demo/dormant-revenue-recovery/reset
```

The mock response includes:

- 5 recovery accounts
- $940k recoverable revenue
- Data360, Sales Cloud, Service Cloud, product telemetry, Marketing Cloud, and Slack context
- evidence cards for each selected account
- 11 proposed actions with approval state
- SendGrid activation story with approval-gated email sequence
- recovery and retention funnel from identified to revenue recovered
- impact metrics for the leadership demo

See `docs/dormant-revenue-sendgrid-demo-story.md` for the narrative and measurement model.

## LLM Providers

The planner can use Anthropic directly, OpenRouter, or deterministic fallback.
Without provider credentials, the app still generates scenario-grounded fallback
plans.

Anthropic direct:

```bash
export APP_LLM_PROVIDER=anthropic
export ANTHROPIC_API_KEY="..."
export ANTHROPIC_MODEL="claude-sonnet-4-5"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

OpenRouter:

```bash
export APP_LLM_PROVIDER=openrouter
export OPENROUTER_API_KEY="..."
export OPENROUTER_MODEL="anthropic/claude-sonnet-4.5"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Fallback:

```bash
export APP_LLM_PROVIDER=fallback
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Provider-specific behavior is isolated behind the `LlmClient` boundary. Model
output must validate into PlanSpec JSON before execution.

## Plan And Monitor APIs

```text
GET  /api/scenarios
POST /api/plans
GET  /api/plans
GET  /api/plans/{planId}
GET  /api/plans/{planId}/export
POST /api/plans/{planId}/runs
GET  /api/runs/{runId}
GET  /api/runs/{runId}/approvals
GET  /api/runs/{runId}/audit
GET  /api/runs/{runId}/export
POST /api/runs/{runId}/steps/{stepId}/approve
GET  /api/monitors
GET  /api/monitors/recommendations
POST /api/monitors/recommendations/{recommendationId}/approve
POST /api/monitors/recommendations/{recommendationId}/reject
GET  /api/monitors/{monitorId}
GET  /api/monitors/{monitorId}/runs
POST /api/monitors/{monitorId}/run-now
GET  /api/data360/diagnostics
POST /api/data360/diagnostics/smoke
```

## Auth

Production mode expects a JWT resource server:

```bash
export APP_SECURITY_ENABLED=true
export OAUTH2_ISSUER_URI="https://issuer.example.com"
export OAUTH2_JWK_SET_URI="https://issuer.example.com/.well-known/jwks.json"
export APP_SECURITY_REQUIRED_AUDIENCE="data360-agent-console"
export APP_SECURITY_ALLOWED_ORIGINS="https://console.example.com"
```

The API is split by scope:

- `data360.read`: read scenarios, templates, plans, runs, monitors, and demo state
- `data360.plan`: create PlanSpec drafts
- `data360.execute`: start setup runs
- `data360.approve`: approve setup steps and monitor recommendations
- `data360.monitor`: run monitors on demand
- `data360.demo`: mutate the demo action state
- `data360.admin` or `ROLE_DATA360_ADMIN`: diagnostics and audit access

`GET /api/me` returns the effective actor and authorities. Approval and monitor
recommendation reviews persist that actor into the audit trail.

## State And Scheduling

Production should use PostgreSQL with Flyway migrations:

```bash
export DATA360_AGENT_DB_URL="jdbc:postgresql://db.example.com:5432/data360_agent"
export DATA360_AGENT_DB_USERNAME="data360_agent"
export DATA360_AGENT_DB_PASSWORD="..."
export DATA360_AGENT_FLYWAY_ENABLED=true
```

The `dev` profile uses file-backed H2 at `./data/data360-agent-console`. Use
`app.state.store=memory` only for throwaway local experiments. JDBC mode keeps
PlanSpec drafts, setup runs, step output snapshots, approval records, audit events,
monitor definitions/runs/recommendations, monitor leases, and Connect API
idempotency records.

Flyway `baseline-on-migrate` defaults to `false` in production. Only set
`DATA360_AGENT_FLYWAY_BASELINE_ON_MIGRATE=true` during an explicit one-time
adoption of an existing schema.

Tests use an in-memory H2 datasource via `src/test/resources/application.properties`
so local runs do not create file-backed database state.

The monitor scheduler is disabled by default. Enable it when you want background
goal-health checks:

```properties
app.monitors.scheduler.enabled=true
app.monitors.scheduler.fixed-delay-ms=60000
```

The scheduler claims due monitors with a short lease before running them. Cadence
values currently map to minute, hourly, daily, or weekly intervals. Manual
`run-now` still works regardless of the scheduler.

## PlanSpec Validation

PlanSpec is validated in layers: standard ASL shape, the stricter Data 360 ASL
profile, and per-capability input rules before execution. The LangGraph planner
uses validation feedback to repair invalid model output before returning a draft.
Developers and agents can run the bundled ASL validator against a PlanSpec file:

```bash
npm run validate:asl
npm run validate:asl -- path/to/plan.json
```

## Temporal Executor

Local execution remains the default:

```properties
app.executor=local
```

Temporal execution is available behind the same `PlanExecutor` API:

```bash
export TEMPORAL_TARGET="127.0.0.1:7233"
export TEMPORAL_NAMESPACE="default"
export TEMPORAL_TASK_QUEUE="data360-plan-task-queue"
mvn spring-boot:run -Dspring-boot.run.arguments='--app.executor=temporal'
```

Temporal mode creates a workflow per setup run, using workflow IDs shaped like:

```text
data360-plan-{runId}
```

The workflow owns the ordered execution loop, approval waits, cancellation signal,
read-activity retries, and monitor-step skipping. Mutating Data 360 activities run
with a single Temporal attempt; Connect calls also receive a stable
`Idempotency-Key` header where upstream services honor it. Activities own the side
effects:

- `Data360Activities` executes Data 360/MCP/Connect calls through the configured `Data360Client`.
- `PlanRunActivities` persists step/run state, approval records, audit events, and monitor registration.

Workflow start receives the approved `OperationBindingSnapshot` list and never
constructs an operation registry during replay. This keeps Temporal deterministic
and makes the approved call boundary auditable.

Worker startup is on by default in Temporal mode. For a web-only instance that
only starts/signals workflows, disable the embedded worker:

```properties
app.temporal.worker-enabled=false
```

The Temporal workflow is covered by `Data360PlanWorkflowImplTest` using the
Temporal in-memory test environment.

## Data 360 MCP

The default client is mock mode:

```properties
app.data360.client=mock
```

To route calls through the Data 360 MCP stdio server, build `forcedotcom/d360-mcp-server` and start this app with:

```bash
mvn spring-boot:run \
  -Dspring-boot.run.arguments='--app.data360.client=mcp --app.data360.mcp.command="java -jar /absolute/path/to/data360-mcp-server-1.0.0.jar"'
```

The MCP adapter compiles MVP actions to the server's facade tools:

- `data360.search` -> `search`
- `data360.metadata.describe` -> `execute(d360_metadata_describe)`
- `data360.query` -> `execute(d360_query_sql)`
- `data360.dataStream.snowflake.create` -> `execute(d360_datastream_create_snowflake)`
- `data360.dataStream.crm.create` -> `execute(d360_datastream_create_sfdc)`
- `data360.mapping.create` -> `execute(d360_dmo_mapping_create)`
- `data360.calculatedInsight.create` -> `execute(d360_calculated_insight_create)`
- `data360.calculatedInsight.run` -> `execute(d360_calculated_insight_run)`
- `data360.createSegment` -> `execute(d360_segment_create)`
- `data360.updateSegment` -> `execute(d360_segment_update)`
- `data360.publishSegment` -> `execute(d360_segment_publish)`
- `data360.createActivation` -> `execute(d360_activation_create)`
- `data360.runActivation` -> mock-only until a target-specific Data 360 MCP operation is mapped
- `data360.identityRuleset.get` -> `execute(d360_identity_ruleset_get)`
- `data360.identityResolution.ruleset.create` -> `execute(d360_ir_create)`
- `data360.identityResolution.run` -> `execute(d360_ir_run)`
- `data360.monitor.metric` -> mock/local monitor evaluation

## Data 360 Connect API

The app can also route approved PlanSpec steps through Data 360 Connect REST
API by setting:

```bash
export APP_LLM_PROVIDER=fallback
export DATA360_CONNECT_INSTANCE_URL="https://your-dne-cdp-instance.example"
export DATA360_CONNECT_ACCESS_TOKEN="..."
mvn spring-boot:run -Dspring-boot.run.arguments='--app.data360.client=connect'
```

For real org authentication, prefer a Salesforce OAuth token exchange or JWT
bearer flow rather than long-lived static Data 360 tokens.

Salesforce access token exchange:

```bash
export SALESFORCE_INSTANCE_URL="https://your-org.my.salesforce.com"
export SALESFORCE_ACCESS_TOKEN="..."
mvn spring-boot:run -Dspring-boot.run.arguments='--app.data360.client=connect'
```

JWT bearer flow:

```bash
export SALESFORCE_LOGIN_URL="https://login.salesforce.com"
export SALESFORCE_CLIENT_ID="..."
export SALESFORCE_USERNAME="integration-user@example.com"
export SALESFORCE_PRIVATE_KEY_PATH="/secure/path/server.key.pkcs8.pem"
mvn spring-boot:run -Dspring-boot.run.arguments='--app.data360.client=connect'
```

Connect configuration:

```properties
app.data360.connect.api-version=v66.0
app.data360.connect.workload-name=data360-agent-console
app.data360.connect.timeout-seconds=45
```

The Connect client is typed by PlanSpec action:

- `data360.metadata.describe` -> `GET /api/v1/metadata/`
- `data360.query` -> `POST /services/data/{version}/ssot/query-sql`
- `data360.dataStream.snowflake.create` -> `POST /services/data/{version}/ssot/data-streams`
- `data360.dataStream.crm.create` -> `POST /services/data/{version}/ssot/data-streams`
- `data360.mapping.create` -> `POST /services/data/{version}/ssot/data-model-object-mappings`
- `data360.calculatedInsight.create` -> `POST /services/data/{version}/ssot/calculated-insights`
- `data360.calculatedInsight.run` -> `POST /services/data/{version}/ssot/calculated-insights/{id}/actions/run`
- `data360.createSegment` -> `POST /services/data/{version}/ssot/segments`
- `data360.updateSegment` -> `PATCH /services/data/{version}/ssot/segments/{id}`
- `data360.publishSegment` -> `POST /services/data/{version}/ssot/segments/{id}/actions/publish`
- `data360.createActivation` -> `POST /services/data/{version}/ssot/activations`
- `data360.runActivation` -> `POST /services/data/{version}/ssot/activations/{id}/actions/publish`
- `data360.identityRuleset.get` -> `GET /services/data/{version}/ssot/identity-resolutions`
- `data360.identityResolution.ruleset.create` -> `POST /services/data/{version}/ssot/identity-resolutions`
- `data360.identityResolution.run` -> `POST /services/data/{version}/ssot/identity-resolutions/{id}/actions/run-now`
- `data360.monitor.metric` -> Connect query execution against a monitor SQL/query context

Mutation calls are idempotent by run, step, and resolved input so duplicate
approval clicks do not replay successful create/publish/activation calls. In the
default JDBC mode, those idempotency records are durable.

Before approving setup against a real org, use the read-only diagnostics endpoint
or the **Check Data 360** button in the PlanSpec Lab:

```bash
curl http://localhost:8080/api/data360/diagnostics
curl -X POST http://localhost:8080/api/data360/diagnostics/smoke
```

`diagnostics` reports mode and configuration readiness without making an external
call. `smoke` performs one read-only metadata check through the selected Data 360
client.

## Tests

```bash
mvn test
```

## Salesforce Demo Package

The `salesforce/` folder contains a Salesforce DX demo package for the
Dormant Revenue Recovery story:

- custom `D360_*` objects for goal packs, runs, recovery accounts, evidence,
  proposed actions, approvals, and impact metrics
- a Lightning app named `Data360 Goal Agent Demo`
- a permission set named `Data360 Goal Agent Demo Admin`
- an idempotent Apex seed script with mock recovery-account data

See `salesforce/README.md` for deploy and demo instructions.
