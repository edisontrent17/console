import { LightningElement, api } from "lwc";
import { dateTime, number, slug } from "c/format";

export default class MonitorWorkspace extends LightningElement {
    @api demo;
    @api monitors = [];
    @api lastMonitorRun;
    @api recommendations = [];
    @api recommendationUnavailable;

    get monitorRows() {
        return (this.monitors || []).map((monitor) => ({
            ...monitor,
            className: `monitor-row ${slug(monitor.status)}`,
            statusClass: `slds-badge status ${slug(monitor.status)}`,
            detail: `${monitor.cadence} • ${monitor.lastRunAt ? dateTime(monitor.lastRunAt) : "Not run yet"}`
        }));
    }

    get hasMonitors() {
        return this.monitorRows.length > 0;
    }

    get recommendationRows() {
        return (this.recommendations || []).map((item) => ({
            ...item,
            key: item.id || item.title || item.metric,
            title: item.title || item.metric || "Recommendation",
            detail: item.detail || item.recommendation || item.rationale || item.summary || "Review recommended action.",
            observed: `${number(item.observedValue || 0)} observed${item.createdAt ? ` • ${dateTime(item.createdAt)}` : ""}`,
            className: `recommendation ${slug(item.priority || item.status)}`,
            badgeClass: `slds-badge status ${slug(item.priority || item.status)}`,
            badge: item.priority || item.status || "Review",
            canApprove: slug(item.status) === "pending-approval"
        }));
    }

    get hasRecommendations() {
        return this.recommendationRows.length > 0;
    }

    get recommendationFallback() {
        return this.recommendationUnavailable
            ? `Recommendation API unavailable: ${this.recommendationUnavailable}`
            : "No recommendations returned.";
    }

    get hasLastRun() {
        return Boolean(this.lastMonitorRun);
    }

    get lastRunClass() {
        return `slds-badge status ${slug(this.lastMonitorRun?.status)}`;
    }

    get lastRunObserved() {
        return `${number(this.lastMonitorRun?.observedValue || 0)} observed`;
    }

    get activation() {
        return this.demo?.emailActivation;
    }

    handleRunMonitor(event) {
        this.dispatchEvent(new CustomEvent("runmonitor", { detail: { monitorId: event.currentTarget.dataset.monitorId } }));
    }

    handleRecommendation(event) {
        this.dispatchEvent(new CustomEvent("recommendationtransition", {
            detail: {
                recommendationId: event.currentTarget.dataset.recommendationId,
                transition: event.currentTarget.dataset.transition
            }
        }));
    }
}
