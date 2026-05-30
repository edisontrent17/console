package com.acme.data360agent.temporal;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.execution.PlanRunSupport;
import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.execution.RunStatus;
import com.acme.data360agent.execution.StepStatus;
import com.acme.data360agent.monitor.MonitorService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class PlanRunActivitiesImpl implements PlanRunActivities {
    private final PlanStore store;
    private final MonitorService monitors;
    private final AuditService audit;

    public PlanRunActivitiesImpl(PlanStore store, MonitorService monitors, AuditService audit) {
        this.store = store;
        this.monitors = monitors;
        this.audit = audit;
    }

    @Override
    public void waitingForApproval(String runId, String planId, String stepId, String action) {
        store.withRunLock(runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setStatus(StepStatus.WAITING_APPROVAL);
            run.setStatus(RunStatus.WAITING_APPROVAL);
            store.saveRun(run);
            audit.event(runId, planId, stepId, "step_waiting_approval", Map.of("action", action));
            return run;
        });
    }

    @Override
    public void stepApproved(String runId, String planId, String stepId, String approvedBy) {
        store.withRunLock(runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            run.getApprovedSteps().add(stepId);
            step.setStatus(StepStatus.PENDING);
            run.setStatus(RunStatus.RUNNING);
            store.saveRun(run);
            audit.approval(runId, stepId, approvedBy, "APPROVED", Map.of("planId", planId, "executor", "temporal"));
            audit.event(runId, planId, stepId, "step_approved", Map.of("status", step.getStatus().name(), "executor", "temporal"));
            return run;
        });
    }

    @Override
    public void stepStarted(String runId, String planId, String stepId, String action) {
        store.withRunLock(runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setStatus(StepStatus.RUNNING);
            step.setStartedAt(Instant.now());
            run.setStatus(RunStatus.RUNNING);
            store.saveRun(run);
            audit.event(runId, planId, stepId, "step_started", Map.of("action", action));
            return run;
        });
    }

    @Override
    public void stepSucceeded(String runId, String planId, String stepId, String action, Map<String, Object> output, Map<String, Object> raw) {
        store.withRunLock(runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setOutput(output);
            step.setRaw(raw);
            step.setStatus(StepStatus.SUCCEEDED);
            step.setFinishedAt(Instant.now());
            store.saveRun(run);
            audit.event(runId, planId, stepId, "step_succeeded", Map.of("action", action));
            return run;
        });
    }

    @Override
    public void stepFailed(String runId, String planId, String stepId, String error) {
        store.withRunLock(runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setError(error);
            step.setStatus(StepStatus.FAILED);
            step.setFinishedAt(Instant.now());
            run.setStatus(RunStatus.FAILED);
            store.saveRun(run);
            audit.event(runId, planId, stepId, "step_failed", Map.of("error", error == null ? "" : error));
            return run;
        });
    }

    @Override
    public void skipMonitorStep(String runId, String planId, String stepId) {
        store.withRunLock(runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setStatus(StepStatus.SKIPPED);
            step.setOutput(Map.of("registeredAsMonitor", true));
            step.setFinishedAt(Instant.now());
            store.saveRun(run);
            return run;
        });
    }

    @Override
    public void completeRun(String runId, String planId) {
        store.withRunLock(runId, run -> {
            run.setStatus(RunStatus.SUCCEEDED);
            monitors.registerReadyFromRun(run);
            store.saveRun(run);
            audit.event(runId, planId, null, "run_succeeded", Map.of("status", RunStatus.SUCCEEDED.name()));
            return run;
        });
    }

    @Override
    public void cancelRun(String runId, String planId, String reason) {
        store.withRunLock(runId, run -> {
            run.setStatus(RunStatus.CANCELED);
            store.saveRun(run);
            audit.event(runId, planId, null, "run_canceled", Map.of("reason", reason == null ? "" : reason));
            return run;
        });
    }
}
