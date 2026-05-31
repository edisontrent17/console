package com.acme.data360agent.planner;

import com.acme.data360agent.plan.PlanSpec;
import com.acme.data360agent.plan.PlanValidationResult;
import com.acme.data360agent.plan.PlanValidator;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Service
public class LangGraphData360Planner implements Data360Planner {
    private static final int MAX_REPAIR_ATTEMPTS = 2;

    private final LlmPlanGenerator generator;
    private final PlanValidator validator;

    public LangGraphData360Planner(LlmPlanGenerator generator, PlanValidator validator) {
        this.generator = generator;
        this.validator = validator;
    }

    @Override
    public PlanDraft draft(PlanRequest request) {
        try {
            var graph = new StateGraph<>(AgentState::new)
                    .addNode("draft_plan", node_async(this::draftPlan))
                    .addNode("validate_plan", node_async(this::validatePlan))
                    .addEdge("__START__", "draft_plan")
                    .addEdge("draft_plan", "validate_plan")
                    .addEdge("validate_plan", "__END__")
                    .compile();

            var result = graph.invoke(Map.of("request", request, "stages", new ArrayList<String>()));
            var state = result.orElseThrow(() -> new IllegalStateException("Planner graph returned no state."));
            return new PlanDraft(
                    state.<PlanSpec>value("plan").orElseThrow(),
                    state.<PlanValidationResult>value("validation").orElseThrow(),
                    state.<List<String>>value("stages").orElse(List.of())
            );
        } catch (Exception e) {
            throw new IllegalStateException("LangGraph planner failed.", e);
        }
    }

    private Map<String, Object> draftPlan(AgentState state) {
        PlanRequest request = state.<PlanRequest>value("request").orElseThrow();
        var plan = generator.generate(request);
        var stages = appendStage(state, "draft_plan");
        return Map.of("plan", plan, "stages", stages);
    }

    private Map<String, Object> validatePlan(AgentState state) {
        PlanRequest request = state.<PlanRequest>value("request").orElseThrow();
        PlanSpec plan = state.<PlanSpec>value("plan").orElseThrow();
        var stages = appendStage(state, "validate_plan");
        PlanValidationResult validation = validator.validate(plan);
        for (var attempt = 1; !validation.ok() && generator.canRepair() && attempt <= MAX_REPAIR_ATTEMPTS; attempt++) {
            stages.add("repair_plan_" + attempt);
            plan = generator.repair(request, plan, validation);
            stages.add("validate_repair_" + attempt);
            validation = validator.validate(plan);
        }
        return Map.of("plan", plan, "validation", validation, "stages", stages);
    }

    @SuppressWarnings("unchecked")
    private List<String> appendStage(AgentState state, String stage) {
        var current = new ArrayList<>(state.<List<String>>value("stages").orElse(List.of()));
        current.add(stage);
        return current;
    }
}
