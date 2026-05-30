import { api } from "./api.js";
import {
    $,
    escapeHtml,
    formatDateTime,
    formatNumber,
    sleep,
    setBusy,
    slug
} from "./dom.js";

let scenarios = [];
let currentDraft = null;
let currentRun = null;
let lastMonitorRun = null;
let recommendations = [];
let recommendationUnavailable = null;
let approvalHistory = [];
let approvalHistoryUnavailable = null;

export async function initPlanLab() {
    $("scenarioSelect").addEventListener("change", selectScenario);
    $("generatePlanButton").addEventListener("click", generatePlan);
    $("startPlanButton").addEventListener("click", startPlan);
    $("data360SmokeButton").addEventListener("click", smokeData360);
    $("planSpecPreview").addEventListener("click", async (event) => {
        const button = event.target.closest("button[data-approve-step]");
        if (!button || !currentRun) return;
        await approvePlanStep(button.dataset.approveStep);
    });
    $("monitorList").addEventListener("click", async (event) => {
        const button = event.target.closest("button[data-monitor-id]");
        if (!button) return;
        await runMonitor(button.dataset.monitorId);
    });
    $("recommendationList").addEventListener("click", async (event) => {
        const button = event.target.closest("button[data-recommendation-id]");
        if (!button) return;
        await updateRecommendation(button.dataset.recommendationId, button.dataset.transition);
    });

    await loadPlanLab();
}

async function loadPlanLab() {
    try {
        scenarios = await api("/api/scenarios");
        renderScenarioOptions();
    } catch (error) {
        $("plannerStatus").textContent = "Error";
        $("planSpecPreview").innerHTML = `<div class="issue">${escapeHtml(error.message)}</div>`;
    }
    await loadData360Diagnostics();
    await loadMonitors();
    await loadRecommendations();
}

async function loadData360Diagnostics() {
    try {
        const diagnostics = await api("/api/data360/diagnostics");
        renderData360Diagnostics(diagnostics);
    } catch (error) {
        $("data360Diagnostics").className = "diagnostics-line failed";
        $("data360Diagnostics").textContent = `Data 360 diagnostics unavailable: ${error.message}`;
    }
}

async function smokeData360() {
    setBusy("data360SmokeButton", true);
    try {
        const diagnostics = await api("/api/data360/diagnostics/smoke", "POST");
        renderData360Diagnostics(diagnostics);
    } finally {
        setBusy("data360SmokeButton", false);
    }
}

function renderData360Diagnostics(diagnostics) {
    const configured = diagnostics.configured ? "configured" : "not configured";
    $("data360Diagnostics").className = `diagnostics-line ${slug(diagnostics.status)}`;
    $("data360Diagnostics").textContent = `Data 360 ${diagnostics.mode}: ${diagnostics.status} (${configured})`;
}

function renderScenarioOptions() {
    $("scenarioSelect").innerHTML = scenarios.map((scenario) => `
        <option value="${escapeHtml(scenario.id)}">${escapeHtml(scenario.name)}</option>
    `).join("");
    selectScenario();
}

function selectScenario() {
    const scenario = selectedScenario();
    if (!scenario) return;
    $("goalInput").value = scenario.defaultUtterances?.[0] || scenario.name;
}

async function generatePlan() {
    const scenario = selectedScenario();
    if (!scenario) return;
    setBusy("generatePlanButton", true);
    $("plannerStatus").textContent = "Planning";
    try {
        currentDraft = await api("/api/plans", "POST", {
            scenarioId: scenario.id,
            goal: $("goalInput").value,
            context: {
                org: "demo-org",
                dataspace: "default",
                environment: "sandbox"
            }
        });
        currentRun = null;
        approvalHistory = [];
        approvalHistoryUnavailable = null;
        renderPlanDraft();
        $("plannerStatus").textContent = currentDraft.validation?.ok ? "Ready" : "Review";
        $("startPlanButton").disabled = !currentDraft.validation?.ok;
    } catch (error) {
        $("plannerStatus").textContent = "Error";
        $("planSpecPreview").innerHTML = `<div class="issue">${escapeHtml(error.message)}</div>`;
    } finally {
        setBusy("generatePlanButton", false);
    }
}

async function startPlan() {
    if (!currentDraft) return;
    setBusy("startPlanButton", true);
    $("plannerStatus").textContent = "Running";
    try {
        approvalHistory = [];
        approvalHistoryUnavailable = null;
        currentRun = await api(`/api/plans/${currentDraft.plan.id}/runs`, "POST");
        if (!currentRun.id) {
            currentDraft = currentRun;
            currentRun = null;
            renderPlanDraft();
            $("plannerStatus").textContent = "Review";
            return;
        }
        await pollRun(currentRun.id);
        await loadMonitors();
    } finally {
        setBusy("startPlanButton", false);
    }
}

async function approvePlanStep(stepId) {
    $("plannerStatus").textContent = "Approving";
    currentRun = await api(`/api/runs/${currentRun.id}/steps/${stepId}/approve`, "POST");
    await pollRun(currentRun.id);
    await loadMonitors();
}

async function pollRun(runId) {
    for (let i = 0; i < 20; i++) {
        await sleep(150);
        currentRun = await api(`/api/runs/${runId}`);
        renderPlanDraft();
        if (["WAITING_APPROVAL", "SUCCEEDED", "FAILED"].includes(currentRun.status)) {
            await loadApprovalHistory(runId);
            renderPlanDraft();
            $("plannerStatus").textContent = currentRun.status;
            return;
        }
    }
    await loadApprovalHistory(runId);
    renderPlanDraft();
    $("plannerStatus").textContent = currentRun.status;
}

function renderPlanDraft() {
    if (!currentDraft) {
        $("planSpecPreview").className = "plan-preview empty";
        $("planSpecPreview").textContent = "No plan generated.";
        return;
    }
    const plan = currentDraft.plan;
    const runSteps = currentRun ? Object.fromEntries(currentRun.steps.map((step) => [step.stepId, step])) : {};
    $("planSpecPreview").className = "plan-preview";
    $("planSpecPreview").innerHTML = `
        <div class="plan-head">
            <div>
                <strong>${escapeHtml(plan.goal)}</strong>
                <p>${renderPlanContext(plan)}</p>
            </div>
            <span>${escapeHtml(plan.scenarioId || "custom")}</span>
        </div>
        ${renderPlanReviewSummary(plan)}
        ${renderPlanIssues(currentDraft.validation?.issues)}
        ${renderRunTimeline(plan.steps, runSteps)}
        <div class="plan-steps">
            ${plan.steps.map((step) => renderPlanStep(step, runSteps[step.id])).join("")}
        </div>
        ${renderApprovalHistory()}
    `;
}

function renderPlanIssues(issues) {
    if (!issues?.length) {
        return `<div class="review-strip ok"><strong>Validation</strong><span>No blocking issues found.</span></div>`;
    }
    return `
        <div class="review-block">
            <div class="review-block-title">Validation issues</div>
            ${issues.map((issue) => `<div class="issue ${slug(issue.severity)}">${escapeHtml(issue.severity)} · ${escapeHtml(issue.stepId || "plan")} · ${escapeHtml(issue.message)}</div>`).join("")}
        </div>
    `;
}

function renderPlanStep(step, runStep) {
    const waiting = runStep?.status === "WAITING_APPROVAL";
    return `
        <article class="plan-step ${slug(step.phase)}">
            <div>
                <strong>${escapeHtml(step.title)}</strong>
                <p>${escapeHtml(step.action)} · ${escapeHtml(step.phase)}${runStep ? ` · ${escapeHtml(runStep.status)}` : ""}</p>
                ${renderStepMeta(step, runStep)}
            </div>
            <span>${step.needsApproval ? "Approval" : "Auto"}</span>
            ${waiting ? `<button class="secondary small" data-approve-step="${escapeHtml(step.id)}">Approve</button>` : ""}
        </article>
    `;
}

function renderPlanReviewSummary(plan) {
    const approvalCount = plan.steps.filter((step) => step.needsApproval).length;
    const bindingCount = plan.steps.reduce((total, step) => total + Object.keys(step.inputBindings || {}).length, 0);
    const monitorCount = plan.steps.filter((step) => slug(step.phase) === "monitor").length;
    return `
        <div class="review-kpis">
            <div><strong>${formatNumber(plan.steps.length)}</strong><span>steps</span></div>
            <div><strong>${formatNumber(approvalCount)}</strong><span>approval gates</span></div>
            <div><strong>${formatNumber(bindingCount)}</strong><span>bound inputs</span></div>
            <div><strong>${formatNumber(monitorCount)}</strong><span>monitors</span></div>
        </div>
    `;
}

function renderPlanContext(plan) {
    const context = plan.context || {};
    const parts = [
        context.org ? `org ${context.org}` : null,
        context.dataspace ? `dataspace ${context.dataspace}` : null,
        context.environment ? context.environment : null
    ].filter(Boolean);
    return escapeHtml(parts.length ? parts.join(" · ") : "No context supplied");
}

function renderRunTimeline(steps, runSteps) {
    const hasRun = currentRun && Object.keys(runSteps).length;
    if (!hasRun) {
        return `<div class="review-strip"><strong>Run timeline</strong><span>Start setup to populate execution statuses.</span></div>`;
    }
    return `
        <div class="run-timeline" aria-label="Run timeline">
            ${steps.map((step) => {
                const runStep = runSteps[step.id];
                const status = runStep?.status || "PENDING";
                return `
                    <div class="timeline-step ${slug(status)}" title="${escapeHtml(step.title)}: ${escapeHtml(status)}">
                        <span></span>
                        <small>${escapeHtml(status)}</small>
                    </div>
                `;
            }).join("")}
        </div>
    `;
}

function renderStepMeta(step, runStep) {
    const input = step.input || {};
    const bindings = step.inputBindings || {};
    const output = runStep?.output || {};
    const raw = runStep?.raw || {};
    const error = runStep?.error;
    return `
        <details class="step-details">
            <summary>Review details</summary>
            <div class="step-detail-grid">
                ${renderObjectPreview("Input", input)}
                ${renderObjectPreview("Bindings", bindings)}
                ${renderObjectPreview("Output", output)}
                ${renderObjectPreview("Raw", raw)}
            </div>
            ${error ? `<div class="issue error">${escapeHtml(error)}</div>` : ""}
            ${renderStepTiming(runStep)}
        </details>
    `;
}

function renderObjectPreview(label, value) {
    const empty = !value || (typeof value === "object" && !Object.keys(value).length);
    return `
        <div class="object-preview ${empty ? "empty-object" : ""}">
            <strong>${escapeHtml(label)}</strong>
            <pre>${escapeHtml(empty ? "No data" : JSON.stringify(value, null, 2))}</pre>
        </div>
    `;
}

function renderStepTiming(runStep) {
    if (!runStep?.startedAt && !runStep?.finishedAt) return "";
    const started = runStep.startedAt ? `Started ${formatDateTime(runStep.startedAt)}` : "Not started";
    const finished = runStep.finishedAt ? `Finished ${formatDateTime(runStep.finishedAt)}` : "In progress";
    return `<p class="step-timing">${escapeHtml(started)} · ${escapeHtml(finished)}</p>`;
}

function renderApprovalHistory() {
    const approved = currentRun?.approvedSteps || [];
    const history = approvalHistory.length ? approvalHistory : currentRun?.approvalHistory || currentRun?.approvals || [];
    if (history.length) {
        return `
            <div class="review-block">
                <div class="review-block-title">Approval history</div>
                ${history.map((item) => `
                    <div class="history-item">
                        <strong>${escapeHtml(item.stepId || item.step || "plan")}</strong>
                        <span>${escapeHtml(item.status || item.decision || "recorded")}</span>
                        <p>${escapeHtml(item.actor || item.approvedBy || "system")} ${item.createdAt || item.approvedAt ? ` · ${formatDateTime(item.createdAt || item.approvedAt)}` : ""}</p>
                    </div>
                `).join("")}
            </div>
        `;
    }
    if (approved.length) {
        return `
            <div class="review-block">
                <div class="review-block-title">Approval history</div>
                ${approved.map((stepId) => `<div class="history-item"><strong>${escapeHtml(stepId)}</strong><span>Approved</span><p>Approval recorded by run state.</p></div>`).join("")}
            </div>
        `;
    }
    if (approvalHistoryUnavailable) {
        return `<div class="review-strip"><strong>Approval history</strong><span>Approval API unavailable: ${escapeHtml(approvalHistoryUnavailable)}</span></div>`;
    }
    return `<div class="review-strip"><strong>Approval history</strong><span>No approval records available yet.</span></div>`;
}

async function loadApprovalHistory(runId) {
    approvalHistory = [];
    approvalHistoryUnavailable = null;
    try {
        approvalHistory = await api(`/api/runs/${runId}/approvals`);
    } catch (error) {
        approvalHistoryUnavailable = error.message;
    }
}

async function loadMonitors() {
    try {
        const monitors = await api("/api/monitors");
        renderMonitors(monitors);
    } catch (error) {
        $("monitorList").className = "monitor-list empty";
        $("monitorList").innerHTML = `<div class="placeholder-note">Monitor API unavailable: ${escapeHtml(error.message)}</div>`;
    }
}

function renderMonitors(monitors) {
    if (!monitors.length) {
        $("monitorList").className = lastMonitorRun ? "monitor-list" : "monitor-list empty";
        $("monitorList").innerHTML = lastMonitorRun
            ? `<div class="placeholder-note">No monitors registered.</div>${renderLastMonitorRun()}`
            : "No monitors registered.";
        return;
    }
    $("monitorList").className = "monitor-list";
    $("monitorList").innerHTML = monitors.map((monitor) => `
        <article class="monitor-item ${slug(monitor.status)}">
            <div>
                <strong>${escapeHtml(monitor.metric)}</strong>
                <p>${escapeHtml(monitor.cadence)} · ${escapeHtml(monitor.status)}${monitor.lastRunAt ? ` · ${formatDateTime(monitor.lastRunAt)}` : ""}</p>
            </div>
            <button class="ghost small" data-monitor-id="${escapeHtml(monitor.id)}">Run</button>
        </article>
    `).join("") + renderLastMonitorRun();
}

function renderLastMonitorRun() {
    if (!lastMonitorRun) return "";
    return `
        <div class="monitor-result">
            <div class="result-head">
                <strong>${escapeHtml(lastMonitorRun.status)}</strong>
                <span>${lastMonitorRun.thresholdBreached ? "Threshold breached" : "Within threshold"}</span>
            </div>
            <p>${escapeHtml(lastMonitorRun.recommendation)}</p>
            <p>${formatNumber(lastMonitorRun.observedValue)} observed${lastMonitorRun.createdAt ? ` · ${formatDateTime(lastMonitorRun.createdAt)}` : ""}</p>
        </div>
    `;
}

async function runMonitor(monitorId) {
    try {
        lastMonitorRun = await api(`/api/monitors/${monitorId}/run-now`, "POST");
        await loadMonitors();
        await loadRecommendations();
    } catch (error) {
        lastMonitorRun = { status: "ERROR", recommendation: error.message, observedValue: 0, thresholdBreached: true };
        renderMonitors([]);
    }
}

async function loadRecommendations() {
    recommendationUnavailable = null;
    try {
        recommendations = await api("/api/monitors/recommendations");
    } catch (error) {
        recommendations = [];
        recommendationUnavailable = error.message;
    }
    renderRecommendations();
}

async function updateRecommendation(recommendationId, transition) {
    try {
        await api(`/api/monitors/recommendations/${recommendationId}/${transition}`, "POST");
        await loadRecommendations();
    } catch (error) {
        recommendationUnavailable = error.message;
        renderRecommendations();
    }
}

function renderRecommendations() {
    if (!recommendations?.length) {
        $("recommendationList").className = "recommendation-list empty";
        $("recommendationList").innerHTML = `
            <div class="placeholder-note">
                ${recommendationUnavailable
                    ? `Recommendation API unavailable: ${escapeHtml(recommendationUnavailable)}`
                    : "No recommendations returned."}
            </div>
        `;
        return;
    }
    $("recommendationList").className = "recommendation-list";
    $("recommendationList").innerHTML = recommendations.map((item) => `
        <article class="recommendation-item ${slug(item.priority || item.status)}">
            <div>
                <strong>${escapeHtml(item.title || item.metric || "Recommendation")}</strong>
                <p>${escapeHtml(item.detail || item.recommendation || item.rationale || item.summary || "Review recommended action.")}</p>
                <p>${formatNumber(item.observedValue || 0)} observed${item.createdAt ? ` · ${formatDateTime(item.createdAt)}` : ""}</p>
            </div>
            <span>${escapeHtml(item.priority || item.status || "Review")}</span>
            ${slug(item.status) === "pending-approval" ? `
                <div class="recommendation-actions">
                    <button class="secondary small" data-recommendation-id="${escapeHtml(item.id)}" data-transition="approve">Approve</button>
                    <button class="ghost small" data-recommendation-id="${escapeHtml(item.id)}" data-transition="reject">Reject</button>
                </div>
            ` : ""}
        </article>
    `).join("");
}

function selectedScenario() {
    return scenarios.find((scenario) => scenario.id === $("scenarioSelect").value);
}
