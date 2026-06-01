package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanStep;

import java.util.Map;

public final class PlanRunSupport {
    private PlanRunSupport() {
    }

    public static StepRun stepRun(PlanRun run, String stepId) {
        return run.getSteps().stream()
                .filter(step -> step.getStepId().equals(stepId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown step: " + stepId));
    }

    public static PlanStep planStep(PlanRun run, String stepId) {
        return run.getPlan().steps().stream()
                .filter(step -> step.id().equals(stepId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown step: " + stepId));
    }

    public static Map<String, Object> outputForStep(PlanRun run, String stepId) {
        return stepRun(run, stepId).getOutput();
    }

    public static boolean terminal(RunStatus status) {
        return status == RunStatus.SUCCEEDED || status == RunStatus.FAILED || status == RunStatus.CANCELED;
    }

    public static boolean terminal(StepStatus status) {
        return status == StepStatus.SUCCEEDED || status == StepStatus.FAILED || status == StepStatus.SKIPPED || status == StepStatus.CANCELED;
    }
}
