package com.acme.data360agent.data360;

import java.util.Optional;

public interface ConnectApiIdempotencyStore {
    Optional<Data360CallResult> completed(String key);

    Data360CallResult remember(String key, Data360CallResult result);
}
