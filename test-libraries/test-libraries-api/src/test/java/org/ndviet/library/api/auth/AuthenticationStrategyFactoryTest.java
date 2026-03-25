package org.ndviet.library.api.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationStrategyFactoryTest {

    @Test
    void shouldCreateBearerStrategy() {
        AuthenticationStrategy strategy = AuthenticationStrategyFactory.createInstance("bearer", null, null, "token-123");

        assertInstanceOf(BearerTokenAuthenticationStrategy.class, strategy);
    }

    @Test
    void shouldCreateNoAuthStrategyWhenTypeIsMissing() {
        AuthenticationStrategy strategy = AuthenticationStrategyFactory.createInstance(null, null, null, null);

        assertInstanceOf(NoAuthenticationStrategy.class, strategy);
    }

    @Test
    void shouldRejectUnsupportedType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AuthenticationStrategyFactory.createInstance("digest", null, null, null));
    }
}
