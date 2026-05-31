package com.acme.data360agent.archive;

import com.acme.data360agent.audit.InMemoryAuditService;
import com.acme.data360agent.data360.MockData360Client;
import com.acme.data360agent.execution.InMemoryPlanStore;
import com.acme.data360agent.execution.LocalPlanExecutor;
import com.acme.data360agent.execution.RunStatus;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.plan.PlanValidationResult;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ArchiveServiceTest {
    @Test
    void exportsPlanSpecArchiveWithOperationBindings() {
        var store = new InMemoryPlanStore();
        var plan = plan();
        store.saveDraft(new PlanDraft(plan, new PlanValidationResult(List.of()), List.of("draft_plan", "validate_plan")));
        var service = new ArchiveService(store, new InMemoryAuditService());

        var archive = service.planSpec(plan.id());

        assertThat(archive.archiveType()).isEqualTo("planspec");
        assertThat(archive.draft().plan()).isEqualTo(plan);
        assertThat(archive.draft().operationBindings()).isNotEmpty();
    }

    @Test
    void exportsExecutionLogWithToolCallsResolvedInputsAndAuditEvents() throws Exception {
        var store = new InMemoryPlanStore();
        var audit = new InMemoryAuditService();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client(), null, audit);
        var run = executor.start(plan());
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));
        var service = new ArchiveService(store, audit);

        var archive = service.executionLog(run.getId());

        assertThat(archive.archiveType()).isEqualTo("execution-log");
        assertThat(archive.run().getId()).isEqualTo(run.getId());
        assertThat(archive.auditEvents()).extracting("eventType").contains("step_tool_call_prepared", "step_succeeded", "run_succeeded");
        assertThat(archive.toolCalls()).hasSize(1);
        assertThat(archive.toolCalls().getFirst().resolvedInput()).containsEntry("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100");
        assertThat(archive.toolCalls().getFirst().raw()).containsKeys("resolvedInput", "output");
    }

    private PlanSpec plan() {
        return new PlanSpec(
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
