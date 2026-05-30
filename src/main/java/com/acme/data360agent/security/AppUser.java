package com.acme.data360agent.security;

import java.util.Set;

public record AppUser(
        String username,
        Set<String> authorities,
        boolean authenticated
) {
    public static AppUser anonymous() {
        return new AppUser("anonymous", Set.of(), false);
    }
}
