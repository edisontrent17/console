# Data 360 Agent Console

Java/Spring Boot prototype for a Data 360 plan-review-execute agent.

The current primary demo surface is an external **Data360 Goal Cockpit** for
the Dormant Revenue Recovery story. It runs outside Salesforce, shows a
business goal, trusted Data360/Salesforce context, account-level evidence,
approval-gated actions, and impact metrics.

The app keeps three boundaries separate:

- **LangGraph4j planner** drafts a small `PlanSpec`.
- **PlanSpec** is the human-reviewable contract.
- **Executor** runs approved steps, pauses for write/publish/activation approvals, and stores runtime outputs outside the plan.

The default mode is local and mock-backed so the product loop can be tested before wiring real org credentials.

## Solution Library

The left rail includes reusable Data 360 implementation templates for complex cross-cloud patterns:

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

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

If port 8080 is busy:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8082'
```

Open:

```text
http://localhost:8082
```

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

## Anthropic

If `ANTHROPIC_API_KEY` is set, the planner calls Anthropic Messages API. Without it, the app uses a deterministic fallback plan.

```bash
export ANTHROPIC_API_KEY="..."
export ANTHROPIC_MODEL="claude-sonnet-4-5"
mvn spring-boot:run
```

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
- `data360.query` -> `execute(d360_query_sql)`
- `data360.createSegment` -> `execute(d360_segment_create)`
- `data360.publishSegment` -> `execute(d360_segment_publish)`
- `data360.createActivation` -> `execute(d360_activation_create)`
- `data360.runActivation` -> mock-only until a target-specific Data 360 MCP operation is mapped

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
