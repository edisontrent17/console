package com.acme.data360agent.support;

import java.util.UUID;

public final class Ids {
    private Ids() {
    }

    public static String prefixed(String prefix) {
        return prefix + "_" + shortId();
    }

    public static String shortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
