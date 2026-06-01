package com.acme.data360agent.audit;

import com.acme.data360agent.support.Ids;
import com.acme.data360agent.execution.PlanStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "memory")
public class InMemoryAuditService implements AuditService {
    private final CopyOnWriteArrayList<ScopedAuditEvent> events = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ScopedApprovalRecord> approvals = new CopyOnWriteArrayList<>();

    @Override
    public void event(String organizationId, String runId, String planId, String stepId, String eventType, Map<String, Object> detail) {
        events.add(new ScopedAuditEvent(normalize(organizationId), new AuditEvent(Ids.prefixed("evt"), runId, planId, stepId, eventType, detail, Instant.now())));
    }

    @Override
    public void approval(String organizationId, String runId, String stepId, String approvedBy, String decision, Map<String, Object> payload) {
        approvals.add(new ScopedApprovalRecord(normalize(organizationId), new ApprovalRecord(Ids.prefixed("appr"), runId, stepId, approvedBy, decision, payload, Instant.now())));
    }

    @Override
    public List<AuditEvent> events(String organizationId, String runId) {
        return events.stream()
                .filter(event -> event.organizationId().equals(normalize(organizationId)))
                .map(ScopedAuditEvent::event)
                .filter(event -> event.runId() != null && event.runId().equals(runId))
                .toList();
    }

    @Override
    public List<ApprovalRecord> approvals(String organizationId, String runId) {
        return approvals.stream()
                .filter(record -> record.organizationId().equals(normalize(organizationId)))
                .map(ScopedApprovalRecord::record)
                .filter(record -> record.runId().equals(runId))
                .toList();
    }

    private String normalize(String organizationId) {
        return organizationId == null || organizationId.isBlank() ? PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
    }

    private record ScopedAuditEvent(String organizationId, AuditEvent event) {
    }

    private record ScopedApprovalRecord(String organizationId, ApprovalRecord record) {
    }
}
