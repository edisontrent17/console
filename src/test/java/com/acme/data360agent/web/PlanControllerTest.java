package com.acme.data360agent.web;

import com.acme.data360agent.audit.AuditService;
import com.acme.data360agent.audit.InMemoryAuditService;
import com.acme.data360agent.archive.ArchiveService;
import com.acme.data360agent.archive.ExecutionRunArchive;
import com.acme.data360agent.execution.InMemoryPlanStore;
import com.acme.data360agent.execution.PlanExecutor;
import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.operation.OperationRegistry;
import com.acme.data360agent.plan.Data360Action;
import com.acme.data360agent.plan.PlanPhase;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.plan.PlanValidator;
import com.acme.data360agent.planner.ApprovedExecutablePlan;
import com.acme.data360agent.planner.Data360Planner;
import com.acme.data360agent.planner.PlanDraft;
import com.acme.data360agent.security.CurrentUserService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlanControllerTest {
    private final InMemoryPlanStore store = new InMemoryPlanStore();
    private final PlanExecutor executor = mock(PlanExecutor.class);
    private final CurrentUserService users = mock(CurrentUserService.class);
    private final PlanController controller = new PlanController(
            mock(Data360Planner.class),
            new PlanValidator(new OperationRegistry()),
            store,
            executor,
            new InMemoryAuditService(),
            users,
            new OperationRegistry(),
            null
    );

    PlanControllerTest() {
        when(users.organizationId()).thenReturn("org_a");
        when(users.actor()).thenReturn("reviewer@example.com");
    }

    @Test
    void approvePlanStoresApprovedExecutablePlanWithoutStartingRun() {
        var plan = validPlan("plan_approve");
        store.saveDraft("org_a", draft(plan));

        var response = controller.approvePlan("plan_approve");

        assertThat(response).isInstanceOf(ApprovedExecutablePlan.class);
        var approved = (ApprovedExecutablePlan) response;
        assertThat(approved.artifactId()).startsWith("aplan_");
        assertThat(approved.planHash()).startsWith("sha256:");
        assertThat(approved.approvedBy()).isEqualTo("reviewer@example.com");
        assertThat(store.approvedPlan("org_a", "plan_approve")).get().isEqualTo(approved);
        verifyNoInteractions(executor);
    }

    @Test
    void startRunConsumesStoredApprovedExecutablePlanArtifact() {
        var plan = validPlan("plan_run");
        store.saveDraft("org_a", draft(plan));
        var approved = (ApprovedExecutablePlan) controller.approvePlan("plan_run");
        when(executor.start(eq("org_a"), any(ApprovedExecutablePlan.class)))
                .thenAnswer(invocation -> new PlanRun("run_1", "org_a", invocation.getArgument(1, ApprovedExecutablePlan.class)));

        var response = controller.start("plan_run", new PlanController.StartPlanRunRequest(approved.artifactId(), approved.planHash()));

        assertThat(response).isInstanceOf(PlanRun.class);
        assertThat(((PlanRun) response).getApprovedPlan()).isEqualTo(approved);
    }

    @Test
    void startRunRejectsMissingApprovedExecutablePlanArtifact() {
        store.saveDraft("org_a", draft(validPlan("plan_unapproved")));

        assertThatThrownBy(() -> controller.start("plan_unapproved"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Plan must be approved before execution");
    }

    @Test
    void startRunRequiresExactApprovedArtifactReference() {
        var plan = validPlan("plan_artifact_match");
        store.saveDraft("org_a", draft(plan));
        var approved = (ApprovedExecutablePlan) controller.approvePlan("plan_artifact_match");

        assertThatThrownBy(() -> controller.start("plan_artifact_match", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("artifactId and planHash");
        assertThatThrownBy(() -> controller.start("plan_artifact_match", new PlanController.StartPlanRunRequest(approved.artifactId(), "sha256:wrong")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not match");
    }

    @Test
    void startRunRevalidatesApprovedArtifactBeforeExecution() {
        var invalidAfterApproval = new PlanSpec(
                "old-schema",
                "plan_revalidate",
                null,
                "Preview audience",
                context(),
                List.of(queryStep())
        );
        var approved = ApprovedExecutablePlan.approve(draft(validPlan("plan_revalidate")), new PlanValidationResult(List.of()), "reviewer@example.com");
        var tampered = new ApprovedExecutablePlan(
                approved.artifactType(),
                approved.artifactId(),
                null,
                approved.approvedAt(),
                approved.approvedBy(),
                invalidAfterApproval,
                approved.validation(),
                approved.graphStages(),
                approved.operationBindings(),
                approved.artifacts()
        );
        store.saveApprovedPlan("org_a", tampered);

        var response = controller.start("plan_revalidate", new PlanController.StartPlanRunRequest(tampered.artifactId(), tampered.planHash()));

        assertThat(response).isInstanceOf(PlanDraft.class);
        assertThat(((PlanDraft) response).validation().ok()).isFalse();
        verifyNoInteractions(executor);
    }

    @Test
    void approvePlanReturnsValidationDraftWhenPlanIsInvalid() {
        var invalid = new PlanSpec(
                "old-schema",
                "plan_invalid",
                null,
                "Preview audience",
                context(),
                List.of(queryStep())
        );
        store.saveDraft("org_a", draft(invalid));

        var response = controller.approvePlan("plan_invalid");

        assertThat(response).isInstanceOf(PlanDraft.class);
        assertThat(((PlanDraft) response).validation().ok()).isFalse();
        assertThat(store.approvedPlan("org_a", "plan_invalid")).isEmpty();
        verifyNoInteractions(executor);
    }

    @Test
    void importPlanRejectsDuplicateIdUnlessReplaceIsExplicit() {
        var plan = validPlan("plan_import_duplicate");
        store.saveDraft("org_a", draft(plan));

        assertThatThrownBy(() -> controller.importPlan(plan, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("replace=true");

        var replaced = controller.importPlan(plan, true);
        assertThat(replaced.plan().id()).isEqualTo("plan_import_duplicate");
    }

    @Test
    void importPlanWritesAuditEventWithArchiveHashAndApprovalInvalidationSignal() {
        var audit = mock(AuditService.class);
        var localStore = new InMemoryPlanStore();
        var localController = new PlanController(
                mock(Data360Planner.class),
                new PlanValidator(new OperationRegistry()),
                localStore,
                executor,
                audit,
                users,
                new OperationRegistry(),
                null
        );
        var plan = validPlan("plan_import_audit");
        localStore.saveDraft("org_a", draft(plan));

        localController.importPlan(plan, true);

        verify(audit).event(
                eq("org_a"),
                eq(null),
                eq("plan_import_audit"),
                eq(null),
                eq("plan_imported"),
                org.mockito.ArgumentMatchers.argThat(detail ->
                        Boolean.TRUE.equals(detail.get("replace"))
                                && Boolean.TRUE.equals(detail.get("approvalInvalidated"))
                                && String.valueOf(detail.get("sourceArchiveHash")).startsWith("sha256:")
                )
        );
    }

    @Test
    void exportedPlanSpecArchiveCanRoundTripThroughImportShapeAndReplaceSemantics() {
        var sourceStore = new InMemoryPlanStore();
        var exportedPlan = validPlan("plan_roundtrip_archive");
        sourceStore.saveDraft("org_a", draft(exportedPlan));
        var archive = new ArchiveService(sourceStore, new InMemoryAuditService()).planSpec("org_a", exportedPlan.id());

        assertThat(archive.archiveType()).isEqualTo("planspec");
        assertThat(archive.plan().id()).isEqualTo(exportedPlan.id());
        assertThat(archive.taskHashes())
                .isNotEmpty()
                .allSatisfy(taskHash -> {
                    assertThat(taskHash).containsKeys("resource", "schemaHash", "bindingVersion");
                    assertThat(taskHash.get("resource")).asString().startsWith("urn:salesforce:data360:capability:");
                    assertThat(taskHash.get("schemaHash")).asString().startsWith("sha256:");
                });
        assertThat(archive.artifacts())
                .anySatisfy(artifact -> {
                    assertThat(artifact.type()).isEqualTo("plan_dag");
                    assertThat(artifact.data()).containsKeys("nodes", "edges", "topologicalOrder");
                });

        var importShape = planSpecFromArchive(archive.plan());
        var imported = controller.importPlan(importShape, false);

        assertThat(imported.plan().id()).isEqualTo(exportedPlan.id());
        assertThat(imported.validation().ok()).isTrue();
        assertThat(imported.artifacts()).anyMatch(artifact -> "plan_dag".equals(artifact.type()));
        assertThat(imported.operationBindings()).isNotEmpty();

        assertThatThrownBy(() -> controller.importPlan(importShape, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("replace=true");

        var replaced = controller.importPlan(importShape, true);
        assertThat(replaced.plan().id()).isEqualTo(exportedPlan.id());
        assertThat(replaced.validation().ok()).isTrue();
    }

    private PlanDraft draft(PlanSpec plan) {
        return new PlanDraft(plan, new PlanValidationResult(List.of()), List.of("draft_plan", "validate_plan"));
    }

    private PlanSpec planSpecFromArchive(ExecutionRunArchive.Plan archived) {
        return new PlanSpec(
                archived.schemaVersion(),
                archived.id(),
                archived.scenarioId(),
                archived.goal(),
                archived.context(),
                archived.definition(),
                archived.steps().stream()
                        .map(step -> new PlanStep(
                                step.id(),
                                step.title(),
                                PlanPhase.from(step.phase()),
                                Data360Action.from(step.action()),
                                step.input(),
                                step.dependsOn(),
                                step.inputBindings(),
                                step.needsApproval()
                        ))
                        .toList()
        );
    }

    private PlanSpec validPlan(String id) {
        return new PlanSpec(id, "Preview audience", context(), List.of(queryStep()));
    }

    private PlanStep queryStep() {
        return new PlanStep(
                "preview",
                "Preview audience",
                Data360Action.QUERY,
                Map.of("sql", "SELECT unified_individual_id FROM UnifiedIndividual LIMIT 10"),
                List.of(),
                false
        );
    }

    private PlanContext context() {
        return new PlanContext("org", "default", "sandbox");
    }
}
