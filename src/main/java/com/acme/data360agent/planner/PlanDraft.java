package com.acme.data360agent.planner;

import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanValidationResult;

import java.io.Serializable;
import java.util.List;

public record PlanDraft(
        PlanSpec plan,
        PlanValidationResult validation,
        List<String> graphStages
) implements Serializable {
}
