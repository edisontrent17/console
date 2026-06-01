import { LightningElement, api } from "lwc";
import { slug } from "c/format";

export default class ChatWorkspace extends LightningElement {
    @api messages = [];
    @api plannerStatus = "Ready";
    @api modelLabel = "Model not configured";
    @api userLabel = "Not signed in";
    @api currentDraft;
    @api approvedPlan;
    @api currentRun;
    @api busy = {};
    @api chatMode = "auto";
    draftMessage = "";

    get subtitle() {
        return this.currentRun?.status
            ? `Run ${this.currentRun.status}`
            : "Plan, review, and execute governed Data 360 setup.";
    }

    get plannerStatusClass() {
        return `chip status ${slug(this.plannerStatus || "ready")}`;
    }

    get chatScrollClass() {
        const userMessages = (this.messages || []).filter((message) => message.role === "user").length;
        return `chat-scroll ${userMessages === 0 && !this.currentDraft ? "empty" : ""}`;
    }

    get messageRows() {
        const rows = this.messages?.length ? this.messages : [
            { role: "assistant", text: "What should Data 360 set up?", meta: "Describe the customer goal and the systems involved." }
        ];
        return rows.map((message, index) => {
            const traceRows = normalizeTrace(message.trace, index);
            return {
                ...message,
                key: message.id || `${message.role}-${index}`,
                avatar: message.role === "user" ? "You" : "D360",
                className: `message ${message.role === "user" ? "user" : "assistant"}`,
                hasTrace: traceRows.length > 0,
                traceRows,
                traceSummary: traceRows.length === 1 ? "1 step" : `${traceRows.length} steps`
            };
        });
    }

    get hasDraft() {
        return Boolean(this.currentDraft?.plan);
    }

    get planTitle() {
        return this.currentDraft?.plan?.goal || "PlanSpec drafted";
    }

    get planSummary() {
        const steps = this.currentDraft?.plan?.steps?.length || 0;
        const validation = this.currentDraft?.validation?.ok ? "validated" : "needs review";
        return `${steps} ${steps === 1 ? "step" : "steps"} • ${validation}`;
    }

    get sendDisabled() {
        return !this.draftMessage.trim() || this.busy.draftPlan || this.busy.chat;
    }

    get sendLabel() {
        if (this.busy.chat) return "Thinking...";
        return this.busy.draftPlan ? "Planning..." : "Send";
    }

    get startDisabled() {
        return !this.approvedPlan || this.busy.startPlan || this.busy.approvePlan;
    }

    get approveDisabled() {
        return !this.currentDraft?.validation?.ok || this.busy.approvePlan || Boolean(this.approvedPlan);
    }

    get startLabel() {
        return this.busy.startPlan ? "Running..." : "Run plan";
    }

    get approveLabel() {
        if (this.busy.approvePlan) return "Approving...";
        return this.approvedPlan ? "Approved" : "Approve";
    }

    get exportPlanDisabled() {
        return !this.currentDraft?.plan?.id || this.busy.exportPlanSpec;
    }

    get modeRows() {
        return [
            { value: "auto", label: "Auto", title: "Decide between exploration and planning from the request." },
            { value: "plan", label: "Plan", title: "Draft a governed PlanSpec for approval." },
            { value: "execute", label: "Execute", title: "Use connected tools for live read-only exploration." }
        ].map((mode) => ({
            ...mode,
            className: `mode-button ${this.chatMode === mode.value ? "active" : ""}`
        }));
    }

    handleInput(event) {
        this.draftMessage = event.target.value;
    }

    handleKeydown(event) {
        if (event.key !== "Enter" || event.shiftKey) {
            return;
        }
        event.preventDefault();
        this.handleSend();
    }

    handleSend() {
        const text = this.draftMessage.trim();
        if (!text) return;
        this.draftMessage = "";
        const input = this.template.querySelector(".composer-input");
        if (input) {
            input.value = "";
        }
        this.dispatchEvent(new CustomEvent("sendmessage", { detail: { text, mode: this.chatMode } }));
    }

    handleModeChange(event) {
        this.dispatchEvent(new CustomEvent("chatmodechange", {
            detail: { mode: event.currentTarget.dataset.mode }
        }));
    }

    handleStartPlan() {
        this.dispatchEvent(new CustomEvent("startplan"));
    }

    handleApprovePlan() {
        this.dispatchEvent(new CustomEvent("approveplan"));
    }

    handleExportPlanSpec() {
        this.dispatchEvent(new CustomEvent("exportplanspec"));
    }
}

function normalizeTrace(trace, messageIndex) {
    return (trace || []).map((entry, index) => {
        const dataRows = Object.entries(entry.data || {}).map(([name, value]) => ({
            key: `${messageIndex}-${index}-${name}`,
            name,
            value: traceValue(value)
        }));
        return {
            key: `${messageIndex}-${index}`,
            className: `trace-step ${slug(entry.status || "completed")}`,
            label: entry.label || entry.stage || "Trace step",
            detail: entry.detail || "",
            stage: entry.stage || "",
            status: entry.status || "",
            duration: typeof entry.durationMs === "number" ? `${entry.durationMs} ms` : "",
            dataRows,
            hasData: dataRows.length > 0
        };
    });
}

function traceValue(value) {
    if (value === null || value === undefined) {
        return "";
    }
    if (typeof value === "object") {
        return JSON.stringify(value);
    }
    return String(value);
}
