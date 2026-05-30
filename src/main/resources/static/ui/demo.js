import { api } from "./api.js";
import {
    $,
    escapeHtml,
    formatCompactCurrency,
    formatCurrency,
    formatMetric,
    formatNumber,
    formatPercent,
    setBusy,
    slug
} from "./dom.js";

let demo = null;
let selectedAccountId = null;

export async function initDemo() {
    $("resetButton").addEventListener("click", resetDemo);
    $("approvalQueue").addEventListener("click", async (event) => {
        const button = event.target.closest("button[data-action-id]");
        if (!button) return;
        await updateAction(button.dataset.actionId, button.dataset.transition);
    });

    await loadDemo();
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
            renderActions();
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
    const accountActions = demo.actions.filter((action) => action.accountId === selected?.id);
    const sharedActions = demo.actions.filter((action) => !action.accountId);
    const focused = selected ? [...accountActions, ...sharedActions] : demo.actions;
    const ordered = [...focused.filter((action) => action.status !== "Executed"), ...focused.filter((action) => action.status === "Executed")];
    const queue = ordered.slice(0, 3);
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
