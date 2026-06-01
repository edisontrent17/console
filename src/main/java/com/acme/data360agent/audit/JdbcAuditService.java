package com.acme.data360agent.audit;

import com.acme.data360agent.state.JsonStateCodec;
import com.acme.data360agent.support.Ids;
import com.acme.data360agent.support.SensitiveData;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "jdbc", matchIfMissing = true)
public class JdbcAuditService implements AuditService {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final JdbcTemplate jdbc;
    private final JsonStateCodec codec;

    public JdbcAuditService(JdbcTemplate jdbc, JsonStateCodec codec) {
        this.jdbc = jdbc;
        this.codec = codec;
    }

    @Override
    public void event(String organizationId, String runId, String planId, String stepId, String eventType, Map<String, Object> detail) {
        jdbc.update("""
                INSERT INTO audit_events (organization_id, event_id, run_id, plan_id, step_id, event_type, detail_json)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, normalize(organizationId), Ids.prefixed("evt"), runId, planId, stepId, eventType, codec.write(detail == null ? Map.of() : SensitiveData.redactMap(detail)));
    }

    @Override
    public void approval(String organizationId, String runId, String stepId, String approvedBy, String decision, Map<String, Object> payload) {
        jdbc.update("""
                INSERT INTO approval_records (organization_id, approval_id, run_id, step_id, approved_by, decision, payload_json)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, normalize(organizationId), Ids.prefixed("appr"), runId, stepId, approvedBy, decision, codec.write(payload == null ? Map.of() : SensitiveData.redactMap(payload)));
    }

    @Override
    public List<AuditEvent> events(String organizationId, String runId) {
        return jdbc.query("""
                SELECT * FROM audit_events
                WHERE organization_id = ? AND run_id = ?
                ORDER BY created_at ASC
                """, (rs, rowNum) -> new AuditEvent(
                rs.getString("event_id"),
                rs.getString("run_id"),
                rs.getString("plan_id"),
                rs.getString("step_id"),
                rs.getString("event_type"),
                codec.read(rs.getString("detail_json"), MAP),
                codec.instant(rs.getTimestamp("created_at"))
        ), normalize(organizationId), runId);
    }

    @Override
    public List<ApprovalRecord> approvals(String organizationId, String runId) {
        return jdbc.query("""
                SELECT * FROM approval_records
                WHERE organization_id = ? AND run_id = ?
                ORDER BY created_at ASC
                """, (rs, rowNum) -> new ApprovalRecord(
                rs.getString("approval_id"),
                rs.getString("run_id"),
                rs.getString("step_id"),
                rs.getString("approved_by"),
                rs.getString("decision"),
                codec.read(rs.getString("payload_json"), MAP),
                codec.instant(rs.getTimestamp("created_at"))
        ), normalize(organizationId), runId);
    }

    private String normalize(String organizationId) {
        return organizationId == null || organizationId.isBlank() ? com.acme.data360agent.execution.PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
    }
}
