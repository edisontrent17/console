package com.acme.data360agent.archive;

import com.acme.data360agent.execution.PlanRun;
import com.acme.data360agent.execution.PlanRunSupport;
import com.acme.data360agent.execution.StepRun;
import com.acme.data360agent.operation.OperationBindingSnapshot;
import com.acme.data360agent.plan.AslState;
import com.acme.data360agent.plan.AslStateMachine;
import com.acme.data360agent.plan.InputBinding;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanStep;
import com.acme.data360agent.planner.PlanArtifact;
import com.acme.data360agent.support.SensitiveData;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ExecutionRunArchive(
        String id,
        String organizationId,
        String status,
        Instant createdAt,
        Plan plan,
        List<Map<String, Object>> taskHashes,
        List<PlanArtifact> artifacts,
        List<Step> steps
) implements Serializable {
    public ExecutionRunArchive {
        taskHashes = taskHashes == null ? List.of() : List.copyOf(taskHashes);
        artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
        steps = steps == null ? List.of() : List.copyOf(steps);
    }

    public static ExecutionRunArchive from(PlanRun run) {
        return new ExecutionRunArchive(
                run.getId(),
                run.getOrganizationId(),
                run.getStatus().name(),
                run.getCreatedAt(),
                Plan.from(run.getPlan()),
                run.getOperationBindings().stream().map(ExecutionRunArchive::taskHash).toList(),
                redactedArtifacts(run.getApprovedPlan().artifacts()),
                run.getPlan().steps().stream()
                        .map(step -> Step.from(step, PlanRunSupport.stepRun(run, step.id())))
                        .toList()
        );
    }

    static Map<String, Object> taskHash(OperationBindingSnapshot binding) {
        return Map.of(
                "resource", binding.resource(),
                "transport", binding.transport().name(),
                "mcpServerId", binding.mcpServerId() == null ? "" : binding.mcpServerId(),
                "facadeTool", binding.facadeTool() == null ? "" : binding.facadeTool(),
                "underlyingTool", binding.underlyingTool() == null ? "" : binding.underlyingTool(),
                "effect", binding.effect().name(),
                "requiresApproval", binding.requiresApproval(),
                "schemaHash", binding.schemaHash(),
                "connectorDefinitionHash", binding.connectorDefinitionHash(),
                "bindingVersion", binding.bindingVersion()
        );
    }

    static List<PlanArtifact> redactedArtifacts(List<PlanArtifact> artifacts) {
        return artifacts == null ? List.of() : artifacts.stream()
                .map(artifact -> new PlanArtifact(artifact.type(), artifact.title(), SensitiveData.redactMap(artifact.data())))
                .toList();
    }

    public record Plan(
            String schemaVersion,
            String id,
            String scenarioId,
            String goal,
            PlanContext context,
            AslStateMachine definition,
            List<StepSpec> steps
    ) implements Serializable {
        public Plan {
            steps = steps == null ? List.of() : List.copyOf(steps);
        }

        public static Plan from(PlanSpec plan) {
            return new Plan(
                    plan.schemaVersion(),
                    plan.id(),
                    plan.scenarioId(),
                    SensitiveData.redactText(plan.goal()),
                    plan.context(),
                    redactedDefinition(plan.definition()),
                    plan.steps().stream().map(StepSpec::from).toList()
            );
        }
    }

    public record StepSpec(
            String id,
            String title,
            String phase,
            String action,
            Map<String, Object> input,
            List<String> dependsOn,
            Map<String, InputBinding> inputBindings,
            boolean needsApproval
    ) implements Serializable {
        public StepSpec {
            input = input == null ? Map.of() : SensitiveData.redactMap(input);
            dependsOn = dependsOn == null ? List.of() : List.copyOf(dependsOn);
            inputBindings = inputBindings == null ? Map.of() : Map.copyOf(inputBindings);
        }

        public static StepSpec from(PlanStep step) {
            return new StepSpec(
                    step.id(),
                    SensitiveData.redactText(step.title()),
                    step.phase().value(),
                    step.action().value(),
                    step.input(),
                    step.dependsOn(),
                    step.inputBindings(),
                    step.needsApproval()
            );
        }
    }

    public record Step(
            String id,
            String status,
            Instant startedAt,
            Instant finishedAt,
            Map<String, Object> selectedOutput,
            String error
    ) implements Serializable {
        public Step {
            selectedOutput = selectedOutput == null ? Map.of() : SensitiveData.redactMap(selectedOutput);
            error = SensitiveData.redactText(error);
        }

        public static Step from(PlanStep step, StepRun run) {
            return new Step(
                    step.id(),
                    run.getStatus().name(),
                    run.getStartedAt(),
                    run.getFinishedAt(),
                    run.getOutput(),
                    run.getError()
            );
        }
    }

    private static AslStateMachine redactedDefinition(AslStateMachine definition) {
        if (definition == null) {
            return null;
        }
        var states = new LinkedHashMap<String, AslState>();
        definition.states().forEach((name, state) -> states.put(name, redactedState(state)));
        return new AslStateMachine(definition.version(), definition.queryLanguage(), definition.startAt(), states);
    }

    private static AslState redactedState(AslState state) {
        if (state == null) {
            return null;
        }
        return new AslState(
                state.type(),
                SensitiveData.redactText(state.comment()),
                state.resource(),
                SensitiveData.redactMap(state.parameters()),
                state.resultPath(),
                state.next(),
                state.end(),
                state.inputPath(),
                state.outputPath(),
                state.timeoutSeconds(),
                state.heartbeatSeconds(),
                state.retry(),
                state.catchers()
        );
    }
}
