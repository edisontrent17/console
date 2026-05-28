package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanSpec;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PlanRun {
    private final String id;
    private final PlanSpec plan;
    private final Instant createdAt = Instant.now();
    private RunStatus status = RunStatus.RUNNING;
    private final List<StepRun> steps;
    private final Set<String> approvedSteps = new LinkedHashSet<>();

    public PlanRun(String id, PlanSpec plan) {
        this.id = id;
        this.plan = plan;
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
