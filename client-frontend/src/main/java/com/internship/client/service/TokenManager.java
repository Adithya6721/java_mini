package com.internship.client.service;

import java.util.Optional;

public final class TokenManager {
    private static TokenManager INSTANCE;
    private volatile String jwtToken;

    private TokenManager() {}

    public static synchronized TokenManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TokenManager();
        }
        return INSTANCE;
    }

    public void setToken(String token) {
        this.jwtToken = token;
    }

    public Optional<String> getToken() {
        return Optional.ofNullable(jwtToken);
    }

    public void clear() {
        this.jwtToken = null;
    }
}


