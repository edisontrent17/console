package com.acme.data360agent.temporal;

import com.acme.data360agent.config.TemporalProperties;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.SmartLifecycle;

public class TemporalWorkerLifecycle implements SmartLifecycle {
    private final WorkflowClient client;
    private final TemporalProperties properties;
    private final Data360Activities data360Activities;
    private final PlanRunActivities planRunActivities;
    private WorkerFactory factory;
    private boolean running;

    public TemporalWorkerLifecycle(WorkflowClient client, TemporalProperties properties, Data360Activities data360Activities, PlanRunActivities planRunActivities) {
        this.client = client;
        this.properties = properties;
        this.data360Activities = data360Activities;
        this.planRunActivities = planRunActivities;
    }

    @Override
    public void start() {
        if (running || !properties.resolvedWorkerEnabled()) {
            return;
        }
        factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(properties.resolvedTaskQueue());
        worker.registerWorkflowImplementationTypes(Data360PlanWorkflowImpl.class);
        worker.registerActivitiesImplementations(data360Activities, planRunActivities);
        factory.start();
        running = true;
    }

    @Override
    public void stop() {
        if (factory != null) {
            factory.shutdown();
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
