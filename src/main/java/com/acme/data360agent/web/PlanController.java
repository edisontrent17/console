package com.acme.data360agent.web;

import com.acme.data360agent.execution.InMemoryPlanStore;
import com.acme.data360agent.execution.PlanExecutor;
import com.acme.data360agent.planner.Data360Planner;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.planner.PlanRequest;
import com.acme.data360agent.plan.PlanValidator;
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
    private final InMemoryPlanStore store;
    private final PlanExecutor executor;

    public PlanController(Data360Planner planner, PlanValidator validator, InMemoryPlanStore store, PlanExecutor executor) {
        this.planner = planner;
        this.validator = validator;
        this.store = store;
        this.executor = executor;
    }

    @PostMapping("/plans")
    public PlanDraft draft(@Valid @RequestBody PlanRequest request) {
        var draft = planner.draft(request);
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
            return new PlanDraft(draft.plan(), validation, draft.graphStages());
        }
        return executor.start(draft.plan());
    }

    @GetMapping("/runs/{runId}")
    public Object run(@PathVariable String runId) {
        return store.run(runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
    }

    @PostMapping("/runs/{runId}/steps/{stepId}/approve")
    public Object approveStep(@PathVariable String runId, @PathVariable String stepId) {
        return executor.approveStep(runId, stepId);
    }
}
