package com.acme.data360agent.execution;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationBindingResolver;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.planner.ApprovedExecutablePlan;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PlanRun {
    private final String id;
    private final String organizationId;
    private final ApprovedExecutablePlan approvedPlan;
    private final PlanSpec plan;
    private final Instant createdAt;
    private RunStatus status = RunStatus.RUNNING;
    private final List<StepRun> steps;
    private final Set<String> approvedSteps = new LinkedHashSet<>();
    private List<OperationBindingSnapshot> operationBindings;

    public PlanRun(String id, ApprovedExecutablePlan approvedPlan) {
        this(id, PlanStore.DEFAULT_ORGANIZATION_ID, approvedPlan, Instant.now());
    }

    public PlanRun(String id, ApprovedExecutablePlan approvedPlan, Instant createdAt) {
        this(id, PlanStore.DEFAULT_ORGANIZATION_ID, approvedPlan, createdAt);
    }

    public PlanRun(String id, String organizationId, ApprovedExecutablePlan approvedPlan) {
        this(id, organizationId, approvedPlan, Instant.now());
    }

    public PlanRun(String id, String organizationId, ApprovedExecutablePlan approvedPlan, Instant createdAt) {
        this.id = id;
        this.organizationId = organizationId == null || organizationId.isBlank() ? PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
        this.approvedPlan = approvedPlan;
        this.plan = approvedPlan.plan();
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.steps = this.plan.steps().stream().map(step -> new StepRun(step.id())).toList();
        this.operationBindings = approvedPlan.operationBindings().isEmpty()
                ? defaultBindings(this.plan)
                : approvedPlan.operationBindings();
    }

    public String getId() {
        return id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public PlanSpec getPlan() {
        return plan;
    }

    public ApprovedExecutablePlan getApprovedPlan() {
        return approvedPlan;
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

    public OperationBindingSnapshot bindingForStep(PlanStep step) {
        return bindingForResource(OperationBindingResolver.resourceFor(step));
    }

    private static List<OperationBindingSnapshot> defaultBindings(PlanSpec plan) {
        return OperationBindingResolver.snapshotsFor(new OperationRegistry(), plan);
    }
}
