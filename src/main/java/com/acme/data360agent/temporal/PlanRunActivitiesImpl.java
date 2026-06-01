package com.acme.data360agent.temporal;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.execution.PlanRunSupport;
import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.execution.RunStatus;
import com.acme.data360agent.execution.StepStatus;
import com.acme.data360agent.monitor.MonitorService;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.support.SensitiveData;
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
    public void waitingForApproval(String organizationId, String runId, String planId, String stepId, String action) {
        store.withRunLock(organizationId, runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setStatus(StepStatus.WAITING_APPROVAL);
            run.setStatus(RunStatus.WAITING_APPROVAL);
            store.saveRun(run);
            audit.event(organizationId, runId, planId, stepId, "step_waiting_approval", Map.of("action", action));
            return run;
        });
    }

    @Override
    public void stepApproved(String organizationId, String runId, String planId, String stepId, String approvedBy) {
        store.withRunLock(organizationId, runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            run.getApprovedSteps().add(stepId);
            step.setStatus(StepStatus.PENDING);
            run.setStatus(RunStatus.RUNNING);
            store.saveRun(run);
            audit.approval(organizationId, runId, stepId, approvedBy, "APPROVED", Map.of("planId", planId, "executor", "temporal"));
            audit.event(organizationId, runId, planId, stepId, "step_approved", Map.of("status", step.getStatus().name(), "executor", "temporal"));
            return run;
        });
    }

    @Override
    public void stepStarted(String organizationId, String runId, String planId, String stepId, String action) {
        store.withRunLock(organizationId, runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setStatus(StepStatus.RUNNING);
            step.setStartedAt(Instant.now());
            run.setStatus(RunStatus.RUNNING);
            store.saveRun(run);
            audit.event(organizationId, runId, planId, stepId, "step_started", Map.of("action", action));
            return run;
        });
    }

    @Override
    public void stepToolCallPrepared(String organizationId, String runId, String planId, String stepId, String action, OperationBindingSnapshot binding, Map<String, Object> resolvedInput, String idempotencyKey) {
        store.withRunLock(organizationId, runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setBinding(binding);
            step.setResolvedInput(resolvedInput);
            store.saveRun(run);
            audit.event(organizationId, runId, planId, stepId, "step_tool_call_prepared", Map.of(
                    "action", action,
                    "binding", binding.auditSummary(),
                    "idempotencyKey", idempotencyKey == null ? "" : idempotencyKey,
                    "resolvedInputKeys", resolvedInput == null ? java.util.List.of() : resolvedInput.keySet().stream().sorted().toList(),
                    "executor", "temporal"
            ));
            return run;
        });
    }

    @Override
    public void stepSucceeded(String organizationId, String runId, String planId, String stepId, String action, Map<String, Object> output, Map<String, Object> raw) {
        store.withRunLock(organizationId, runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setOutput(output);
            step.setRaw(raw);
            step.setStatus(StepStatus.SUCCEEDED);
            step.setFinishedAt(Instant.now());
            store.saveRun(run);
            audit.event(organizationId, runId, planId, stepId, "step_succeeded", Map.of("action", action));
            return run;
        });
    }

    @Override
    public void stepFailed(String organizationId, String runId, String planId, String stepId, String error) {
        store.withRunLock(organizationId, runId, run -> {
            var redactedError = SensitiveData.redactText(error);
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setError(redactedError);
            step.setStatus(StepStatus.FAILED);
            step.setFinishedAt(Instant.now());
            run.setStatus(RunStatus.FAILED);
            store.saveRun(run);
            audit.event(organizationId, runId, planId, stepId, "step_failed", Map.of("error", redactedError == null ? "" : redactedError));
            return run;
        });
    }

    @Override
    public void skipMonitorStep(String organizationId, String runId, String planId, String stepId) {
        store.withRunLock(organizationId, runId, run -> {
            var step = PlanRunSupport.stepRun(run, stepId);
            step.setStatus(StepStatus.SKIPPED);
            step.setOutput(Map.of("registeredAsMonitor", true));
            step.setFinishedAt(Instant.now());
            store.saveRun(run);
            return run;
        });
    }

    @Override
    public void completeRun(String organizationId, String runId, String planId) {
        store.withRunLock(organizationId, runId, run -> {
            run.setStatus(RunStatus.SUCCEEDED);
            monitors.registerReadyFromRun(run);
            store.saveRun(run);
            audit.event(organizationId, runId, planId, null, "run_succeeded", Map.of("status", RunStatus.SUCCEEDED.name()));
            return run;
        });
    }

    @Override
    public void cancelRun(String organizationId, String runId, String planId, String reason) {
        store.withRunLock(organizationId, runId, run -> {
            run.setStatus(RunStatus.CANCELED);
            var now = Instant.now();
            for (var step : run.getSteps()) {
                if (!PlanRunSupport.terminal(step.getStatus())) {
                    step.setStatus(StepStatus.CANCELED);
                    step.setFinishedAt(now);
                }
            }
            store.saveRun(run);
            audit.event(organizationId, runId, planId, null, "run_canceled", Map.of("reason", reason == null ? "" : reason));
            return run;
        });
    }
}
