import { LightningElement, api } from "lwc";
import { toJson } from "c/format";

export default class AuditWorkspace extends LightningElement {
    @api demo;
    @api currentDraft;
    @api currentRun;
    @api diagnostics;

    get signalSources() {
        return this.demo?.signalSources || [];
    }

    get planStages() {
        return this.demo?.plan || [];
    }

    get planJson() {
        return toJson(this.currentDraft?.plan || {});
    }

    get runJson() {
        return toJson(this.currentRun || {});
    }

    get diagnosticsJson() {
        return toJson(this.diagnostics || {});
    }
}
