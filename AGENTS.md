# Agent Handoff Guide

This repository is a Java/Spring Boot prototype for a first-class Data 360/Data Cloud
plan-review-execute agent. It is meant to help a user describe a business goal,
review a small plan, approve gated side effects, and then execute Data 360/Salesforce
operations through a narrow tool boundary.

Current clean baseline: `c652942 Initial Data 360 agent console`.

## Where To Start

- Root: `/home/manoj/Projects/data360-agent-console`
- App entry point: `src/main/java/com/acme/data360agent/Data360AgentApplication.java`
- Browser UI: `src/main/resources/static/index.html`, `app.js`, `styles.css`
- Primary README: `README.md`
- Salesforce demo package: `salesforce/README.md`
- Current demo narrative docs: `docs/`

Always check `git status --short --branch` before editing. Other agents may have
changed files. Work with those changes; do not revert them unless the user explicitly
asks.

## Run And Test

Use Java 21 and Maven.

```bash
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

## Architecture Map

The important boundary is:

```text
Goal -> Planner -> PlanSpec -> Human review -> Executor -> Data 360/MCP tools
```

Keep these responsibilities separate:

- Planner: drafts a small plan, using LangGraph4j plus Anthropic, OpenRouter, or deterministic fallback.
- PlanSpec: the reviewable contract. It should stay small and serializable.
- Executor: runs approved steps in order and stores runtime outputs outside the plan.
- Monitor service: registers monitor-phase steps and evaluates goal health after setup.
- MCP/Data360 client: the only place that translates approved actions into tool calls.
- Temporal: production orchestration boundary, not the local demo runner yet.

## Key Java Packages

- `plan/`: `PlanSpec`, `PlanStep`, `PlanContext`, `Data360Action`, validation.
- `planner/`: LangGraph4j planner and Anthropic-backed plan drafting.
- `execution/`: local ordered executor, run state, step state, in-memory store.
- `data360/`: mock and MCP-backed Data 360 clients.
- `monitor/`: monitor definitions, run-now execution, threshold evaluation.
- `operation/`: allowed operation registry and effect metadata.
- `temporal/`: workflow/activity interfaces and notes for production wiring.
- `library/`: reusable solution templates.
- `scenario/`: publicly grounded customer scenario packs.
- `demo/`: Dormant Revenue Recovery demo API.
- `web/`: REST controllers and API exception handling.

## PlanSpec Rules

Be critical about PlanSpec changes. The current shape is intentionally simpler than
a workflow engine:

- Each node is a `PlanStep`.
- Each step has a phase: `discover`, `setup`, or `monitor`.
- Step input is the API/action parameters for that step, not arbitrary hidden state.
- Step output belongs in runtime execution records such as `StepRun`, not in the
  static plan.
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
- The executor should run only validated, approved, known operations.
- Never expose a generic unrestricted `d360.execute` from the plan surface.
- Route side effects through named operations such as query, create segment, publish
  segment, create activation, or run activation.
- Keep execution deterministic and auditable.
- Keep secrets, org credentials, and bearer tokens out of source files and logs.

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

`ConnectApiIdempotencyStore` is in-memory. It prevents duplicate mutation replay
inside this local process, but production should replace it with durable storage.

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
