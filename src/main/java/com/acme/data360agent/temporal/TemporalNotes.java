package com.acme.data360agent.temporal;

/**
 * This package is the Temporal boundary for the app. LocalPlanExecutor remains
 * the default for demo use, while TemporalPlanExecutor can be enabled with
 * app.executor=temporal for durable workflow orchestration.
 *
 * Workflow code owns deterministic sequencing and approval waits. Activities own
 * side effects: Data360Activities calls the configured Data 360 client, and
 * PlanRunActivities persists run state, audit records, approvals, and monitor
 * registration.
 */
public final class TemporalNotes {
    private TemporalNotes() {
    }
}
