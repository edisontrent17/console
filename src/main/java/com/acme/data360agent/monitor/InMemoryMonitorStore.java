package com.acme.data360agent.monitor;

import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Collection;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "memory")
public class InMemoryMonitorStore implements MonitorStore {
    private final ConcurrentHashMap<String, MonitorDefinition> definitions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MonitorRun> runs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MonitorRecommendation> recommendations = new ConcurrentHashMap<>();

    @Override
    public MonitorDefinition save(MonitorDefinition definition) {
        definitions.put(definition.id(), definition);
        return definition;
    }

    @Override
    public Optional<MonitorDefinition> definition(String id) {
        return Optional.ofNullable(definitions.get(id));
    }

    @Override
    public Optional<MonitorDefinition> definition(String organizationId, String id) {
        var normalized = MonitorStore.normalizeOrganizationId(organizationId);
        return Optional.ofNullable(definitions.get(id))
                .filter(definition -> definition.organizationId().equals(normalized));
    }

    @Override
    public Optional<MonitorDefinition> definitionFor(String runId, String stepId) {
        return definitions.values().stream()
                .filter(definition -> definition.runId().equals(runId) && definition.stepId().equals(stepId))
                .findFirst();
    }

    @Override
    public Optional<MonitorDefinition> definitionFor(String organizationId, String runId, String stepId) {
        var normalized = MonitorStore.normalizeOrganizationId(organizationId);
        return definitions.values().stream()
                .filter(definition -> definition.organizationId().equals(normalized))
                .filter(definition -> definition.runId().equals(runId) && definition.stepId().equals(stepId))
                .findFirst();
    }

    @Override
    public Collection<MonitorDefinition> definitions() {
        return definitions.values();
    }

    @Override
    public Collection<MonitorDefinition> definitions(String organizationId) {
        var normalized = MonitorStore.normalizeOrganizationId(organizationId);
        return definitions.values().stream()
                .filter(definition -> definition.organizationId().equals(normalized))
                .toList();
    }

    @Override
    public List<MonitorDefinition> claimDue(Instant now, Instant leaseUntil, int limit) {
        return definitions.values().stream()
                .filter(definition -> definition.status() != MonitorStatus.PAUSED)
                .filter(definition -> definition.nextRunAt() == null || !definition.nextRunAt().isAfter(now))
                .filter(definition -> definition.leaseUntil() == null || !definition.leaseUntil().isAfter(now))
                .sorted((left, right) -> sortInstant(left.nextRunAt(), left.createdAt()).compareTo(sortInstant(right.nextRunAt(), right.createdAt())))
                .limit(limit)
                .map(definition -> save(definition.withLease(leaseUntil)))
                .toList();
    }

    @Override
    public MonitorRun saveRun(MonitorRun run) {
        runs.put(run.id(), run);
        return run;
    }

    @Override
    public List<MonitorRun> runsFor(String monitorId) {
        return runs.values().stream()
                .filter(run -> run.monitorId().equals(monitorId))
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    @Override
    public MonitorRecommendation saveRecommendation(MonitorRecommendation recommendation) {
        recommendations.put(recommendation.id(), recommendation);
        return recommendation;
    }

    @Override
    public boolean reviewRecommendation(String recommendationId, MonitorRecommendationStatus status, Instant reviewedAt) {
        var reviewed = recommendations.computeIfPresent(recommendationId, (id, current) -> {
            if (current.status() != MonitorRecommendationStatus.PENDING_APPROVAL) {
                return current;
            }
            return current.withStatus(status, reviewedAt);
        });
        return reviewed != null && reviewed.status() == status;
    }

    @Override
    public boolean reviewRecommendation(String organizationId, String recommendationId, MonitorRecommendationStatus status, Instant reviewedAt) {
        var normalized = MonitorStore.normalizeOrganizationId(organizationId);
        var reviewed = recommendations.computeIfPresent(recommendationId, (id, current) -> {
            if (!current.organizationId().equals(normalized) || current.status() != MonitorRecommendationStatus.PENDING_APPROVAL) {
                return current;
            }
            return current.withStatus(status, reviewedAt);
        });
        return reviewed != null && reviewed.organizationId().equals(normalized) && reviewed.status() == status;
    }

    @Override
    public Optional<MonitorRecommendation> recommendation(String id) {
        return Optional.ofNullable(recommendations.get(id));
    }

    @Override
    public Optional<MonitorRecommendation> recommendation(String organizationId, String id) {
        var normalized = MonitorStore.normalizeOrganizationId(organizationId);
        return Optional.ofNullable(recommendations.get(id))
                .filter(recommendation -> recommendation.organizationId().equals(normalized));
    }

    @Override
    public Optional<MonitorRecommendation> pendingRecommendationFor(String monitorId) {
        return recommendations.values().stream()
                .filter(recommendation -> recommendation.monitorId().equals(monitorId))
                .filter(recommendation -> recommendation.status() == MonitorRecommendationStatus.PENDING_APPROVAL)
                .findFirst();
    }

    @Override
    public Optional<MonitorRecommendation> pendingRecommendationFor(String organizationId, String monitorId) {
        var normalized = MonitorStore.normalizeOrganizationId(organizationId);
        return recommendations.values().stream()
                .filter(recommendation -> recommendation.organizationId().equals(normalized))
                .filter(recommendation -> recommendation.monitorId().equals(monitorId))
                .filter(recommendation -> recommendation.status() == MonitorRecommendationStatus.PENDING_APPROVAL)
                .findFirst();
    }

    @Override
    public List<MonitorRecommendation> recommendations() {
        return recommendations.values().stream()
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    @Override
    public List<MonitorRecommendation> recommendations(String organizationId) {
        var normalized = MonitorStore.normalizeOrganizationId(organizationId);
        return recommendations.values().stream()
                .filter(recommendation -> recommendation.organizationId().equals(normalized))
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    private Instant sortInstant(Instant nextRunAt, Instant createdAt) {
        return nextRunAt == null ? createdAt : nextRunAt;
    }
}
