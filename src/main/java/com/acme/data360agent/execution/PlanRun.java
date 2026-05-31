package com.acme.data360agent.execution;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanSpec;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlanRun {
    private final String id;
    private final PlanSpec plan;
    private final Instant createdAt;
    private RunStatus status = RunStatus.RUNNING;
    private final List<StepRun> steps;
    private final Set<String> approvedSteps = new LinkedHashSet<>();
    private List<OperationBindingSnapshot> operationBindings;

    public PlanRun(String id, PlanSpec plan) {
        this(id, plan, Instant.now());
    }

    public PlanRun(String id, PlanSpec plan, Instant createdAt) {
        this.id = id;
        this.plan = plan;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.steps = plan.steps().stream().map(step -> new StepRun(step.id())).toList();
        this.operationBindings = defaultBindings(plan);
    }

    public String getId() {
        return id;
    }

    public PlanSpec getPlan() {
        return plan;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public RunStatus getStatus() {
        return status;
    }

    public void setStatus(RunStatus status) {
        this.status = status;
    }

    public List<StepRun> getSteps() {
        return steps;
    }

    public Set<String> getApprovedSteps() {
        return approvedSteps;
    }

    public List<OperationBindingSnapshot> getOperationBindings() {
        return operationBindings;
    }

    public void setOperationBindings(List<OperationBindingSnapshot> operationBindings) {
        this.operationBindings = operationBindings == null || operationBindings.isEmpty()
                ? defaultBindings(plan)
                : List.copyOf(operationBindings);
    }

    public OperationBindingSnapshot bindingForResource(String resource) {
        return operationBindings.stream()
                .filter(binding -> binding.resource().equals(resource))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No operation binding snapshot for resource: " + resource));
    }

    private static List<OperationBindingSnapshot> defaultBindings(PlanSpec plan) {
        var registry = new OperationRegistry();
        var bindings = new LinkedHashMap<String, OperationBindingSnapshot>();
        for (var step : plan.steps()) {
            bindings.putIfAbsent(step.action().resource(), registry.bindingFor(step.action()));
        }
        return List.copyOf(bindings.values());
    }
}
