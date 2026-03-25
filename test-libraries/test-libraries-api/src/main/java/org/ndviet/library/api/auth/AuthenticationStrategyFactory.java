package org.ndviet.library.api.auth;

import org.apache.commons.lang3.StringUtils;
import org.ndviet.library.configuration.ConfigurationManager;

import static org.ndviet.library.configuration.Constants.API_AUTH_BASIC_PASSWORD;
import static org.ndviet.library.configuration.Constants.API_AUTH_BASIC_USERNAME;
import static org.ndviet.library.configuration.Constants.API_AUTH_BEARER_TOKEN;
import static org.ndviet.library.configuration.Constants.API_AUTH_TYPE;

public final class AuthenticationStrategyFactory {

    private AuthenticationStrategyFactory() {
    }

    public static AuthenticationStrategy createInstance() {
        return createInstance(
                ConfigurationManager.getInstance().getValue(API_AUTH_TYPE),
                ConfigurationManager.getInstance().getValue(API_AUTH_BASIC_USERNAME),
                ConfigurationManager.getInstance().getValue(API_AUTH_BASIC_PASSWORD),
                ConfigurationManager.getInstance().getValue(API_AUTH_BEARER_TOKEN));
    }

    public static AuthenticationStrategy createInstance(
            String authType,
            String username,
            String password,
            String bearerToken) {
        String normalizedType = StringUtils.defaultIfBlank(authType, AuthenticationType.NONE.name());
        AuthenticationType authenticationType = AuthenticationType.valueOf(normalizedType.trim().toUpperCase());
        switch (authenticationType) {
            case BASIC:
                return createBasic(username, password);
            case BEARER:
                return createBearer(bearerToken);
            case NONE:
            default:
                return createNone();
        }
    }

    public static AuthenticationStrategy createNone() {
        return new NoAuthenticationStrategy();
    }

    public static AuthenticationStrategy createBearer(String bearerToken) {
        return new BearerTokenAuthenticationStrategy(bearerToken);
    }

    public static AuthenticationStrategy createBasic(String username, String password) {
        return new BasicAuthenticationStrategy(username, password);
    }
}
