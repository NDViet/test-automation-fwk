package org.ndviet.library.api.auth;

import io.restassured.builder.RequestSpecBuilder;
import org.apache.commons.lang3.StringUtils;

public class BasicAuthenticationStrategy implements AuthenticationStrategy {

    private final String username;
    private final String password;

    public BasicAuthenticationStrategy(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void apply(RequestSpecBuilder builder) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Basic auth username must not be blank");
        }
        builder.setAuth(io.restassured.RestAssured.preemptive().basic(username, password == null ? "" : password));
    }
}
