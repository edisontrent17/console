package com.acme.data360agent.monitor;

import com.acme.data360agent.data360.MockData360Client;
import com.acme.data360agent.execution.InMemoryPlanStore;
import com.acme.data360agent.execution.LocalPlanExecutor;
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
        var planStore = new InMemoryPlanStore();
        var operations = new OperationRegistry();
        var data360 = new MockData360Client();
        var executor = new LocalPlanExecutor(planStore, operations, data360);
        var monitors = new MonitorService(new InMemoryMonitorStore(), planStore, operations, data360);
        var plan = new PlanSpec(
                "plan_monitor",
                "Monitor activation rate",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "preview",
                                "Preview audience",
                                PlanPhase.DISCOVER,
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100"),
                                List.of(),
                                Map.of(),
                                false
                        ),
                        new PlanStep(
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
                        )
                )
        );

        var run = executor.start(plan);
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));
        var definitions = monitors.registerReadyFromRun(run);

        var monitorRun = monitors.runNow(definitions.getFirst().id());

        assertThat(definitions).hasSize(1);
        assertThat(run.getSteps().get(1).getStatus()).isEqualTo(StepStatus.SKIPPED);
        assertThat(monitorRun.thresholdBreached()).isTrue();
        assertThat(monitorRun.status()).isEqualTo(MonitorStatus.ATTENTION_REQUIRED);
        assertThat(monitors.definition(definitions.getFirst().id()).lastRunAt()).isNotNull();
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
}
