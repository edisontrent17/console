import { expect, test } from "@playwright/test";

const email = process.env.E2E_EMAIL || "owner@example.com";
const password = process.env.E2E_PASSWORD || "password123";

async function login(page) {
    await page.goto("/");
    await expect(page).toHaveTitle("Data 360 Agent Console");
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
    await expect(page.getByRole("link", { name: "Admin" })).toBeVisible();
}

async function seedData360McpConfig(page) {
    await page.evaluate(async () => {
        const currentResponse = await fetch("/api/mcp-settings");
        if (!currentResponse.ok) {
            throw new Error(`Unable to load MCP config: ${currentResponse.status}`);
        }
        const current = await currentResponse.json();
        const servers = current.servers.map((server) => {
            if (server.id !== "data360") {
                return { ...server, enabled: false };
            }
            return {
                ...server,
                name: "Salesforce Data 360 MCP",
                enabled: true,
                transport: "stdio",
                command: "java",
                endpoint: "",
                arguments: ["-jar", "/opt/d360-mcp-server.jar"],
                environment: [
                    { key: "DATA360_LOGIN_URL", value: "https://login.salesforce.com" },
                    { key: "DATA360_API_VERSION", value: "66.0" }
                ],
                environmentPassthrough: [
                    "DATA360_INSTANCE_URL",
                    "DATA360_ACCESS_TOKEN",
                    "DATA360_CLIENT_ID",
                    "DATA360_CLIENT_SECRET",
                    "DATA360_AUTH_FLOW",
                    "DATA360_LOGIN_URL",
                    "DATA360_API_VERSION",
                    "DATA360_SEARCH_STRATEGY",
                    "OPENAI_API_KEY",
                    "SPRING_AI_MODEL_EMBEDDING"
                ],
                workingDirectory: ""
            };
        });
        const response = await fetch("/api/mcp-settings", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ servers })
        });
        if (!response.ok) {
            throw new Error(`Unable to seed MCP config: ${response.status}`);
        }
    });
}

async function openAdmin(page) {
    await page.getByRole("link", { name: "Admin" }).click();
    await expect(page.getByRole("heading", { name: "Connect to custom MCP servers" })).toBeVisible();
}

function data360ArgumentInputs(page) {
    return page.locator('input[data-server-id="data360"][placeholder="--flag=value"]');
}

test.beforeEach(async ({ page }) => {
    await login(page);
    await seedData360McpConfig(page);
    await page.reload();
    await expect(page.getByRole("link", { name: "Admin" })).toBeVisible();
});

test("admin settings render model and MCP controls cleanly", async ({ page }) => {
    const consoleErrors = [];
    page.on("console", (message) => {
        if (message.type() === "error") {
            consoleErrors.push(message.text());
        }
    });
    page.on("pageerror", (error) => consoleErrors.push(error.message));

    await openAdmin(page);

    await expect(page.getByRole("button", { name: "MCP servers" })).toBeVisible();
    await expect(page.getByText("Salesforce Data 360 MCP").first()).toBeVisible();
    await expect(page.getByText("Command to launch")).toBeVisible();
    await expect(page.getByRole("button", { name: "STDIO" })).toBeVisible();
    const data360Arguments = data360ArgumentInputs(page);
    await expect(data360Arguments.nth(0)).toHaveValue("-jar");
    await expect(data360Arguments.nth(1)).toHaveValue("/opt/d360-mcp-server.jar");
    await expect(page.getByRole("button", { name: "+ Add argument" })).toBeVisible();
    await expect(page.getByRole("button", { name: "+ Add environment variable" })).toBeVisible();

    await page.getByRole("button", { name: "Models" }).click();
    await expect(page.getByRole("heading", { name: "Model" })).toBeVisible();
    await expect(page.locator('select[data-field="settingsModel"]')).toBeVisible();
    await page.locator('select[data-field="settingsProvider"]').selectOption("openrouter");
    await expect.poll(async () => page.locator('select[data-field="settingsModel"] option').count(), {
        timeout: 15_000
    }).toBeGreaterThan(50);

    expect(consoleErrors).toEqual([]);
});

test("admin page saves MCP command arguments", async ({ page }) => {
    await openAdmin(page);

    await page.locator('input[data-server-id="data360"][data-field="command"]').fill("node");
    const data360Arguments = data360ArgumentInputs(page);
    await data360Arguments.nth(0).fill("/tmp/data360-mcp-server.js");
    await data360Arguments.nth(1).fill("--transport=stdio");
    await page.getByRole("button", { name: "+ Add argument" }).click();
    await data360ArgumentInputs(page).nth(2).fill("--log-level=debug");

    const saveResponse = page.waitForResponse((response) =>
        response.url().endsWith("/api/mcp-settings") && response.request().method() === "PUT"
    );
    await page.getByRole("button", { name: "Save and validate" }).click();
    await expect((await saveResponse).ok()).toBe(true);
    await expect(page.getByText("MCP settings saved.")).toBeVisible();
    await expect(page.getByText("1 MCP server validation failed.")).toBeVisible();

    await page.reload();
    await expect(page.getByRole("heading", { name: "Connect to custom MCP servers" })).toBeVisible();
    await expect(page.locator('input[data-server-id="data360"][data-field="command"]')).toHaveValue("node");
    const persistedArguments = data360ArgumentInputs(page);
    await expect(persistedArguments.nth(0)).toHaveValue("/tmp/data360-mcp-server.js");
    await expect(persistedArguments.nth(1)).toHaveValue("--transport=stdio");
    await expect(persistedArguments.nth(2)).toHaveValue("--log-level=debug");

    const saved = await page.evaluate(async () => {
        const response = await fetch("/api/mcp-settings");
        const settings = await response.json();
        return settings.servers.find((server) => server.id === "data360");
    });
    expect(saved.command).toBe("node");
    expect(saved.arguments).toEqual(["/tmp/data360-mcp-server.js", "--transport=stdio", "--log-level=debug"]);
});
