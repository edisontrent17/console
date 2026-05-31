package com.acme.data360agent.archive;

import com.acme.data360agent.audit.ApprovalRecord;
import com.acme.data360agent.audit.AuditEvent;
import com.acme.data360agent.execution.PlanRun;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record ExecutionLogArchive(
        String archiveType,
        Instant exportedAt,
        PlanRun run,
        List<AuditEvent> auditEvents,
        List<ApprovalRecord> approvals,
        List<ToolCallArchive> toolCalls
) implements Serializable {
    public ExecutionLogArchive {
        auditEvents = auditEvents == null ? List.of() : List.copyOf(auditEvents);
        approvals = approvals == null ? List.of() : List.copyOf(approvals);
        toolCalls = toolCalls == null ? List.of() : List.copyOf(toolCalls);
    }
}
