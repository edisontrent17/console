package com.acme.data360agent.temporal;

import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.execution.PlanStore;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanTopology;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class Data360PlanWorkflowImplTest {
    private static final String TASK_QUEUE = "test-data360-plan";

    private TestWorkflowEnvironment environment;
    private RecordingPlanRunActivities planRunActivities;

    @BeforeEach
    void setUp() {
        environment = TestWorkflowEnvironment.newInstance();
        Worker worker = environment.newWorker(TASK_QUEUE);
        planRunActivities = new RecordingPlanRunActivities();
        worker.registerWorkflowImplementationTypes(Data360PlanWorkflowImpl.class);
        worker.registerActivitiesImplementations(new FakeData360Activities(), planRunActivities);
        environment.start();
    }

    @AfterEach
    void tearDown() {
        environment.close();
    }

    @Test
    void waitsForApprovalSignalThenCompletesSetupAndSkipsMonitor() {
        var workflow = environment.getWorkflowClient().newWorkflowStub(Data360PlanWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId("test-run-1")
                .setTaskQueue(TASK_QUEUE)
                .build());
        var plan = plan();
        var registry = new OperationRegistry();
        var operationBindings = plan.steps().stream()
                .map(step -> registry.bindingFor(step.action()))
                .distinct()
                .toList();
        WorkflowClient.start(workflow::run, PlanStore.DEFAULT_ORGANIZATION_ID, "run_1", plan, operationBindings, PlanTopology.stepIds(plan));

        waitForStatus(workflow, "WAITING_APPROVAL");
        assertThat(workflow.status()).containsEntry("waitingStepId", "create_segment");

        workflow.approveStep("create_segment", "reviewer@example.com");
        var result = WorkflowStub.fromTyped(workflow).getResult(String.class);

        assertThat(result).isEqualTo("run_1");
        assertThat(workflow.status()).containsEntry("status", "SUCCEEDED");
        assertThat(planRunActivities.events).containsExactly(
                "started:preview",
                "prepared:preview",
                "succeeded:preview",
                "waiting:create_segment",
                "approved:create_segment",
                "started:create_segment",
                "prepared:create_segment",
                "succeeded:create_segment",
                "skipped:monitor_goal",
                "completed:run_1"
        );
    }

    @Test
    void cancelSignalWhileWaitingForApprovalCancelsRun() {
        var workflow = environment.getWorkflowClient().newWorkflowStub(Data360PlanWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId("test-run-cancel")
                .setTaskQueue(TASK_QUEUE)
                .build());
        var plan = plan();
        var registry = new OperationRegistry();
        var operationBindings = plan.steps().stream()
                .map(step -> registry.bindingFor(step.action()))
                .distinct()
                .toList();
        WorkflowClient.start(workflow::run, PlanStore.DEFAULT_ORGANIZATION_ID, "run_cancel", plan, operationBindings, PlanTopology.stepIds(plan));

        waitForStatus(workflow, "WAITING_APPROVAL");
        workflow.cancel("No longer needed");
        var result = WorkflowStub.fromTyped(workflow).getResult(String.class);

        assertThat(result).isEqualTo("run_cancel");
        assertThat(workflow.status()).containsEntry("status", "CANCELED");
        assertThat(planRunActivities.events).contains("canceled:run_cancel");
    }

    private void waitForStatus(Data360PlanWorkflow workflow, String status) {
        for (int i = 0; i < 20; i++) {
            if (status.equals(workflow.status().get("status"))) {
                return;
            }
            environment.sleep(Duration.ofMillis(100));
        }
        throw new AssertionError("Workflow did not reach " + status + ": " + workflow.status());
    }

    private PlanSpec plan() {
        return new PlanSpec(
                "plan_temporal_test",
                "Temporal approval flow",
                new PlanContext("org", "default", "sandbox"),
                List.of(
                        new PlanStep(
                                "preview",
                                "Preview audience",
                                PlanPhase.DISCOVER,
                                Data360Action.QUERY,
                                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                                List.of(),
                                Map.of(),
                                false
                        ),
                        new PlanStep(
                                "create_segment",
                                "Create segment",
                                PlanPhase.SETUP,
                                Data360Action.CREATE_SEGMENT,
                                Map.of("name", "Temporal Segment", "criteriaFromStep", "preview"),
                                List.of("preview"),
                                Map.of(),
                                true
                        ),
                        new PlanStep(
                                "monitor_goal",
                                "Monitor activation rate",
                                PlanPhase.MONITOR,
                                Data360Action.MONITOR_METRIC,
                                Map.of("metric", "activation_rate", "cadence", "daily", "threshold", Map.of("operator", "<", "value", 0.13), "queryFromStep", "preview"),
                                List.of("preview", "create_segment"),
                                Map.of(),
                                false
                        )
                )
        );
    }

    private static class FakeData360Activities implements Data360Activities {
        @Override
        public ActivityResult executeStep(ActivityCommand command) {
            assertThat(command.idempotencyKey()).startsWith("exec_");
            assertThat(command.binding().resource()).isEqualTo(command.step().action().resource());
            if (command.step().action() == Data360Action.CREATE_SEGMENT) {
                assertThat(command.binding().underlyingTool()).isEqualTo("d360_segment_create");
                assertThat(command.resolvedInput()).containsKey("criteria");
                return new ActivityResult(Map.of("segmentId", "seg_temporal"), Map.of("mode", "test"));
            }
            assertThat(command.binding().underlyingTool()).isEqualTo("d360_query_sql");
            return new ActivityResult(Map.of("rowCount", 2, "sql", command.resolvedInput().get("sql")), Map.of("mode", "test"));
        }
    }

    private static class RecordingPlanRunActivities implements PlanRunActivities {
        private final List<String> events = new ArrayList<>();

        @Override
        public void waitingForApproval(String organizationId, String runId, String planId, String stepId, String action) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            events.add("waiting:" + stepId);
        }

        @Override
        public void stepApproved(String organizationId, String runId, String planId, String stepId, String approvedBy) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            assertThat(approvedBy).isEqualTo("reviewer@example.com");
            events.add("approved:" + stepId);
        }

        @Override
        public void stepStarted(String organizationId, String runId, String planId, String stepId, String action) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            events.add("started:" + stepId);
        }

        @Override
        public void stepToolCallPrepared(String organizationId, String runId, String planId, String stepId, String action, OperationBindingSnapshot binding, Map<String, Object> resolvedInput, String idempotencyKey) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            assertThat(binding.resource()).isNotBlank();
            assertThat(resolvedInput).isNotEmpty();
            assertThat(idempotencyKey).startsWith("exec_");
            events.add("prepared:" + stepId);
        }

        @Override
        public void stepSucceeded(String organizationId, String runId, String planId, String stepId, String action, Map<String, Object> output, Map<String, Object> raw) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            events.add("succeeded:" + stepId);
        }

        @Override
        public void stepFailed(String organizationId, String runId, String planId, String stepId, String error) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            events.add("failed:" + stepId);
        }

        @Override
        public void skipMonitorStep(String organizationId, String runId, String planId, String stepId) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            events.add("skipped:" + stepId);
        }

        @Override
        public void completeRun(String organizationId, String runId, String planId) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            events.add("completed:" + runId);
        }

        @Override
        public void cancelRun(String organizationId, String runId, String planId, String reason) {
            assertThat(organizationId).isEqualTo(PlanStore.DEFAULT_ORGANIZATION_ID);
            events.add("canceled:" + runId);
        }

    }
}
