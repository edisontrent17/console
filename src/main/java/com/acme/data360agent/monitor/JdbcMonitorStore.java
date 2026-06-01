package com.acme.data360agent.monitor;

import com.acme.data360agent.state.JsonStateCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.util.Collection;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "jdbc", matchIfMissing = true)
public class JdbcMonitorStore implements MonitorStore {
    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };
    private static final String DEFINITION_SELECT = """
            SELECT md.*
            FROM monitor_definitions md
            """;
    private static final String RECOMMENDATION_SELECT = """
            SELECT rec.*
            FROM monitor_recommendations rec
            """;

    private final JdbcTemplate jdbc;
    private final JsonStateCodec codec;
    private final TransactionTemplate transaction;

    public JdbcMonitorStore(JdbcTemplate jdbc, JsonStateCodec codec, org.springframework.transaction.PlatformTransactionManager transactionManager) {
        this.jdbc = jdbc;
        this.codec = codec;
        this.transaction = new TransactionTemplate(transactionManager);
    }

    @Override
    public MonitorDefinition save(MonitorDefinition definition) {
        var thresholdJson = codec.write(definition.threshold());
        var updated = jdbc.update("""
                UPDATE monitor_definitions
                SET status = ?, metric = ?, cadence = ?, threshold_json = ?, last_run_at = ?, next_run_at = ?, lease_until = ?
                WHERE monitor_id = ? AND organization_id = ?
                """, definition.status().name(), definition.metric(), definition.cadence(), thresholdJson, codec.timestamp(definition.lastRunAt()),
                codec.timestamp(definition.nextRunAt()), codec.timestamp(definition.leaseUntil()), definition.id(), definition.organizationId());
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO monitor_definitions
                    (monitor_id, organization_id, plan_id, run_id, step_id, metric, cadence, threshold_json, status, created_at, last_run_at, next_run_at, lease_until)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    definition.id(), definition.organizationId(), definition.planId(), definition.runId(), definition.stepId(), definition.metric(), definition.cadence(),
                    thresholdJson, definition.status().name(), codec.timestamp(definition.createdAt()), codec.timestamp(definition.lastRunAt()),
                    codec.timestamp(definition.nextRunAt()), codec.timestamp(definition.leaseUntil()));
        }
        return definition;
    }

    @Override
    public Optional<MonitorDefinition> definition(String id) {
        return jdbc.query(DEFINITION_SELECT + "WHERE md.monitor_id = ?", (rs, rowNum) -> definition(rs), id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MonitorDefinition> definition(String organizationId, String id) {
        return jdbc.query(DEFINITION_SELECT + "WHERE md.organization_id = ? AND md.monitor_id = ?", (rs, rowNum) -> definition(rs),
                        MonitorStore.normalizeOrganizationId(organizationId), id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MonitorDefinition> definitionFor(String runId, String stepId) {
        return jdbc.query(DEFINITION_SELECT + "WHERE md.run_id = ? AND md.step_id = ?", (rs, rowNum) -> definition(rs), runId, stepId)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MonitorDefinition> definitionFor(String organizationId, String runId, String stepId) {
        return jdbc.query(DEFINITION_SELECT + "WHERE md.organization_id = ? AND md.run_id = ? AND md.step_id = ?", (rs, rowNum) -> definition(rs),
                        MonitorStore.normalizeOrganizationId(organizationId), runId, stepId)
                .stream()
                .findFirst();
    }

    @Override
    public Collection<MonitorDefinition> definitions() {
        return jdbc.query(DEFINITION_SELECT + "ORDER BY md.created_at DESC", (rs, rowNum) -> definition(rs));
    }

    @Override
    public Collection<MonitorDefinition> definitions(String organizationId) {
        return jdbc.query(DEFINITION_SELECT + "WHERE md.organization_id = ? ORDER BY md.created_at DESC", (rs, rowNum) -> definition(rs),
                MonitorStore.normalizeOrganizationId(organizationId));
    }

    @Override
    public List<MonitorDefinition> claimDue(Instant now, Instant leaseUntil, int limit) {
        return transaction.execute(status -> {
            var candidates = jdbc.query("""
                    SELECT md.*
                    FROM monitor_definitions md
                    WHERE md.status <> 'PAUSED'
                      AND (md.next_run_at IS NULL OR md.next_run_at <= ?)
                      AND (md.lease_until IS NULL OR md.lease_until <= ?)
                    ORDER BY COALESCE(md.next_run_at, md.created_at) ASC
                    LIMIT ?
                    """, (rs, rowNum) -> definition(rs), codec.timestamp(now), codec.timestamp(now), limit);
            return candidates.stream()
                    .filter(candidate -> jdbc.update("""
                            UPDATE monitor_definitions
                            SET lease_until = ?
                            WHERE monitor_id = ?
                              AND (lease_until IS NULL OR lease_until <= ?)
                            """, codec.timestamp(leaseUntil), candidate.id(), codec.timestamp(now)) == 1)
                    .map(candidate -> candidate.withLease(leaseUntil))
                    .toList();
        });
    }

    @Override
    public MonitorRun saveRun(MonitorRun run) {
        var rawJson = codec.write(run.raw());
        var updated = jdbc.update("""
                UPDATE monitor_runs
                SET observed_value = ?, threshold_breached = ?, status = ?, recommendation = ?, raw_json = ?
                WHERE organization_id = ? AND monitor_run_id = ?
                """, run.observedValue(), run.thresholdBreached(), run.status().name(), run.recommendation(), rawJson, run.organizationId(), run.id());
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO monitor_runs
                    (monitor_run_id, organization_id, monitor_id, observed_value, threshold_breached, status, recommendation, raw_json, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    run.id(), run.organizationId(), run.monitorId(), run.observedValue(), run.thresholdBreached(), run.status().name(), run.recommendation(),
                    rawJson, codec.timestamp(run.createdAt()));
        }
        return run;
    }

    @Override
    public List<MonitorRun> runsFor(String monitorId) {
        return jdbc.query("SELECT * FROM monitor_runs WHERE monitor_id = ? ORDER BY created_at DESC", (rs, rowNum) -> run(rs), monitorId);
    }

    @Override
    public List<MonitorRun> runsFor(String organizationId, String monitorId) {
        return jdbc.query("SELECT * FROM monitor_runs WHERE organization_id = ? AND monitor_id = ? ORDER BY created_at DESC",
                (rs, rowNum) -> run(rs), MonitorStore.normalizeOrganizationId(organizationId), monitorId);
    }

    @Override
    public MonitorRecommendation saveRecommendation(MonitorRecommendation recommendation) {
        var thresholdJson = codec.write(recommendation.threshold());
        var updated = jdbc.update("""
                UPDATE monitor_recommendations
                SET status = ?, reviewed_at = ?, summary = ?, observed_value = ?, threshold_json = ?
                WHERE recommendation_id = ? AND organization_id = ?
                """, recommendation.status().name(), codec.timestamp(recommendation.reviewedAt()), recommendation.summary(), recommendation.observedValue(),
                thresholdJson, recommendation.id(), recommendation.organizationId());
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO monitor_recommendations
                    (recommendation_id, organization_id, monitor_id, monitor_run_id, metric, observed_value, threshold_json, summary, status, created_at, reviewed_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    recommendation.id(), recommendation.organizationId(), recommendation.monitorId(), recommendation.monitorRunId(), recommendation.metric(), recommendation.observedValue(),
                    thresholdJson, recommendation.summary(), recommendation.status().name(), codec.timestamp(recommendation.createdAt()), codec.timestamp(recommendation.reviewedAt()));
        }
        return recommendation;
    }

    @Override
    public boolean reviewRecommendation(String recommendationId, MonitorRecommendationStatus status, Instant reviewedAt) {
        return jdbc.update("""
                UPDATE monitor_recommendations
                SET status = ?, reviewed_at = ?
                WHERE recommendation_id = ?
                  AND status = 'PENDING_APPROVAL'
                """, status.name(), codec.timestamp(reviewedAt), recommendationId) == 1;
    }

    @Override
    public boolean reviewRecommendation(String organizationId, String recommendationId, MonitorRecommendationStatus status, Instant reviewedAt) {
        return jdbc.update("""
                UPDATE monitor_recommendations
                SET status = ?, reviewed_at = ?
                WHERE recommendation_id = ?
                  AND status = 'PENDING_APPROVAL'
                  AND organization_id = ?
                """, status.name(), codec.timestamp(reviewedAt), recommendationId, MonitorStore.normalizeOrganizationId(organizationId)) == 1;
    }

    @Override
    public Optional<MonitorRecommendation> recommendation(String id) {
        return jdbc.query(RECOMMENDATION_SELECT + "WHERE rec.recommendation_id = ?", (rs, rowNum) -> recommendation(rs), id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MonitorRecommendation> recommendation(String organizationId, String id) {
        return jdbc.query(RECOMMENDATION_SELECT + "WHERE rec.organization_id = ? AND rec.recommendation_id = ?", (rs, rowNum) -> recommendation(rs),
                        MonitorStore.normalizeOrganizationId(organizationId), id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MonitorRecommendation> pendingRecommendationFor(String monitorId) {
        return jdbc.query("""
                        SELECT rec.*
                        FROM monitor_recommendations rec
                        WHERE rec.monitor_id = ? AND rec.status = 'PENDING_APPROVAL'
                        ORDER BY rec.created_at DESC
                        LIMIT 1
                        """, (rs, rowNum) -> recommendation(rs), monitorId)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MonitorRecommendation> pendingRecommendationFor(String organizationId, String monitorId) {
        return jdbc.query("""
                        SELECT rec.*
                        FROM monitor_recommendations rec
                        WHERE rec.organization_id = ?
                          AND rec.monitor_id = ?
                          AND rec.status = 'PENDING_APPROVAL'
                        ORDER BY rec.created_at DESC
                        LIMIT 1
                        """, (rs, rowNum) -> recommendation(rs), MonitorStore.normalizeOrganizationId(organizationId), monitorId)
                .stream()
                .findFirst();
    }

    @Override
    public List<MonitorRecommendation> recommendations() {
        return jdbc.query(RECOMMENDATION_SELECT + "ORDER BY rec.created_at DESC", (rs, rowNum) -> recommendation(rs));
    }

    @Override
    public List<MonitorRecommendation> recommendations(String organizationId) {
        return jdbc.query(RECOMMENDATION_SELECT + "WHERE rec.organization_id = ? ORDER BY rec.created_at DESC", (rs, rowNum) -> recommendation(rs),
                MonitorStore.normalizeOrganizationId(organizationId));
    }

    private MonitorDefinition definition(ResultSet rs) {
        try {
            return new MonitorDefinition(
                    rs.getString("monitor_id"),
                    rs.getString("organization_id"),
                    rs.getString("plan_id"),
                    rs.getString("run_id"),
                    rs.getString("step_id"),
                    rs.getString("metric"),
                    rs.getString("cadence"),
                    codec.read(rs.getString("threshold_json"), MAP),
                    MonitorStatus.valueOf(rs.getString("status")),
                    codec.instant(rs.getTimestamp("created_at")),
                    codec.instant(rs.getTimestamp("last_run_at")),
                    codec.instant(rs.getTimestamp("next_run_at")),
                    codec.instant(rs.getTimestamp("lease_until"))
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read monitor definition.", e);
        }
    }

    private MonitorRun run(ResultSet rs) {
        try {
            return new MonitorRun(
                    rs.getString("monitor_run_id"),
                    rs.getString("organization_id"),
                    rs.getString("monitor_id"),
                    rs.getDouble("observed_value"),
                    rs.getBoolean("threshold_breached"),
                    MonitorStatus.valueOf(rs.getString("status")),
                    rs.getString("recommendation"),
                    codec.read(rs.getString("raw_json"), MAP),
                    codec.instant(rs.getTimestamp("created_at"))
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read monitor run.", e);
        }
    }

    private MonitorRecommendation recommendation(ResultSet rs) {
        try {
            return new MonitorRecommendation(
                    rs.getString("recommendation_id"),
                    rs.getString("organization_id"),
                    rs.getString("monitor_id"),
                    rs.getString("monitor_run_id"),
                    rs.getString("metric"),
                    rs.getDouble("observed_value"),
                    codec.read(rs.getString("threshold_json"), MAP),
                    rs.getString("summary"),
                    MonitorRecommendationStatus.valueOf(rs.getString("status")),
                    codec.instant(rs.getTimestamp("created_at")),
                    codec.instant(rs.getTimestamp("reviewed_at"))
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read monitor recommendation.", e);
        }
    }

}
