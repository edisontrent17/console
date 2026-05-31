package com.acme.data360agent.archive;

import com.acme.data360agent.planner.PlanDraft;

import java.io.Serializable;
import java.time.Instant;

public record PlanSpecArchive(
        String archiveType,
        Instant exportedAt,
        PlanDraft draft
) implements Serializable {
    public static PlanSpecArchive from(PlanDraft draft) {
        return new PlanSpecArchive("planspec", Instant.now(), draft);
    }
}
