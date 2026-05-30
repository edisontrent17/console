package com.acme.data360agent.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public record PlanStep(
        @NotBlank String id,
        @NotBlank String title,
        @NotNull PlanPhase phase,
        @NotNull Data360Action action,
        Map<String, Object> input,
        List<String> dependsOn,
        Map<String, InputBinding> inputBindings,
        Boolean needsApproval
) implements Serializable {
    public PlanStep(String id, String title, Data360Action action, Map<String, Object> input, List<String> dependsOn, Boolean needsApproval) {
        this(id, title, PlanPhase.SETUP, action, input, dependsOn, Map.of(), needsApproval);
    }

    public PlanStep {
        phase = phase == null ? PlanPhase.SETUP : phase;
        input = input == null ? Map.of() : Map.copyOf(input);
        dependsOn = dependsOn == null ? List.of() : List.copyOf(dependsOn);
        inputBindings = inputBindings == null ? Map.of() : Map.copyOf(inputBindings);
        needsApproval = needsApproval != null && needsApproval;
    }
}
