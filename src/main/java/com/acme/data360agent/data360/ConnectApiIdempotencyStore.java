package com.acme.data360agent.data360;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectApiIdempotencyStore {
    private final ConcurrentHashMap<String, Data360CallResult> completed = new ConcurrentHashMap<>();

    public Optional<Data360CallResult> completed(String key) {
        return Optional.ofNullable(completed.get(key));
    }

    public Data360CallResult remember(String key, Data360CallResult result) {
        completed.putIfAbsent(key, result);
        return completed.get(key);
    }
}
