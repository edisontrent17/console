export async function request(path, options = {}) {
    const init = { method: options.method || "GET", headers: await apiHeaders(options.body) };
    if (options.body) {
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

async function apiHeaders(hasBody) {
    const headers = {};
    if (hasBody) {
        headers["Content-Type"] = "application/json";
    }
    if (window.data360Desktop?.apiHeaders) {
        Object.assign(headers, await window.data360Desktop.apiHeaders());
    }
    return headers;
}

function parseJson(text) {
    try {
        return JSON.parse(text);
    } catch {
        return null;
    }
}
