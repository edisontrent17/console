export const $ = (id) => document.getElementById(id);

export function setBusy(id, busy) {
    const button = $(id);
    button.disabled = busy;
    button.dataset.originalText ||= button.textContent;
    button.textContent = busy ? "Working..." : button.dataset.originalText;
}

export function formatCurrency(value) {
    return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD", maximumFractionDigits: 0 }).format(value);
}

export function formatCompactCurrency(value) {
    if (value >= 1000000) return `$${trimZeros((value / 1000000).toFixed(2))}M`;
    if (value >= 1000) return `$${trimZeros((value / 1000).toFixed(1))}K`;
    return formatCurrency(value);
}

export function formatDateTime(value) {
    return new Intl.DateTimeFormat("en-US", { hour: "numeric", minute: "2-digit" }).format(new Date(value));
}

export function formatMetric(value, unit) {
    if (unit === "currency") return formatCurrency(value);
    if (unit === "percent") return `${value}%`;
    return formatNumber(value);
}

export function formatNumber(value) {
    return new Intl.NumberFormat("en-US", { maximumFractionDigits: 0 }).format(value);
}

export function formatPercent(value) {
    return `${Math.round(value * 100)}%`;
}

export function sleep(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

export function slug(value) {
    return String(value || "").toLowerCase().replaceAll(" ", "-").replaceAll("_", "-");
}

export function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function trimZeros(value) {
    return value.replace(/\.0+$/, "").replace(/(\.\d*[1-9])0+$/, "$1");
}
