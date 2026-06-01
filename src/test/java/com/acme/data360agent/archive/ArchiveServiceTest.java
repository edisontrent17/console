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
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArchiveServiceTest {
    @Test
    void exportsPlanSpecArchiveWithOperationBindings() {
        var store = new InMemoryPlanStore();
        var plan = plan();
        store.saveDraft(new PlanDraft(plan, new PlanValidationResult(List.of()), List.of("draft_plan", "validate_plan")));
        var service = new ArchiveService(store, new InMemoryAuditService());

        var archive = service.planSpec(plan.id());

        assertThat(archive.archiveType()).isEqualTo("planspec");
        assertThat(archive.archiveSchemaVersion()).isNotBlank();
        assertThat(archive.redactionVersion()).isNotBlank();
        assertThat(archive.plan().id()).isEqualTo(plan.id());
        assertThat(archive.taskHashes()).isNotEmpty();
        assertThat(archive.artifacts()).anyMatch(artifact -> "plan_dag".equals(artifact.type()));
    }

    @Test
    void planSpecArchiveRedactsSensitiveInputsAndKeepsDagAndTaskHashes() {
        var store = new InMemoryPlanStore();
        var plan = new PlanSpec(
                "plan_sensitive_export",
                "Preview traveler traveler@example.com",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview audience",
                        Data360Action.QUERY,
                        Map.of(
                                "sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100",
                                "accessToken", "should-not-export"
                        ),
                        List.of(),
                        false
                ))
        );
        store.saveDraft(new PlanDraft(plan, new PlanValidationResult(List.of()), List.of("draft_plan", "validate_plan")));
        var service = new ArchiveService(store, new InMemoryAuditService());

        var archive = service.planSpec(plan.id());

        assertThat(archive.plan().steps().getFirst().input()).containsEntry("accessToken", "***");
        assertThat(archive.plan().steps().getFirst().action()).isEqualTo(Data360Action.QUERY.value());
        assertThat(archive.taskHashes()).isNotEmpty();
        assertThat(archive.artifacts()).anySatisfy(artifact -> {
            assertThat(artifact.type()).isEqualTo("plan_dag");
            assertThat(artifact.data()).containsKeys("nodes", "edges", "topologicalOrder");
        });
    }

    @Test
    void exportsPlanSpecOnlyWithinOrganization() {
        var store = new InMemoryPlanStore();
        var plan = plan();
        store.saveDraft("org_a", new PlanDraft(plan, new PlanValidationResult(List.of()), List.of("draft_plan", "validate_plan")));
        var service = new ArchiveService(store, new InMemoryAuditService());

        assertThat(service.planSpec("org_a", plan.id()).plan().id()).isEqualTo(plan.id());
        assertThatThrownBy(() -> service.planSpec("org_b", plan.id()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(plan.id());
    }

    @Test
    void exportsExecutionLogWithToolCallsResolvedInputsAndAuditEvents() throws Exception {
        var store = new InMemoryPlanStore();
        var audit = new InMemoryAuditService();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client(), null, audit);
        var run = executor.start(ApprovedExecutablePlan.legacy(plan(), List.of()));
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));
        var service = new ArchiveService(store, audit);

        var archive = service.executionLog(run.getId());

        assertThat(archive.archiveType()).isEqualTo("execution-log");
        assertThat(archive.archiveSchemaVersion()).isNotBlank();
        assertThat(archive.redactionVersion()).isNotBlank();
        assertThat(archive.run().id()).isEqualTo(run.getId());
        assertThat(archive.run().taskHashes()).isNotEmpty();
        assertThat(archive.run().artifacts()).anyMatch(artifact -> "plan_dag".equals(artifact.type()));
        assertThat(archive.run().steps()).extracting("id").containsExactly("preview");
        assertThat(archive.auditEvents()).extracting("eventType").contains("step_tool_call_prepared", "step_succeeded", "run_succeeded");
        assertThat(archive.toolCalls()).hasSize(1);
        assertThat(archive.toolCalls().getFirst().resolvedInput()).containsEntry("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 100");
        assertThat(archive.toolCalls().getFirst().raw()).containsKeys("resolvedInput", "output");
    }

    @Test
    void exportsExecutionLogOnlyWithinOrganization() throws Exception {
        var store = new InMemoryPlanStore();
        var audit = new InMemoryAuditService();
        var executor = new LocalPlanExecutor(store, new OperationRegistry(), new MockData360Client(), null, audit);
        var run = executor.start("org_a", com.acme.data360agent.planner.ApprovedExecutablePlan.legacy(plan(), List.of()));
        waitFor(() -> run.getStatus() == RunStatus.SUCCEEDED, Duration.ofSeconds(3));
        var service = new ArchiveService(store, audit);

        assertThat(service.executionLog("org_a", run.getId()).run().id()).isEqualTo(run.getId());
        assertThatThrownBy(() -> service.executionLog("org_b", run.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(run.getId());
    }

    @Test
    void executionLogArchiveDoesNotExposeMutableRunAndRedactsSecrets() {
        var store = new InMemoryPlanStore();
        var audit = new InMemoryAuditService();
        var run = store.createRun(ApprovedExecutablePlan.legacy(new PlanSpec(
                "plan_secret_archive",
                "Archive bearer secret",
                new PlanContext("org", "default", "sandbox"),
                List.of(new PlanStep(
                        "preview",
                        "Preview",
                        Data360Action.QUERY,
                        Map.of("sql", "SELECT Id FROM UnifiedIndividual LIMIT 10", "accessToken", "secret-token"),
                        List.of(),
                        false
                ))
        ), List.of()));
        var stepRun = com.acme.data360agent.execution.PlanRunSupport.stepRun(run, "preview");
        stepRun.setResolvedInput(Map.of("authorization", "Bearer should-not-export"));
        stepRun.setOutput(Map.of("customerEmail", "traveler@example.com", "apiKey", "should-not-export"));
        stepRun.setRaw(Map.of("frame", "client_secret=should-not-export"));
        stepRun.setError("failed with Bearer should-not-export");
        store.saveRun(run);
        audit.event(run.getId(), run.getPlan().id(), "preview", "step_failed", Map.of("token", "should-not-export"));
        audit.approval(run.getId(), "preview", "reviewer@example.com", "APPROVED", Map.of("password", "should-not-export"));

        var archive = new ArchiveService(store, audit).executionLog(run.getId());

        assertThat(archive.run().plan().steps().getFirst().input()).containsEntry("accessToken", "***");
        assertThat(archive.run().steps().getFirst().selectedOutput()).containsEntry("apiKey", "***");
        assertThat(archive.toolCalls().getFirst().resolvedInput()).containsEntry("authorization", "***");
        assertThat(archive.toolCalls().getFirst().raw()).containsEntry("frame", "client_secret=***");
        assertThat(archive.toolCalls().getFirst().error()).contains("Bearer ***");
        assertThat(archive.auditEvents().getFirst().detail()).containsEntry("token", "***");
        assertThat(archive.approvals().getFirst().payload()).containsEntry("password", "***");
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
