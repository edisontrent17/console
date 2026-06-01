package com.acme.data360agent.execution;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.support.SensitiveData;

import java.time.Instant;
import java.util.Map;

public class StepRun {
    private final String stepId;
    private StepStatus status = StepStatus.PENDING;
    private OperationBindingSnapshot binding;
    private Map<String, Object> resolvedInput = Map.of();
    private Map<String, Object> output = Map.of();
    private Map<String, Object> raw = Map.of();
    private String error;
    private Instant startedAt;
    private Instant finishedAt;

    public StepRun(String stepId) {
        this.stepId = stepId;
    }

    public String getStepId() {
        return stepId;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
    }

    public OperationBindingSnapshot getBinding() {
        return binding;
    }

    public void setBinding(OperationBindingSnapshot binding) {
        this.binding = binding;
    }

    public Map<String, Object> getResolvedInput() {
        return resolvedInput;
    }

    public void setResolvedInput(Map<String, Object> resolvedInput) {
        this.resolvedInput = resolvedInput == null ? Map.of() : SensitiveData.redactMap(resolvedInput);
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Object> output) {
        this.output = output == null ? Map.of() : Map.copyOf(output);
    }

    public Map<String, Object> getRaw() {
        return raw;
    }

    public void setRaw(Map<String, Object> raw) {
        this.raw = raw == null ? Map.of() : SensitiveData.redactMap(raw);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = SensitiveData.redactText(error);
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }
}
