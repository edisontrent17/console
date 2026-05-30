package com.acme.data360agent.temporal;

import com.acme.data360agent.plan.PlanSpec;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Map;

@WorkflowInterface
public interface Data360PlanWorkflow {
    @WorkflowMethod
    String run(String runId, PlanSpec plan);

    @SignalMethod
    void approveStep(String stepId, String approvedBy);

    @SignalMethod
    void cancel(String reason);

    @QueryMethod
    Map<String, Object> status();
}
