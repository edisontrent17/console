package com.acme.data360agent.audit;

import com.acme.data360agent.execution.PlanStore;

import java.util.List;
import java.util.Map;

public interface AuditService {
    void event(String organizationId, String runId, String planId, String stepId, String eventType, Map<String, Object> detail);

    default void event(String runId, String planId, String stepId, String eventType, Map<String, Object> detail) {
        event(PlanStore.DEFAULT_ORGANIZATION_ID, runId, planId, stepId, eventType, detail);
    }

    void approval(String organizationId, String runId, String stepId, String approvedBy, String decision, Map<String, Object> payload);

    default void approval(String runId, String stepId, String approvedBy, String decision, Map<String, Object> payload) {
        approval(PlanStore.DEFAULT_ORGANIZATION_ID, runId, stepId, approvedBy, decision, payload);
    }

    List<AuditEvent> events(String organizationId, String runId);

    default List<AuditEvent> events(String runId) {
        return events(PlanStore.DEFAULT_ORGANIZATION_ID, runId);
    }

    List<ApprovalRecord> approvals(String organizationId, String runId);

    default List<ApprovalRecord> approvals(String runId) {
        return approvals(PlanStore.DEFAULT_ORGANIZATION_ID, runId);
    }
}
