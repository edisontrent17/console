import { expect, test } from "@playwright/test";

const scenario = {
    id: "travel_ltv_snowflake_crm",
    name: "Travel Customer LTV",
    industry: "Travel and Hospitality",
    defaultUtterances: [
        "Create a travel LTV audience from Snowflake bookings and CRM contacts"
    ]
};

const goal = "Create a travel LTV audience from Snowflake DCBOOTCAMP customer, itinerary, itinerary order, and CRM Contact data with lifetime value greater than 10000.";

const plan = {
    schemaVersion: "data360-asl-profile-2026-05-31",
    id: "plan_travel_ltv_snowflake_crm",
    scenarioId: scenario.id,
    goal,
    context: { org: "Acme Travel", dataspace: "default", environment: "sandbox" },
    definition: {
        Version: "1.0",
        QueryLanguage: "JSONPath",
        StartAt: "create_snowflake_streams",
        States: {
            create_snowflake_streams: {
                Type: "Task",
                Resource: "urn:salesforce:data360:capability:dataStream.snowflake.create",
                Parameters: { database: "DCBOOTCAMP", warehouse: "DCDEMO", tables: ["CUSTOMER", "ITINERARY", "ITINERARY_ORDER"] },
                ResultPath: "$.create_snowflake_streams",
                Next: "create_crm_contact_stream"
            },
            create_crm_contact_stream: {
                Type: "Task",
                Resource: "urn:salesforce:data360:capability:dataStream.crm.create",
                Parameters: { object: "Contact" },
                ResultPath: "$.create_crm_contact_stream",
                Next: "run_identity_resolution"
            },
            run_identity_resolution: {
                Type: "Task",
                Resource: "urn:salesforce:data360:capability:identityResolution.run",
                Parameters: {
                    "snowflakeDataStreamId.$": "$.create_snowflake_streams.dataStreamIds.customer",
                    "crmDataStreamId.$": "$.create_crm_contact_stream.dataStreamId"
                },
                ResultPath: "$.run_identity_resolution",
                Next: "create_ltv_insight"
            },
            create_ltv_insight: {
                Type: "Task",
                Resource: "urn:salesforce:data360:capability:calculatedInsight.create",
                Parameters: { name: "Travel Customer Lifetime Value", measure: "SUM(transactionAmount)" },
                ResultPath: "$.create_ltv_insight",
                Next: "preview_high_ltv_travelers"
            },
            preview_high_ltv_travelers: {
                Type: "Task",
                Resource: "urn:salesforce:data360:capability:query",
                Parameters: {
                    sql: "SELECT unified_individual_id, lifetime_value FROM UnifiedIndividual WHERE lifetime_value > 10000 LIMIT 100"
                },
                ResultPath: "$.preview_high_ltv_travelers",
                Next: "create_high_ltv_segment"
            },
            create_high_ltv_segment: {
                Type: "Task",
                Resource: "urn:salesforce:data360:capability:segment.create",
                Parameters: {
                    name: "High LTV Travelers",
                    criteria: "lifetime_value > 10000",
                    "audienceIds.$": "$.preview_high_ltv_travelers.rows"
                },
                ResultPath: "$.create_high_ltv_segment",
                End: true
            }
        }
    },
    steps: [
        step("create_snowflake_streams", "Create Snowflake travel streams", "data360.dataStream.snowflake.create", {
            database: "DCBOOTCAMP",
            warehouse: "DCDEMO",
            tables: ["CUSTOMER", "ITINERARY", "ITINERARY_ORDER"]
        }, [], {}, true),
        step("create_crm_contact_stream", "Create CRM Contact stream", "data360.dataStream.crm.create", { object: "Contact" }, ["create_snowflake_streams"], {}, true),
        step("run_identity_resolution", "Run identity resolution", "data360.identityResolution.run", {}, ["create_crm_contact_stream"], {
            snowflakeDataStreamId: { fromStep: "create_snowflake_streams", path: "$.dataStreamIds.customer" },
            crmDataStreamId: { fromStep: "create_crm_contact_stream", path: "$.dataStreamId" }
        }, true),
        step("create_ltv_insight", "Create Travel Customer Lifetime Value insight", "data360.calculatedInsight.create", {
            name: "Travel Customer Lifetime Value",
            measure: "SUM(transactionAmount)"
        }, ["run_identity_resolution"], {}, true),
        step("preview_high_ltv_travelers", "Preview high LTV travelers", "data360.query", {
            sql: "SELECT unified_individual_id, lifetime_value FROM UnifiedIndividual WHERE lifetime_value > 10000 LIMIT 100"
        }, ["create_ltv_insight"], {}, false),
        step("create_high_ltv_segment", "Create High LTV Travelers segment", "data360.createSegment", {
            name: "High LTV Travelers",
            criteria: "lifetime_value > 10000"
        }, ["preview_high_ltv_travelers"], {
            audienceIds: { fromStep: "preview_high_ltv_travelers", path: "$.rows" }
        }, true)
    ]
};

const artifact = {
    type: "plan_dag",
    title: "Plan DAG",
    data: {
        nodes: plan.steps.map((item) => ({
            id: item.id,
            label: item.title,
            phase: item.phase,
            action: item.action,
            resource: resourceFor(item.action),
            effect: item.needsApproval ? "write" : "read",
            approvalRequired: item.needsApproval,
            taskSnapshotHash: `sha256:${item.id}`
        })),
        edges: [
            { from: "create_snowflake_streams", to: "create_crm_contact_stream", type: "control", kind: "depends_on" },
            { from: "create_crm_contact_stream", to: "run_identity_resolution", type: "control", kind: "depends_on" },
            { from: "create_snowflake_streams", to: "run_identity_resolution", type: "data", kind: "input_binding", label: "snowflakeDataStreamId <- $.dataStreamIds.customer" },
            { from: "preview_high_ltv_travelers", to: "create_high_ltv_segment", type: "data", kind: "input_binding", label: "audienceIds <- $.rows" }
        ],
        groups: [
            { id: "setup", label: "Setup", nodeIds: plan.steps.map((item) => item.id) }
        ],
        topologicalOrder: plan.steps.map((item) => item.id),
        warnings: []
    }
};

const operationBindings = plan.steps.map((item) => ({
    resource: resourceFor(item.action),
    transport: "MCP",
    mcpServerId: "data360",
    facadeTool: item.action,
    underlyingTool: toolFor(item.action),
    effect: item.needsApproval ? "WRITE" : "READ",
    requiresApproval: item.needsApproval,
    schemaHash: `sha256:${item.id}`,
    registryHash: "sha256:registry",
    toolSchemaHash: `sha256:tool-${item.id}`,
    bindingVersion: "2026-05-31"
}));

const draft = {
    plan,
    validation: { ok: true, issues: [] },
    graphStages: ["draft_plan", "validate_plan"],
    operationBindings,
    artifacts: [artifact]
};

const approved = {
    artifactType: "approved-executable-plan",
    artifactId: "aplan_travel_ltv",
    planHash: "sha256:approved-travel-ltv",
    approvedAt: "2026-06-01T12:00:00Z",
    approvedBy: "owner@example.com",
    ...draft
};

const succeededRun = {
    id: "run_travel_ltv",
    organizationId: "org_a",
    status: "SUCCEEDED",
    createdAt: "2026-06-01T12:01:00Z",
    plan,
    approvedPlan: approved,
    operationBindings,
    steps: plan.steps.map((item) => ({
        stepId: item.id,
        status: "SUCCEEDED",
        startedAt: "2026-06-01T12:01:00Z",
        finishedAt: "2026-06-01T12:01:02Z",
        resolvedInput: item.input,
        output: outputFor(item.id),
        raw: { mcpTool: toolFor(item.action), idempotencyKey: `exec_${item.id}` }
    }))
};

const planArchive = {
    archiveType: "planspec",
    archiveSchemaVersion: "2026-06-01",
    redactionVersion: "2026-06-01",
    exportedAt: "2026-06-01T12:02:00Z",
    plan,
    validation: draft.validation,
    graphStages: draft.graphStages,
    taskHashes: operationBindings.map((binding) => ({
        resource: binding.resource,
        transport: binding.transport,
        mcpServerId: binding.mcpServerId,
        facadeTool: binding.facadeTool,
        underlyingTool: binding.underlyingTool,
        effect: binding.effect,
        requiresApproval: binding.requiresApproval,
        schemaHash: binding.schemaHash,
        bindingVersion: binding.bindingVersion
    })),
    artifacts: [artifact]
};

const executionArchive = {
    archiveType: "execution-log",
    archiveSchemaVersion: "2026-06-01",
    redactionVersion: "2026-06-01",
    exportedAt: "2026-06-01T12:03:00Z",
    run: {
        id: succeededRun.id,
        organizationId: succeededRun.organizationId,
        status: succeededRun.status,
        createdAt: succeededRun.createdAt,
        plan,
        taskHashes: planArchive.taskHashes,
        artifacts: [artifact],
        steps: succeededRun.steps.map(({ stepId, status, output }) => ({ id: stepId, status, selectedOutput: output }))
    },
    auditEvents: [
        { eventType: "step_tool_call_prepared", stepId: "create_snowflake_streams", detail: { idempotencyKey: "exec_create_snowflake_streams" } },
        { eventType: "run_succeeded", detail: { runId: succeededRun.id } }
    ],
    approvals: [{ stepId: "plan", actor: "owner@example.com", status: "APPROVED" }],
    toolCalls: succeededRun.steps.map((item) => ({
        stepId: item.stepId,
        status: item.status,
        action: plan.steps.find((stepItem) => stepItem.id === item.stepId)?.action,
        resource: resourceFor(plan.steps.find((stepItem) => stepItem.id === item.stepId)?.action),
        resolvedInput: item.resolvedInput,
        output: item.output,
        raw: item.raw
    }))
};

test("review approval execution and trace archive round-trip for Travel LTV", async ({ page }) => {
    const importedPlans = [];
    let runStartedWithArtifact = false;
    await setupMockApi(page, importedPlans, () => {
        runStartedWithArtifact = true;
    });

    await page.goto("/#review");
    await expect(page.getByRole("heading", { name: "Request" })).toBeVisible();
    await expect(page.getByLabel("Scenario")).toContainText("Travel Customer LTV");

    await page.getByLabel("Scenario").selectOption(scenario.id);
    await page.getByLabel("Goal").fill(goal);
    await page.getByRole("button", { name: "Draft plan" }).click();

    await expect(page.getByText("Create Travel Customer Lifetime Value insight")).toBeVisible();
    await expect(page.getByText("Validated")).toBeVisible();
    await page.getByText("Create High LTV Travelers segment").click();
    await expect(page.getByText("lifetime_value > 10000").first()).toBeVisible();

    await page.getByRole("button", { name: "DAG" }).click();
    await expect(page.getByRole("region", { name: "Plan DAG" })).toContainText("Draft DAG");
    await expect(page.getByText("preview_high_ltv_travelers", { exact: true }).first()).toBeVisible();

    await page.getByRole("button", { name: "Approve plan" }).click();
    await expect(page.getByText("Executable artifact approved")).toBeVisible();
    await expect(page.getByText("aplan_travel_ltv", { exact: true }).first()).toBeVisible();

    const download = page.waitForEvent("download");
    await page.getByRole("button", { name: "Export PlanSpec" }).click();
    await download;
    await expect(page.getByText("PlanSpec exported.")).toBeVisible();
    await page.getByRole("button", { name: "Dismiss export notification" }).click();
    await expect(page.getByText("PlanSpec exported.")).toHaveCount(0);

    await page.getByRole("button", { name: "Start run" }).click();
    await expect.poll(() => runStartedWithArtifact).toBe(true);
    await expect(page.getByText("SUCCEEDED").first()).toBeVisible();
    await expect(page.getByText("Travel Customer Lifetime Value")).toBeVisible();

    const archiveRoundTrip = await page.evaluate(async () => {
        const exported = await fetch("/api/runs/run_travel_ltv/export").then((response) => response.json());
        const imported = await fetch("/api/plans/import?replace=true", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(exported.run.plan)
        }).then((response) => response.json());
        return {
            archiveType: exported.archiveType,
            toolCallCount: exported.toolCalls.length,
            firstTool: exported.toolCalls[0].raw.mcpTool,
            taskHashCount: exported.run.taskHashes.length,
            importedPlanId: imported.plan.id,
            importedValidationOk: imported.validation.ok
        };
    });

    expect(archiveRoundTrip).toEqual({
        archiveType: "execution-log",
        toolCallCount: plan.steps.length,
        firstTool: "d360_datastream_create",
        taskHashCount: plan.steps.length,
        importedPlanId: plan.id,
        importedValidationOk: true
    });
    expect(importedPlans).toHaveLength(1);
    expect(importedPlans[0].id).toBe(plan.id);
});

async function setupMockApi(page, importedPlans, markRunStarted) {
    await page.route("**/api/**", async (route) => {
        const request = route.request();
        const url = new URL(request.url());
        const path = url.pathname;
        const method = request.method();

        if (method === "GET" && path === "/api/auth/status") {
            return json(route, {
                setupRequired: false,
                user: {
                    authenticated: true,
                    username: "owner@example.com",
                    organizationId: "org_a",
                    organizationName: "Acme Travel",
                    authorities: ["data360.plan", "data360.approve", "data360.execute"]
                }
            });
        }
        if (method === "GET" && path === "/api/scenarios") return json(route, [scenario]);
        if (method === "GET" && path === "/api/library") return json(route, []);
        if (method === "GET" && path === "/api/data360/diagnostics") return json(route, { status: "passed", mode: "mcp", configured: true });
        if (method === "GET" && path === "/api/demo/dormant-revenue-recovery") return json(route, { accounts: [] });
        if (method === "GET" && path === "/api/monitors/recommendations") return json(route, []);
        if (method === "GET" && path === "/api/monitors") return json(route, []);
        if (method === "GET" && path === "/api/llm-settings") return json(route, { provider: "anthropic", model: "claude-sonnet-4-6", apiKeyConfigured: true });
        if (method === "GET" && path === "/api/llm-settings/models") return json(route, [{ id: "claude-sonnet-4-6", label: "Claude Sonnet 4.6" }]);
        if (method === "GET" && path === "/api/mcp-settings") return json(route, { servers: [] });
        if (method === "GET" && path === "/api/organizations") return json(route, [{ id: "org_a", name: "Acme Travel" }]);
        if (method === "GET" && path === "/api/organizations/org_a/users") return json(route, []);
        if (method === "POST" && path === "/api/plans") {
            const body = request.postDataJSON();
            expect(body.scenarioId).toBe(scenario.id);
            expect(body.goal).toContain("lifetime value greater than 10000");
            return json(route, draft);
        }
        if (method === "POST" && path === `/api/plans/${plan.id}/approve`) return json(route, approved);
        if (method === "GET" && path === `/api/plans/${plan.id}/export`) return json(route, planArchive);
        if (method === "POST" && path === `/api/plans/${plan.id}/runs`) {
            expect(request.postDataJSON()).toEqual({ artifactId: approved.artifactId, planHash: approved.planHash });
            markRunStarted();
            return json(route, { ...succeededRun, status: "RUNNING" });
        }
        if (method === "GET" && path === `/api/runs/${succeededRun.id}`) return json(route, succeededRun);
        if (method === "GET" && path === `/api/runs/${succeededRun.id}/approvals`) return json(route, executionArchive.approvals);
        if (method === "GET" && path === `/api/runs/${succeededRun.id}/export`) return json(route, executionArchive);
        if (method === "POST" && path === "/api/plans/import") {
            expect(url.searchParams.get("replace")).toBe("true");
            const body = request.postDataJSON();
            importedPlans.push(body);
            return json(route, {
                plan: body,
                validation: { ok: true, issues: [] },
                graphStages: ["import_plan", "validate_plan"],
                operationBindings,
                artifacts: [artifact]
            });
        }

        return json(route, { message: `Unhandled mock route: ${method} ${path}` }, 404);
    });
}

async function json(route, body, status = 200) {
    await route.fulfill({
        status,
        contentType: "application/json",
        body: JSON.stringify(body)
    });
}

function step(id, title, action, input, dependsOn, inputBindings, needsApproval) {
    return {
        id,
        title,
        phase: action === "data360.query" ? "discover" : "setup",
        action,
        input,
        dependsOn,
        inputBindings,
        needsApproval
    };
}

function outputFor(stepId) {
    if (stepId === "create_snowflake_streams") {
        return { dataStreamIds: { customer: "ds_customer", itinerary: "ds_itinerary", itineraryOrder: "ds_itinerary_order" } };
    }
    if (stepId === "create_crm_contact_stream") return { dataStreamId: "ds_contact" };
    if (stepId === "preview_high_ltv_travelers") return { rows: [{ unified_individual_id: "uid-001", lifetime_value: 18500 }] };
    if (stepId === "create_high_ltv_segment") return { segmentId: "seg_high_ltv_travelers" };
    return { id: `${stepId}_result` };
}

function toolFor(action) {
    if (action.includes("dataStream")) return "d360_datastream_create";
    if (action.includes("identityResolution")) return "d360_identity_resolution_run";
    if (action.includes("calculatedInsight")) return "d360_calculated_insight_create";
    if (action.includes("createSegment")) return "d360_segment_create";
    return "d360_query";
}

function resourceFor(action) {
    const suffix = {
        "data360.dataStream.snowflake.create": "dataStream.snowflake.create",
        "data360.dataStream.crm.create": "dataStream.crm.create",
        "data360.identityResolution.run": "identityResolution.run",
        "data360.calculatedInsight.create": "calculatedInsight.create",
        "data360.query": "query",
        "data360.createSegment": "segment.create"
    }[action] || action.replace("data360.", "");
    return `urn:salesforce:data360:capability:${suffix}`;
}
