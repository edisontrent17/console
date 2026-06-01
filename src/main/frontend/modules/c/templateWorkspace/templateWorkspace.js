import { LightningElement, api } from "lwc";

export default class TemplateWorkspace extends LightningElement {
    @api scenarios = [];
    @api templates = [];
    @api selectedScenarioId = "";
    @api currentDraft;
    @api currentRun;
    @api busy = {};

    get scenarioRows() {
        return (this.scenarios || []).map((scenario) => {
            const selected = scenario.id === this.selectedScenarioId;
            const utterance = scenario.defaultUtterances?.[0] || scenario.sourceSummary || "No default goal supplied.";
            const metricLabel = (scenario.goalMetrics || []).slice(0, 3).join(" • ");
            return {
                ...scenario,
                selected,
                key: scenario.id,
                label: scenario.name || scenario.id,
                detail: scenario.industry || "General",
                utterance,
                metricLabel,
                hasMetrics: Boolean(metricLabel),
                className: `scenario-row ${selected ? "selected" : ""}`,
                badgeClass: `slds-badge scenario-badge ${selected ? "slds-theme_success" : ""}`
            };
        });
    }

    get hasScenarios() {
        return this.scenarioRows.length > 0;
    }

    get templateRows() {
        return (this.templates || []).map((item) => {
            const clouds = (item.clouds || []).slice(0, 4).join(" • ");
            const notes = (item.architectureNotes || []).slice(0, 2);
            const steps = item.steps || [];
            return {
                ...item,
                key: item.id,
                title: item.title || item.name || item.id,
                subtitle: [item.industry, item.complexity].filter(Boolean).join(" • ") || "Solution template",
                summary: item.summary || item.outcome || "No summary supplied.",
                clouds,
                notes,
                stepCount: steps.length,
                stepLabel: `${steps.length} ${steps.length === 1 ? "step" : "steps"}`,
                disabled: this.templateActionDisabled,
                useLabel: this.busy.useTemplate ? "Using..." : "Use",
                instantiateLabel: this.busy.instantiateTemplate ? "Instantiating..." : "Instantiate"
            };
        });
    }

    get hasTemplates() {
        return this.templateRows.length > 0;
    }

    get templateActionDisabled() {
        return Boolean(this.busy.useTemplate || this.busy.instantiateTemplate);
    }

    get selectedScenario() {
        return (this.scenarios || []).find((scenario) => scenario.id === this.selectedScenarioId);
    }

    get selectedScenarioName() {
        return this.selectedScenario?.name || this.selectedScenarioId || "No scenario selected";
    }

    get draftStatusLabel() {
        if (!this.currentDraft?.plan) return "No draft";
        return this.currentDraft.validation?.ok ? "Draft validated" : "Draft needs review";
    }

    get runStatusLabel() {
        return this.currentRun?.status || "No run";
    }

    get planStepLabel() {
        const count = this.currentDraft?.plan?.steps?.length || 0;
        return `${count} ${count === 1 ? "step" : "steps"}`;
    }

    handleScenarioClick(event) {
        const scenarioId = event.currentTarget.dataset.scenarioId;
        const scenario = (this.scenarios || []).find((item) => item.id === scenarioId);
        this.dispatchEvent(new CustomEvent("scenariochange", { detail: { scenarioId, scenario } }));
    }

    handleScenarioKeydown(event) {
        if (!["Enter", " "].includes(event.key)) return;
        event.preventDefault();
        this.handleScenarioClick(event);
    }

    handleUseTemplate(event) {
        const templateId = event.currentTarget.dataset.templateId;
        const template = (this.templates || []).find((item) => item.id === templateId);
        this.dispatchEvent(new CustomEvent("usetemplate", { detail: { templateId, template } }));
    }

    handleInstantiateTemplate(event) {
        const templateId = event.currentTarget.dataset.templateId;
        const template = (this.templates || []).find((item) => item.id === templateId);
        this.dispatchEvent(new CustomEvent("instantiatetemplate", { detail: { templateId, template } }));
    }
}
