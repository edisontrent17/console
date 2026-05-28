package com.acme.data360agent.temporal;

import com.acme.data360agent.data360.Data360CallResult;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanStep;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.Map;

@ActivityInterface
public interface Data360Activities {
    @ActivityMethod
    Data360CallResult executeStep(
            String runId,
            String planId,
            PlanContext context,
            OperationDefinition operation,
            PlanStep step,
            Map<String, Object> resolvedInput
    );
}
