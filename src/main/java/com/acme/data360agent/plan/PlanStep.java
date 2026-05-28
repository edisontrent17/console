package com.acme.data360agent.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public record PlanStep(
        @NotBlank String id,
        @NotBlank String title,
        @NotNull Data360Action action,
        Map<String, Object> input,
        List<String> dependsOn,
        Boolean needsApproval
) implements Serializable {
    public PlanStep {
        input = input == null ? Map.of() : Map.copyOf(input);
        dependsOn = dependsOn == null ? List.of() : List.copyOf(dependsOn);
        needsApproval = needsApproval != null && needsApproval;
    }
}
