package com.acme.data360agent.monitor;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.data360.Data360Client;
import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.execution.RunStatus;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.MonitorThreshold;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.support.Ids;
import com.acme.data360agent.support.SensitiveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitorService {
    private final MonitorStore store;
    private final PlanStore planStore;
    private final OperationRegistry operations;
    private final Data360Client data360Client;
    private final AuditService audit;

    @Autowired
    public MonitorService(MonitorStore store, PlanStore planStore, OperationRegistry operations, Data360Client data360Client, AuditService audit) {
        this.store = store;
        this.planStore = planStore;
        this.operations = operations;
        this.data360Client = data360Client;
        this.audit = audit;
    }

    public MonitorService(MonitorStore store, PlanStore planStore, OperationRegistry operations, Data360Client data360Client) {
        this(store, planStore, operations, data360Client, null);
    }

    public List<MonitorDefinition> registerFromPlan(String runId, PlanSpec plan) {
        return registerFromPlan(PlanStore.DEFAULT_ORGANIZATION_ID, runId, plan);
    }

    public List<MonitorDefinition> registerFromPlan(String organizationId, String runId, PlanSpec plan) {
        return plan.steps().stream()
                .filter(step -> step.phase() == PlanPhase.MONITOR)
                .map(step -> register(organizationId, runId, plan, step))
                .toList();
    }

    public List<MonitorDefinition> registerReadyFromRun(PlanRun run) {
        if (run.getStatus() != RunStatus.SUCCEEDED) {
            return List.of();
        }
        return run.getPlan().steps().stream()
                .filter(step -> step.phase() == PlanPhase.MONITOR)
                .map(step -> store.definitionFor(run.getOrganizationId(), run.getId(), step.id()).orElseGet(() -> register(run.getOrganizationId(), run.getId(), run.getPlan(), step)))
                .toList();
    }

    public List<MonitorDefinition> all() {
        return store.definitions().stream()
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    public List<MonitorDefinition> all(String organizationId) {
        return store.definitions(organizationId).stream()
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    public MonitorDefinition definition(String monitorId) {
        return store.definition(monitorId).orElseThrow(() -> new IllegalArgumentException("Monitor not found: " + monitorId));
    }

    public MonitorDefinition definition(String organizationId, String monitorId) {
        return store.definition(organizationId, monitorId).orElseThrow(() -> new IllegalArgumentException("Monitor not found: " + monitorId));
    }

    public List<MonitorRun> runsFor(String monitorId) {
        definition(monitorId);
        return store.runsFor(monitorId);
    }

    public List<MonitorRun> runsFor(String organizationId, String monitorId) {
        definition(organizationId, monitorId);
        return store.runsFor(organizationId, monitorId);
    }

    public List<MonitorRun> runScheduled() {
        var now = Instant.now();
        var leaseUntil = now.plus(MonitorCadence.leaseDuration());
        return store.claimDue(now, leaseUntil, 25).stream()
                .map(definition -> runNow(definition.organizationId(), definition.id()))
                .toList();
    }

    public MonitorRun runNow(String monitorId) {
        return runNow(PlanStore.DEFAULT_ORGANIZATION_ID, monitorId);
    }

    public MonitorRun runNow(String organizationId, String monitorId) {
        var definition = definition(organizationId, monitorId);
        var run = planStore.run(definition.organizationId(), definition.runId()).orElseThrow(() -> new IllegalArgumentException("Run not found: " + definition.runId()));
        if (run.getStatus() != RunStatus.SUCCEEDED) {
            throw new IllegalStateException("Monitor cannot run until setup run has succeeded: " + definition.runId());
        }
        var step = run.getPlan().steps().stream()
                .filter(candidate -> candidate.id().equals(definition.stepId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Monitor step not found: " + definition.stepId()));

        var operation = operations.require(step.action());
        var resolvedInput = resolveInput(step, run);
        try {
            var result = data360Client.call(operation, step, resolvedInput, new RunContext(definition.organizationId(), run.getId(), run.getPlan().id(), run.getPlan().context()));
            var observed = observedValue(result.output());
            var breached = MonitorThreshold.require(definition.threshold()).isBreached(observed);
            var status = breached ? MonitorStatus.ATTENTION_REQUIRED : MonitorStatus.ACTIVE;
            var checkedAt = Instant.now();
            var monitorRun = new MonitorRun(
                    Ids.prefixed("monrun"),
                    definition.organizationId(),
                    monitorId,
                    observed,
                    breached,
                    status,
                    recommendation(definition, observed, breached),
                    result.raw(),
                    checkedAt
            );
            store.save(definition.withStatus(status, monitorRun.createdAt(), MonitorCadence.nextRunAt(definition.cadence(), checkedAt)));
            var saved = store.saveRun(monitorRun);
            if (breached && store.pendingRecommendationFor(definition.organizationId(), definition.id()).isEmpty()) {
                store.saveRecommendation(recommendation(definition, saved));
            }
            return saved;
        } catch (Exception e) {
            var error = SensitiveData.redactText(e.getMessage());
            var monitorRun = new MonitorRun(
                    Ids.prefixed("monrun"),
                    definition.organizationId(),
                    monitorId,
                    0,
                    true,
                    MonitorStatus.ERROR,
                    "Monitor failed: " + error,
                    Map.of("error", error),
                    Instant.now()
            );
            store.save(definition.withStatus(MonitorStatus.ERROR, monitorRun.createdAt(), MonitorCadence.nextRunAt(definition.cadence(), monitorRun.createdAt())));
            return store.saveRun(monitorRun);
        }
    }

    public List<MonitorRecommendation> recommendations() {
        return store.recommendations();
    }

    public List<MonitorRecommendation> recommendations(String organizationId) {
        return store.recommendations(organizationId);
    }

    public MonitorRecommendation recommendation(String recommendationId) {
        return store.recommendation(recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + recommendationId));
    }

    public MonitorRecommendation recommendation(String organizationId, String recommendationId) {
        return store.recommendation(organizationId, recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + recommendationId));
    }

    public MonitorRecommendation approveRecommendation(String recommendationId) {
        return approveRecommendation(recommendationId, "system");
    }

    public MonitorRecommendation approveRecommendation(String recommendationId, String actor) {
        return reviewRecommendation(recommendationId, MonitorRecommendationStatus.APPROVED, actor);
    }

    public MonitorRecommendation rejectRecommendation(String recommendationId) {
        return rejectRecommendation(recommendationId, "system");
    }

    public MonitorRecommendation rejectRecommendation(String recommendationId, String actor) {
        return reviewRecommendation(recommendationId, MonitorRecommendationStatus.REJECTED, actor);
    }

    public MonitorRecommendation approveRecommendation(String organizationId, String recommendationId, String actor) {
        return reviewRecommendation(organizationId, recommendationId, MonitorRecommendationStatus.APPROVED, actor);
    }

    public MonitorRecommendation rejectRecommendation(String organizationId, String recommendationId, String actor) {
        return reviewRecommendation(organizationId, recommendationId, MonitorRecommendationStatus.REJECTED, actor);
    }

    private MonitorRecommendation reviewRecommendation(String recommendationId, MonitorRecommendationStatus status, String actor) {
        var recommendation = recommendation(recommendationId);
        if (recommendation.status() != MonitorRecommendationStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Recommendation has already been reviewed: " + recommendationId);
        }
        if (!store.reviewRecommendation(recommendationId, status, Instant.now())) {
            throw new IllegalStateException("Recommendation has already been reviewed: " + recommendationId);
        }
        var reviewed = recommendation(recommendationId);
        auditRecommendation(reviewed, actor);
        return reviewed;
    }

    private MonitorRecommendation reviewRecommendation(String organizationId, String recommendationId, MonitorRecommendationStatus status, String actor) {
        var recommendation = recommendation(organizationId, recommendationId);
        if (recommendation.status() != MonitorRecommendationStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Recommendation has already been reviewed: " + recommendationId);
        }
        if (!store.reviewRecommendation(organizationId, recommendationId, status, Instant.now())) {
            throw new IllegalStateException("Recommendation has already been reviewed: " + recommendationId);
        }
        var reviewed = recommendation(organizationId, recommendationId);
        auditRecommendation(reviewed, actor);
        return reviewed;
    }

    private void auditRecommendation(MonitorRecommendation recommendation, String actor) {
        if (audit == null) {
            return;
        }
        var definition = definition(recommendation.organizationId(), recommendation.monitorId());
        audit.event(definition.organizationId(), definition.runId(), definition.planId(), definition.stepId(), "monitor_recommendation_reviewed", Map.of(
                "recommendationId", recommendation.id(),
                "decision", recommendation.status().name(),
                "actor", actor == null || actor.isBlank() ? "system" : actor
        ));
    }

    private MonitorRecommendation recommendation(MonitorDefinition definition, MonitorRun run) {
        return new MonitorRecommendation(
                Ids.prefixed("monrec"),
                definition.organizationId(),
                definition.id(),
                run.id(),
                definition.metric(),
                run.observedValue(),
                definition.threshold(),
                run.recommendation(),
                MonitorRecommendationStatus.PENDING_APPROVAL,
                Instant.now(),
                null
        );
    }

    private MonitorDefinition register(String organizationId, String runId, PlanSpec plan, PlanStep step) {
        if (step.action() != Data360Action.MONITOR_METRIC) {
            throw new IllegalArgumentException("Only data360.monitor.metric can be registered as a monitor: " + step.id());
        }
        var input = step.input();
        var definition = new MonitorDefinition(
                Ids.prefixed("mon"),
                organizationId,
                plan.id(),
                runId,
                step.id(),
                String.valueOf(input.getOrDefault("metric", step.id())),
                String.valueOf(input.getOrDefault("cadence", "daily")),
                threshold(input),
                MonitorStatus.ACTIVE,
                Instant.now(),
                null
        );
        return store.save(definition);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> threshold(Map<String, Object> input) {
        return MonitorThreshold.require(input.get("threshold")).asMap();
    }

    private Map<String, Object> resolveInput(PlanStep step, com.acme.data360agent.execution.PlanRun run) {
        var resolved = new LinkedHashMap<>(step.input());
        if (resolved.containsKey("queryFromStep")) {
            var source = String.valueOf(resolved.get("queryFromStep"));
            run.getSteps().stream()
                    .filter(candidate -> candidate.getStepId().equals(source))
                    .findFirst()
                    .ifPresent(sourceRun -> resolved.put("queryContext", sourceRun.getOutput()));
        }
        return Map.copyOf(resolved);
    }

    private double observedValue(Map<String, Object> output) {
        var observed = output.get("observedValue");
        if (observed instanceof Number number) {
            return number.doubleValue();
        }
        throw new IllegalStateException("Monitor output missing numeric observedValue.");
    }

    private String recommendation(MonitorDefinition definition, double observed, boolean breached) {
        if (!breached) {
            return "Goal metric " + definition.metric() + " is within the approved threshold.";
        }
        return "Goal metric " + definition.metric() + " is outside threshold at " + observed + ". Review the PlanSpec and approve a follow-up action before mutating Data 360.";
    }

}
