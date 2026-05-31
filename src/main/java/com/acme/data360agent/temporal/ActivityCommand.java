package com.acme.data360agent.temporal;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanStep;

import java.io.Serializable;
import java.util.Map;

public record ActivityCommand(
        String runId,
        String planId,
        PlanContext context,
        OperationBindingSnapshot binding,
        PlanStep step,
        Map<String, Object> resolvedInput
) implements Serializable {
    public ActivityCommand {
        resolvedInput = resolvedInput == null ? Map.of() : Map.copyOf(resolvedInput);
    }
}
