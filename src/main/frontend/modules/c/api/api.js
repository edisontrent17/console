export async function request(path, options = {}) {
    const init = { method: options.method || "GET" };
    if (options.body) {
        init.headers = { "Content-Type": "application/json" };
        init.body = JSON.stringify(options.body);
    }

    const response = await fetch(path, init);
    const text = await response.text();
    const json = text ? parseJson(text) : null;
    if (!response.ok) {
        throw new Error(json?.error || json?.message || response.statusText);
    }
    return json;
}

function parseJson(text) {
    try {
        return JSON.parse(text);
    } catch {
        return null;
    }
}
