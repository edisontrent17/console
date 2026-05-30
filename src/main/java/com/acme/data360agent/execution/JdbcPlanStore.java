package com.acme.data360agent.execution;

import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.state.JsonStateCodec;
import com.acme.data360agent.support.Ids;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "jdbc", matchIfMissing = true)
public class JdbcPlanStore implements PlanStore {
    private static final TypeReference<List<StepSnapshot>> STEPS = new TypeReference<>() {
    };
    private static final TypeReference<LinkedHashSet<String>> APPROVALS = new TypeReference<>() {
    };

    private final JdbcTemplate jdbc;
    private final JsonStateCodec codec;
    private final TransactionTemplate transaction;

    public JdbcPlanStore(JdbcTemplate jdbc, JsonStateCodec codec, PlatformTransactionManager transactionManager) {
        this.jdbc = jdbc;
        this.codec = codec;
        this.transaction = new TransactionTemplate(transactionManager);
    }

    @Override
    public PlanDraft saveDraft(PlanDraft draft) {
        var json = codec.write(draft);
        var updated = jdbc.update("UPDATE plan_drafts SET draft_json = ?, updated_at = CURRENT_TIMESTAMP WHERE plan_id = ?", json, draft.plan().id());
        if (updated == 0) {
            jdbc.update("INSERT INTO plan_drafts (plan_id, draft_json) VALUES (?, ?)", draft.plan().id(), json);
        }
        return draft;
    }

    @Override
    public Optional<PlanDraft> draft(String planId) {
        var results = jdbc.query("SELECT draft_json FROM plan_drafts WHERE plan_id = ?", (rs, rowNum) -> codec.read(rs.getString("draft_json"), PlanDraft.class), planId);
        return results.stream().findFirst();
    }

    @Override
    public Collection<PlanDraft> drafts() {
        return jdbc.query("SELECT draft_json FROM plan_drafts ORDER BY updated_at DESC", (rs, rowNum) -> codec.read(rs.getString("draft_json"), PlanDraft.class));
    }

    @Override
    public PlanRun createRun(PlanSpec plan) {
        var run = new PlanRun(Ids.prefixed("run"), plan);
        saveRun(run);
        return run;
    }

    @Override
    public Optional<PlanRun> run(String runId) {
        var results = jdbc.query("SELECT * FROM plan_runs WHERE run_id = ?", (rs, rowNum) -> readRun(rs), runId);
        return results.stream().findFirst();
    }

    @Override
    public PlanRun saveRun(PlanRun run) {
        var planJson = codec.write(run.getPlan());
        var stepsJson = codec.write(run.getSteps().stream().map(StepSnapshot::from).toList());
        var approvalsJson = codec.write(run.getApprovedSteps());
        var updated = jdbc.update("""
                UPDATE plan_runs
                SET status = ?, plan_json = ?, steps_json = ?, approved_steps_json = ?, updated_at = CURRENT_TIMESTAMP
                WHERE run_id = ?
                """, run.getStatus().name(), planJson, stepsJson, approvalsJson, run.getId());
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO plan_runs (run_id, plan_id, status, plan_json, steps_json, approved_steps_json, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, run.getId(), run.getPlan().id(), run.getStatus().name(), planJson, stepsJson, approvalsJson, codec.timestamp(run.getCreatedAt()));
        }
        return run;
    }

    @Override
    public <T> T withRunLock(String runId, Function<PlanRun, T> work) {
        return transaction.execute(status -> {
            var results = jdbc.query("SELECT * FROM plan_runs WHERE run_id = ? FOR UPDATE", (rs, rowNum) -> readRun(rs), runId);
            if (results.isEmpty()) {
                throw new IllegalArgumentException("Run not found: " + runId);
            }
            return work.apply(results.getFirst());
        });
    }

    private PlanRun readRun(ResultSet rs) {
        try {
            var plan = codec.read(rs.getString("plan_json"), PlanSpec.class);
            var run = new PlanRun(rs.getString("run_id"), plan, codec.instant(rs.getTimestamp("created_at")));
            run.setStatus(RunStatus.valueOf(rs.getString("status")));
            run.getApprovedSteps().clear();
            run.getApprovedSteps().addAll(codec.read(rs.getString("approved_steps_json"), APPROVALS));
            var snapshots = codec.read(rs.getString("steps_json"), STEPS);
            for (var snapshot : snapshots) {
                run.getSteps().stream()
                        .filter(step -> step.getStepId().equals(snapshot.stepId()))
                        .findFirst()
                        .ifPresent(snapshot::applyTo);
            }
            return run;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read plan run.", e);
        }
    }

    private record StepSnapshot(
            String stepId,
            StepStatus status,
            Map<String, Object> output,
            Map<String, Object> raw,
            String error,
            Instant startedAt,
            Instant finishedAt
    ) {
        static StepSnapshot from(StepRun step) {
            return new StepSnapshot(step.getStepId(), step.getStatus(), step.getOutput(), step.getRaw(), step.getError(), step.getStartedAt(), step.getFinishedAt());
        }

        void applyTo(StepRun step) {
            step.setStatus(status);
            step.setOutput(output);
            step.setRaw(raw);
            step.setError(error);
            step.setStartedAt(startedAt);
            step.setFinishedAt(finishedAt);
        }
    }
}
