import { LightningElement, api } from "lwc";
import { slug } from "c/format";

export default class ChatWorkspace extends LightningElement {
    @api messages = [];
    @api plannerStatus = "Ready";
    @api modelLabel = "Model not configured";
    @api userLabel = "Not signed in";
    @api currentDraft;
    @api currentRun;
    @api busy = {};
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
        return rows.map((message, index) => ({
            ...message,
            key: message.id || `${message.role}-${index}`,
            avatar: message.role === "user" ? "You" : "D360",
            className: `message ${message.role === "user" ? "user" : "assistant"}`
        }));
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
        return !this.draftMessage.trim() || this.busy.draftPlan;
    }

    get sendLabel() {
        return this.busy.draftPlan ? "Planning..." : "Send";
    }

    get startDisabled() {
        return !this.currentDraft?.validation?.ok || this.busy.startPlan;
    }

    get startLabel() {
        return this.busy.startPlan ? "Running..." : "Run plan";
    }

    get exportPlanDisabled() {
        return !this.currentDraft?.plan?.id || this.busy.exportPlanSpec;
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
        this.dispatchEvent(new CustomEvent("sendmessage", { detail: { text } }));
    }

    handleStartPlan() {
        this.dispatchEvent(new CustomEvent("startplan"));
    }

    handleExportPlanSpec() {
        this.dispatchEvent(new CustomEvent("exportplanspec"));
    }
}
