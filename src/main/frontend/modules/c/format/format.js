export function compactCurrency(value) {
    if (value >= 1000000) return `$${trimZeros((value / 1000000).toFixed(2))}M`;
    if (value >= 1000) return `$${trimZeros((value / 1000).toFixed(1))}K`;
    return currency(value);
}

export function currency(value) {
    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        maximumFractionDigits: 0
    }).format(value || 0);
}

export function dateTime(value) {
    if (!value) return "";
    return new Intl.DateTimeFormat("en-US", {
        hour: "numeric",
        minute: "2-digit"
    }).format(new Date(value));
}

export function metric(value, unit) {
    if (unit === "currency") return currency(value);
    if (unit === "percent") return `${value || 0}%`;
    return number(value);
}

export function number(value) {
    return new Intl.NumberFormat("en-US", { maximumFractionDigits: 0 }).format(value || 0);
}

export function percent(value) {
    return `${Math.round((value || 0) * 100)}%`;
}

export function slug(value) {
    return String(value || "").toLowerCase().replaceAll(" ", "-").replaceAll("_", "-");
}

export function toJson(value) {
    const empty = !value || (typeof value === "object" && !Object.keys(value).length);
    return empty ? "No data" : JSON.stringify(value, null, 2);
}

function trimZeros(value) {
    return value.replace(/\.0+$/, "").replace(/(\.\d*[1-9])0+$/, "$1");
}
