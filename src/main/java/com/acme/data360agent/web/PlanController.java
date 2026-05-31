package com.acme.data360agent.web;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.execution.PlanExecutor;
import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.planner.Data360Planner;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.planner.PlanRequest;
import com.acme.data360agent.plan.PlanValidator;
import com.acme.data360agent.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class PlanController {
    private final Data360Planner planner;
    private final PlanValidator validator;
    private final PlanStore store;
    private final PlanExecutor executor;
    private final AuditService audit;
    private final CurrentUserService users;

    public PlanController(Data360Planner planner, PlanValidator validator, PlanStore store, PlanExecutor executor, AuditService audit, CurrentUserService users) {
        this.planner = planner;
        this.validator = validator;
        this.store = store;
        this.executor = executor;
        this.audit = audit;
        this.users = users;
    }

    @PostMapping("/plans")
    public PlanDraft draft(@Valid @RequestBody PlanRequest request) {
        var draft = draftPlan(request);
        return store.saveDraft(draft);
    }

    @GetMapping("/plans")
    public Collection<PlanDraft> plans() {
        return store.drafts();
    }

    @GetMapping("/plans/{planId}")
    public PlanDraft plan(@PathVariable String planId) {
        return store.draft(planId).orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));
    }

    @PostMapping("/plans/{planId}/runs")
    public Object start(@PathVariable String planId) {
        var draft = store.draft(planId).orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));
        var validation = validator.validate(draft.plan());
        if (!validation.ok()) {
            return new PlanDraft(draft.plan(), validation, draft.graphStages(), draft.operationBindings());
        }
        return executor.start(draft.plan(), draft.operationBindings());
    }

    @GetMapping("/runs/{runId}")
    public Object run(@PathVariable String runId) {
        return store.run(runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
    }

    @PostMapping("/runs/{runId}/steps/{stepId}/approve")
    public Object approveStep(@PathVariable String runId, @PathVariable String stepId) {
        return executor.approveStep(runId, stepId, users.actor());
    }

    @GetMapping("/runs/{runId}/approvals")
    public Object approvals(@PathVariable String runId) {
        return audit.approvals(runId);
    }

    @GetMapping("/runs/{runId}/audit")
    public Object audit(@PathVariable String runId) {
        return audit.events(runId);
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

    private Throwable rootCause(Throwable throwable) {
        var current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }
}
