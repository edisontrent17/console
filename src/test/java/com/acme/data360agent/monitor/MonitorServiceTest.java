package com.acme.data360agent.monitor;

import com.acme.data360agent.data360.MockData360Client;
import com.acme.data360agent.execution.InMemoryPlanStore;
import com.acme.data360agent.execution.LocalPlanExecutor;
import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.execution.RunStatus;
import com.acme.data360agent.execution.StepStatus;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MonitorServiceTest {
    @Test
    void registersAndRunsMonitorFromPlan() throws Exception {
        var fixture = registeredMonitor("plan_monitor");

        var monitorRun = fixture.monitors().runNow(fixture.definition().id());

        assertThat(fixture.run().getSteps().get(1).getStatus()).isEqualTo(StepStatus.SKIPPED);
        assertThat(monitorRun.thresholdBreached()).isTrue();
        assertThat(monitorRun.status()).isEqualTo(MonitorStatus.ATTENTION_REQUIRED);
        assertThat(fixture.monitors().definition(fixture.definition().id()).lastRunAt()).isNotNull();
        assertThat(fixture.monitors().recommendations())
                .singleElement()
                .satisfies(recommendation -> {
                    assertThat(recommendation.monitorRunId()).isEqualTo(monitorRun.id());
                    assertThat(recommendation.status()).isEqualTo(MonitorRecommendationStatus.PENDING_APPROVAL);
                    assertThat(recommendation.reviewedAt()).isNull();
                });
    }

    @Test
    void approvesAndRejectsRecommendationsWithoutChangingMonitorRun() throws Exception {
        var monitors = monitorWithBreachingRecommendation();
        var firstRecommendation = monitors.recommendations().getFirst();

        var approved = monitors.approveRecommendation(firstRecommendation.id());

        assertThat(approved.status()).isEqualTo(MonitorRecommendationStatus.APPROVED);
        assertThat(approved.reviewedAt()).isNotNull();

        var secondRun = monitors.runNow(approved.monitorId());
        var secondRecommendation = monitors.recommendations().stream()
                .filter(recommendation -> recommendation.monitorRunId().equals(secondRun.id()))
                .findFirst()
                .orElseThrow();

        var rejected = monitors.rejectRecommendation(secondRecommendation.id());

        assertThat(rejected.status()).isEqualTo(MonitorRecommendationStatus.REJECTED);
        assertThat(rejected.reviewedAt()).isNotNull();
        assertThat(monitors.runsFor(approved.monitorId())).hasSize(2);
    }

    @Test
    void scheduledRunClaimsOnlyDueMonitors() throws Exception {
        var fixture = registeredMonitor("plan_monitor_schedule");

        var firstBatch = fixture.monitors().runScheduled();
        var secondBatch = fixture.monitors().runScheduled();

        assertThat(firstBatch).hasSize(1);
        assertThat(secondBatch).isEmpty();
        assertThat(fixture.monitors().definition(fixture.definition().id()).nextRunAt()).isNotNull();
        assertThat(fixture.monitors().runsFor(fixture.definition().id())).hasSize(1);
    }

    private MonitorService monitorWithBreachingRecommendation() throws Exception {
        var fixture = registeredMonitor("plan_monitor_review");
        fixture.monitors().runNow(fixture.definition().id());
        return fixture.monitors();
    }

    private MonitorFixture registeredMonitor(String planId) throws Exception {
        var planStore = new InMemoryPlanStore();
        var operations = new OperationRegistry();
        var data360 = new MockData360Client();
        var executor = new LocalPlanExecutor(planStore, operations, data360);
        var monitors = new MonitorService(new InMemoryMonitorStore(), planStore, operations, data360);
        var plan = monitorPlan(planId);

        var run = executor.start(plan);
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));
        var definitions = monitors.registerReadyFromRun(run);
        assertThat(definitions).hasSize(1);
        return new MonitorFixture(monitors, run, definitions.getFirst());
    }

    private PlanSpec monitorPlan(String planId) {
        return new PlanSpec(
                planId,
                "Monitor activation rate",
                new PlanContext("org", "default", "sandbox"),
                List.of(previewStep(), monitorStep())
        );
    }

    private PlanStep previewStep() {
        return new PlanStep(
                "preview",
                "Preview audience",
                PlanPhase.DISCOVER,
                Data360Action.QUERY,
                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100"),
                List.of(),
                Map.of(),
                false
        );
    }

    private PlanStep monitorStep() {
        return new PlanStep(
                "monitor_goal",
                "Monitor activation rate",
                PlanPhase.MONITOR,
                Data360Action.MONITOR_METRIC,
                Map.of(
                        "metric", "activation_rate",
                        "cadence", "daily",
                        "threshold", Map.of("operator", "<", "value", 0.13),
                        "queryFromStep", "preview"
                ),
                List.of("preview"),
                Map.of(),
                false
        );
    }

    private void waitFor(Check check, Duration timeout) throws Exception {
        var deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (check.ok()) {
                return;
            }
            Thread.sleep(50);
        }
        throw new AssertionError("Condition was not met within " + timeout);
    }

    private interface Check {
        boolean ok();
    }

    private record MonitorFixture(MonitorService monitors, PlanRun run, MonitorDefinition definition) {
    }
}
