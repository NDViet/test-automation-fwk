package org.ndviet.library.api.client;

import io.restassured.specification.RequestSpecification;

public class ApiSessionManager {

    private static final ThreadLocal<ApiSessionManager> INSTANCE = ThreadLocal.withInitial(ApiSessionManager::new);

    private RequestSpecification requestSpecification;

    private ApiSessionManager() {
    }

    public static ApiSessionManager getInstance() {
        return INSTANCE.get();
    }

    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    public void setRequestSpecification(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public void release() {
        this.requestSpecification = null;
        INSTANCE.remove();
    }
}
