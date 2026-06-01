import { LightningElement, api } from "lwc";
import { currency, metric, percent, slug } from "c/format";

export default class ExecutionWorkspace extends LightningElement {
    @api demo;
    @api selectedAccountId;
    @api busy = {};

    get hasDemo() {
        return Boolean(this.demo);
    }

    get accounts() {
        return (this.demo?.accounts || []).map((account) => ({
            ...account,
            contractLabel: currency(account.contractValue),
            scoreLabel: percent(account.recoveryPriorityScore),
            riskLabel: percent(account.supportBlockerScore),
            rowClass: `account-row ${account.id === this.selectedAccountId ? "selected" : ""}`,
            badgeClass: `priority ${slug(account.priority)}`
        }));
    }

    get selectedAccount() {
        return this.accounts.find((account) => account.id === this.selectedAccountId) || this.accounts[0];
    }

    get selectedEvidence() {
        return this.selectedAccount?.evidence || [];
    }

    get actions() {
        const all = this.demo?.actions || [];
        const accountActions = all.filter((action) => action.accountId === this.selectedAccount?.id);
        const sharedActions = all.filter((action) => !action.accountId);
        const focused = this.selectedAccount ? [...accountActions, ...sharedActions] : all;
        return [...focused.filter((action) => action.status !== "Executed"), ...focused.filter((action) => action.status === "Executed")]
            .slice(0, 3)
            .map((action) => ({
                ...action,
                className: `action-card ${slug(action.status)}`,
                statusClass: `slds-badge status ${slug(action.status)}`,
                isExecuted: action.status === "Executed",
                isApproved: action.status === "Approved",
                isRejected: action.status === "Rejected",
                primaryLabel: action.status === "Approved" ? "Execute" : "Approve",
                primaryTransition: action.status === "Approved" ? "execute" : "approve",
                showReject: action.status !== "Executed"
            }));
    }

    get activation() {
        return this.demo?.emailActivation;
    }

    get activationSentLabel() {
        return metric(this.activation?.sentEmails, "number");
    }

    get funnel() {
        const visible = ["Identified", "Holdout", "Email approved", "Sent", "Delivered", "Clicked", "Activated", "Revenue recovered"];
        return (this.demo?.recoveryFunnel || [])
            .filter((item) => visible.includes(item.label))
            .map((item) => this.metricItem(item));
    }

    get impact() {
        const visible = ["Recoverable revenue", "Eligible audience", "Recovery rate", "Revenue recovered"];
        return (this.demo?.impact || [])
            .filter((item) => visible.includes(item.label))
            .map((item) => this.metricItem(item));
    }

    handleReset() {
        this.dispatchEvent(new CustomEvent("resetdemo"));
    }

    handleSelectAccount(event) {
        this.dispatchEvent(new CustomEvent("selectaccount", { detail: { accountId: event.currentTarget.dataset.accountId } }));
    }

    handleAction(event) {
        this.dispatchEvent(new CustomEvent("actiontransition", {
            detail: {
                actionId: event.currentTarget.dataset.actionId,
                transition: event.currentTarget.dataset.transition
            }
        }));
    }

    metricItem(item) {
        const target = item.target || 0;
        const pct = target ? Math.min(100, Math.round((item.current / target) * 100)) : 0;
        return {
            ...item,
            value: metric(item.current, item.unit),
            style: `width:${pct}%`
        };
    }
}
