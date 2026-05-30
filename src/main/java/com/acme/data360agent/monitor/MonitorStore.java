package com.acme.data360agent.monitor;

import java.util.Collection;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MonitorStore {
    MonitorDefinition save(MonitorDefinition definition);

    Optional<MonitorDefinition> definition(String id);

    Optional<MonitorDefinition> definitionFor(String runId, String stepId);

    Collection<MonitorDefinition> definitions();

    List<MonitorDefinition> claimDue(Instant now, Instant leaseUntil, int limit);

    MonitorRun saveRun(MonitorRun run);

    List<MonitorRun> runsFor(String monitorId);

    MonitorRecommendation saveRecommendation(MonitorRecommendation recommendation);

    Optional<MonitorRecommendation> recommendation(String id);

    Optional<MonitorRecommendation> pendingRecommendationFor(String monitorId);

    List<MonitorRecommendation> recommendations();
}
