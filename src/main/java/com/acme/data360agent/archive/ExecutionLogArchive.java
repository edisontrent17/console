package com.acme.data360agent.archive;

import com.acme.data360agent.audit.ApprovalRecord;
import com.acme.data360agent.audit.AuditEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record ExecutionLogArchive(
        String archiveType,
        String archiveSchemaVersion,
        String redactionVersion,
        Instant exportedAt,
        ExecutionRunArchive run,
        List<AuditEvent> auditEvents,
        List<ApprovalRecord> approvals,
        List<ToolCallArchive> toolCalls
) implements Serializable {
    public ExecutionLogArchive {
        archiveSchemaVersion = archiveSchemaVersion == null || archiveSchemaVersion.isBlank() ? ArchiveVersions.SCHEMA_VERSION : archiveSchemaVersion;
        redactionVersion = redactionVersion == null || redactionVersion.isBlank() ? ArchiveVersions.REDACTION_VERSION : redactionVersion;
        auditEvents = auditEvents == null ? List.of() : List.copyOf(auditEvents);
        approvals = approvals == null ? List.of() : List.copyOf(approvals);
        toolCalls = toolCalls == null ? List.of() : List.copyOf(toolCalls);
    }
}
