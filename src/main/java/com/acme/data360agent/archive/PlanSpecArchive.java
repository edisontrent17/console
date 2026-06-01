package com.acme.data360agent.archive;

import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.planner.PlanArtifact;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record PlanSpecArchive(
        String archiveType,
        String archiveSchemaVersion,
        String redactionVersion,
        Instant exportedAt,
        ExecutionRunArchive.Plan plan,
        PlanValidationResult validation,
        List<String> graphStages,
        List<Map<String, Object>> taskHashes,
        List<PlanArtifact> artifacts
) implements Serializable {
    public PlanSpecArchive {
        archiveSchemaVersion = archiveSchemaVersion == null || archiveSchemaVersion.isBlank() ? ArchiveVersions.SCHEMA_VERSION : archiveSchemaVersion;
        redactionVersion = redactionVersion == null || redactionVersion.isBlank() ? ArchiveVersions.REDACTION_VERSION : redactionVersion;
        graphStages = graphStages == null ? List.of() : List.copyOf(graphStages);
        taskHashes = taskHashes == null ? List.of() : List.copyOf(taskHashes);
        artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
    }

    public static PlanSpecArchive from(PlanDraft draft) {
        return new PlanSpecArchive(
                "planspec",
                ArchiveVersions.SCHEMA_VERSION,
                ArchiveVersions.REDACTION_VERSION,
                Instant.now(),
                ExecutionRunArchive.Plan.from(draft.plan()),
                draft.validation(),
                draft.graphStages(),
                draft.operationBindings().stream().map(ExecutionRunArchive::taskHash).toList(),
                ExecutionRunArchive.redactedArtifacts(draft.artifacts())
        );
    }
}
