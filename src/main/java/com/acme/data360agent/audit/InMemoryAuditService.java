package com.acme.data360agent.audit;

import com.acme.data360agent.support.Ids;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "memory")
public class InMemoryAuditService implements AuditService {
    private final CopyOnWriteArrayList<AuditEvent> events = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ApprovalRecord> approvals = new CopyOnWriteArrayList<>();

    @Override
    public void event(String runId, String planId, String stepId, String eventType, Map<String, Object> detail) {
        events.add(new AuditEvent(Ids.prefixed("evt"), runId, planId, stepId, eventType, detail, Instant.now()));
    }

    @Override
    public void approval(String runId, String stepId, String approvedBy, String decision, Map<String, Object> payload) {
        approvals.add(new ApprovalRecord(Ids.prefixed("appr"), runId, stepId, approvedBy, decision, payload, Instant.now()));
    }

    @Override
    public List<AuditEvent> events(String runId) {
        return events.stream()
                .filter(event -> event.runId() != null && event.runId().equals(runId))
                .toList();
    }

    @Override
    public List<ApprovalRecord> approvals(String runId) {
        return approvals.stream()
                .filter(record -> record.runId().equals(runId))
                .toList();
    }

}
