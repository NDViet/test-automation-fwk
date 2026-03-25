package org.ndviet.library.api.auth;

import io.restassured.builder.RequestSpecBuilder;

public class NoAuthenticationStrategy implements AuthenticationStrategy {
    @Override
    public void apply(RequestSpecBuilder builder) {
        // Intentionally empty.
    }
}
