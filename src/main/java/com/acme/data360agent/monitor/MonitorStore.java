package com.acme.data360agent.monitor;

import com.acme.data360agent.execution.PlanStore;

import java.util.Collection;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MonitorStore {
    MonitorDefinition save(MonitorDefinition definition);

    Optional<MonitorDefinition> definition(String id);

    default Optional<MonitorDefinition> definition(String organizationId, String id) {
        var normalized = normalizeOrganizationId(organizationId);
        return definition(id).filter(definition -> definition.organizationId().equals(normalized));
    }

    Optional<MonitorDefinition> definitionFor(String runId, String stepId);

    default Optional<MonitorDefinition> definitionFor(String organizationId, String runId, String stepId) {
        var normalized = normalizeOrganizationId(organizationId);
        return definitionFor(runId, stepId).filter(definition -> definition.organizationId().equals(normalized));
    }

    Collection<MonitorDefinition> definitions();

    default Collection<MonitorDefinition> definitions(String organizationId) {
        var normalized = normalizeOrganizationId(organizationId);
        return definitions().stream()
                .filter(definition -> definition.organizationId().equals(normalized))
                .toList();
    }

    List<MonitorDefinition> claimDue(Instant now, Instant leaseUntil, int limit);

    MonitorRun saveRun(MonitorRun run);

    List<MonitorRun> runsFor(String monitorId);

    default List<MonitorRun> runsFor(String organizationId, String monitorId) {
        var normalized = normalizeOrganizationId(organizationId);
        return runsFor(monitorId).stream()
                .filter(run -> run.organizationId().equals(normalized))
                .toList();
    }

    MonitorRecommendation saveRecommendation(MonitorRecommendation recommendation);

    boolean reviewRecommendation(String recommendationId, MonitorRecommendationStatus status, Instant reviewedAt);

    default boolean reviewRecommendation(String organizationId, String recommendationId, MonitorRecommendationStatus status, Instant reviewedAt) {
        return recommendation(organizationId, recommendationId)
                .filter(recommendation -> recommendation.status() == MonitorRecommendationStatus.PENDING_APPROVAL)
                .map(recommendation -> reviewRecommendation(recommendationId, status, reviewedAt))
                .orElse(false);
    }

    Optional<MonitorRecommendation> recommendation(String id);

    default Optional<MonitorRecommendation> recommendation(String organizationId, String id) {
        var normalized = normalizeOrganizationId(organizationId);
        return recommendation(id).filter(recommendation -> recommendation.organizationId().equals(normalized));
    }

    Optional<MonitorRecommendation> pendingRecommendationFor(String monitorId);

    default Optional<MonitorRecommendation> pendingRecommendationFor(String organizationId, String monitorId) {
        var normalized = normalizeOrganizationId(organizationId);
        return pendingRecommendationFor(monitorId)
                .filter(recommendation -> recommendation.organizationId().equals(normalized));
    }

    List<MonitorRecommendation> recommendations();

    default List<MonitorRecommendation> recommendations(String organizationId) {
        var normalized = normalizeOrganizationId(organizationId);
        return recommendations().stream()
                .filter(recommendation -> recommendation.organizationId().equals(normalized))
                .toList();
    }

    static String normalizeOrganizationId(String organizationId) {
        return organizationId == null || organizationId.isBlank() ? PlanStore.DEFAULT_ORGANIZATION_ID : organizationId;
    }
}
