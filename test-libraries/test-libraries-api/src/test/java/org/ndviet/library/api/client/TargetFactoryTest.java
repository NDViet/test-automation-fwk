package org.ndviet.library.api.client;

import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.ndviet.library.api.auth.AuthenticationStrategyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TargetFactoryTest {

    @Test
    void shouldCreateRequestSpecificationWithBearerHeader() {
        RequestSpecification specification =
                TargetFactory.createInstance("http://localhost:8180",
                        AuthenticationStrategyFactory.createBearer("local-api-token"));

        FilterableRequestSpecification filterable = (FilterableRequestSpecification) specification;
        assertEquals("http://localhost:8180", filterable.getBaseUri());
        assertEquals("Bearer local-api-token", filterable.getHeaders().getValue("Authorization"));
    }

    @Test
    void shouldRejectBlankBaseUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TargetFactory.createInstance(" ", AuthenticationStrategyFactory.createNone()));
    }
}
