import { expect, test } from "@playwright/test";

const email = process.env.E2E_EMAIL || "owner@example.com";
const password = process.env.E2E_PASSWORD || "password123";

test.describe("live chat API", () => {
    test.skip(process.env.RUN_LIVE_LLM_TESTS !== "1", "Set RUN_LIVE_LLM_TESTS=1 to call the configured live model.");

    test("responds through the configured model provider", async ({ request }) => {
        const login = await request.post("/api/auth/login", {
            data: { email, password }
        });
        expect(login.ok()).toBeTruthy();

        const settings = await request.get("/api/llm-settings");
        expect(settings.ok()).toBeTruthy();
        const configured = await settings.json();
        expect(configured.apiKeyConfigured).toBeTruthy();

        const chat = await request.post("/api/chat", {
            data: { message: "Who are you?" },
            timeout: 60_000
        });
        expect(chat.ok()).toBeTruthy();
        const body = await chat.json();
        expect(body.text).toContain("Data 360");
        expect(body.provider).toBe(configured.provider);
        expect(body.model).toBe(configured.model);
    });
});

test.describe("live MCP exploration API", () => {
    test.skip(process.env.RUN_LIVE_MCP_TESTS !== "1", "Set RUN_LIVE_MCP_TESTS=1 to call the configured Data 360 MCP server.");

    test("answers read-only dataspace questions directly", async ({ request }) => {
        const login = await request.post("/api/auth/login", {
            data: { email, password }
        });
        expect(login.ok()).toBeTruthy();

        const chat = await request.post("/api/chat", {
            data: { message: "Can you show me a list of dataspces?" },
            timeout: 60_000
        });
        expect(chat.ok()).toBeTruthy();
        const body = await chat.json();
        expect(body.provider).toBe("mcp");
        expect(body.model).toBe("data360");
        expect(body.text).toContain("dataspace");
        expect(body.text).toContain("default");
        expect(body.trace.some((entry) => entry.stage === "tool" && entry.data?.data360Tool === "d360_dataspace_list")).toBeTruthy();
    });

    test("answers read-only data stream questions directly", async ({ request }) => {
        const login = await request.post("/api/auth/login", {
            data: { email, password }
        });
        expect(login.ok()).toBeTruthy();

        const chat = await request.post("/api/chat", {
            data: { message: "Can you show me a list of data streams?", mode: "execute" },
            timeout: 60_000
        });
        expect(chat.ok()).toBeTruthy();
        const body = await chat.json();
        expect(body.provider).toBe("mcp");
        expect(body.model).toBe("data360");
        expect(body.text).toContain("data stream");
        expect(body.trace.some((entry) => entry.stage === "tool" && entry.data?.data360Tool === "d360_datastream_list")).toBeTruthy();
    });
});
