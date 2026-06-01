import { LightningElement, api } from "lwc";
import { dateTime, number, slug, toJson } from "c/format";

export default class PlanWorkspace extends LightningElement {
    @api scenarios = [];
    @api selectedScenarioId = "";
    @api goal = "";
    @api diagnostics;
    @api plannerStatus = "Ready";
    @api currentDraft;
    @api approvedPlan;
    @api currentRun;
    @api approvalHistory = [];
    @api approvalHistoryUnavailable;
    @api selectedStepId;
    @api busy = {};
    activePlanView = "steps";

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
        return !this.approvedPlan || this.busy.startPlan || this.busy.approvePlan;
    }

    get exportDisabled() {
        return !this.currentDraft?.plan?.id || this.busy.exportPlanSpec;
    }

    get approveDisabled() {
        return !this.currentDraft?.validation?.ok || this.busy.approvePlan || Boolean(this.approvedPlan);
    }

    get draftLabel() {
        return this.busy.draftPlan ? "Drafting..." : "Draft plan";
    }

    get startLabel() {
        return this.busy.startPlan ? "Starting..." : "Start run";
    }

    get exportLabel() {
        return this.busy.exportPlanSpec ? "Exporting..." : "Export PlanSpec";
    }

    get approveLabel() {
        if (this.busy.approvePlan) return "Approving...";
        return this.approvedPlan ? "Approved" : "Approve plan";
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

    get approvalClass() {
        return this.approvedPlan ? "slds-badge slds-theme_success" : "slds-badge";
    }

    get approvalLabel() {
        return this.approvedPlan ? "Executable artifact approved" : "Awaiting plan approval";
    }

    get activeArtifacts() {
        return this.approvedPlan?.artifacts?.length ? this.approvedPlan.artifacts : this.currentDraft?.artifacts || [];
    }

    get activeOperationBindings() {
        if (this.approvedPlan?.operationBindings?.length) return this.approvedPlan.operationBindings;
        if (this.currentRun?.operationBindings?.length) return this.currentRun.operationBindings;
        return this.currentDraft?.operationBindings || [];
    }

    get artifactSummary() {
        if (this.approvedPlan) {
            const detail = [
                this.approvedPlan.approvedBy ? `by ${this.approvedPlan.approvedBy}` : null,
                this.approvedPlan.approvedAt ? dateTime(this.approvedPlan.approvedAt) : null,
                shortHash(this.approvedPlan.planHash)
            ].filter(Boolean).join(" • ");
            return {
                label: "Approved",
                value: this.approvedPlan.artifactId || "approved artifact",
                detail: detail || "Executable artifact frozen",
                className: "governance-card good"
            };
        }
        return {
            label: "Draft",
            value: this.currentDraft?.plan?.id || "No plan",
            detail: "Approve to freeze executable metadata",
            className: "governance-card neutral"
        };
    }

    get approvalSummary() {
        const required = this.activeOperationBindings.filter((binding) => binding.requiresApproval).length
            || (this.plan?.steps || []).filter((step) => step.needsApproval).length;
        if (this.approvedPlan) {
            return {
                label: "Approval",
                value: "Approved",
                detail: `${number(required)} gated ${required === 1 ? "operation" : "operations"}`,
                className: "governance-card good"
            };
        }
        return {
            label: "Approval",
            value: "Pending",
            detail: `${number(required)} gated ${required === 1 ? "operation" : "operations"}`,
            className: "governance-card warning"
        };
    }

    get registrySummary() {
        if (this.hasRegistryDriftSignals) {
            return {
                label: "Registry",
                value: "Review drift",
                detail: `${number(this.registryDriftSignals.length)} registry ${this.registryDriftSignals.length === 1 ? "signal" : "signals"}`,
                className: "governance-card error"
            };
        }
        const frozen = this.activeOperationBindings.filter((binding) => binding.registryHash || binding.toolSchemaHash).length;
        if (frozen > 0) {
            return {
                label: "Registry",
                value: "Frozen",
                detail: `${number(frozen)} MCP ${frozen === 1 ? "binding" : "bindings"} with hashes`,
                className: "governance-card good"
            };
        }
        return {
            label: "Registry",
            value: "Catalog only",
            detail: "No live MCP registry hash in this artifact",
            className: "governance-card neutral"
        };
    }

    get redactionSummary() {
        const detected = this.redactionRows.filter((row) => row.detected).length;
        return {
            label: "Redaction",
            value: detected ? `${number(detected)} detected` : "Export guarded",
            detail: detected ? "Sensitive or truncated values are hidden" : "Exports use the redacted archive payload",
            className: detected ? "governance-card warning" : "governance-card good"
        };
    }

    get governanceCards() {
        return [
            this.artifactSummary,
            this.approvalSummary,
            this.registrySummary,
            this.redactionSummary
        ];
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

    get planViewOptions() {
        return [
            { key: "steps", label: "Steps", className: this.activePlanView === "steps" ? "view-tab active" : "view-tab" },
            { key: "dag", label: "DAG", className: this.activePlanView === "dag" ? "view-tab active" : "view-tab" }
        ];
    }

    get showStepsView() {
        return this.activePlanView === "steps";
    }

    get showDagView() {
        return this.activePlanView === "dag";
    }

    get planDag() {
        return this.activeArtifacts.find((artifact) => artifact.type === "plan_dag")?.data || {};
    }

    get dagRows() {
        const nodes = this.planDag.nodes || [];
        const groups = this.planDag.groups?.length
            ? this.planDag.groups
            : [
                { id: "discover", label: "Discover", nodeIds: nodes.filter((node) => slug(node.phase) === "discover").map((node) => node.id) },
                { id: "setup", label: "Setup", nodeIds: nodes.filter((node) => slug(node.phase) === "setup").map((node) => node.id) },
                { id: "monitor", label: "Monitor", nodeIds: nodes.filter((node) => slug(node.phase) === "monitor").map((node) => node.id) }
            ];
        const nodeMap = new Map(nodes.map((node) => [node.id, node]));
        const warningsByNode = new Map();
        (this.planDag.warnings || []).forEach((warning) => {
            const nodeId = warning.nodeId || "plan";
            warningsByNode.set(nodeId, [...(warningsByNode.get(nodeId) || []), warning.message]);
        });
        return groups
            .map((group) => ({
                ...group,
                nodes: (group.nodeIds || [])
                    .map((nodeId) => this.decorateDagNode(nodeMap.get(nodeId), warningsByNode))
                    .filter(Boolean)
            }))
            .filter((group) => group.nodes.length);
    }

    get dagEdges() {
        return (this.planDag.edges || []).map((edge, index) => ({
            ...edge,
            key: `${edge.from}-${edge.to}-${edge.type}-${index}`,
            className: `dag-edge ${slug(edge.type)}`
        }));
    }

    get hasDag() {
        return this.dagRows.length > 0;
    }

    get dagSourceLabel() {
        return this.approvedPlan ? "Approved artifact DAG" : "Draft DAG";
    }

    get dagOrderCount() {
        return number((this.planDag.topologicalOrder || []).length);
    }

    get bindingRows() {
        return this.activeOperationBindings.map((binding, index) => {
            const hasRegistry = Boolean(binding.registryHash || binding.toolSchemaHash || binding.connectorDefinitionHash);
            const tool = [binding.mcpServerId, binding.underlyingTool || binding.facadeTool].filter(Boolean).join(" / ");
            return {
                key: `${binding.resource}-${index}`,
                resource: binding.resource,
                tool: tool || binding.transport || "internal",
                effect: (binding.effect || "READ").toLowerCase(),
                approval: binding.requiresApproval ? "Approval" : "Auto",
                schemaHash: shortHash(binding.schemaHash) || "n/a",
                registryHash: shortHash(binding.registryHash) || "not frozen",
                toolSchemaHash: shortHash(binding.toolSchemaHash) || "not frozen",
                connectorDefinitionHash: shortHash(binding.connectorDefinitionHash) || "not frozen",
                statusLabel: hasRegistry ? "Frozen MCP" : "Local catalog",
                className: `binding-row ${hasRegistry ? "frozen" : "catalog"}`
            };
        });
    }

    get hasBindingRows() {
        return this.bindingRows.length > 0;
    }

    get registryDriftSignals() {
        const issues = (this.currentDraft?.validation?.issues || [])
            .filter((issue) => containsRegistryDrift(issue.message))
            .map((issue, index) => ({
                key: `issue-${issue.stepId || "plan"}-${index}`,
                source: issue.stepId || "plan",
                status: issue.severity || "warning",
                detail: issue.message,
                className: `signal-row ${slug(issue.severity || "warning")}`
            }));
        const runErrors = (this.currentRun?.steps || [])
            .filter((step) => containsRegistryDrift(step.error))
            .map((step) => ({
                key: `run-${step.stepId}`,
                source: step.stepId,
                status: step.status || "FAILED",
                detail: step.error,
                className: "signal-row error"
            }));
        return [...issues, ...runErrors];
    }

    get hasRegistryDriftSignals() {
        return this.registryDriftSignals.length > 0;
    }

    get registryFallback() {
        return this.approvedPlan
            ? "No registry drift reported for this approved artifact. Runtime execution still checks frozen hashes."
            : "Approve the plan to freeze live MCP registry and tool schema hashes when available.";
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
            { label: "Resolved Input", value: toJson(this.selectedRunStep?.resolvedInput || {}) },
            { label: "Output", value: toJson(this.selectedRunStep?.output || {}) },
            { label: "Raw", value: toJson(this.selectedRunStep?.raw || {}) }
        ];
    }

    get redactionRows() {
        const rows = [{
            key: "export",
            scope: "PlanSpec export",
            status: "Redacted",
            detail: "Export uses the server archive payload with redacted plan artifacts.",
            detected: false,
            className: "signal-row good"
        }];
        const selectedStepPayload = {
            resolvedInput: this.selectedRunStep?.resolvedInput,
            output: this.selectedRunStep?.output,
            raw: this.selectedRunStep?.raw,
            error: this.selectedRunStep?.error
        };
        if (containsRedactionMarker(selectedStepPayload)) {
            rows.push({
                key: `selected-${this.selectedStep?.id || "step"}`,
                scope: this.selectedStep?.id || "Selected step",
                status: "Masked",
                detail: "Selected step data contains redacted or truncated values.",
                detected: true,
                className: "signal-row warning"
            });
        }
        const runMatches = (this.currentRun?.steps || [])
            .filter((step) => step.stepId !== this.selectedStep?.id)
            .filter((step) => containsRedactionMarker({
                resolvedInput: step.resolvedInput,
                output: step.output,
                raw: step.raw,
                error: step.error
            }))
            .map((step) => ({
                key: `run-redaction-${step.stepId}`,
                scope: step.stepId,
                status: "Masked",
                detail: "Run data contains redacted or truncated values.",
                detected: true,
                className: "signal-row warning"
            }));
        return [...rows, ...runMatches];
    }

    get hasRedactionRows() {
        return this.redactionRows.length > 0;
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

    get showApprovalFallback() {
        return !this.hasApprovalRows && !this.hasApprovedArtifactRows;
    }

    get approvedArtifactRows() {
        if (!this.approvedPlan) return [];
        return [{
            key: this.approvedPlan.artifactId || "approved-plan",
            step: "plan",
            status: "APPROVED",
            detail: [
                this.approvedPlan.artifactId,
                this.approvedPlan.approvedBy ? `by ${this.approvedPlan.approvedBy}` : null,
                this.approvedPlan.approvedAt ? dateTime(this.approvedPlan.approvedAt) : null,
                shortHash(this.approvedPlan.planHash)
            ].filter(Boolean).join(" • ")
        }];
    }

    get hasApprovedArtifactRows() {
        return this.approvedArtifactRows.length > 0;
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

    handleExportPlanSpec() {
        this.dispatchEvent(new CustomEvent("exportplanspec"));
    }

    handleApprovePlan() {
        this.dispatchEvent(new CustomEvent("approveplan"));
    }

    handleSmoke() {
        this.dispatchEvent(new CustomEvent("smokedata360"));
    }

    handleSelectStep(event) {
        const stepId = event.currentTarget.dataset.stepId;
        this.dispatchEvent(new CustomEvent("selectstep", { detail: { stepId } }));
    }

    handlePlanView(event) {
        this.activePlanView = event.currentTarget.dataset.view;
    }

    handleDagNode(event) {
        this.dispatchEvent(new CustomEvent("selectstep", { detail: { stepId: event.currentTarget.dataset.stepId } }));
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

    decorateDagNode(node, warningsByNode) {
        if (!node) return null;
        const runStep = this.runStepMap.get(node.id);
        const status = runStep?.status || "DRAFT";
        const warnings = warningsByNode.get(node.id) || [];
        return {
            ...node,
            status,
            warnings,
            warningText: warnings.join(" • "),
            hasWarnings: warnings.length > 0,
            className: `dag-node ${slug(node.effect)} ${slug(status)} ${this.selectedStepId === node.id ? "selected" : ""}`,
            effectLabel: node.effect || "read",
            gateLabel: node.approvalRequired ? "Approval" : "Auto"
        };
    }
}

function shortHash(value) {
    if (!value) return "";
    const text = String(value);
    return text.startsWith("sha256:") ? `sha256:${text.slice(7, 15)}` : text.slice(0, 12);
}

function containsRegistryDrift(value) {
    const text = String(value || "").toLowerCase();
    return text.includes("registry drift")
        || text.includes("registry validation failed")
        || text.includes("schema drift")
        || text.includes("tool schema")
        || text.includes("mcp registry validation");
}

function containsRedactionMarker(value) {
    if (value == null) return false;
    if (typeof value === "string") {
        return value.includes("***") || value.includes("[truncated]") || value.includes("_truncated");
    }
    if (Array.isArray(value)) {
        return value.some((item) => containsRedactionMarker(item));
    }
    if (typeof value === "object") {
        return Object.entries(value).some(([key, item]) => key === "_truncated" || containsRedactionMarker(item));
    }
    return false;
}
