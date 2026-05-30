package com.acme.data360agent.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.Map;

@ActivityInterface
public interface PlanRunActivities {
    @ActivityMethod
    void waitingForApproval(String runId, String planId, String stepId, String action);

    @ActivityMethod
    void stepApproved(String runId, String planId, String stepId);

    @ActivityMethod
    void stepStarted(String runId, String planId, String stepId, String action);

    @ActivityMethod
    void stepSucceeded(String runId, String planId, String stepId, String action, Map<String, Object> output, Map<String, Object> raw);

    @ActivityMethod
    void stepFailed(String runId, String planId, String stepId, String error);

    @ActivityMethod
    void skipMonitorStep(String runId, String planId, String stepId);

    @ActivityMethod
    void completeRun(String runId, String planId);

    @ActivityMethod
    void cancelRun(String runId, String planId, String reason);
}
