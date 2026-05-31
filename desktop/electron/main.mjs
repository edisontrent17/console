import { app, BrowserWindow, dialog, ipcMain, safeStorage, session, shell } from "electron";
import { spawn } from "node:child_process";
import { createWriteStream, existsSync } from "node:fs";
import { mkdir, readFile, writeFile } from "node:fs/promises";
import net from "node:net";
import path from "node:path";
import crypto from "node:crypto";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const APP_NAME = "Data 360 Agent Console";
const SETTINGS_FILE = "desktop-settings.json";
const DEFAULT_SETTINGS = Object.freeze({
    provider: "anthropic",
    anthropic: { model: "claude-sonnet-4-5", apiKey: null },
    openrouter: { model: "anthropic/claude-sonnet-4.5", apiKey: null }
});

let mainWindow;
let backendProcess;
let backendUrl;
let backendLog;
let desktopApiToken;
let quitting = false;
let runtimeSettingsOverride = null;

const singleInstance = app.requestSingleInstanceLock();
if (!singleInstance) {
    app.quit();
}

app.on("second-instance", () => {
    if (mainWindow) {
        if (mainWindow.isMinimized()) mainWindow.restore();
        mainWindow.focus();
    }
});

app.whenReady().then(async () => {
    registerIpc();
    hardenSession();
    mainWindow = createWindow();
    try {
        await startBackend();
        await mainWindow.loadURL(backendUrl);
    } catch (error) {
        await showStartupFailure(error);
    }
});

app.on("before-quit", async (event) => {
    if (quitting) return;
    event.preventDefault();
    quitting = true;
    await stopBackend();
    app.exit(0);
});

app.on("window-all-closed", () => {
    if (process.platform !== "darwin") {
        app.quit();
    }
});

function createWindow() {
    const window = new BrowserWindow({
        width: 1440,
        height: 960,
        minWidth: 1100,
        minHeight: 720,
        title: APP_NAME,
        show: false,
        webPreferences: {
            preload: path.join(__dirname, "preload.cjs"),
            contextIsolation: true,
            nodeIntegration: false,
            sandbox: true,
            webSecurity: true
        }
    });
    window.once("ready-to-show", () => window.show());
    window.webContents.setWindowOpenHandler(({ url }) => {
        if (url.startsWith("https://")) {
            shell.openExternal(url);
        }
        return { action: "deny" };
    });
    window.webContents.on("will-navigate", (event, url) => {
        if (backendUrl && !url.startsWith(backendUrl)) {
            event.preventDefault();
        }
    });
    return window;
}

function hardenSession() {
    session.defaultSession.setPermissionRequestHandler((_webContents, _permission, callback) => callback(false));
}

function registerIpc() {
    ipcMain.handle("desktop:app-info", () => ({
        name: APP_NAME,
        version: app.getVersion(),
        backendUrl,
        logPath: backendLog,
        userDataPath: app.getPath("userData")
    }));
    ipcMain.handle("desktop:api-headers", () => (
        desktopApiToken ? { "X-Data360-Desktop-Token": desktopApiToken } : {}
    ));
    ipcMain.handle("desktop:get-settings", async () => publicSettings(await readSettings(true)));
    ipcMain.handle("desktop:save-settings", async (_event, incoming) => {
        const current = await readSettings(true);
        const next = normalizeSettings(incoming, current);
        await writeSettings(next);
        runtimeSettingsOverride = next;
        await restartBackend();
        return publicSettings(next);
    });
    ipcMain.handle("desktop:export-json", async (_event, request) => exportJson(request));
}

async function startBackend() {
    if (process.env.DATA360_ELECTRON_BACKEND_URL) {
        backendUrl = process.env.DATA360_ELECTRON_BACKEND_URL;
        return;
    }
    const port = await findFreePort();
    desktopApiToken = crypto.randomBytes(32).toString("base64url");
    backendUrl = `http://127.0.0.1:${port}/`;
    backendLog = await backendLogPath();
    const log = createWriteStream(backendLog, { flags: "a" });
    const settings = runtimeSettingsOverride || await readSettings(true);
    const java = javaCommand();
    const jar = backendJarPath();
    const args = [
        "-jar",
        jar,
        "--spring.profiles.active=desktop",
        "--server.address=127.0.0.1",
        `--server.port=${port}`,
        `--app.desktop.api-token=${desktopApiToken}`
    ];
    const env = {
        ...process.env,
        DATA360_AGENT_DESKTOP_HOME: app.getPath("userData"),
        APP_LLM_PROVIDER: settings.provider,
        ANTHROPIC_MODEL: settings.anthropic.model,
        ANTHROPIC_API_KEY: settings.anthropic.apiKey || "",
        OPENROUTER_MODEL: settings.openrouter.model,
        OPENROUTER_API_KEY: settings.openrouter.apiKey || "",
        OPENROUTER_SITE_URL: backendUrl,
        APP_SECURITY_ENABLED: "false"
    };
    backendProcess = spawn(java, args, {
        cwd: app.getPath("userData"),
        env,
        windowsHide: true
    });
    backendProcess.stdout.pipe(log);
    backendProcess.stderr.pipe(log);
    backendProcess.once("exit", (code, signal) => {
        if (!quitting && mainWindow && !mainWindow.isDestroyed()) {
            dialog.showErrorBox(APP_NAME, `The local backend stopped unexpectedly (${signal || code}). Log: ${backendLog}`);
        }
    });
    await waitForBackend(backendUrl);
}

async function restartBackend() {
    await stopBackend();
    await startBackend();
    if (mainWindow && !mainWindow.isDestroyed()) {
        await mainWindow.loadURL(backendUrl);
    }
}

async function stopBackend() {
    if (!backendProcess || backendProcess.killed) {
        backendProcess = null;
        return;
    }
    const processToStop = backendProcess;
    backendProcess = null;
    let exited = false;
    processToStop.once("exit", () => {
        exited = true;
    });
    if (process.platform === "win32") {
        spawn("taskkill", ["/pid", String(processToStop.pid), "/T", "/F"], { windowsHide: true });
    } else {
        processToStop.kill("SIGTERM");
    }
    await new Promise((resolve) => {
        const timeout = setTimeout(() => {
            if (!exited) {
                processToStop.kill("SIGKILL");
            }
            resolve();
        }, 8000);
        processToStop.once("exit", () => {
            clearTimeout(timeout);
            resolve();
        });
    });
}

function backendJarPath() {
    const packaged = path.join(process.resourcesPath || "", "backend", "data360-agent-console.jar");
    if (app.isPackaged && existsSync(packaged)) {
        return packaged;
    }
    const prepared = path.join(app.getAppPath(), "desktop", "dist", "backend", "data360-agent-console.jar");
    if (existsSync(prepared)) {
        return prepared;
    }
    const target = path.join(app.getAppPath(), "target", "data360-agent-console-0.0.1-SNAPSHOT.jar");
    if (existsSync(target)) {
        return target;
    }
    throw new Error("Backend jar is missing. Run npm run backend:package and npm run desktop:prepare.");
}

function javaCommand() {
    const runtimeJava = path.join(process.resourcesPath || "", "backend", "runtime", "bin", javaBinary());
    if (app.isPackaged && existsSync(runtimeJava)) {
        return runtimeJava;
    }
    const preparedJava = path.join(app.getAppPath(), "desktop", "dist", "backend", "runtime", "bin", javaBinary());
    if (existsSync(preparedJava)) {
        return preparedJava;
    }
    const javaHome = process.env.D360_DESKTOP_JAVA_HOME || process.env.JAVA_HOME;
    if (javaHome) {
        const candidate = path.join(javaHome, "bin", javaBinary());
        if (existsSync(candidate)) {
            return candidate;
        }
    }
    return "java";
}

function javaBinary() {
    return process.platform === "win32" ? "java.exe" : "java";
}

async function backendLogPath() {
    const dir = path.join(app.getPath("userData"), "logs");
    await mkdir(dir, { recursive: true });
    return path.join(dir, `backend-${new Date().toISOString().replace(/[:.]/g, "-")}.log`);
}

async function findFreePort() {
    return new Promise((resolve, reject) => {
        const server = net.createServer();
        server.once("error", reject);
        server.listen(0, "127.0.0.1", () => {
            const address = server.address();
            const port = typeof address === "object" && address ? address.port : 0;
            server.close(() => resolve(port));
        });
    });
}

async function waitForBackend(url) {
    const deadline = Date.now() + 60000;
    let lastError;
    while (Date.now() < deadline) {
        try {
            const response = await fetch(url, {
                headers: { "X-Data360-Desktop-Token": desktopApiToken || "" }
            });
            if (response.ok) {
                return;
            }
        } catch (error) {
            lastError = error;
        }
        await delay(500);
    }
    throw new Error(`Backend did not become ready at ${url}. ${lastError ? lastError.message : ""}`);
}

async function exportJson(request) {
    const payload = request?.payload;
    if (!payload || typeof payload !== "object") {
        throw new Error("Export payload must be an object.");
    }
    const defaultPath = sanitizeFileName(request.defaultFileName || "data360-export.json");
    const result = await dialog.showSaveDialog(mainWindow, {
        title: "Export JSON",
        defaultPath,
        filters: [{ name: "JSON", extensions: ["json"] }]
    });
    if (result.canceled || !result.filePath) {
        return { canceled: true };
    }
    await writeFile(result.filePath, JSON.stringify(payload, null, 2), "utf8");
    return { canceled: false, filePath: result.filePath };
}

async function readSettings(includeSecrets = false) {
    const file = settingsPath();
    if (!existsSync(file)) {
        return structuredClone(DEFAULT_SETTINGS);
    }
    try {
        const parsed = JSON.parse(await readFile(file, "utf8"));
        return normalizePersistedSettings(parsed, includeSecrets);
    } catch {
        return structuredClone(DEFAULT_SETTINGS);
    }
}

async function writeSettings(settings) {
    const file = settingsPath();
    await mkdir(path.dirname(file), { recursive: true });
    const persisted = {
        provider: settings.provider,
        anthropic: {
            model: settings.anthropic.model,
            apiKey: encryptSecret(settings.anthropic.apiKey)
        },
        openrouter: {
            model: settings.openrouter.model,
            apiKey: encryptSecret(settings.openrouter.apiKey)
        }
    };
    await writeFile(file, JSON.stringify(persisted, null, 2), "utf8");
}

function settingsPath() {
    return path.join(app.getPath("userData"), SETTINGS_FILE);
}

function normalizeSettings(incoming, current) {
    const provider = ["anthropic", "openrouter"].includes(incoming?.provider) ? incoming.provider : current.provider;
    return {
        provider,
        anthropic: normalizeProviderSettings(incoming?.anthropic, current.anthropic, DEFAULT_SETTINGS.anthropic.model),
        openrouter: normalizeProviderSettings(incoming?.openrouter, current.openrouter, DEFAULT_SETTINGS.openrouter.model)
    };
}

function normalizeProviderSettings(incoming, current, defaultModel) {
    const model = sanitizeModel(incoming?.model || current.model || defaultModel);
    let apiKey = current.apiKey || null;
    if (incoming?.clearApiKey) {
        apiKey = null;
    } else if (typeof incoming?.apiKey === "string" && incoming.apiKey.trim()) {
        apiKey = incoming.apiKey.trim();
    }
    return { model, apiKey };
}

function normalizePersistedSettings(parsed, includeSecrets) {
    const provider = ["anthropic", "openrouter"].includes(parsed?.provider) ? parsed.provider : DEFAULT_SETTINGS.provider;
    return {
        provider,
        anthropic: {
            model: sanitizeModel(parsed?.anthropic?.model || DEFAULT_SETTINGS.anthropic.model),
            apiKey: includeSecrets ? decryptSecret(parsed?.anthropic?.apiKey) : null
        },
        openrouter: {
            model: sanitizeModel(parsed?.openrouter?.model || DEFAULT_SETTINGS.openrouter.model),
            apiKey: includeSecrets ? decryptSecret(parsed?.openrouter?.apiKey) : null
        }
    };
}

function publicSettings(settings) {
    return {
        provider: settings.provider,
        keyStorageAvailable: safeStorage.isEncryptionAvailable(),
        backendUrl,
        anthropic: {
            model: settings.anthropic.model,
            apiKeyConfigured: Boolean(settings.anthropic.apiKey),
            apiKeyLast4: last4(settings.anthropic.apiKey)
        },
        openrouter: {
            model: settings.openrouter.model,
            apiKeyConfigured: Boolean(settings.openrouter.apiKey),
            apiKeyLast4: last4(settings.openrouter.apiKey)
        }
    };
}

function encryptSecret(value) {
    if (!value) {
        return null;
    }
    if (!safeStorage.isEncryptionAvailable()) {
        return { unavailable: true };
    }
    return {
        encoding: "electron-safe-storage",
        ciphertext: safeStorage.encryptString(value).toString("base64")
    };
}

function decryptSecret(value) {
    if (!value?.ciphertext || value.encoding !== "electron-safe-storage" || !safeStorage.isEncryptionAvailable()) {
        return null;
    }
    try {
        return safeStorage.decryptString(Buffer.from(value.ciphertext, "base64"));
    } catch {
        return null;
    }
}

function sanitizeModel(value) {
    const text = String(value || "").trim();
    if (!/^[A-Za-z0-9._:/@-]{1,120}$/.test(text)) {
        return DEFAULT_SETTINGS.anthropic.model;
    }
    return text;
}

function sanitizeFileName(value) {
    return String(value).replace(/[<>:"/\\|?*\x00-\x1F]/g, "_").slice(0, 180);
}

function last4(value) {
    return value ? value.slice(-4) : "";
}

async function showStartupFailure(error) {
    dialog.showErrorBox(APP_NAME, `Unable to start the local backend.\n\n${error.message}\n\nLog: ${backendLog || "not available"}`);
    if (mainWindow && !mainWindow.isDestroyed()) {
        await mainWindow.loadURL(`data:text/html,<h1>${APP_NAME}</h1><p>Unable to start local backend.</p>`);
    }
}

function delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}
