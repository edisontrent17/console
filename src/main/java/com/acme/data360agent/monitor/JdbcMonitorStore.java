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
                WHERE monitor_id = ?
                """, definition.status().name(), definition.metric(), definition.cadence(), thresholdJson, codec.timestamp(definition.lastRunAt()),
                codec.timestamp(definition.nextRunAt()), codec.timestamp(definition.leaseUntil()), definition.id());
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO monitor_definitions
                    (monitor_id, plan_id, run_id, step_id, metric, cadence, threshold_json, status, created_at, last_run_at, next_run_at, lease_until)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    definition.id(), definition.planId(), definition.runId(), definition.stepId(), definition.metric(), definition.cadence(),
                    thresholdJson, definition.status().name(), codec.timestamp(definition.createdAt()), codec.timestamp(definition.lastRunAt()),
                    codec.timestamp(definition.nextRunAt()), codec.timestamp(definition.leaseUntil()));
        }
        return definition;
    }

    @Override
    public Optional<MonitorDefinition> definition(String id) {
        return jdbc.query("SELECT * FROM monitor_definitions WHERE monitor_id = ?", (rs, rowNum) -> definition(rs), id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MonitorDefinition> definitionFor(String runId, String stepId) {
        return jdbc.query("SELECT * FROM monitor_definitions WHERE run_id = ? AND step_id = ?", (rs, rowNum) -> definition(rs), runId, stepId)
                .stream()
                .findFirst();
    }

    @Override
    public Collection<MonitorDefinition> definitions() {
        return jdbc.query("SELECT * FROM monitor_definitions ORDER BY created_at DESC", (rs, rowNum) -> definition(rs));
    }

    @Override
    public List<MonitorDefinition> claimDue(Instant now, Instant leaseUntil, int limit) {
        return transaction.execute(status -> {
            var candidates = jdbc.query("""
                    SELECT * FROM monitor_definitions
                    WHERE status <> 'PAUSED'
                      AND (next_run_at IS NULL OR next_run_at <= ?)
                      AND (lease_until IS NULL OR lease_until <= ?)
                    ORDER BY COALESCE(next_run_at, created_at) ASC
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
                WHERE monitor_run_id = ?
                """, run.observedValue(), run.thresholdBreached(), run.status().name(), run.recommendation(), rawJson, run.id());
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO monitor_runs
                    (monitor_run_id, monitor_id, observed_value, threshold_breached, status, recommendation, raw_json, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    run.id(), run.monitorId(), run.observedValue(), run.thresholdBreached(), run.status().name(), run.recommendation(),
                    rawJson, codec.timestamp(run.createdAt()));
        }
        return run;
    }

    @Override
    public List<MonitorRun> runsFor(String monitorId) {
        return jdbc.query("SELECT * FROM monitor_runs WHERE monitor_id = ? ORDER BY created_at DESC", (rs, rowNum) -> run(rs), monitorId);
    }

    @Override
    public MonitorRecommendation saveRecommendation(MonitorRecommendation recommendation) {
        var thresholdJson = codec.write(recommendation.threshold());
        var updated = jdbc.update("""
                UPDATE monitor_recommendations
                SET status = ?, reviewed_at = ?, summary = ?, observed_value = ?, threshold_json = ?
                WHERE recommendation_id = ?
                """, recommendation.status().name(), codec.timestamp(recommendation.reviewedAt()), recommendation.summary(), recommendation.observedValue(),
                thresholdJson, recommendation.id());
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO monitor_recommendations
                    (recommendation_id, monitor_id, monitor_run_id, metric, observed_value, threshold_json, summary, status, created_at, reviewed_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    recommendation.id(), recommendation.monitorId(), recommendation.monitorRunId(), recommendation.metric(), recommendation.observedValue(),
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
    public Optional<MonitorRecommendation> recommendation(String id) {
        return jdbc.query("SELECT * FROM monitor_recommendations WHERE recommendation_id = ?", (rs, rowNum) -> recommendation(rs), id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MonitorRecommendation> pendingRecommendationFor(String monitorId) {
        return jdbc.query("""
                        SELECT * FROM monitor_recommendations
                        WHERE monitor_id = ? AND status = 'PENDING_APPROVAL'
                        ORDER BY created_at DESC
                        LIMIT 1
                        """, (rs, rowNum) -> recommendation(rs), monitorId)
                .stream()
                .findFirst();
    }

    @Override
    public List<MonitorRecommendation> recommendations() {
        return jdbc.query("SELECT * FROM monitor_recommendations ORDER BY created_at DESC", (rs, rowNum) -> recommendation(rs));
    }

    private MonitorDefinition definition(ResultSet rs) {
        try {
            return new MonitorDefinition(
                    rs.getString("monitor_id"),
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
