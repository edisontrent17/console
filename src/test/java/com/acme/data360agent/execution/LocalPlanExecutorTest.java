package com.acme.data360agent.execution;

import com.acme.data360agent.data360.MockData360Client;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.operation.OperationTransport;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void approvalIsIdempotentAfterStepSucceeded() throws Exception {
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client());
        var plan = new PlanSpec(
                "plan_test",
                "Create segment",
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
        executor.approveStep(run.getId(), "create_segment");
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));
        var firstOutput = run.getSteps().get(1).getOutput();

        executor.approveStep(run.getId(), "create_segment");

        assertThat(run.getStatus()).isEqualTo(RunStatus.SUCCEEDED);
        assertThat(run.getSteps().get(1).getOutput()).isEqualTo(firstOutput);
    }

    @Test
    void rejectsApprovalForStepThatIsNotWaiting() {
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client());
        var plan = new PlanSpec(
                "plan_test",
                "Preview audience",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview audience",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100"),
                        List.of(),
                        false
                ))
        );
        var run = executor.start(plan);

        assertThatThrownBy(() -> executor.approveStep(run.getId(), "preview"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not require approval");
    }

    @Test
    void executionUsesFrozenOperationBindingFromStartRequest() throws Exception {
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client());
        var plan = new PlanSpec(
                "plan_test",
                "Preview with pinned binding",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview audience",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100"),
                        List.of(),
                        false
                ))
        );
        var binding = new OperationBindingSnapshot(
                Data360Action.QUERY.resource(),
                OperationTransport.MCP,
                "data360",
                "execute",
                "d360_query_sql_pinned",
                Effect.READ,
                false,
                Map.of("type", "object", "requiredAllOf", List.of("sql"), "requiredAnyOf", List.of()),
                Map.of("type", "object", "properties", Map.of("rowCount", "number")),
                null,
                "test-binding"
        );

        var run = executor.start(plan, List.of(binding));
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));

        assertThat(run.getOperationBindings()).containsExactly(binding);
        assertThat(run.getSteps().getFirst().getRaw()).containsEntry("mcpOperation", "d360_query_sql_pinned");
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
