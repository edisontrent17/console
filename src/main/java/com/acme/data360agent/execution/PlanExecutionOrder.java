package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanTopology;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import com.acme.data360agent.planner.PlanArtifact;

import java.util.List;

public final class PlanExecutionOrder {
    private PlanExecutionOrder() {
    }

    public static List<String> stepIds(PlanRun run) {
        if (run == null) {
            return List.of();
        }
        return stepIds(run.getApprovedPlan());
    }

    public static List<String> stepIds(ApprovedExecutablePlan approvedPlan) {
        if (approvedPlan == null) {
            return List.of();
        }
        var artifactOrder = dagOrder(approvedPlan.artifacts());
        return artifactOrder.isEmpty() ? PlanTopology.stepIds(approvedPlan.plan()) : artifactOrder;
    }

    public static List<PlanStep> steps(PlanRun run) {
        if (run == null) {
            return List.of();
        }
        return steps(run.getPlan(), stepIds(run));
    }

    public static List<PlanStep> steps(PlanSpec plan, List<String> stepIds) {
        return PlanTopology.steps(plan, stepIds);
    }

    private static List<String> dagOrder(List<PlanArtifact> artifacts) {
        if (artifacts == null) {
            return List.of();
        }
        for (var artifact : artifacts) {
            if (artifact == null || !"plan_dag".equals(artifact.type())) {
                continue;
            }
            var value = artifact.data().get("topologicalOrder");
            if (value instanceof Iterable<?> iterable) {
                var order = new java.util.ArrayList<String>();
                for (var item : iterable) {
                    if (item != null && !String.valueOf(item).isBlank()) {
                        order.add(String.valueOf(item));
                    }
                }
                return List.copyOf(order);
            }
        }
        return List.of();
    }
}
