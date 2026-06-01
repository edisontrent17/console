package com.acme.data360agent.temporal;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanStep;

import java.io.Serializable;
import java.util.Map;

public record ActivityCommand(
        String organizationId,
        String runId,
        String planId,
        PlanContext context,
        OperationBindingSnapshot binding,
        PlanStep step,
        Map<String, Object> resolvedInput,
        String idempotencyKey
) implements Serializable {
    public ActivityCommand(String runId, String planId, PlanContext context, OperationBindingSnapshot binding, PlanStep step, Map<String, Object> resolvedInput) {
        this(com.acme.data360agent.execution.PlanStore.DEFAULT_ORGANIZATION_ID, runId, planId, context, binding, step, resolvedInput, "");
    }

    public ActivityCommand(String organizationId, String runId, String planId, PlanContext context, OperationBindingSnapshot binding, PlanStep step, Map<String, Object> resolvedInput) {
        this(organizationId, runId, planId, context, binding, step, resolvedInput, "");
    }

    public ActivityCommand {
        organizationId = organizationId == null || organizationId.isBlank() ? com.acme.data360agent.execution.PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
        resolvedInput = resolvedInput == null ? Map.of() : Map.copyOf(resolvedInput);
        idempotencyKey = idempotencyKey == null ? "" : idempotencyKey;
    }
}
