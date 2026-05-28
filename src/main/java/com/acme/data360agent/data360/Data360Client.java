package com.acme.data360agent.data360;

import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.PlanStep;

import java.util.Map;

public interface Data360Client {
    Data360CallResult call(OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput, RunContext context);
}
