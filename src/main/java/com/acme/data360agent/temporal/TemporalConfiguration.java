package com.acme.data360agent.temporal;

import com.acme.data360agent.config.TemporalProperties;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "app.executor", havingValue = "temporal")
public class TemporalConfiguration {
    @Bean
    public WorkflowServiceStubs workflowServiceStubs(TemporalProperties properties) {
        return WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions.newBuilder()
                .setTarget(properties.resolvedTarget())
                .build());
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs, TemporalProperties properties) {
        return WorkflowClient.newInstance(serviceStubs, WorkflowClientOptions.newBuilder()
                .setNamespace(properties.resolvedNamespace())
                .build());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.temporal", name = "worker-enabled", havingValue = "true", matchIfMissing = true)
    public SmartLifecycle temporalWorkerLifecycle(
            WorkflowClient client,
            TemporalProperties properties,
            Data360Activities data360Activities,
            PlanRunActivities planRunActivities
    ) {
        return new TemporalWorkerLifecycle(client, properties, data360Activities, planRunActivities);
    }
}
