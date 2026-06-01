package com.acme.data360agent.execution;

public enum StepStatus {
    PENDING,
    RUNNING,
    WAITING_APPROVAL,
    SUCCEEDED,
    FAILED,
    CANCELED,
    SKIPPED
}
