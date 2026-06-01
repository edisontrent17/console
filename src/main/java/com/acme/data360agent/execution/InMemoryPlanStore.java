package com.acme.data360agent.execution;

import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
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
    private final Map<String, ApprovedExecutablePlan> approvedPlans = new ConcurrentHashMap<>();
    private final Map<String, PlanRun> runs = new ConcurrentHashMap<>();

    public PlanDraft saveDraft(String organizationId, PlanDraft draft) {
        var key = key(organizationId, draft.plan().id());
        drafts.put(key, draft);
        approvedPlans.remove(key);
        return draft;
    }

    public Optional<PlanDraft> draft(String organizationId, String planId) {
        return Optional.ofNullable(drafts.get(key(organizationId, planId)));
    }

    public Collection<PlanDraft> drafts(String organizationId) {
        var prefix = normalize(organizationId) + "\u0000";
        return drafts.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public ApprovedExecutablePlan saveApprovedPlan(String organizationId, ApprovedExecutablePlan approvedPlan) {
        approvedPlans.put(key(organizationId, approvedPlan.plan().id()), approvedPlan);
        return approvedPlan;
    }

    @Override
    public Optional<ApprovedExecutablePlan> approvedPlan(String organizationId, String planId) {
        return Optional.ofNullable(approvedPlans.get(key(organizationId, planId)));
    }

    @Override
    public void clearApprovedPlan(String organizationId, String planId) {
        approvedPlans.remove(key(organizationId, planId));
    }

    public PlanRun createRun(String organizationId, ApprovedExecutablePlan approvedPlan) {
        var run = new PlanRun(Ids.prefixed("run"), normalize(organizationId), approvedPlan);
        runs.put(key(organizationId, run.getId()), run);
        return run;
    }

    public Optional<PlanRun> run(String organizationId, String runId) {
        return Optional.ofNullable(runs.get(key(organizationId, runId)));
    }

    @Override
    public PlanRun saveRun(String organizationId, PlanRun run) {
        runs.put(key(organizationId, run.getId()), run);
        return run;
    }

    @Override
    public <T> T withRunLock(String organizationId, String runId, Function<PlanRun, T> work) {
        var run = run(organizationId, runId).orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        synchronized (run) {
            return work.apply(run);
        }
    }

    private String key(String organizationId, String id) {
        return normalize(organizationId) + "\u0000" + id;
    }

    private String normalize(String organizationId) {
        return organizationId == null || organizationId.isBlank() ? DEFAULT_ORGANIZATION_ID : organizationId;
    }
}
