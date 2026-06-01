package com.acme.data360agent.temporal;

import com.acme.data360agent.data360.Data360Client;
import com.acme.data360agent.execution.OperationBindingDefinitions;
import com.acme.data360agent.execution.RunContext;
import org.springframework.stereotype.Component;

@Component
public class Data360ActivitiesImpl implements Data360Activities {
    private final Data360Client data360Client;

    public Data360ActivitiesImpl(Data360Client data360Client) {
        this.data360Client = data360Client;
    }

    @Override
    public ActivityResult executeStep(ActivityCommand command) {
        var operation = OperationBindingDefinitions.from(command.binding());
        var result = data360Client.call(
                operation,
                command.binding(),
                command.step(),
                command.resolvedInput(),
                new RunContext(command.organizationId(), command.runId(), command.planId(), command.context(), command.idempotencyKey())
        );
        return new ActivityResult(result.output(), result.raw());
    }
}
