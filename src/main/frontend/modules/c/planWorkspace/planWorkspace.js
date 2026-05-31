import { LightningElement, api } from "lwc";
import { dateTime, number, slug, toJson } from "c/format";

export default class PlanWorkspace extends LightningElement {
    @api scenarios = [];
    @api selectedScenarioId = "";
    @api goal = "";
    @api diagnostics;
    @api plannerStatus = "Ready";
    @api currentDraft;
    @api currentRun;
    @api approvalHistory = [];
    @api approvalHistoryUnavailable;
    @api selectedStepId;
    @api busy = {};

    get hasDraft() {
        return Boolean(this.currentDraft?.plan);
    }

    get scenarioOptions() {
        return (this.scenarios || []).map((scenario) => ({
            ...scenario,
            selected: scenario.id === this.selectedScenarioId
        }));
    }

    get startDisabled() {
        return !this.currentDraft?.validation?.ok || this.busy.startPlan;
    }

    get draftLabel() {
        return this.busy.draftPlan ? "Drafting..." : "Draft plan";
    }

    get startLabel() {
        return this.busy.startPlan ? "Starting..." : "Start run";
    }

    get smokeLabel() {
        return this.busy.smokeData360 ? "Testing..." : "Test connection";
    }

    get plan() {
        return this.currentDraft?.plan;
    }

    get planContext() {
        const context = this.plan?.context || {};
        return [
            context.org ? `org ${context.org}` : null,
            context.dataspace ? `dataspace ${context.dataspace}` : null,
            context.environment
        ].filter(Boolean).join(" • ") || "No context supplied";
    }

    get validationClass() {
        return this.currentDraft?.validation?.ok ? "slds-badge slds-theme_success" : "slds-badge slds-theme_warning";
    }

    get validationLabel() {
        return this.currentDraft?.validation?.ok ? "Validated" : "Needs review";
    }

    get reviewStats() {
        const steps = this.plan?.steps || [];
        const states = Object.keys(this.plan?.definition?.States || {});
        return [
            { label: "ASL states", value: number(states.length || steps.length) },
            { label: "Approval gates", value: number(steps.filter((step) => step.needsApproval).length) },
            { label: "StartAt", value: this.plan?.definition?.StartAt || "n/a" },
            { label: "Monitors", value: number(steps.filter((step) => slug(step.phase) === "monitor").length) }
        ];
    }

    get issues() {
        return (this.currentDraft?.validation?.issues || []).map((issue) => ({
            ...issue,
            key: `${issue.stepId || "plan"}-${issue.message}`,
            className: `slds-box slds-theme_alert-texture issue ${slug(issue.severity)}`
        }));
    }

    get hasIssues() {
        return this.issues.length > 0;
    }

    get runStepMap() {
        return new Map((this.currentRun?.steps || []).map((step) => [step.stepId, step]));
    }

    get planRows() {
        return (this.plan?.steps || []).map((step, index) => {
            const runStep = this.runStepMap.get(step.id);
            const aslState = this.plan?.definition?.States?.[step.id] || {};
            const status = runStep?.status || "PENDING";
            return {
                ...step,
                resource: aslState.Resource || step.action,
                resultPath: aslState.ResultPath || `$.${step.id}`,
                index: index + 1,
                status,
                key: step.id,
                rowClass: `plan-row ${this.selectedStepId === step.id ? "selected" : ""}`,
                statusClass: `slds-badge status ${slug(status)}`,
                approvalClass: `slds-badge ${step.needsApproval ? "slds-theme_warning" : ""}`,
                approvalLabel: step.needsApproval ? "Approval" : "Auto",
                canApprove: status === "WAITING_APPROVAL"
            };
        });
    }

    get hasRun() {
        return Boolean(this.currentRun?.steps?.length);
    }

    get timelineSteps() {
        return this.planRows.map((row) => ({
            ...row,
            className: `timeline-dot ${slug(row.status)}`
        }));
    }

    get selectedStep() {
        const step = (this.plan?.steps || []).find((item) => item.id === this.selectedStepId) || this.plan?.steps?.[0];
        if (!step) return step;
        const aslState = this.plan?.definition?.States?.[step.id] || {};
        return {
            ...step,
            resource: aslState.Resource || step.action,
            resultPath: aslState.ResultPath || `$.${step.id}`
        };
    }

    get selectedRunStep() {
        return this.selectedStep ? this.runStepMap.get(this.selectedStep.id) : null;
    }

    get hasSelectedStep() {
        return Boolean(this.selectedStep);
    }

    get selectedStatus() {
        return this.selectedRunStep?.status || "PENDING";
    }

    get selectedTiming() {
        if (!this.selectedRunStep?.startedAt && !this.selectedRunStep?.finishedAt) return "Not started";
        const started = this.selectedRunStep.startedAt ? `Started ${dateTime(this.selectedRunStep.startedAt)}` : "Not started";
        const finished = this.selectedRunStep.finishedAt ? `Finished ${dateTime(this.selectedRunStep.finishedAt)}` : "In progress";
        return `${started} • ${finished}`;
    }

    get selectedApproval() {
        return this.selectedStep?.needsApproval ? "Required" : "Automatic";
    }

    get selectedPreviews() {
        const aslState = this.plan?.definition?.States?.[this.selectedStep?.id] || {};
        return [
            { label: "ASL State", value: toJson(aslState) },
            { label: "Input", value: toJson(this.selectedStep?.input || {}) },
            { label: "Bindings", value: toJson(this.selectedStep?.inputBindings || {}) },
            { label: "Output", value: toJson(this.selectedRunStep?.output || {}) },
            { label: "Raw", value: toJson(this.selectedRunStep?.raw || {}) }
        ];
    }

    get approvalRows() {
        const history = this.approvalHistory?.length
            ? this.approvalHistory
            : this.currentRun?.approvalHistory || this.currentRun?.approvals || [];
        return history.map((item, index) => ({
            key: `${item.stepId || item.step || "plan"}-${index}`,
            step: item.stepId || item.step || "plan",
            status: item.status || item.decision || "recorded",
            detail: `${item.actor || item.approvedBy || "system"}${item.createdAt || item.approvedAt ? ` • ${dateTime(item.createdAt || item.approvedAt)}` : ""}`
        }));
    }

    get hasApprovalRows() {
        return this.approvalRows.length > 0;
    }

    get approvalFallback() {
        return this.approvalHistoryUnavailable
            ? `Approval API unavailable: ${this.approvalHistoryUnavailable}`
            : "No approval records available yet.";
    }

    handleScenarioChange(event) {
        this.dispatchEvent(new CustomEvent("scenariochange", { detail: { scenarioId: event.target.value } }));
    }

    handleGoalChange(event) {
        this.dispatchEvent(new CustomEvent("goalchange", { detail: { goal: event.target.value } }));
    }

    handleDraft() {
        this.dispatchEvent(new CustomEvent("draftplan"));
    }

    handleStart() {
        this.dispatchEvent(new CustomEvent("startplan"));
    }

    handleSmoke() {
        this.dispatchEvent(new CustomEvent("smokedata360"));
    }

    handleSelectStep(event) {
        const stepId = event.currentTarget.dataset.stepId;
        this.dispatchEvent(new CustomEvent("selectstep", { detail: { stepId } }));
    }

    handleStepKeydown(event) {
        if (!["Enter", " "].includes(event.key)) return;
        event.preventDefault();
        this.handleSelectStep(event);
    }

    handleApproveStep(event) {
        event.stopPropagation();
        this.dispatchEvent(new CustomEvent("approvestep", { detail: { stepId: event.currentTarget.dataset.stepId } }));
    }
}
