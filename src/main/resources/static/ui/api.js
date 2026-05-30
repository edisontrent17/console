export async function api(path, method = "GET", body = null) {
    const options = { method };
    if (body) {
        options.headers = { "Content-Type": "application/json" };
        options.body = JSON.stringify(body);
    }
    const response = await fetch(path, options);
    const text = await response.text();
    const json = text ? safeJson(text) : null;
    if (!response.ok) throw new Error(json?.error || json?.message || response.statusText);
    return json;
}

function safeJson(text) {
    try {
        return JSON.parse(text);
    } catch {
        return null;
    }
}
