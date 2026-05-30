package com.acme.data360agent.monitor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration(proxyBeanMethods = false)
@EnableScheduling
@ConditionalOnProperty(prefix = "app.monitors.scheduler", name = "enabled", havingValue = "true")
public class MonitorScheduler {
    private final MonitorService monitors;

    public MonitorScheduler(MonitorService monitors) {
        this.monitors = monitors;
    }

    @Scheduled(fixedDelayString = "${app.monitors.scheduler.fixed-delay-ms:60000}")
    public void runDueMonitors() {
        monitors.runScheduled();
    }
}
