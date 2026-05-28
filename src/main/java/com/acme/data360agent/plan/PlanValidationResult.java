package com.acme.data360agent.plan;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record PlanValidationResult(List<ValidationIssue> issues) implements Serializable {
    @JsonProperty("ok")
    public boolean ok() {
        return issues.stream().noneMatch(issue -> "error".equals(issue.severity()));
    }
}
