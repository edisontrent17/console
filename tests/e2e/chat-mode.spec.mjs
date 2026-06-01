import { expect, test } from "@playwright/test";

const email = process.env.E2E_EMAIL || "owner@example.com";
const password = process.env.E2E_PASSWORD || "password123";

async function login(page) {
    await page.goto("/");
    await expect(page.getByLabel("Email")).toBeVisible();
    if (await page.getByRole("button", { name: "Create organization" }).isVisible()) {
        await page.getByLabel("Name").fill("Owner");
        await page.getByLabel("Email").fill(email);
        await page.getByLabel("Password").fill(password);
        const [bootstrapResponse] = await Promise.all([
            page.waitForResponse((response) => response.url().endsWith("/api/auth/bootstrap")),
            page.getByRole("button", { name: "Create organization" }).click()
        ]);
        if (!bootstrapResponse.ok()) {
            await page.goto("/");
            await expect(page.getByRole("button", { name: "Log in" })).toBeVisible();
            await page.getByLabel("Email").fill(email);
            await page.getByLabel("Password").fill(password);
            await page.getByRole("button", { name: "Log in" }).click();
        }
    } else {
        await page.getByLabel("Email").fill(email);
        await page.getByLabel("Password").fill(password);
        await page.getByRole("button", { name: "Log in" }).click();
    }
    await expect(page.getByRole("link", { name: "Chat" })).toBeVisible();
}

async function sendMessage(page, text) {
    await page.locator(".composer-input").fill(text);
    await page.locator(".send-button").click();
}

test("chat mode selector controls planning behavior", async ({ page }) => {
    await page.route("**/api/plans", async (route) => {
        if (route.request().method() !== "POST") {
            return route.fallback();
        }
        await route.fulfill({
            status: 200,
            contentType: "application/json",
            body: JSON.stringify({
                plan: {
                    id: "plan-test",
                    goal: "Show me the dataspaces",
                    steps: [
                        { id: "discover_dataspaces", title: "Discover dataspaces" }
                    ]
                },
                validation: { ok: true, issues: [] },
                graphStages: []
            })
        });
    });
    await page.route("**/api/plans/plan-test/export", async (route) => {
        await route.fulfill({
            status: 200,
            contentType: "application/json",
            body: JSON.stringify({ archiveType: "planspec", planId: "plan-test", plan: { id: "plan-test" } })
        });
    });
    await page.route("**/api/chat", async (route) => {
        if (route.request().method() !== "POST") {
            return route.fallback();
        }
        const body = route.request().postDataJSON();
        expect(body.mode).toBe("execute");
        await route.fulfill({
            status: 200,
            contentType: "application/json",
            body: JSON.stringify({
                text: "I found 1 data stream:\n- Test stream - profile - Active",
                provider: "mcp",
                model: "data360",
                trace: [
                    { stage: "request", label: "Received chat request", status: "completed", detail: "Mode: execute", data: {} },
                    { stage: "tool", label: "Called Data 360 MCP", status: "completed", detail: "Data 360 MCP tool completed.", durationMs: 123, data: { data360Tool: "d360_datastream_list" } }
                ]
            })
        });
    });

    await login(page);

    await expect(page.getByRole("button", { name: "Auto", exact: true })).toHaveClass(/active/);
    await page.getByRole("button", { name: "Plan" }).click();
    await expect(page.getByRole("button", { name: "Plan", exact: true })).toHaveClass(/active/);
    await expect(page.locator(".message").filter({ hasText: "Plan mode" })).toHaveCount(0);

    await sendMessage(page, "Show me the dataspaces");
    await expect(page.getByText("I am drafting a governed PlanSpec for that goal.")).toBeVisible();
    await expect(page.getByRole("button", { name: "Export PlanSpec" })).toBeVisible();
    const download = page.waitForEvent("download");
    await page.getByRole("button", { name: "Export PlanSpec" }).click();
    await download;
    await expect(page.getByText("PlanSpec exported.")).toBeVisible();
    await page.getByRole("button", { name: "Dismiss export notification" }).click();
    await expect(page.getByText("PlanSpec exported.")).toHaveCount(0);

    await page.getByRole("button", { name: "Execute" }).click();
    await expect(page.getByRole("button", { name: "Execute", exact: true })).toHaveClass(/active/);
    await expect(page.locator(".message").filter({ hasText: "Execute mode" })).toHaveCount(0);
    await sendMessage(page, "Can you show me a list of data streams?");
    await expect(page.getByText(/I found \d+ data stream/)).toBeVisible({ timeout: 60_000 });
    await expect(page.getByText("Data 360 MCP / data360")).toBeVisible();
    await page.getByText("Trace").click();
    await expect(page.getByText("Called Data 360 MCP")).toBeVisible();
    await expect(page.getByText("d360_datastream_list")).toBeVisible();
});
