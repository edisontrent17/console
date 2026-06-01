package com.acme.data360agent.execution;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationCatalogHash;
import com.acme.data360agent.plan.PlanStep;

import java.util.Map;

public final class ExecutionIdempotency {
    private ExecutionIdempotency() {
    }

    public static String forStep(String organizationId,
                                 String runId,
                                 String planId,
                                 PlanStep step,
                                 OperationBindingSnapshot binding,
                                 Map<String, Object> resolvedInput) {
        return "exec_" + OperationCatalogHash.sha256Hex(Map.of(
                "organizationId", organizationId == null ? PlanStore.DEFAULT_ORGANIZATION_ID : organizationId,
                "runId", runId == null ? "" : runId,
                "planId", planId == null ? "" : planId,
                "stepId", step == null ? "" : step.id(),
                "resource", binding == null ? "" : binding.resource(),
                "schemaHash", binding == null ? "" : binding.schemaHash(),
                "toolSchemaHash", binding == null ? "" : binding.toolSchemaHash(),
                "connectorDefinitionHash", binding == null ? "" : binding.connectorDefinitionHash(),
                "input", resolvedInput == null ? Map.of() : resolvedInput
        ));
    }
}
