package com.acme.data360agent.temporal;

import com.acme.data360agent.data360.Data360CallResult;
import com.acme.data360agent.data360.Data360Client;
import com.acme.data360agent.execution.RunContext;
import com.acme.data360agent.operation.OperationDefinition;
import com.acme.data360agent.plan.PlanContext;
import com.acme.data360agent.plan.PlanStep;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Data360ActivitiesImpl implements Data360Activities {
    private final Data360Client data360Client;

    public Data360ActivitiesImpl(Data360Client data360Client) {
        this.data360Client = data360Client;
    }

    @Override
    public Data360CallResult executeStep(String runId, String planId, PlanContext context, OperationDefinition operation, PlanStep step, Map<String, Object> resolvedInput) {
        return data360Client.call(operation, step, resolvedInput, new RunContext(runId, planId, context));
    }
}
