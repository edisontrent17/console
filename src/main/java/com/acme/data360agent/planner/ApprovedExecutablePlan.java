package com.acme.data360agent.planner;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationCatalogHash;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.support.Ids;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ApprovedExecutablePlan(
        String artifactType,
        String artifactId,
        String planHash,
        Instant approvedAt,
        String approvedBy,
        PlanSpec plan,
        PlanValidationResult validation,
        List<String> graphStages,
        List<OperationBindingSnapshot> operationBindings,
        List<PlanArtifact> artifacts
) implements Serializable {
    public static final String ARTIFACT_TYPE = "approved-executable-plan";

    public ApprovedExecutablePlan {
        if (plan == null) {
            throw new IllegalArgumentException("ApprovedExecutablePlan requires a PlanSpec.");
        }
        if (validation == null || !validation.ok()) {
            throw new IllegalArgumentException("ApprovedExecutablePlan requires a valid PlanSpec.");
        }
        artifactType = artifactType == null || artifactType.isBlank() ? ARTIFACT_TYPE : artifactType;
        artifactId = artifactId == null || artifactId.isBlank() ? Ids.prefixed("aplan") : artifactId;
        approvedAt = approvedAt == null ? Instant.now() : approvedAt;
        approvedBy = approvedBy == null || approvedBy.isBlank() ? "system" : approvedBy;
        graphStages = graphStages == null ? List.of() : List.copyOf(graphStages);
        operationBindings = operationBindings == null || operationBindings.isEmpty()
                ? new PlanDraft(plan, validation, graphStages).operationBindings()
                : List.copyOf(operationBindings);
        artifacts = artifacts == null || artifacts.isEmpty()
                ? List.of(PlanArtifact.planDag(plan, validation))
                : List.copyOf(artifacts);
        var computedHash = hash(plan, validation, graphStages, operationBindings, artifacts);
        if (planHash != null && !planHash.isBlank() && !computedHash.equals(planHash)) {
            throw new IllegalArgumentException("ApprovedExecutablePlan planHash does not match artifact content.");
        }
        planHash = computedHash;
    }

    public static ApprovedExecutablePlan approve(PlanDraft draft, PlanValidationResult validation, String approvedBy) {
        if (draft == null) {
            throw new IllegalArgumentException("Draft plan is required.");
        }
        var effectiveValidation = validation == null ? draft.validation() : validation;
        return new ApprovedExecutablePlan(
                ARTIFACT_TYPE,
                null,
                null,
                Instant.now(),
                approvedBy,
                draft.plan(),
                effectiveValidation,
                appendStage(draft.graphStages(), "approve_executable_plan"),
                draft.operationBindings(),
                List.of(PlanArtifact.planDag(draft.plan(), effectiveValidation, draft.operationBindings()))
        );
    }

    public static ApprovedExecutablePlan legacy(PlanSpec plan, List<OperationBindingSnapshot> operationBindings) {
        var validation = new PlanValidationResult(List.of());
        var draft = new PlanDraft(plan, validation, List.of("legacy_plan_executor"), operationBindings);
        return approve(draft, validation, "system");
    }

    private static List<String> appendStage(List<String> stages, String stage) {
        var safeStages = stages == null ? List.<String>of() : stages;
        if (safeStages.contains(stage)) {
            return List.copyOf(safeStages);
        }
        var updated = new java.util.ArrayList<>(safeStages);
        updated.add(stage);
        return List.copyOf(updated);
    }

    private static String hash(PlanSpec plan, PlanValidationResult validation, List<String> graphStages, List<OperationBindingSnapshot> operationBindings, List<PlanArtifact> artifacts) {
        return "sha256:" + OperationCatalogHash.sha256Hex(Map.of(
                "plan", plan,
                "validation", validation,
                "graphStages", graphStages,
                "operationBindings", operationBindings,
                "artifacts", artifacts
        ));
    }
}
