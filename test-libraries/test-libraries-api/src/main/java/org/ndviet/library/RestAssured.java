package org.ndviet.library;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Collections;
import java.util.Map;
import org.ndviet.library.api.auth.AuthenticationStrategy;
import org.ndviet.library.api.auth.AuthenticationStrategyFactory;
import org.ndviet.library.api.client.ApiSessionManager;
import org.ndviet.library.api.client.TargetFactory;
import org.ndviet.library.configuration.ConfigurationManager;

import static org.ndviet.library.configuration.Constants.API_GRAPHQL_PATH;

public final class RestAssured {

    private RestAssured() {
    }

    private static RequestSpecification getRequestSpecification() {
        RequestSpecification specification = ApiSessionManager.getInstance().getRequestSpecification();
        if (specification == null) {
            specification = openSession();
        }
        return specification;
    }

    public static RequestSpecification openSession() {
        ApiSessionManager.getInstance().setRequestSpecification(TargetFactory.createInstance());
        return getRequestSpecification();
    }

    public static RequestSpecification openSession(String baseUrl) {
        ApiSessionManager.getInstance().setRequestSpecification(TargetFactory.createInstance(baseUrl));
        return getRequestSpecification();
    }

    public static RequestSpecification openSessionWithBearerToken(String baseUrl, String bearerToken) {
        ApiSessionManager.getInstance().setRequestSpecification(
                TargetFactory.createInstance(baseUrl, AuthenticationStrategyFactory.createBearer(bearerToken)));
        return getRequestSpecification();
    }

    public static RequestSpecification openSessionWithBasicAuth(String baseUrl, String username, String password) {
        ApiSessionManager.getInstance().setRequestSpecification(
                TargetFactory.createInstance(baseUrl, AuthenticationStrategyFactory.createBasic(username, password)));
        return getRequestSpecification();
    }

    public static RequestSpecification openSessionWithoutAuthentication(String baseUrl) {
        ApiSessionManager.getInstance().setRequestSpecification(
                TargetFactory.createInstance(baseUrl, AuthenticationStrategyFactory.createNone()));
        return getRequestSpecification();
    }

    public static RequestSpecification openSession(String baseUrl, AuthenticationStrategy authenticationStrategy) {
        ApiSessionManager.getInstance().setRequestSpecification(
                TargetFactory.createInstance(baseUrl, authenticationStrategy));
        return getRequestSpecification();
    }

    public static io.restassured.specification.RequestSpecification given() {
        return io.restassured.RestAssured.given().spec(getRequestSpecification());
    }

    public static Response get(String path) {
        return given().when().get(path);
    }

    public static Response delete(String path) {
        return given().when().delete(path);
    }

    public static Response post(String path, Object body) {
        return given().contentType(ContentType.JSON).body(body).when().post(path);
    }

    public static Response put(String path, Object body) {
        return given().contentType(ContentType.JSON).body(body).when().put(path);
    }

    public static Response patch(String path, Object body) {
        return given().contentType(ContentType.JSON).body(body).when().patch(path);
    }

    public static Response graphQl(String document) {
        return graphQl(document, Collections.emptyMap());
    }

    public static Response graphQl(String document, Map<String, ?> variables) {
        return post(getGraphQlPath(), Map.of("query", document, "variables", variables));
    }

    public static void closeSession() {
        ApiSessionManager.getInstance().release();
    }

    private static String getGraphQlPath() {
        String graphqlPath = ConfigurationManager.getInstance().getValue(API_GRAPHQL_PATH);
        return graphqlPath == null || graphqlPath.isBlank() ? "/graphql" : graphqlPath;
    }
}
