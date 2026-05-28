package com.acme.data360agent.plan;

import java.io.Serializable;

public record ValidationIssue(
        String severity,
        String stepId,
        String message
) implements Serializable {
    public static ValidationIssue error(String stepId, String message) {
        return new ValidationIssue("error", stepId, message);
    }

    public static ValidationIssue warning(String stepId, String message) {
        return new ValidationIssue("warning", stepId, message);
    }
}
