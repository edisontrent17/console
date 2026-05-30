let demo = null;
let selectedAccountId = null;
let scenarios = [];
let currentDraft = null;
let currentRun = null;
let lastMonitorRun = null;
let recommendations = [];
let recommendationUnavailable = null;
let approvalHistory = [];
let approvalHistoryUnavailable = null;

const $ = (id) => document.getElementById(id);

$("resetButton").addEventListener("click", resetDemo);
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
$("approvalQueue").addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action-id]");
    if (!button) return;
    await updateAction(button.dataset.actionId, button.dataset.transition);
});

loadPlanLab();
loadDemo();

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

async function loadDemo() {
    try {
        demo = await api("/api/demo/dormant-revenue-recovery");
        selectedAccountId ||= demo.accounts[0]?.id;
        renderDemo();
    } catch (error) {
        $("runStatus").textContent = "Error";
        $("accountList").innerHTML = `<div class="issue">${escapeHtml(error.message)}</div>`;
    }
}

async function resetDemo() {
    setBusy("resetButton", true);
    try {
        demo = await api("/api/demo/dormant-revenue-recovery/reset", "POST");
        selectedAccountId = demo.accounts[0]?.id;
        renderDemo();
    } finally {
        setBusy("resetButton", false);
    }
}

async function updateAction(actionId, transition) {
    demo = await api(`/api/demo/dormant-revenue-recovery/actions/${actionId}/${transition}`, "POST");
    renderDemo();
}

function selectedScenario() {
    return scenarios.find((scenario) => scenario.id === $("scenarioSelect").value);
}

function renderDemo() {
    renderSummary();
    renderAccounts();
    renderEvidence();
    renderActions();
    renderActivation();
    renderFunnel();
    renderImpact();
    renderContext();
}

function renderSummary() {
    const summary = demo.summary;
    $("runStatus").textContent = summary.status;
    $("goalCommand").textContent = summary.command;
    $("recoverableRevenue").textContent = formatCompactCurrency(summary.recoverableRevenue);
    $("accountsIdentified").textContent = formatNumber(summary.accountsIdentified);
    $("actionsWaiting").textContent = summary.actionsWaitingApproval;
}

function renderAccounts() {
    const accounts = demo.accounts;
    $("accountList").classList.remove("loading");
    $("accountList").innerHTML = accounts.map((account) => `
        <button class="account-row ${account.id === selectedAccountId ? "selected" : ""}" data-account-id="${escapeHtml(account.id)}">
            <span>
                <strong>${escapeHtml(account.name)}</strong>
                <small>${formatCurrency(account.contractValue)} · ${account.activationGapDays}d gap</small>
            </span>
            <em class="${slug(account.priority)}">${escapeHtml(account.priority)}</em>
        </button>
    `).join("");

    $("accountList").querySelectorAll(".account-row").forEach((row) => {
        row.addEventListener("click", () => {
            selectedAccountId = row.dataset.accountId;
            renderAccounts();
            renderEvidence();
        });
    });
}

function renderEvidence() {
    const account = selectedAccount();
    if (!account) return;

    $("selectedPriority").textContent = account.priority;
    $("selectedPriority").className = `mini-badge ${slug(account.priority)}`;
    $("selectedAccount").classList.remove("empty");
    $("selectedAccount").innerHTML = `
        <h3>${escapeHtml(account.name)}</h3>
        <p>${escapeHtml(account.recommendedMotion)}</p>
        <div class="score-row">
            <span><strong>${formatCurrency(account.contractValue)}</strong> contract</span>
            <span><strong>${formatPercent(account.recoveryPriorityScore)}</strong> priority</span>
            <span><strong>${formatPercent(account.supportBlockerScore)}</strong> support risk</span>
        </div>
    `;

    $("evidenceList").innerHTML = account.evidence.map((item) => `
        <article class="evidence-item">
            <div>
                <strong>${escapeHtml(item.signal)}</strong>
                <p>${escapeHtml(item.detail)}</p>
            </div>
            <span>${escapeHtml(item.source)}</span>
        </article>
    `).join("");
}

function renderActions() {
    const selected = selectedAccount();
    const focused = demo.actions.filter((action) => !action.accountId || action.accountId === selected?.id);
    const ordered = [...focused.filter((action) => action.status !== "Executed"), ...focused.filter((action) => action.status === "Executed")];
    const queue = ordered.slice(0, 2);
    const hidden = Math.max(0, ordered.length - queue.length);
    const waiting = demo.actions.filter((action) => action.status === "Waiting Approval").length;

    $("approvalCount").textContent = waiting;
    $("approvalQueue").classList.remove("loading");
    $("approvalQueue").innerHTML = queue.map((action) => `
        <article class="action-item ${slug(action.status)}">
            <div class="action-head">
                <div>
                    <strong>${escapeHtml(action.title)}</strong>
                    <p>${escapeHtml(action.targetSystem)} · ${escapeHtml(action.type)}</p>
                </div>
                <span>${escapeHtml(action.status)}</span>
            </div>
            <p>${escapeHtml(action.rationale)}</p>
            <details>
                <summary>Draft</summary>
                <p>${escapeHtml(action.draftContent)}</p>
            </details>
            <div class="action-buttons">${renderActionButtons(action)}</div>
        </article>
    `).join("") + (hidden ? `<div class="more-actions">${hidden} more actions queued for this account</div>` : "");
}

function renderActionButtons(action) {
    if (action.status === "Executed") return `<span class="done-text">Executed</span>`;
    if (action.status === "Approved") {
        return `
            <button class="secondary small" data-action-id="${escapeHtml(action.id)}" data-transition="execute">Execute</button>
            <button class="ghost small" data-action-id="${escapeHtml(action.id)}" data-transition="reject">Reject</button>
        `;
    }
    if (action.status === "Rejected") {
        return `<button class="ghost small" data-action-id="${escapeHtml(action.id)}" data-transition="approve">Approve</button>`;
    }
    return `
        <button class="secondary small" data-action-id="${escapeHtml(action.id)}" data-transition="approve">Approve</button>
        <button class="ghost small" data-action-id="${escapeHtml(action.id)}" data-transition="reject">Reject</button>
    `;
}

function renderActivation() {
    const activation = demo.emailActivation;
    $("emailMode").textContent = activation.mode;
    $("emailActivation").classList.remove("loading");
    $("emailActivation").innerHTML = `
        <div class="activation-summary">
            <div>
                <strong>${escapeHtml(activation.provider)}</strong>
                <p>${escapeHtml(activation.safetyRule)}</p>
                <p>${escapeHtml(activation.audience)}</p>
            </div>
            <span>${formatNumber(activation.sentEmails)} sent</span>
        </div>
        <ol class="email-steps">
            ${activation.sequence.map((step) => `
                <li>
                    <strong>${escapeHtml(step.timing)} · ${escapeHtml(step.title)}</strong>
                    <p>${escapeHtml(step.content)}</p>
                </li>
            `).join("")}
        </ol>
        <details>
            <summary>Tracking</summary>
            <p>${escapeHtml(activation.trackingEvents.join(", "))}</p>
            <p>${escapeHtml(activation.outcomeJoin)}</p>
        </details>
    `;
}

function renderFunnel() {
    const visible = ["Identified", "Holdout", "Email approved", "Sent", "Delivered", "Clicked", "Activated", "Revenue recovered"];
    $("recoveryFunnel").classList.remove("loading");
    $("recoveryFunnel").innerHTML = demo.recoveryFunnel
        .filter((stage) => visible.includes(stage.label))
        .map((stage) => metricCard(stage))
        .join("");
}

function renderImpact() {
    const visible = ["Recoverable revenue", "Eligible audience", "Recovery rate", "Revenue recovered"];
    $("impactMetrics").classList.remove("loading");
    $("impactMetrics").innerHTML = demo.impact
        .filter((metric) => visible.includes(metric.label))
        .map((metric) => metricCard(metric))
        .join("");
}

function metricCard(metric) {
    const pct = metric.target ? Math.min(100, Math.round((metric.current / metric.target) * 100)) : 0;
    return `
        <article class="metric-item">
            <div class="metric-head">
                <strong>${escapeHtml(metric.label)}</strong>
                <span>${formatMetric(metric.current, metric.unit)}</span>
            </div>
            <div class="progress"><span style="width:${pct}%"></span></div>
            <p>${escapeHtml(metric.narrative)}</p>
        </article>
    `;
}

function renderContext() {
    $("signalSources").classList.remove("loading");
    $("signalSources").innerHTML = demo.signalSources.map((source) => `
        <div><strong>${escapeHtml(source.name)}</strong><p>${escapeHtml(source.detail)}</p></div>
    `).join("");

    $("planStages").classList.remove("loading");
    $("planStages").innerHTML = demo.plan.map((stage) => `
        <div><strong>${escapeHtml(stage.title)}</strong><p>${escapeHtml(stage.detail)}</p></div>
    `).join("");
}

function selectedAccount() {
    return demo.accounts.find((account) => account.id === selectedAccountId);
}

async function api(path, method = "GET", body = null) {
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

function setBusy(id, busy) {
    const button = $(id);
    button.disabled = busy;
    button.dataset.originalText ||= button.textContent;
    button.textContent = busy ? "Working..." : button.dataset.originalText;
}

function formatCurrency(value) {
    return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD", maximumFractionDigits: 0 }).format(value);
}

function formatCompactCurrency(value) {
    if (value >= 1000000) return `$${trimZeros((value / 1000000).toFixed(2))}M`;
    if (value >= 1000) return `$${trimZeros((value / 1000).toFixed(1))}K`;
    return formatCurrency(value);
}

function trimZeros(value) {
    return value.replace(/\.0+$/, "").replace(/(\.\d*[1-9])0+$/, "$1");
}

function formatDateTime(value) {
    return new Intl.DateTimeFormat("en-US", { hour: "numeric", minute: "2-digit" }).format(new Date(value));
}

function sleep(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

function formatPercent(value) {
    return `${Math.round(value * 100)}%`;
}

function formatMetric(value, unit) {
    if (unit === "currency") return formatCurrency(value);
    if (unit === "percent") return `${value}%`;
    return formatNumber(value);
}

function formatNumber(value) {
    return new Intl.NumberFormat("en-US", { maximumFractionDigits: 0 }).format(value);
}

function slug(value) {
    return String(value || "").toLowerCase().replaceAll(" ", "-").replaceAll("_", "-");
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
