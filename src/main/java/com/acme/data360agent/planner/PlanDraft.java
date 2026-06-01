package com.acme.data360agent.planner;

import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationBindingResolver;
import com.acme.data360agent.operation.OperationRegistry;

import java.io.Serializable;
import java.util.List;

public record PlanDraft(
        PlanSpec plan,
        PlanValidationResult validation,
        List<String> graphStages,
        List<OperationBindingSnapshot> operationBindings,
        List<PlanArtifact> artifacts
) implements Serializable {
    public PlanDraft(PlanSpec plan, PlanValidationResult validation, List<String> graphStages) {
        this(plan, validation, graphStages, defaultBindings(plan));
    }

    public PlanDraft(PlanSpec plan, PlanValidationResult validation, List<String> graphStages, List<OperationBindingSnapshot> operationBindings) {
        this(plan, validation, graphStages, operationBindings, defaultArtifacts(plan, validation));
    }

    public PlanDraft {
        graphStages = graphStages == null ? List.of() : List.copyOf(graphStages);
        operationBindings = operationBindings == null || operationBindings.isEmpty()
                ? defaultBindings(plan)
                : List.copyOf(operationBindings);
        artifacts = artifacts == null || artifacts.isEmpty()
                ? defaultArtifacts(plan, validation, operationBindings)
                : List.copyOf(artifacts);
    }

    private static List<OperationBindingSnapshot> defaultBindings(PlanSpec plan) {
        if (plan == null) {
            return List.of();
        }
        return OperationBindingResolver.snapshotsFor(new OperationRegistry(), plan);
    }

    private static List<PlanArtifact> defaultArtifacts(PlanSpec plan, PlanValidationResult validation) {
        return defaultArtifacts(plan, validation, defaultBindings(plan));
    }

    private static List<PlanArtifact> defaultArtifacts(PlanSpec plan, PlanValidationResult validation, List<OperationBindingSnapshot> operationBindings) {
        if (plan == null) {
            return List.of();
        }
        return List.of(PlanArtifact.planDag(plan, validation, operationBindings));
    }
}
