import { LightningElement } from "lwc";
import { request } from "c/api";
import { compactCurrency, number, slug } from "c/format";

const TABS = [
    { id: "plan", label: "Plan Review" },
    { id: "execution", label: "Execution" },
    { id: "monitors", label: "Monitors" },
    { id: "audit", label: "Audit" }
];

export default class Data360Console extends LightningElement {
    activeTab = "plan";
    user = { username: "Demo mode", authenticated: false };
    authUnavailable = null;
    demo = null;
    selectedAccountId = null;
    scenarios = [];
    selectedScenarioId = "";
    goal = "Recover dormant high-value accounts";
    currentDraft = null;
    currentRun = null;
    plannerStatus = "Ready";
    selectedStepId = null;
    diagnostics = null;
    monitors = [];
    lastMonitorRun = null;
    recommendations = [];
    recommendationUnavailable = null;
    approvalHistory = [];
    approvalHistoryUnavailable = null;
    isDesktop = false;
    desktopSettings = null;
    desktopSettingsOpen = false;
    desktopProvider = "anthropic";
    desktopModel = "claude-sonnet-4-5";
    desktopApiKey = "";
    desktopMessage = "";
    exportMessage = "";
    busy = {};
    error = null;

    connectedCallback() {
        const hash = window.location.hash.replace("#", "");
        if (TABS.some((tab) => tab.id === hash)) {
            this.activeTab = hash;
        }
        window.addEventListener("hashchange", this.handleHashChange);
        this.loadDesktopSettings();
        this.loadAll();
    }

    disconnectedCallback() {
        window.removeEventListener("hashchange", this.handleHashChange);
    }

    handleHashChange = () => {
        const hash = window.location.hash.replace("#", "");
        if (TABS.some((tab) => tab.id === hash)) {
            this.activeTab = hash;
        }
    };

    get tabs() {
        return TABS.map((tab) => ({
            ...tab,
            itemClass: `slds-tabs_default__item ${this.activeTab === tab.id ? "slds-is-active" : ""}`,
            panelClass: this.activeTab === tab.id ? "slds-tabs_default__content slds-show" : "slds-tabs_default__content slds-hide",
            selected: this.activeTab === tab.id ? "true" : "false",
            href: `#${tab.id}`
        }));
    }

    get isPlan() {
        return this.activeTab === "plan";
    }

    get isExecution() {
        return this.activeTab === "execution";
    }

    get isMonitors() {
        return this.activeTab === "monitors";
    }

    get isAudit() {
        return this.activeTab === "audit";
    }

    get summary() {
        return this.demo?.summary || {};
    }

    get goalTitle() {
        return this.goal || this.summary.command;
    }

    get userLabel() {
        if (this.authUnavailable) return "Auth unavailable";
        return this.user.authenticated ? this.user.username : "Demo mode";
    }

    get userClass() {
        if (this.authUnavailable) return "slds-badge slds-theme_error";
        return this.user.authenticated ? "slds-badge slds-theme_success" : "slds-badge";
    }

    get diagnosticsLabel() {
        if (!this.diagnostics) return "Checking Data 360 client";
        const configured = this.diagnostics.configured ? "configured" : "not configured";
        return `Data 360 ${this.diagnostics.mode}: ${this.diagnostics.status} (${configured})`;
    }

    get diagnosticsClass() {
        return `slds-badge status-badge ${slug(this.diagnostics?.status || "checking")}`;
    }

    get metricCards() {
        const summary = this.summary;
        const waiting = this.demo?.actions?.filter((action) => action.status === "Waiting Approval").length || 0;
        const approvalGates = this.currentDraft?.plan?.steps?.filter((step) => step.needsApproval).length || 0;
        return [
            { label: "Recoverable revenue", value: compactCurrency(summary.recoverableRevenue || 0) },
            { label: "Eligible audience", value: number(summary.accountsIdentified || 0) },
            { label: "Actions waiting", value: number(summary.actionsWaitingApproval || waiting) },
            { label: "Plan approvals", value: number(approvalGates) }
        ];
    }

    get headerStatusClass() {
        return `slds-badge status-badge ${slug(this.summary.status || this.plannerStatus)}`;
    }

    get headerStatusLabel() {
        return this.summary.status || this.plannerStatus;
    }

    get canExportPlanSpec() {
        return Boolean(this.currentDraft?.plan?.id);
    }

    get canExportRunLog() {
        return Boolean(this.currentRun?.id);
    }

    get planExportDisabled() {
        return !this.canExportPlanSpec || this.busy.exportPlanSpec;
    }

    get runLogExportDisabled() {
        return !this.canExportRunLog || this.busy.exportRunLog;
    }

    get desktopProviderIsAnthropic() {
        return this.desktopProvider === "anthropic";
    }

    get desktopProviderIsOpenRouter() {
        return this.desktopProvider === "openrouter";
    }

    get desktopKeyStatus() {
        const provider = this.desktopSettings?.[this.desktopProvider] || {};
        if (!provider.apiKeyConfigured) {
            return "No API key stored";
        }
        return provider.apiKeyLast4 ? `Stored key ending ${provider.apiKeyLast4}` : "API key stored";
    }

    get desktopStorageStatus() {
        if (!this.desktopSettings) return "";
        return this.desktopSettings.keyStorageAvailable
            ? "Stored with OS-backed encryption"
            : "Secure key storage unavailable; keys will not be persisted";
    }

    async loadAll() {
        await Promise.all([
            this.loadUser(),
            this.loadDemo(),
            this.loadScenarios(),
            this.loadDiagnostics(),
            this.loadMonitors(),
            this.loadRecommendations()
        ]);
    }

    async loadUser() {
        try {
            this.user = await request("/api/me");
            this.authUnavailable = null;
        } catch (error) {
            this.authUnavailable = error.message;
        }
    }

    async loadDemo() {
        try {
            this.demo = await request("/api/demo/dormant-revenue-recovery");
            this.selectedAccountId ||= this.demo.accounts?.[0]?.id;
        } catch (error) {
            this.error = error.message;
        }
    }

    async loadScenarios() {
        try {
            this.scenarios = await request("/api/scenarios");
            const first = this.scenarios[0];
            this.selectedScenarioId = this.selectedScenarioId || first?.id || "";
            this.goal = first?.defaultUtterances?.[0] || first?.name || this.goal;
            this.resetCurrentDraft();
        } catch (error) {
            this.error = error.message;
            this.plannerStatus = "Error";
        }
    }

    async loadDiagnostics() {
        try {
            this.diagnostics = await request("/api/data360/diagnostics");
        } catch (error) {
            this.diagnostics = { status: "failed", mode: "connect", configured: false, error: error.message };
        }
    }

    async loadMonitors() {
        try {
            this.monitors = await request("/api/monitors");
        } catch (error) {
            this.monitors = [];
            this.lastMonitorRun = { status: "ERROR", recommendation: error.message, observedValue: 0, thresholdBreached: true };
        }
    }

    async loadRecommendations() {
        this.recommendationUnavailable = null;
        try {
            this.recommendations = await request("/api/monitors/recommendations");
        } catch (error) {
            this.recommendations = [];
            this.recommendationUnavailable = error.message;
        }
    }

    async loadDesktopSettings() {
        if (!window.data360Desktop?.getSettings) {
            return;
        }
        this.isDesktop = true;
        try {
            this.desktopSettings = await window.data360Desktop.getSettings();
            this.desktopProvider = this.desktopSettings.provider || "anthropic";
            this.desktopModel = this.desktopSettings[this.desktopProvider]?.model || this.desktopModel;
        } catch (error) {
            this.desktopMessage = error.message;
        }
    }

    handleTab(event) {
        event.preventDefault();
        const tab = event.currentTarget.dataset.tab;
        this.activeTab = tab;
        if (window.location.hash !== `#${tab}`) {
            history.pushState(null, "", `#${tab}`);
        }
    }

    async handleResetDemo() {
        this.setBusy("resetDemo", true);
        try {
            this.demo = await request("/api/demo/dormant-revenue-recovery/reset", { method: "POST" });
            this.selectedAccountId = this.demo.accounts?.[0]?.id;
        } finally {
            this.setBusy("resetDemo", false);
        }
    }

    handleSelectAccount(event) {
        this.selectedAccountId = event.detail.accountId;
    }

    async handleActionTransition(event) {
        const { actionId, transition } = event.detail;
        this.demo = await request(`/api/demo/dormant-revenue-recovery/actions/${actionId}/${transition}`, {
            method: "POST"
        });
    }

    handleScenarioChange(event) {
        const scenario = this.scenarios.find((item) => item.id === event.detail.scenarioId);
        this.selectedScenarioId = scenario?.id || "";
        this.goal = scenario?.defaultUtterances?.[0] || scenario?.name || "";
        this.resetCurrentDraft();
    }

    handleGoalChange(event) {
        this.goal = event.detail.goal;
        this.resetCurrentDraft();
    }

    resetCurrentDraft() {
        this.currentDraft = null;
        this.currentRun = null;
        this.selectedStepId = null;
        this.approvalHistory = [];
        this.approvalHistoryUnavailable = null;
        this.plannerStatus = "Ready";
    }

    async handleDraftPlan() {
        const scenario = this.scenarios.find((item) => item.id === this.selectedScenarioId);
        if (!scenario) return;
        this.setBusy("draftPlan", true);
        this.plannerStatus = "Planning";
        try {
            this.currentDraft = await request("/api/plans", {
                method: "POST",
                body: {
                    scenarioId: scenario.id,
                    goal: this.goal,
                    context: {
                        org: "demo-org",
                        dataspace: "default",
                        environment: "sandbox"
                    }
                }
            });
            this.currentRun = null;
            this.selectedStepId = this.currentDraft.plan.steps?.[0]?.id || null;
            this.approvalHistory = [];
            this.approvalHistoryUnavailable = null;
            this.plannerStatus = this.currentDraft.validation?.ok ? "Ready" : "Review";
        } catch (error) {
            this.error = error.message;
            this.plannerStatus = "Error";
        } finally {
            this.setBusy("draftPlan", false);
        }
    }

    async handleStartPlan() {
        if (!this.currentDraft) return;
        this.setBusy("startPlan", true);
        this.plannerStatus = "Running";
        try {
            this.approvalHistory = [];
            this.approvalHistoryUnavailable = null;
            const response = await request(`/api/plans/${this.currentDraft.plan.id}/runs`, { method: "POST" });
            if (!response.id) {
                this.currentDraft = response;
                this.currentRun = null;
                this.plannerStatus = "Review";
                return;
            }
            this.currentRun = response;
            await this.pollRun(response.id);
            await this.loadMonitors();
        } finally {
            this.setBusy("startPlan", false);
        }
    }

    async handleApproveStep(event) {
        if (!this.currentRun) return;
        this.plannerStatus = "Approving";
        this.currentRun = await request(`/api/runs/${this.currentRun.id}/steps/${event.detail.stepId}/approve`, {
            method: "POST"
        });
        await this.pollRun(this.currentRun.id);
        await this.loadMonitors();
    }

    async pollRun(runId) {
        for (let i = 0; i < 20; i += 1) {
            await delay(150);
            this.currentRun = await request(`/api/runs/${runId}`);
            if (["WAITING_APPROVAL", "SUCCEEDED", "FAILED"].includes(this.currentRun.status)) {
                await this.loadApprovalHistory(runId);
                this.plannerStatus = this.currentRun.status;
                return;
            }
        }
        await this.loadApprovalHistory(runId);
        this.plannerStatus = this.currentRun.status;
    }

    async loadApprovalHistory(runId) {
        this.approvalHistory = [];
        this.approvalHistoryUnavailable = null;
        try {
            this.approvalHistory = await request(`/api/runs/${runId}/approvals`);
        } catch (error) {
            this.approvalHistoryUnavailable = error.message;
        }
    }

    handleSelectStep(event) {
        this.selectedStepId = event.detail.stepId;
    }

    async handleSmokeData360() {
        this.setBusy("smokeData360", true);
        try {
            this.diagnostics = await request("/api/data360/diagnostics/smoke", { method: "POST" });
        } finally {
            this.setBusy("smokeData360", false);
        }
    }

    async handleRunMonitor(event) {
        try {
            this.lastMonitorRun = await request(`/api/monitors/${event.detail.monitorId}/run-now`, { method: "POST" });
            await this.loadMonitors();
            await this.loadRecommendations();
        } catch (error) {
            this.lastMonitorRun = { status: "ERROR", recommendation: error.message, observedValue: 0, thresholdBreached: true };
        }
    }

    async handleRecommendationTransition(event) {
        const { recommendationId, transition } = event.detail;
        try {
            await request(`/api/monitors/recommendations/${recommendationId}/${transition}`, { method: "POST" });
            await this.loadRecommendations();
        } catch (error) {
            this.recommendationUnavailable = error.message;
        }
    }

    handleOpenDesktopSettings() {
        this.desktopSettingsOpen = true;
        this.desktopApiKey = "";
        this.desktopMessage = "";
    }

    handleCloseDesktopSettings() {
        this.desktopSettingsOpen = false;
        this.desktopApiKey = "";
    }

    handleDesktopProviderChange(event) {
        this.desktopProvider = event.target.value;
        this.desktopModel = this.desktopSettings?.[this.desktopProvider]?.model || "";
        this.desktopApiKey = "";
    }

    handleDesktopModelChange(event) {
        this.desktopModel = event.target.value;
    }

    handleDesktopApiKeyChange(event) {
        this.desktopApiKey = event.target.value;
    }

    async handleSaveDesktopSettings() {
        if (!window.data360Desktop?.saveSettings) return;
        this.setBusy("desktopSettings", true);
        this.desktopMessage = "Saving settings and restarting local backend...";
        try {
            const providerSettings = { model: this.desktopModel };
            if (this.desktopApiKey.trim()) {
                providerSettings.apiKey = this.desktopApiKey.trim();
            }
            this.desktopSettings = await window.data360Desktop.saveSettings({
                provider: this.desktopProvider,
                [this.desktopProvider]: providerSettings
            });
            this.desktopApiKey = "";
            this.desktopSettingsOpen = false;
            this.desktopMessage = "Model settings saved.";
            await this.loadAll();
        } catch (error) {
            this.desktopMessage = error.message;
        } finally {
            this.setBusy("desktopSettings", false);
        }
    }

    async handleExportPlanSpec() {
        if (!this.currentDraft?.plan?.id) return;
        this.setBusy("exportPlanSpec", true);
        try {
            const archive = await request(`/api/plans/${this.currentDraft.plan.id}/export`);
            await exportJson(`planspec-${this.currentDraft.plan.id}.json`, archive);
            this.exportMessage = "PlanSpec exported.";
        } catch (error) {
            this.exportMessage = error.message;
        } finally {
            this.setBusy("exportPlanSpec", false);
        }
    }

    async handleExportRunLog() {
        if (!this.currentRun?.id) return;
        this.setBusy("exportRunLog", true);
        try {
            const archive = await request(`/api/runs/${this.currentRun.id}/export`);
            await exportJson(`execution-log-${this.currentRun.id}.json`, archive);
            this.exportMessage = "Execution log exported.";
        } catch (error) {
            this.exportMessage = error.message;
        } finally {
            this.setBusy("exportRunLog", false);
        }
    }

    setBusy(key, value) {
        this.busy = { ...this.busy, [key]: value };
    }
}

function delay(ms) {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
}

async function exportJson(defaultFileName, payload) {
    if (window.data360Desktop?.exportJson) {
        return window.data360Desktop.exportJson({ defaultFileName, payload });
    }
    const blob = new Blob([JSON.stringify(payload, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = defaultFileName;
    link.click();
    URL.revokeObjectURL(url);
    return { canceled: false };
}
