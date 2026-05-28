package com.acme.data360agent.execution;

import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.plan.PlanSpec;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryPlanStore {
    private final Map<String, PlanDraft> drafts = new ConcurrentHashMap<>();
    private final Map<String, PlanRun> runs = new ConcurrentHashMap<>();

    public PlanDraft saveDraft(PlanDraft draft) {
        drafts.put(draft.plan().id(), draft);
        return draft;
    }

    public Optional<PlanDraft> draft(String planId) {
        return Optional.ofNullable(drafts.get(planId));
    }

    public Collection<PlanDraft> drafts() {
        return drafts.values();
    }

    public PlanRun createRun(PlanSpec plan) {
        var run = new PlanRun("run_" + UUID.randomUUID().toString().substring(0, 8), plan);
        runs.put(run.getId(), run);
        return run;
    }

    public Optional<PlanRun> run(String runId) {
        return Optional.ofNullable(runs.get(runId));
    }
}
