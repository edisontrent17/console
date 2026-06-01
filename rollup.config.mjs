import lwc from "@lwc/rollup-plugin";
import resolve from "@rollup/plugin-node-resolve";
import replace from "@rollup/plugin-replace";

export default {
    input: "src/main/frontend/main.js",
    output: {
        file: "src/main/resources/static/app.js",
        format: "esm"
    },
    plugins: [
        replace({
            preventAssignment: true,
            "process.env.NODE_ENV": JSON.stringify("production")
        }),
        lwc({
            rootDir: "src/main/frontend/modules"
        }),
        resolve()
    ]
};
