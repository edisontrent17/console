import { LightningElement } from "lwc";
import { request } from "c/api";
import { slug } from "c/format";

const TABS = [
    { id: "chat", label: "Chat" },
    { id: "templates", label: "Templates" },
    { id: "review", label: "Review" },
    { id: "monitors", label: "Monitors" },
    { id: "audit", label: "Audit" },
    { id: "admin", label: "Admin" }
];

const MODEL_OPTIONS = {
    anthropic: [
        { value: "claude-sonnet-4-6", label: "Claude Sonnet 4.6" },
        { value: "claude-opus-4-7", label: "Claude Opus 4.7" },
        { value: "claude-haiku-4-5", label: "Claude Haiku 4.5" }
    ],
    openrouter: [
        { value: "anthropic/claude-sonnet-4.6", label: "Anthropic Claude Sonnet 4.6" },
        { value: "anthropic/claude-opus-4.7", label: "Anthropic Claude Opus 4.7" },
        { value: "anthropic/claude-haiku-4.5", label: "Anthropic Claude Haiku 4.5" }
    ]
};

export default class Data360Console extends LightningElement {
    activeTab = "chat";
    authReady = false;
    setupRequired = false;
    user = { username: "anonymous", authenticated: false, authorities: [] };
    setupOrganizationName = "Acme Travel";
    setupDisplayName = "";
    setupEmail = "";
    setupPassword = "";
    loginEmail = "";
    loginPassword = "";

    scenarios = [];
    templates = [];
    selectedScenarioId = "";
    goal = "";
    messages = [];
    currentDraft = null;
    currentRun = null;
    plannerStatus = "Ready";
    selectedStepId = null;
    diagnostics = null;
    demo = null;
    selectedAccountId = null;
    monitors = [];
    lastMonitorRun = null;
    recommendations = [];
    recommendationUnavailable = null;
    approvalHistory = [];
    approvalHistoryUnavailable = null;

    llmSettings = null;
    settingsProvider = "anthropic";
    settingsModel = "claude-sonnet-4-6";
    settingsApiKey = "";
    mcpSettings = null;
    mcpServers = [];
    organizations = [];
    selectedAdminOrgId = "";
    organizationUsers = [];
    newOrganizationName = "";
    newUserName = "";
    newUserEmail = "";
    newUserPassword = "";
    newUserRole = "MEMBER";

    busy = {};
    error = null;
    exportMessage = "";

    connectedCallback() {
        const hash = window.location.hash.replace("#", "");
        if (TABS.some((tab) => tab.id === hash)) {
            this.activeTab = hash;
        }
        window.addEventListener("hashchange", this.handleHashChange);
        this.loadAuthStatus();
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

    get showAuthScreen() {
        return this.authReady && !this.user.authenticated;
    }

    get authSubtitle() {
        return this.setupRequired ? "Create the first organization and owner." : "Sign in to continue.";
    }

    get tabs() {
        return TABS.map((tab) => ({
            ...tab,
            href: `#${tab.id}`,
            className: `nav-item ${this.activeTab === tab.id ? "active" : ""}`
        }));
    }

    get isChat() {
        return this.activeTab === "chat";
    }

    get isTemplates() {
        return this.activeTab === "templates";
    }

    get isReview() {
        return this.activeTab === "review";
    }

    get isMonitors() {
        return this.activeTab === "monitors";
    }

    get isAudit() {
        return this.activeTab === "audit";
    }

    get isAdmin() {
        return this.activeTab === "admin";
    }

    get userLabel() {
        return this.user.authenticated ? this.user.username : "Not signed in";
    }

    get modelLabel() {
        if (!this.llmSettings) return "Model not configured";
        const configured = this.llmSettings.apiKeyConfigured ? "ready" : "missing token";
        return `${this.llmSettings.provider} / ${this.llmSettings.model} (${configured})`;
    }

    get settingsProviderIsAnthropic() {
        return this.settingsProvider === "anthropic";
    }

    get settingsProviderIsOpenRouter() {
        return this.settingsProvider === "openrouter";
    }

    get settingsModelOptions() {
        const options = MODEL_OPTIONS[this.settingsProvider] || MODEL_OPTIONS.anthropic;
        const selectedModel = this.settingsModel || options[0]?.value || "";
        const hasSelected = options.some((option) => option.value === selectedModel);
        const visibleOptions = hasSelected || !selectedModel
            ? options
            : [{ value: selectedModel, label: `Saved custom: ${selectedModel}` }, ...options];
        return visibleOptions.map((option) => ({
            ...option,
            selected: option.value === selectedModel
        }));
    }

    get settingsKeyStatus() {
        if (!this.llmSettings?.apiKeyConfigured) return "No token saved";
        return this.llmSettings.apiKeyLast4 ? `Saved token ending ${this.llmSettings.apiKeyLast4}` : "Token saved";
    }

    get organizationRows() {
        return (this.organizations || []).map((org) => ({
            ...org,
            className: `org-row ${org.id === this.selectedAdminOrgId ? "selected" : ""}`
        }));
    }

    get userRows() {
        return this.organizationUsers || [];
    }

    get newUserRoleIsMember() {
        return this.newUserRole === "MEMBER";
    }

    get newUserRoleIsAdmin() {
        return this.newUserRole === "ADMIN";
    }

    get newUserRoleIsViewer() {
        return this.newUserRole === "VIEWER";
    }

    async loadAuthStatus() {
        this.error = null;
        try {
            const status = await request("/api/auth/status");
            this.user = status.user || this.user;
            this.setupRequired = Boolean(status.setupRequired);
            this.authReady = true;
            if (this.user.authenticated) {
                await this.loadAll();
            }
        } catch (error) {
            this.authReady = true;
            this.error = error.message;
        }
    }

    async loadAll() {
        await Promise.all([
            this.loadDemo(),
            this.loadScenarios(),
            this.loadTemplates(),
            this.loadDiagnostics(),
            this.loadMonitors(),
            this.loadRecommendations(),
            this.loadLlmSettings(),
            this.loadMcpSettings(),
            this.loadOrganizations()
        ]);
    }

    async loadDemo() {
        try {
            this.demo = await request("/api/demo/dormant-revenue-recovery");
            this.selectedAccountId ||= this.demo.accounts?.[0]?.id;
        } catch {
            this.demo = null;
        }
    }

    async loadScenarios() {
        try {
            this.scenarios = await request("/api/scenarios");
            const first = this.scenarios[0];
            this.selectedScenarioId = this.selectedScenarioId || first?.id || "";
            this.goal = this.goal || first?.defaultUtterances?.[0] || "";
        } catch (error) {
            this.error = error.message;
        }
    }

    async loadTemplates() {
        try {
            this.templates = await request("/api/library");
        } catch {
            this.templates = [];
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
        } catch {
            this.monitors = [];
        }
    }

    async loadRecommendations() {
        try {
            this.recommendations = await request("/api/monitors/recommendations");
            this.recommendationUnavailable = null;
        } catch (error) {
            this.recommendations = [];
            this.recommendationUnavailable = error.message;
        }
    }

    async loadLlmSettings() {
        try {
            this.llmSettings = await request("/api/llm-settings");
            this.settingsProvider = this.llmSettings.provider || "anthropic";
            this.settingsModel = this.llmSettings.model || this.settingsModel;
        } catch (error) {
            this.error = error.message;
        }
    }

    async loadMcpSettings() {
        try {
            this.mcpSettings = await request("/api/mcp-settings");
            this.mcpServers = this.mcpSettings.servers || [];
        } catch (error) {
            this.error = error.message;
        }
    }

    async loadOrganizations() {
        try {
            this.organizations = await request("/api/organizations");
            this.selectedAdminOrgId = this.selectedAdminOrgId || this.user.organizationId || this.organizations[0]?.id || "";
            if (this.selectedAdminOrgId) {
                await this.loadUsers();
            }
        } catch {
            this.organizations = [];
            this.organizationUsers = [];
        }
    }

    async loadUsers() {
        if (!this.selectedAdminOrgId) return;
        this.organizationUsers = await request(`/api/organizations/${this.selectedAdminOrgId}/users`);
    }

    handleTab(event) {
        event.preventDefault();
        const tab = event.currentTarget.dataset.tab;
        this.activeTab = tab;
        if (window.location.hash !== `#${tab}`) {
            history.pushState(null, "", `#${tab}`);
        }
    }

    handleFieldChange(event) {
        const field = event.target.dataset.field;
        this[field] = event.target.value;
        if (field === "settingsProvider") {
            this.settingsModel = defaultModelForProvider(this.settingsProvider);
        }
    }

    handleLoginKeydown(event) {
        if (event.key !== "Enter") return;
        event.preventDefault();
        this.handleLogin();
    }

    async handleBootstrap() {
        this.setBusy("auth", true);
        this.error = null;
        try {
            const status = await request("/api/auth/bootstrap", {
                method: "POST",
                body: {
                    organizationName: this.setupOrganizationName,
                    displayName: this.setupDisplayName,
                    email: this.setupEmail,
                    password: this.setupPassword
                }
            });
            this.afterAuth(status);
        } catch (error) {
            this.error = error.message;
        } finally {
            this.setBusy("auth", false);
        }
    }

    async handleLogin() {
        this.setBusy("auth", true);
        this.error = null;
        try {
            const status = await request("/api/auth/login", {
                method: "POST",
                body: { email: this.loginEmail, password: this.loginPassword }
            });
            this.afterAuth(status);
        } catch (error) {
            this.error = error.message;
        } finally {
            this.setBusy("auth", false);
        }
    }

    async afterAuth(status) {
        this.user = status.user;
        this.setupRequired = false;
        this.loginPassword = "";
        this.setupPassword = "";
        this.messages = [
            { role: "assistant", text: "What should Data 360 set up?", meta: "I will draft a governed PlanSpec for approval before execution." }
        ];
        await this.loadAll();
    }

    async handleLogout() {
        await request("/api/auth/logout", { method: "POST" });
        this.user = { username: "anonymous", authenticated: false, authorities: [] };
        this.currentDraft = null;
        this.currentRun = null;
        this.messages = [];
        await this.loadAuthStatus();
    }

    async handleChatMessage(event) {
        const text = event.detail.text;
        this.messages = [...this.messages, { role: "user", text }];
        this.goal = text;
        await this.handleDraftPlan();
        if (this.currentDraft?.plan) {
            const count = this.currentDraft.plan.steps?.length || 0;
            this.messages = [...this.messages, {
                role: "assistant",
                text: `I drafted a PlanSpec with ${count} ${count === 1 ? "step" : "steps"}. Review it before running.`,
                meta: this.currentDraft.validation?.ok ? "Validation passed." : "Validation needs attention."
            }];
        }
    }

    handleScenarioChange(event) {
        const scenario = this.scenarios.find((item) => item.id === event.detail.scenarioId);
        this.selectedScenarioId = scenario?.id || "";
        this.goal = scenario?.defaultUtterances?.[0] || scenario?.name || this.goal;
        this.resetCurrentDraft();
    }

    handleGoalChange(event) {
        this.goal = event.detail.goal;
        this.resetCurrentDraft();
    }

    handleUseTemplate(event) {
        const template = event.detail.template;
        this.goal = template ? `${template.title}: ${template.outcome || template.summary || ""}` : this.goal;
        this.activeTab = "chat";
        this.messages = [...this.messages, { role: "assistant", text: this.goal, meta: "Template loaded as the next chat goal." }];
    }

    async handleInstantiateTemplate(event) {
        const templateId = event.detail.templateId;
        if (!templateId) return;
        this.setBusy("instantiateTemplate", true);
        try {
            this.currentDraft = await request(`/api/library/${templateId}/plans`, {
                method: "POST",
                body: {
                    context: {
                        org: this.user.organizationName || "default-org",
                        dataspace: "default",
                        environment: "sandbox"
                    }
                }
            });
            this.selectedStepId = this.currentDraft.plan.steps?.[0]?.id || null;
            this.activeTab = "review";
        } catch (error) {
            this.error = error.message;
        } finally {
            this.setBusy("instantiateTemplate", false);
        }
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
        const scenario = this.scenarios.find((item) => item.id === this.selectedScenarioId) || this.scenarios[0];
        this.setBusy("draftPlan", true);
        this.plannerStatus = "Planning";
        this.error = null;
        try {
            this.currentDraft = await request("/api/plans", {
                method: "POST",
                body: {
                    scenarioId: scenario?.id,
                    goal: this.goal,
                    context: {
                        org: this.user.organizationName || "default-org",
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
            return this.currentDraft;
        } catch (error) {
            this.error = error.message;
            this.plannerStatus = "Error";
            this.messages = [...this.messages, { role: "assistant", text: "I could not draft the PlanSpec.", meta: error.message }];
            return null;
        } finally {
            this.setBusy("draftPlan", false);
        }
    }

    async handleStartPlan() {
        if (!this.currentDraft) return;
        this.setBusy("startPlan", true);
        this.plannerStatus = "Running";
        try {
            const response = await request(`/api/plans/${this.currentDraft.plan.id}/runs`, { method: "POST" });
            if (!response.id) {
                this.currentDraft = response;
                this.currentRun = null;
                this.plannerStatus = "Review";
                return;
            }
            this.currentRun = response;
            this.messages = [...this.messages, { role: "assistant", text: `Run ${response.id} started.`, meta: response.status }];
            await this.pollRun(response.id);
            await this.loadMonitors();
        } catch (error) {
            this.error = error.message;
            this.plannerStatus = "Error";
        } finally {
            this.setBusy("startPlan", false);
        }
    }

    async handleApproveStep(event) {
        if (!this.currentRun) return;
        this.plannerStatus = "Approving";
        this.currentRun = await request(`/api/runs/${this.currentRun.id}/steps/${event.detail.stepId}/approve`, { method: "POST" });
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
        try {
            this.approvalHistory = await request(`/api/runs/${runId}/approvals`);
            this.approvalHistoryUnavailable = null;
        } catch (error) {
            this.approvalHistory = [];
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

    async handleSaveLlmSettings() {
        this.setBusy("saveSettings", true);
        this.error = null;
        try {
            const body = {
                provider: this.settingsProvider,
                model: this.settingsModel,
                apiKey: this.settingsApiKey
            };
            this.llmSettings = await request("/api/llm-settings", { method: "PUT", body });
            this.settingsApiKey = "";
        } catch (error) {
            this.error = error.message;
        } finally {
            this.setBusy("saveSettings", false);
        }
    }

    handleMcpEnabledChange(event) {
        const serverId = event.target.dataset.serverId;
        this.mcpServers = this.mcpServers.map((server) => server.id === serverId
            ? { ...server, enabled: event.target.checked }
            : server);
    }

    handleMcpCommandChange(event) {
        const serverId = event.target.dataset.serverId;
        this.mcpServers = this.mcpServers.map((server) => server.id === serverId
            ? { ...server, command: event.target.value, commandConfigured: Boolean(event.target.value?.trim()) }
            : server);
    }

    async handleSaveMcpSettings() {
        this.setBusy("saveMcpSettings", true);
        this.error = null;
        try {
            const body = {
                servers: this.mcpServers.map((server) => ({
                    id: server.id,
                    enabled: server.enabled,
                    command: server.command
                }))
            };
            this.mcpSettings = await request("/api/mcp-settings", { method: "PUT", body });
            this.mcpServers = this.mcpSettings.servers || [];
        } catch (error) {
            this.error = error.message;
        } finally {
            this.setBusy("saveMcpSettings", false);
        }
    }

    async handleCreateOrganization() {
        if (!this.newOrganizationName.trim()) return;
        this.setBusy("createOrganization", true);
        try {
            const org = await request("/api/organizations", { method: "POST", body: { name: this.newOrganizationName } });
            this.newOrganizationName = "";
            this.selectedAdminOrgId = org.id;
            await this.loadOrganizations();
        } catch (error) {
            this.error = error.message;
        } finally {
            this.setBusy("createOrganization", false);
        }
    }

    handleSelectOrganization(event) {
        this.selectedAdminOrgId = event.currentTarget.dataset.organizationId;
        this.loadUsers();
    }

    async handleCreateUser() {
        if (!this.selectedAdminOrgId) return;
        this.setBusy("createUser", true);
        try {
            await request(`/api/organizations/${this.selectedAdminOrgId}/users`, {
                method: "POST",
                body: {
                    displayName: this.newUserName,
                    email: this.newUserEmail,
                    password: this.newUserPassword,
                    role: this.newUserRole
                }
            });
            this.newUserName = "";
            this.newUserEmail = "";
            this.newUserPassword = "";
            await this.loadUsers();
        } catch (error) {
            this.error = error.message;
        } finally {
            this.setBusy("createUser", false);
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
            this.error = error.message;
        } finally {
            this.setBusy("exportPlanSpec", false);
        }
    }

    setBusy(key, value) {
        this.busy = { ...this.busy, [key]: value };
    }
}

function delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
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

function defaultModelForProvider(provider) {
    return (MODEL_OPTIONS[provider] || MODEL_OPTIONS.anthropic)[0]?.value || "";
}
