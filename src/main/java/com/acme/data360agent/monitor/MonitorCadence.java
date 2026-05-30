package com.acme.data360agent.monitor;

import java.time.Duration;
import java.time.Instant;

public final class MonitorCadence {
    private MonitorCadence() {
    }

    public static Instant nextRunAt(String cadence, Instant from) {
        var base = from == null ? Instant.now() : from;
        return base.plus(duration(cadence));
    }

    public static Duration leaseDuration() {
        return Duration.ofMinutes(5);
    }

    private static Duration duration(String cadence) {
        if (cadence == null) {
            return Duration.ofDays(1);
        }
        return switch (cadence.trim().toLowerCase()) {
            case "minute", "minutely", "every_minute" -> Duration.ofMinutes(1);
            case "hour", "hourly" -> Duration.ofHours(1);
            case "week", "weekly" -> Duration.ofDays(7);
            default -> Duration.ofDays(1);
        };
    }
}
