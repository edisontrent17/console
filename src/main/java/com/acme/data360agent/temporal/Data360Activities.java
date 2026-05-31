package com.acme.data360agent.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface Data360Activities {
    @ActivityMethod
    ActivityResult executeStep(ActivityCommand command);
}
