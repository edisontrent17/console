import { expect, test } from "@playwright/test";

const email = process.env.E2E_EMAIL || "owner@example.com";
const password = process.env.E2E_PASSWORD || "password123";

test("admin settings render model and MCP controls cleanly", async ({ page }) => {
    const consoleErrors = [];
    page.on("console", (message) => {
        if (message.type() === "error") {
            consoleErrors.push(message.text());
        }
    });
    page.on("pageerror", (error) => consoleErrors.push(error.message));

    await page.goto("/");
    await expect(page).toHaveTitle("Data 360 Agent Console");

    await page.getByLabel("Email").fill(email);
    await page.getByLabel("Password").fill(password);
    await page.getByRole("button", { name: "Log in" }).click();

    await page.getByRole("link", { name: "Admin" }).click();

    await expect(page.getByRole("heading", { name: "Admin" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Model" })).toBeVisible();
    await expect(page.locator('select[data-field="settingsModel"]')).toBeVisible();
    await expect(page.getByRole("heading", { name: "MCP Servers" })).toBeVisible();
    await expect(page.getByText("Salesforce Data 360 MCP")).toBeVisible();
    await expect(page.locator('input.slds-input[data-server-id="data360"]')).toBeVisible();

    expect(consoleErrors).toEqual([]);
});
