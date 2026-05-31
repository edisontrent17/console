package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.operation.OperationBindingSnapshot;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface PlanStore {
    PlanDraft saveDraft(PlanDraft draft);

    Optional<PlanDraft> draft(String planId);

    Collection<PlanDraft> drafts();

    PlanRun createRun(PlanSpec plan);

    default PlanRun createRun(PlanSpec plan, List<OperationBindingSnapshot> operationBindings) {
        var run = createRun(plan);
        run.setOperationBindings(operationBindings);
        return saveRun(run);
    }

    Optional<PlanRun> run(String runId);

    PlanRun saveRun(PlanRun run);

    <T> T withRunLock(String runId, Function<PlanRun, T> work);
}
