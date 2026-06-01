package com.acme.data360agent.web;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.execution.PlanExecutor;
import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.mcp.McpToolRegistryService;
import com.acme.data360agent.operation.OperationBindingResolver;
import com.acme.data360agent.operation.OperationCatalogHash;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import com.acme.data360agent.planner.Data360Planner;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.planner.PlanRequest;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanValidator;
import com.acme.data360agent.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlanController {
    private final Data360Planner planner;
    private final PlanValidator validator;
    private final PlanStore store;
    private final PlanExecutor executor;
    private final AuditService audit;
    private final CurrentUserService users;
    private final OperationRegistry operations;
    private final McpToolRegistryService mcpRegistry;

    public PlanController(Data360Planner planner, PlanValidator validator, PlanStore store, PlanExecutor executor, AuditService audit, CurrentUserService users, OperationRegistry operations, ObjectProvider<McpToolRegistryService> mcpRegistry) {
        this.planner = planner;
        this.validator = validator;
        this.store = store;
        this.executor = executor;
        this.audit = audit;
        this.users = users;
        this.operations = operations;
        this.mcpRegistry = mcpRegistry == null ? null : mcpRegistry.getIfAvailable();
    }

    @PostMapping("/plans")
    public PlanDraft draft(@Valid @RequestBody PlanRequest request) {
        var draft = draftPlan(request);
        return store.saveDraft(users.organizationId(), draft);
    }

    @GetMapping("/plans")
    public Collection<PlanDraft> plans() {
        return store.drafts(users.organizationId());
    }

    @PostMapping("/plans/import")
    public PlanDraft importPlan(@Valid @RequestBody PlanSpec plan, @RequestParam(defaultValue = "false") boolean replace) {
        var organizationId = users.organizationId();
        var existingDraft = store.draft(organizationId, plan.id()).isPresent();
        if (!replace && existingDraft) {
            throw new IllegalArgumentException("Plan already exists. Import with replace=true to overwrite: " + plan.id());
        }
        var validation = validator.validate(plan);
        var draft = store.saveDraft(organizationId, new PlanDraft(plan, validation, List.of("import_plan", "validate_plan")));
        audit.event(organizationId, null, plan.id(), null, "plan_imported", Map.of(
                "planId", plan.id(),
                "replace", replace,
                "existingDraftReplaced", existingDraft,
                "approvalInvalidated", existingDraft,
                "validationOk", validation.ok(),
                "sourceArchiveHash", "sha256:" + OperationCatalogHash.sha256Hex(Map.of("plan", plan)),
                "importedBy", users.actor(),
                "dag", dagSummary(draft.artifacts()),
                "taskHashes", draft.operationBindings().stream().map(binding -> Map.of(
                        "resource", binding.resource(),
                        "schemaHash", binding.schemaHash(),
                        "registryHash", binding.registryHash(),
                        "toolSchemaHash", binding.toolSchemaHash(),
                        "connectorDefinitionHash", binding.connectorDefinitionHash()
                )).toList()
        ));
        return draft;
    }

    @GetMapping("/plans/{planId}")
    public PlanDraft plan(@PathVariable String planId) {
        return store.draft(users.organizationId(), planId).orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));
    }

    @PostMapping("/plans/{planId}/approve")
    public Object approvePlan(@PathVariable String planId) {
        var organizationId = users.organizationId();
        var draft = store.draft(organizationId, planId).orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));
        var validation = validator.validate(draft.plan());
        if (!validation.ok()) {
            return new PlanDraft(draft.plan(), validation, draft.graphStages(), draft.operationBindings());
        }
        var approvedPlan = ApprovedExecutablePlan.approve(freezeExecutionBindings(organizationId, draft, validation), validation, users.actor());
        store.saveApprovedPlan(organizationId, approvedPlan);
        audit.event(organizationId, null, planId, null, "plan_approved", Map.of(
                "artifactId", approvedPlan.artifactId(),
                "planHash", approvedPlan.planHash(),
                "approvedBy", approvedPlan.approvedBy(),
                "dag", dagSummary(approvedPlan.artifacts()),
                "taskHashes", approvedPlan.operationBindings().stream().map(binding -> Map.of(
                        "resource", binding.resource(),
                        "schemaHash", binding.schemaHash(),
                        "registryHash", binding.registryHash(),
                        "toolSchemaHash", binding.toolSchemaHash(),
                        "connectorDefinitionHash", binding.connectorDefinitionHash()
                )).toList()
        ));
        return approvedPlan;
    }

    @PostMapping("/plans/{planId}/runs")
    public Object start(@PathVariable String planId, @RequestBody(required = false) StartPlanRunRequest request) {
        var organizationId = users.organizationId();
        var approvedPlan = store.approvedPlan(organizationId, planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan must be approved before execution: " + planId));
        if (!planId.equals(approvedPlan.plan().id())) {
            throw new IllegalArgumentException("Approved artifact does not match requested plan: " + planId);
        }
        requireArtifactMatch(approvedPlan, request);
        var validation = validator.validate(approvedPlan.plan());
        if (!validation.ok()) {
            return new PlanDraft(approvedPlan.plan(), validation, approvedPlan.graphStages(), approvedPlan.operationBindings(), approvedPlan.artifacts());
        }
        return executor.start(organizationId, approvedPlan);
    }

    public Object start(String planId) {
        return start(planId, null);
    }

    @GetMapping("/runs/{runId}")
    public Object run(@PathVariable String runId) {
        return store.run(users.organizationId(), runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
    }

    @PostMapping("/runs/{runId}/steps/{stepId}/approve")
    public Object approveStep(@PathVariable String runId, @PathVariable String stepId) {
        return executor.approveStep(users.organizationId(), runId, stepId, users.actor());
    }

    @PostMapping("/runs/{runId}/cancel")
    public Object cancelRun(@PathVariable String runId, @RequestBody(required = false) Map<String, Object> request) {
        var reason = request == null ? "" : String.valueOf(request.getOrDefault("reason", ""));
        return executor.cancelRun(users.organizationId(), runId, reason, users.actor());
    }

    @GetMapping("/runs/{runId}/approvals")
    public Object approvals(@PathVariable String runId) {
        return audit.approvals(users.organizationId(), runId);
    }

    @GetMapping("/runs/{runId}/audit")
    public Object audit(@PathVariable String runId) {
        return audit.events(users.organizationId(), runId);
    }

    private PlanDraft draftPlan(PlanRequest request) {
        try {
            return planner.draft(request);
        } catch (Exception e) {
            var cause = rootCause(e);
            if (cause instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            }
            throw e;
        }
    }

    private PlanDraft freezeExecutionBindings(String organizationId, PlanDraft draft, com.acme.data360agent.plan.PlanValidationResult validation) {
        if (mcpRegistry == null) {
            return new PlanDraft(draft.plan(), validation, draft.graphStages(), draft.operationBindings(), draft.artifacts());
        }
        var registrySnapshot = mcpRegistry.current(organizationId);
        var bindings = OperationBindingResolver.snapshotsFor(operations, draft.plan(), registrySnapshot);
        return new PlanDraft(draft.plan(), validation, draft.graphStages(), bindings, draft.artifacts());
    }

    private void requireArtifactMatch(ApprovedExecutablePlan approvedPlan, StartPlanRunRequest request) {
        if (request == null || isBlank(request.artifactId()) || isBlank(request.planHash())) {
            throw new IllegalArgumentException("Starting a run requires the approved artifactId and planHash.");
        }
        if (!approvedPlan.artifactId().equals(request.artifactId()) || !approvedPlan.planHash().equals(request.planHash())) {
            throw new IllegalArgumentException("Run start request does not match the approved executable artifact.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> dagSummary(List<com.acme.data360agent.planner.PlanArtifact> artifacts) {
        var dag = artifacts == null ? null : artifacts.stream()
                .filter(artifact -> "plan_dag".equals(artifact.type()))
                .findFirst()
                .orElse(null);
        if (dag == null) {
            return Map.of("available", false);
        }
        var nodes = dag.data().get("nodes") instanceof List<?> list ? list.size() : 0;
        var edges = dag.data().get("edges") instanceof List<?> list ? list.size() : 0;
        var order = dag.data().get("topologicalOrder") instanceof List<?> list ? list : List.of();
        return Map.of(
                "available", true,
                "nodeCount", nodes,
                "edgeCount", edges,
                "topologicalOrder", order
        );
    }

    private Throwable rootCause(Throwable throwable) {
        var current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    public record StartPlanRunRequest(String artifactId, String planHash) {
    }
}
