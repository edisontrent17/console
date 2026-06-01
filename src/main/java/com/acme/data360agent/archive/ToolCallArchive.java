package com.acme.data360agent.archive;

import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.support.SensitiveData;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public record ToolCallArchive(
        String stepId,
        String status,
        String action,
        String phase,
        String resource,
        boolean needsApproval,
        Instant startedAt,
        Instant finishedAt,
        OperationBindingSnapshot binding,
        Map<String, Object> declaredInput,
        Map<String, Object> inputBindings,
        Map<String, Object> resolvedInput,
        Map<String, Object> output,
        Map<String, Object> raw,
        String error
) implements Serializable {
    public ToolCallArchive {
        declaredInput = declaredInput == null ? Map.of() : SensitiveData.redactMap(declaredInput);
        inputBindings = inputBindings == null ? Map.of() : SensitiveData.redactMap(inputBindings);
        resolvedInput = resolvedInput == null ? Map.of() : SensitiveData.redactMap(resolvedInput);
        output = output == null ? Map.of() : SensitiveData.redactMap(output);
        raw = raw == null ? Map.of() : SensitiveData.redactMap(raw);
        error = SensitiveData.redactText(error);
    }

    public static ToolCallArchive from(PlanRun run, PlanStep planStep, com.acme.data360agent.execution.StepRun stepRun) {
        return new ToolCallArchive(
                planStep.id(),
                stepRun.getStatus().name(),
                planStep.action().value(),
                planStep.phase().value(),
                planStep.action().resource(),
                planStep.needsApproval(),
                stepRun.getStartedAt(),
                stepRun.getFinishedAt(),
                stepRun.getBinding() == null ? run.bindingForResource(planStep.action().resource()) : stepRun.getBinding(),
                planStep.input(),
                inputBindings(planStep),
                stepRun.getResolvedInput(),
                stepRun.getOutput(),
                stepRun.getRaw(),
                stepRun.getError()
        );
    }

    private static Map<String, Object> inputBindings(PlanStep planStep) {
        var bindings = new LinkedHashMap<String, Object>();
        planStep.inputBindings().forEach(bindings::put);
        return Map.copyOf(bindings);
    }
}
