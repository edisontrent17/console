package com.acme.data360agent.execution;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
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
    private static final TypeReference<List<OperationBindingSnapshot>> OPERATION_BINDINGS = new TypeReference<>() {
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
    public PlanDraft saveDraft(String organizationId, PlanDraft draft) {
        var org = normalize(organizationId);
        var json = codec.write(draft);
        var updated = jdbc.update("UPDATE plan_drafts SET draft_json = ?, updated_at = CURRENT_TIMESTAMP WHERE organization_id = ? AND plan_id = ?", json, org, draft.plan().id());
        if (updated == 0) {
            jdbc.update("INSERT INTO plan_drafts (organization_id, plan_id, draft_json) VALUES (?, ?, ?)", org, draft.plan().id(), json);
        }
        clearApprovedPlan(org, draft.plan().id());
        return draft;
    }

    @Override
    public Optional<PlanDraft> draft(String organizationId, String planId) {
        var results = jdbc.query("SELECT draft_json FROM plan_drafts WHERE organization_id = ? AND plan_id = ?", (rs, rowNum) -> codec.read(rs.getString("draft_json"), PlanDraft.class), normalize(organizationId), planId);
        return results.stream().findFirst();
    }

    @Override
    public Collection<PlanDraft> drafts(String organizationId) {
        return jdbc.query("SELECT draft_json FROM plan_drafts WHERE organization_id = ? ORDER BY updated_at DESC", (rs, rowNum) -> codec.read(rs.getString("draft_json"), PlanDraft.class), normalize(organizationId));
    }

    @Override
    public ApprovedExecutablePlan saveApprovedPlan(String organizationId, ApprovedExecutablePlan approvedPlan) {
        var org = normalize(organizationId);
        var planId = approvedPlan.plan().id();
        var approvedAt = codec.timestamp(approvedPlan.approvedAt());
        var json = codec.write(approvedPlan);
        var updated = jdbc.update("""
                UPDATE approved_plans
                SET artifact_id = ?, plan_hash = ?, approved_plan_json = ?, approved_by = ?, approved_at = ?, updated_at = CURRENT_TIMESTAMP
                WHERE organization_id = ? AND plan_id = ?
                """, approvedPlan.artifactId(), approvedPlan.planHash(), json, approvedPlan.approvedBy(), approvedAt, org, planId);
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO approved_plans (organization_id, plan_id, artifact_id, plan_hash, approved_plan_json, approved_by, approved_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, org, planId, approvedPlan.artifactId(), approvedPlan.planHash(), json, approvedPlan.approvedBy(), approvedAt);
        }
        return approvedPlan;
    }

    @Override
    public Optional<ApprovedExecutablePlan> approvedPlan(String organizationId, String planId) {
        var results = jdbc.query("SELECT artifact_id, plan_hash, approved_plan_json FROM approved_plans WHERE organization_id = ? AND plan_id = ?", (rs, rowNum) -> readApprovedPlan(rs), normalize(organizationId), planId);
        return results.stream().findFirst();
    }

    @Override
    public void clearApprovedPlan(String organizationId, String planId) {
        jdbc.update("DELETE FROM approved_plans WHERE organization_id = ? AND plan_id = ?", normalize(organizationId), planId);
    }

    @Override
    public PlanRun createRun(String organizationId, ApprovedExecutablePlan approvedPlan) {
        var run = new PlanRun(Ids.prefixed("run"), normalize(organizationId), approvedPlan);
        saveRun(run);
        return run;
    }

    @Override
    public Optional<PlanRun> run(String organizationId, String runId) {
        var results = jdbc.query("SELECT * FROM plan_runs WHERE organization_id = ? AND run_id = ?", (rs, rowNum) -> readRun(rs), normalize(organizationId), runId);
        return results.stream().findFirst();
    }

    @Override
    public PlanRun saveRun(String organizationId, PlanRun run) {
        var org = normalize(organizationId);
        var planJson = codec.write(run.getPlan());
        var stepsJson = codec.write(run.getSteps().stream().map(StepSnapshot::from).toList());
        var approvalsJson = codec.write(run.getApprovedSteps());
        var operationBindingsJson = codec.write(run.getOperationBindings());
        var approvedPlanJson = codec.write(run.getApprovedPlan());
        var updated = jdbc.update("""
                UPDATE plan_runs
                SET status = ?, plan_json = ?, steps_json = ?, approved_steps_json = ?, operation_bindings_json = ?, approved_plan_json = ?, updated_at = CURRENT_TIMESTAMP
                WHERE organization_id = ? AND run_id = ?
                """, run.getStatus().name(), planJson, stepsJson, approvalsJson, operationBindingsJson, approvedPlanJson, org, run.getId());
        if (updated == 0) {
            jdbc.update("""
                    INSERT INTO plan_runs (organization_id, run_id, plan_id, status, plan_json, steps_json, approved_steps_json, operation_bindings_json, approved_plan_json, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, org, run.getId(), run.getPlan().id(), run.getStatus().name(), planJson, stepsJson, approvalsJson, operationBindingsJson, approvedPlanJson, codec.timestamp(run.getCreatedAt()));
        }
        return run;
    }

    @Override
    public <T> T withRunLock(String organizationId, String runId, Function<PlanRun, T> work) {
        return transaction.execute(status -> {
            var results = jdbc.query("SELECT * FROM plan_runs WHERE organization_id = ? AND run_id = ? FOR UPDATE", (rs, rowNum) -> readRun(rs), normalize(organizationId), runId);
            if (results.isEmpty()) {
                throw new IllegalArgumentException("Run not found: " + runId);
            }
            return work.apply(results.getFirst());
        });
    }

    private PlanRun readRun(ResultSet rs) {
        try {
            var plan = codec.read(rs.getString("plan_json"), PlanSpec.class);
            var approvedPlanJson = rs.getString("approved_plan_json");
            var approvedPlan = approvedPlanJson == null || approvedPlanJson.isBlank()
                    ? ApprovedExecutablePlan.legacy(plan, null)
                    : codec.read(approvedPlanJson, ApprovedExecutablePlan.class);
            var run = new PlanRun(rs.getString("run_id"), rs.getString("organization_id"), approvedPlan, codec.instant(rs.getTimestamp("created_at")));
            run.setStatus(RunStatus.valueOf(rs.getString("status")));
            run.getApprovedSteps().clear();
            run.getApprovedSteps().addAll(codec.read(rs.getString("approved_steps_json"), APPROVALS));
            var operationBindingsJson = rs.getString("operation_bindings_json");
            if (operationBindingsJson != null && !operationBindingsJson.isBlank()) {
                run.setOperationBindings(codec.read(operationBindingsJson, OPERATION_BINDINGS));
            }
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

    private ApprovedExecutablePlan readApprovedPlan(ResultSet rs) {
        try {
            var approvedPlan = codec.read(rs.getString("approved_plan_json"), ApprovedExecutablePlan.class);
            var artifactId = rs.getString("artifact_id");
            var planHash = rs.getString("plan_hash");
            if (!approvedPlan.artifactId().equals(artifactId) || !approvedPlan.planHash().equals(planHash)) {
                throw new IllegalStateException("Approved plan artifact metadata does not match persisted columns.");
            }
            return approvedPlan;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read approved executable plan.", e);
        }
    }

    private String normalize(String organizationId) {
        return organizationId == null || organizationId.isBlank() ? DEFAULT_ORGANIZATION_ID : organizationId;
    }

    private record StepSnapshot(
            String stepId,
            StepStatus status,
            OperationBindingSnapshot binding,
            Map<String, Object> resolvedInput,
            Map<String, Object> output,
            Map<String, Object> raw,
            String error,
            Instant startedAt,
            Instant finishedAt
    ) {
        static StepSnapshot from(StepRun step) {
            return new StepSnapshot(step.getStepId(), step.getStatus(), step.getBinding(), step.getResolvedInput(), step.getOutput(), step.getRaw(), step.getError(), step.getStartedAt(), step.getFinishedAt());
        }

        void applyTo(StepRun step) {
            step.setStatus(status);
            step.setBinding(binding);
            step.setResolvedInput(resolvedInput);
            step.setOutput(output);
            step.setRaw(raw);
            step.setError(error);
            step.setStartedAt(startedAt);
            step.setFinishedAt(finishedAt);
        }
    }
}
