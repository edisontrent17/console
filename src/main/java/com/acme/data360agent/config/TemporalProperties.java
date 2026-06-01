package com.acme.data360agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.temporal")
public record TemporalProperties(
        String target,
        String namespace,
        String taskQueue,
        String workflowIdPrefix,
        Boolean workerEnabled
) {
    public String resolvedTarget() {
        return target == null || target.isBlank() ? "127.0.0.1:7233" : target;
    }

    public String resolvedNamespace() {
        return namespace == null || namespace.isBlank() ? "default" : namespace;
    }

    public String resolvedTaskQueue() {
        return taskQueue == null || taskQueue.isBlank() ? "data360-plan-task-queue" : taskQueue;
    }

    public String resolvedWorkflowIdPrefix() {
        return workflowIdPrefix == null || workflowIdPrefix.isBlank() ? "data360-plan" : workflowIdPrefix;
    }

    public boolean resolvedWorkerEnabled() {
        return workerEnabled == null || workerEnabled;
    }

    public String workflowId(String runId) {
        return resolvedWorkflowIdPrefix() + "-" + runId;
    }
}
