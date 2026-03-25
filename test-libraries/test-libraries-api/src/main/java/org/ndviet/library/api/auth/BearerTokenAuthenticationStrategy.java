package org.ndviet.library.api.auth;

import io.restassured.builder.RequestSpecBuilder;
import org.apache.commons.lang3.StringUtils;

public class BearerTokenAuthenticationStrategy implements AuthenticationStrategy {

    private final String bearerToken;

    public BearerTokenAuthenticationStrategy(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    @Override
    public void apply(RequestSpecBuilder builder) {
        if (StringUtils.isBlank(bearerToken)) {
            throw new IllegalArgumentException("Bearer token must not be blank");
        }
        builder.addHeader("Authorization", "Bearer " + bearerToken.trim());
    }
}
