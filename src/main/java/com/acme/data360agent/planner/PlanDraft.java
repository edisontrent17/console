package com.acme.data360agent.planner;

import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationRegistry;

import java.io.Serializable;
import java.util.List;

public record PlanDraft(
        PlanSpec plan,
        PlanValidationResult validation,
        List<String> graphStages,
        List<OperationBindingSnapshot> operationBindings
) implements Serializable {
    public PlanDraft(PlanSpec plan, PlanValidationResult validation, List<String> graphStages) {
        this(plan, validation, graphStages, defaultBindings(plan));
    }

    public PlanDraft {
        graphStages = graphStages == null ? List.of() : List.copyOf(graphStages);
        operationBindings = operationBindings == null || operationBindings.isEmpty()
                ? defaultBindings(plan)
                : List.copyOf(operationBindings);
    }

    private static List<OperationBindingSnapshot> defaultBindings(PlanSpec plan) {
        if (plan == null) {
            return List.of();
        }
        var registry = new OperationRegistry();
        return plan.steps().stream()
                .map(step -> registry.bindingFor(step.action()))
                .distinct()
                .toList();
    }
}
