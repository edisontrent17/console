import { createRequire } from "node:module";
import fs from "node:fs";
import path from "node:path";

const require = createRequire(import.meta.url);
const aslValidator = require("asl-validator");

const files = process.argv.slice(2);
const targets = files.length ? files : ["schemas/examples/dormant-revenue.plan.json"];
let failed = false;

for (const target of targets) {
    const absolute = path.resolve(target);
    const raw = JSON.parse(fs.readFileSync(absolute, "utf8"));
    const definition = raw.definition || raw;
    const result = aslValidator(definition, {
        checkPaths: true,
        checkArn: false
    });

    if (result.isValid) {
        console.log(`ASL valid: ${target}`);
    } else {
        failed = true;
        console.error(`ASL invalid: ${target}`);
        console.error(result.errorsText());
    }
}

if (failed) {
    process.exit(1);
}
