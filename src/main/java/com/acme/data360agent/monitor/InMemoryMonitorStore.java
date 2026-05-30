package com.acme.data360agent.monitor;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryMonitorStore {
    private final ConcurrentHashMap<String, MonitorDefinition> definitions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MonitorRun> runs = new ConcurrentHashMap<>();

    public MonitorDefinition save(MonitorDefinition definition) {
        definitions.put(definition.id(), definition);
        return definition;
    }

    public Optional<MonitorDefinition> definition(String id) {
        return Optional.ofNullable(definitions.get(id));
    }

    public Optional<MonitorDefinition> definitionFor(String runId, String stepId) {
        return definitions.values().stream()
                .filter(definition -> definition.runId().equals(runId) && definition.stepId().equals(stepId))
                .findFirst();
    }

    public Collection<MonitorDefinition> definitions() {
        return definitions.values();
    }

    public MonitorRun saveRun(MonitorRun run) {
        runs.put(run.id(), run);
        return run;
    }

    public List<MonitorRun> runsFor(String monitorId) {
        return runs.values().stream()
                .filter(run -> run.monitorId().equals(monitorId))
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }
}
