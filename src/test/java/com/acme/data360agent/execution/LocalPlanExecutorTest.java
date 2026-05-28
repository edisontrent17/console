package com.acme.data360agent.execution;

import com.acme.data360agent.data360.MockData360Client;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LocalPlanExecutorTest {
    @Test
    void pausesForApprovalAndResumes() throws Exception {
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client());
        var plan = new PlanSpec(
                "plan_test",
                "Create and publish segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "preview",
                                "Preview audience",
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100"),
                                List.of(),
                                false
                        ),
                        new PlanStep(
                                "create_segment",
                                "Create segment",
                                Data360Action.CREATE_SEGMENT,
                                Map.of("name", "Test Segment", "criteriaFromStep", "preview"),
                                List.of("preview"),
                                true
                        )
                )
        );

        var run = executor.start(plan);
        waitFor(() -> run.getStatus() == RunStatus.WAITING_APPROVAL, Duration.ofSeconds(3));

        assertThat(run.getSteps().get(0).getStatus()).isEqualTo(StepStatus.SUCCEEDED);
        assertThat(run.getSteps().get(1).getStatus()).isEqualTo(StepStatus.WAITING_APPROVAL);

        executor.approveStep(run.getId(), "create_segment");
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));

        assertThat(run.getSteps().get(1).getOutput()).containsKey("segmentId");
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
