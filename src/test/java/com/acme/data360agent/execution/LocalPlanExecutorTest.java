package com.acme.data360agent.execution;

import com.acme.data360agent.data360.MockData360Client;
import com.acme.data360agent.data360.Data360CallResult;
import com.acme.data360agent.data360.Data360Client;
import com.acme.data360agent.operation.Effect;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.operation.OperationTransport;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import com.acme.data360agent.planner.PlanDraft;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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

        var run = executor.start(approved(plan));
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

        var run = executor.start(approved(plan));
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
        var run = executor.start(approved(plan));

        assertThatThrownBy(() -> executor.approveStep(run.getId(), "preview"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not require approval");
    }

    @Test
    void executionUsesFrozenOperationBindingFromApprovedArtifact() throws Exception {
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

        var run = executor.start(approved(plan, List.of(binding)));
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));

        assertThat(run.getOperationBindings()).containsExactly(binding);
        var step = run.getSteps().getFirst();
        assertThat(step.getBinding()).isEqualTo(binding);
        assertThat(step.getResolvedInput()).containsEntry("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100");
        assertThat(step.getRaw()).containsEntry("mcpOperation", "d360_query_sql_pinned");
    }

    @Test
    void executionStartsFromApprovedExecutablePlanArtifact() throws Exception {
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client());
        var plan = new PlanSpec(
                "plan_approved_execution",
                "Preview with approved artifact",
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
        var draft = new PlanDraft(plan, new PlanValidationResult(List.of()), List.of("draft_plan", "validate_plan"));
        var approved = ApprovedExecutablePlan.approve(draft, draft.validation(), "tester@example.com");

        var run = executor.start(approved);
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));

        assertThat(run.getApprovedPlan()).isEqualTo(approved);
        assertThat(run.getApprovedPlan().artifacts()).extracting("type").contains("plan_dag");
        assertThat(run.getOperationBindings()).isEqualTo(approved.operationBindings());
    }

    @Test
    void executesGenericMcpStepWithPerStepBindingAndSelectors() throws Exception {
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client());
        var plan = new PlanSpec(
                "plan_mcp",
                "Create identity resolution through MCP",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "create_ir",
                                "Create IR",
                                PlanPhase.SETUP,
                                Data360Action.MCP_EXECUTE,
                                Map.of(
                                        "serverId", "data360",
                                        "toolName", "d360_ir_create",
                                        "effect", "write",
                                        "params", Map.of("request", Map.of("label", "Travel IR")),
                                        "outputSelectors", Map.of("calledTool", "$.output.toolName")
                                ),
                                List.of(),
                                Map.of(),
                                true
                        )
                )
        );
        var binding = OperationBindingSnapshot.mcpExecute(plan.steps().getFirst());

        var run = executor.start(approved(plan, List.of(binding)));
        waitFor(() -> run.getStatus() == RunStatus.WAITING_APPROVAL, Duration.ofSeconds(3));
        executor.approveStep(run.getId(), "create_ir");
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));

        var step = run.getSteps().getFirst();
        assertThat(step.getBinding().resource()).isEqualTo(Data360Action.MCP_EXECUTE.resource() + "#create_ir");
        assertThat(step.getBinding().underlyingTool()).isEqualTo("d360_ir_create");
        assertThat(step.getBinding().effect()).isEqualTo(Effect.WRITE);
        assertThat(step.getOutput()).containsEntry("calledTool", "d360_ir_create");
    }

    @Test
    void redactsMcpErrorsBeforePersistingFailedStep() throws Exception {
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new ThrowingData360Client());
        var plan = new PlanSpec(
                "plan_mcp_error",
                "Preview audience",
                new PlanContext("org", "default", "sandbox"),
                List.of(queryStep("preview"))
        );

        var run = executor.start(approved(plan));
        waitFor(() -> run.getStatus() == RunStatus.FAILED, Duration.ofSeconds(3));

        assertThat(run.getSteps().getFirst().getStatus()).isEqualTo(StepStatus.FAILED);
        assertThat(run.getSteps().getFirst().getError())
                .contains("Bearer ***")
                .doesNotContain("secret-token");
    }

    @Test
    void cancelRunWhileWaitingApprovalCancelsPendingSteps() throws Exception {
        var store = new InMemoryPlanStore();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client());
        var plan = new PlanSpec(
                "plan_cancel_waiting",
                "Create segment",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "create_segment",
                        "Create segment",
                        Data360Action.CREATE_SEGMENT,
                        Map.of("name", "Test Segment", "criteria", Map.of()),
                        List.of(),
                        true
                ))
        );
        var run = executor.start(approved(plan));
        waitFor(() -> run.getStatus() == RunStatus.WAITING_APPROVAL, Duration.ofSeconds(3));

        executor.cancelRun(run.getId(), "No longer needed", "reviewer@example.com");

        assertThat(run.getStatus()).isEqualTo(RunStatus.CANCELED);
        assertThat(run.getSteps().getFirst().getStatus()).isEqualTo(StepStatus.CANCELED);
        assertThatThrownBy(() -> executor.approveStep(run.getId(), "create_segment"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("canceled");
    }

    @Test
    void cancelRunWhileStepIsRunningPreventsSuccessOverwrite() throws Exception {
        var store = new InMemoryPlanStore();
        var client = new BlockingData360Client();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), client);
        var plan = new PlanSpec(
                "plan_cancel_running",
                "Preview audience",
                new PlanContext("org", "default", "sandbox"),
                List.of(queryStep("preview"))
        );

        var run = executor.start(approved(plan));
        client.awaitStarted();
        executor.cancelRun(run.getId(), "Stop running preview", "reviewer@example.com");
        client.release();
        waitFor(() -> run.getSteps().getFirst().getStatus() == StepStatus.CANCELED, Duration.ofSeconds(3));

        assertThat(run.getStatus()).isEqualTo(RunStatus.CANCELED);
        assertThat(run.getSteps().getFirst().getOutput()).isEmpty();
        assertThat(client.idempotencyKey).startsWith("exec_");
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

    private ApprovedExecutablePlan approved(PlanSpec plan) {
        return ApprovedExecutablePlan.legacy(plan, List.of());
    }

    private ApprovedExecutablePlan approved(PlanSpec plan, List<OperationBindingSnapshot> operationBindings) {
        return ApprovedExecutablePlan.legacy(plan, operationBindings);
    }

    private PlanStep queryStep(String id) {
        return new PlanStep(
                id,
                "Preview audience",
                Data360Action.QUERY,
                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100"),
                List.of(),
                false
        );
    }

    private static class BlockingData360Client implements Data360Client {
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);
        private volatile String idempotencyKey = "";

        @Override
        public Data360CallResult call(OperationDefinition operation, OperationBindingSnapshot binding, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
            idempotencyKey = context.idempotencyKey();
            started.countDown();
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return new Data360CallResult(Map.of("rowCount", 1), Map.of("mode", "blocking"));
        }

        @Override
        public Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
            return call(operation, OperationBindingSnapshot.data360Mcp(operation), step, resolvedInput, context);
        }

        void awaitStarted() throws InterruptedException {
            started.await();
        }

        void release() {
            release.countDown();
        }
    }

    private static class ThrowingData360Client implements Data360Client {
        @Override
        public Data360CallResult call(OperationDefinition operation, OperationBindingSnapshot binding, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
            throw new IllegalStateException("MCP request failed with Bearer secret-token and client_secret=abc123");
        }

        @Override
        public Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context) {
            return call(operation, OperationBindingSnapshot.data360Mcp(operation), step, resolvedInput, context);
        }
    }
}
