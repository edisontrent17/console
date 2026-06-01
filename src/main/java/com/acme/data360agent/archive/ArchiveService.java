package com.acme.data360agent.archive;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.execution.PlanRunSupport;
import com.acme.data360agent.execution.PlanStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ArchiveService {
    private final PlanStore store;
    private final AuditService audit;

    public ArchiveService(PlanStore store, AuditService audit) {
        this.store = store;
        this.audit = audit;
    }

    public PlanSpecArchive planSpec(String planId) {
        return planSpec(com.acme.data360agent.execution.PlanStore.DEFAULT_ORGANIZATION_ID, planId);
    }

    public PlanSpecArchive planSpec(String organizationId, String planId) {
        var draft = store.draft(organizationId, planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));
        return PlanSpecArchive.from(draft);
    }

    public ExecutionLogArchive executionLog(String runId) {
        return executionLog(com.acme.data360agent.execution.PlanStore.DEFAULT_ORGANIZATION_ID, runId);
    }

    public ExecutionLogArchive executionLog(String organizationId, String runId) {
        var run = store.run(organizationId, runId)
                .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        var toolCalls = run.getPlan().steps().stream()
                .map(step -> ToolCallArchive.from(run, step, PlanRunSupport.stepRun(run, step.id())))
                .toList();
        return new ExecutionLogArchive(
                "execution-log",
                ArchiveVersions.SCHEMA_VERSION,
                ArchiveVersions.REDACTION_VERSION,
                Instant.now(),
                ExecutionRunArchive.from(run),
                audit.events(organizationId, runId),
                audit.approvals(organizationId, runId),
                toolCalls
        );
    }
}
