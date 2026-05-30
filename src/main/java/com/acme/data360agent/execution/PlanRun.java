package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanSpec;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PlanRun {
    private final String id;
    private final PlanSpec plan;
    private final Instant createdAt;
    private RunStatus status = RunStatus.RUNNING;
    private final List<StepRun> steps;
    private final Set<String> approvedSteps = new LinkedHashSet<>();

    public PlanRun(String id, PlanSpec plan) {
        this(id, plan, Instant.now());
    }

    public PlanRun(String id, PlanSpec plan, Instant createdAt) {
        this.id = id;
        this.plan = plan;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.steps = plan.steps().stream().map(step -> new StepRun(step.id())).toList();
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
}
