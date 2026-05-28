package com.acme.data360agent.temporal;

/**
 * This package is the Temporal boundary for the app.
 *
 * The runnable MVP uses LocalPlanExecutor so the web app can be tried without a
 * Temporal cluster. The workflow and activity interfaces above are the intended
 * production cutover point: move the loop in LocalPlanExecutor into a
 * Data360PlanWorkflow implementation, and keep Data360 MCP calls inside
 * Data360Activities.
 */
public final class TemporalNotes {
    private TemporalNotes() {
    }
}
