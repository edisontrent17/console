package com.acme.data360agent.library;

import com.acme.data360agent.plan.PlanStep;

import java.util.List;

public record SolutionTemplate(
        String id,
        String title,
        String industry,
        String complexity,
        String summary,
        String outcome,
        List<String> clouds,
        List<String> architectureNotes,
        List<PlanStep> steps
) {
}
