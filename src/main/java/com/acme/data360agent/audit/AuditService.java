package com.acme.data360agent.audit;

import java.util.List;
import java.util.Map;

public interface AuditService {
    void event(String runId, String planId, String stepId, String eventType, Map<String, Object> detail);

    void approval(String runId, String stepId, String approvedBy, String decision, Map<String, Object> payload);

    List<AuditEvent> events(String runId);

    List<ApprovalRecord> approvals(String runId);
}
