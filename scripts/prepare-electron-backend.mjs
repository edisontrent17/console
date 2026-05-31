import { cp, mkdir, rm, stat } from "node:fs/promises";
import { existsSync } from "node:fs";
import path from "node:path";

const root = process.cwd();
const backendDir = path.join(root, "desktop", "dist", "backend");
const targetJar = path.join(root, "target", "data360-agent-console-0.0.1-SNAPSHOT.jar");
const packagedJar = path.join(backendDir, "data360-agent-console.jar");
const runtimeSource = process.env.D360_DESKTOP_JAVA_HOME || process.env.JAVA_HOME || "";

await mkdir(backendDir, { recursive: true });

if (!existsSync(targetJar)) {
    throw new Error(`Backend jar not found at ${targetJar}. Run npm run backend:package first.`);
}

await cp(targetJar, packagedJar);

const runtimeTarget = path.join(backendDir, "runtime");
await rm(runtimeTarget, { recursive: true, force: true });

if (runtimeSource && existsSync(path.join(runtimeSource, "bin", javaBinaryName()))) {
    const sourceStat = await stat(runtimeSource);
    if (!sourceStat.isDirectory()) {
        throw new Error(`D360_DESKTOP_JAVA_HOME is not a directory: ${runtimeSource}`);
    }
    await cp(runtimeSource, runtimeTarget, { recursive: true });
    console.log(`Prepared Electron backend jar and bundled Java runtime from ${runtimeSource}`);
} else {
    console.log("Prepared Electron backend jar. No bundled Java runtime found; desktop dev/packaged app will use system Java.");
}

function javaBinaryName() {
    return process.platform === "win32" ? "java.exe" : "java";
}
