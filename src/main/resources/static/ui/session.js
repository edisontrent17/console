import { api } from "./api.js";
import { $, escapeHtml } from "./dom.js";

export async function initSession() {
    const node = $("currentUser");
    if (!node) return;

    try {
        const user = await api("/api/me");
        node.textContent = user.authenticated
            ? user.username
            : "Demo mode";
        node.title = user.authenticated
            ? `Authenticated as ${user.username}`
            : "Authentication is disabled for this environment.";
        node.className = user.authenticated ? "user-pill authenticated" : "user-pill";
    } catch (error) {
        node.className = "user-pill failed";
        node.innerHTML = escapeHtml("Auth unavailable");
        node.title = error.message;
    }
}
