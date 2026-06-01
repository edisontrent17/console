package com.acme.data360agent.data360;

import com.acme.data360agent.state.JsonStateCodec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnProperty(name = "app.state.store", havingValue = "jdbc", matchIfMissing = true)
public class JdbcConnectApiIdempotencyStore implements ConnectApiIdempotencyStore {
    private final JdbcTemplate jdbc;
    private final JsonStateCodec codec;

    public JdbcConnectApiIdempotencyStore(JdbcTemplate jdbc, JsonStateCodec codec) {
        this.jdbc = jdbc;
        this.codec = codec;
    }

    @Override
    public Optional<Data360CallResult> completed(String key) {
        return jdbc.query("SELECT result_json FROM connect_idempotency_records WHERE idempotency_key = ?",
                        (rs, rowNum) -> codec.read(rs.getString("result_json"), Data360CallResult.class),
                        key)
                .stream()
                .findFirst();
    }

    @Override
    public Data360CallResult remember(String key, Data360CallResult result) {
        var existing = completed(key);
        if (existing.isPresent()) {
            return existing.get();
        }
        try {
            jdbc.update("INSERT INTO connect_idempotency_records (idempotency_key, result_json) VALUES (?, ?)", key, codec.write(result));
            return result;
        } catch (DuplicateKeyException e) {
            return completed(key).orElseThrow(() -> new IllegalStateException("Duplicate Connect API idempotency key without stored result.", e));
        }
    }
}
