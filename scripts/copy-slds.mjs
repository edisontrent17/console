import { copyFileSync, mkdirSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = dirname(fileURLToPath(new URL("../package.json", import.meta.url)));
const source = join(root, "node_modules", "@salesforce-ux", "design-system", "assets", "styles", "salesforce-lightning-design-system.min.css");
const target = join(root, "src", "main", "resources", "static", "assets", "slds", "styles", "salesforce-lightning-design-system.min.css");

mkdirSync(dirname(target), { recursive: true });
copyFileSync(source, target);
