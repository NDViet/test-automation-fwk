package org.ndviet.library.api.client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.LinkedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.ndviet.library.api.auth.AuthenticationStrategy;
import org.ndviet.library.api.auth.AuthenticationStrategyFactory;
import org.ndviet.library.configuration.ConfigurationManager;

import static org.ndviet.library.configuration.Constants.API_BASE_PATH;
import static org.ndviet.library.configuration.Constants.API_BASE_URL;
import static org.ndviet.library.configuration.Constants.API_DEFAULT_ACCEPT;
import static org.ndviet.library.configuration.Constants.API_DEFAULT_CONTENT_TYPE;
import static org.ndviet.library.configuration.Constants.API_HEADERS;
import static org.ndviet.library.configuration.Constants.API_RELAXED_HTTPS_VALIDATION;

public final class TargetFactory {

    private TargetFactory() {
    }

    public static RequestSpecification createInstance() {
        return createInstance(ConfigurationManager.getInstance().getValue(API_BASE_URL), AuthenticationStrategyFactory.createInstance());
    }

    public static RequestSpecification createInstance(String baseUrl) {
        return createInstance(baseUrl, AuthenticationStrategyFactory.createInstance());
    }

    public static RequestSpecification createInstance(String baseUrl, AuthenticationStrategy authenticationStrategy) {
        String normalizedBaseUrl = StringUtils.defaultIfBlank(baseUrl, ConfigurationManager.getInstance().getValue(API_BASE_URL));
        if (StringUtils.isBlank(normalizedBaseUrl)) {
            throw new IllegalArgumentException("API base URL must not be blank");
        }

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(normalizedBaseUrl.trim())
                .setContentType(resolveOrDefault(API_DEFAULT_CONTENT_TYPE, ContentType.JSON.toString()))
                .setAccept(resolveOrDefault(API_DEFAULT_ACCEPT, ContentType.JSON.toString()));

        String basePath = ConfigurationManager.getInstance().getValue(API_BASE_PATH);
        if (StringUtils.isNotBlank(basePath)) {
            builder.setBasePath(basePath.trim());
        }

        LinkedHashMap<?, ?> headers = ConfigurationManager.getInstance().getMapValues(API_HEADERS);
        if (headers != null) {
            headers.forEach((key, value) -> builder.addHeader(String.valueOf(key), String.valueOf(value)));
        }

        if (Boolean.parseBoolean(resolveOrDefault(API_RELAXED_HTTPS_VALIDATION, Boolean.FALSE.toString()))) {
            builder.setRelaxedHTTPSValidation();
        }

        if (authenticationStrategy != null) {
            authenticationStrategy.apply(builder);
        }
        return builder.build();
    }

    private static String resolveOrDefault(String key, String fallback) {
        String value = ConfigurationManager.getInstance().getValue(key);
        return StringUtils.defaultIfBlank(value, fallback);
    }
}
