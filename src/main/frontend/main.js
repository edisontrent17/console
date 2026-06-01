import "@lwc/synthetic-shadow";
import { createElement } from "lwc";
import Data360Console from "c/data360Console";

const mount = document.getElementById("app");
mount.replaceChildren(createElement("c-data360-console", { is: Data360Console }));
