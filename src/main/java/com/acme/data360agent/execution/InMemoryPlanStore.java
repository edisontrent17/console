package com.acme.data360agent.execution;

import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.support.Ids;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "memory")
public class InMemoryPlanStore implements PlanStore {
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
        var run = new PlanRun(Ids.prefixed("run"), plan);
        runs.put(run.getId(), run);
        return run;
    }

    public Optional<PlanRun> run(String runId) {
        return Optional.ofNullable(runs.get(runId));
    }

    @Override
    public PlanRun saveRun(PlanRun run) {
        runs.put(run.getId(), run);
        return run;
    }

    @Override
    public <T> T withRunLock(String runId, Function<PlanRun, T> work) {
        var run = run(runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        synchronized (run) {
            return work.apply(run);
        }
    }
}
