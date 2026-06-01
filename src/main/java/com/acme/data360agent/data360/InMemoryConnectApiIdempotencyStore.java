package com.acme.data360agent.data360;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "memory")
public class InMemoryConnectApiIdempotencyStore implements ConnectApiIdempotencyStore {
    private final ConcurrentHashMap<String, Data360CallResult> completed = new ConcurrentHashMap<>();

    @Override
    public Optional<Data360CallResult> completed(String key) {
        return Optional.ofNullable(completed.get(key));
    }

    @Override
    public Data360CallResult remember(String key, Data360CallResult result) {
        completed.putIfAbsent(key, result);
        return completed.get(key);
    }
}
