package com.acme.data360agent.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import com.acme.data360agent.operation.OperationBindingSnapshot;

import java.util.Map;

@ActivityInterface
public interface PlanRunActivities {
    @ActivityMethod
    void waitingForApproval(String organizationId, String runId, String planId, String stepId, String action);

    @ActivityMethod
    void stepApproved(String organizationId, String runId, String planId, String stepId, String approvedBy);

    @ActivityMethod
    void stepStarted(String organizationId, String runId, String planId, String stepId, String action);

    @ActivityMethod
    void stepToolCallPrepared(String organizationId, String runId, String planId, String stepId, String action, OperationBindingSnapshot binding, Map<String, Object> resolvedInput, String idempotencyKey);

    @ActivityMethod
    void stepSucceeded(String organizationId, String runId, String planId, String stepId, String action, Map<String, Object> output, Map<String, Object> raw);

    @ActivityMethod
    void stepFailed(String organizationId, String runId, String planId, String stepId, String error);

    @ActivityMethod
    void skipMonitorStep(String organizationId, String runId, String planId, String stepId);

    @ActivityMethod
    void completeRun(String organizationId, String runId, String planId);

    @ActivityMethod
    void cancelRun(String organizationId, String runId, String planId, String reason);
}
