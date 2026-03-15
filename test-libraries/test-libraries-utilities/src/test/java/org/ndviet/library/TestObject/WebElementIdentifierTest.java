package org.ndviet.library.TestObject;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WebElementIdentifierTest {

    @Test
    public void normalizeIdentifiers_scalarLocator_returnsSingleValue() {
        List<String> identifiers = WebElementIdentifier.normalizeIdentifiers("xpath=//button[@id='login']");

        assertEquals(1, identifiers.size());
        assertEquals("xpath=//button[@id='login']", identifiers.get(0));
    }

    @Test
    public void normalizeIdentifiers_locatorList_returnsSameOrder() {
        List<String> identifiers = WebElementIdentifier.normalizeIdentifiers(Arrays.asList("id=submit", "cssSelector=.submit-btn"));

        assertEquals(Arrays.asList("id=submit", "cssSelector=.submit-btn"), identifiers);
    }

    @Test
    public void normalizeIdentifiers_primaryAndFallbacksMap_returnsPrimaryThenFallbacks() {
        Map<String, Object> locatorMap = new LinkedHashMap<>();
        locatorMap.put("primary", "cssSelector=.login-button");
        locatorMap.put("fallbacks", Arrays.asList("id=login", "xpath=//button[@type='submit']"));

        List<String> identifiers = WebElementIdentifier.normalizeIdentifiers(locatorMap);

        assertEquals(
                Arrays.asList("cssSelector=.login-button", "id=login", "xpath=//button[@type='submit']"),
                identifiers
        );
    }

    @Test
    public void normalizeIdentifiers_primaryAndFallbacksMap_caseInsensitiveKeys_areSupported() {
        Map<String, Object> locatorMap = new LinkedHashMap<>();
        locatorMap.put("Primary", "id=search-input");
        locatorMap.put("Fallbacks", "xpath=//input[@name='q']");

        List<String> identifiers = WebElementIdentifier.normalizeIdentifiers(locatorMap);

        assertEquals(Arrays.asList("id=search-input", "xpath=//input[@name='q']"), identifiers);
    }

    @Test
    public void normalizeIdentifiers_typedLocatorMap_returnsLocatorList() {
        Map<String, Object> locatorMap = new LinkedHashMap<>();
        locatorMap.put("cssSelector", ".login-button");
        locatorMap.put("role", "button");
        locatorMap.put("id", "login");

        List<String> identifiers = WebElementIdentifier.normalizeIdentifiers(locatorMap);

        assertEquals(Arrays.asList("cssselector=.login-button", "role=button", "id=login"), identifiers);
    }

    @Test
    public void normalizeObjectDefinition_withFrameAndShadowParents_returnsOrderedParentContexts() {
        Map<String, Object> frameParent = new LinkedHashMap<>();
        frameParent.put("type", "frame");
        frameParent.put("locator", "id=main-frame");

        Map<String, Object> shadowParent = new LinkedHashMap<>();
        shadowParent.put("shadow", "cssSelector=app-shell");

        Map<String, Object> objectDefinitionMap = new LinkedHashMap<>();
        objectDefinitionMap.put("primary", "id=submit");
        objectDefinitionMap.put("fallbacks", Arrays.asList("cssSelector=.submit"));
        objectDefinitionMap.put("parents", Arrays.asList(frameParent, shadowParent));

        WebElementIdentifier.ObjectDefinition objectDefinition =
                WebElementIdentifier.normalizeObjectDefinition(objectDefinitionMap);

        assertEquals(Arrays.asList("id=submit", "cssSelector=.submit"), objectDefinition.getIdentifiers());
        assertEquals(2, objectDefinition.getParentContexts().size());
        assertEquals(TestObject.ParentType.FRAME, objectDefinition.getParentContexts().get(0).getType());
        assertEquals(Arrays.asList("id=main-frame"), objectDefinition.getParentContexts().get(0).getValues());
        assertEquals(TestObject.ParentType.SHADOW, objectDefinition.getParentContexts().get(1).getType());
        assertEquals(Arrays.asList("cssSelector=app-shell"), objectDefinition.getParentContexts().get(1).getValues());
    }

    @Test
    public void normalizeObjectDefinition_parentWithoutType_throwsException() {
        Map<String, Object> invalidParent = new LinkedHashMap<>();
        invalidParent.put("locator", "id=main-frame");

        Map<String, Object> objectDefinitionMap = new LinkedHashMap<>();
        objectDefinitionMap.put("locator", "id=submit");
        objectDefinitionMap.put("parent", invalidParent);

        assertThrows(IllegalArgumentException.class, () -> WebElementIdentifier.normalizeObjectDefinition(objectDefinitionMap));
    }

    @Test
    public void normalizeObjectDefinition_parentRefByType_resolvesReferencedObject() {
        WebElementIdentifier.ObjectDefinition paymentFrameDefinition = new WebElementIdentifier.ObjectDefinition(
                Arrays.asList("id=payment-iframe"),
                Collections.emptyList()
        );
        Function<String, WebElementIdentifier.ObjectDefinition> resolver = key -> {
            if ("Shared.PaymentFrame".equals(key)) {
                return paymentFrameDefinition;
            }
            return null;
        };

        Map<String, Object> parentMap = new LinkedHashMap<>();
        parentMap.put("type", "frame");
        parentMap.put("ref", "Shared.PaymentFrame");

        Map<String, Object> objectDefinitionMap = new LinkedHashMap<>();
        objectDefinitionMap.put("locator", "id=confirm");
        objectDefinitionMap.put("parent", parentMap);

        WebElementIdentifier.ObjectDefinition objectDefinition =
                WebElementIdentifier.normalizeObjectDefinition(objectDefinitionMap, resolver);

        assertEquals(Arrays.asList("id=confirm"), objectDefinition.getIdentifiers());
        assertEquals(1, objectDefinition.getParentContexts().size());
        assertEquals(TestObject.ParentType.FRAME, objectDefinition.getParentContexts().get(0).getType());
        assertEquals(Arrays.asList("id=payment-iframe"), objectDefinition.getParentContexts().get(0).getValues());
    }

    @Test
    public void normalizeObjectDefinition_parentFrameRefShorthand_resolvesReferencedObject() {
        WebElementIdentifier.ObjectDefinition paymentFrameDefinition = new WebElementIdentifier.ObjectDefinition(
                Arrays.asList("id=payment-iframe"),
                Collections.emptyList()
        );
        Function<String, WebElementIdentifier.ObjectDefinition> resolver = key -> {
            if ("Shared.PaymentFrame".equals(key)) {
                return paymentFrameDefinition;
            }
            return null;
        };

        Map<String, Object> parentMap = new LinkedHashMap<>();
        parentMap.put("frameRef", "Shared.PaymentFrame");

        Map<String, Object> objectDefinitionMap = new LinkedHashMap<>();
        objectDefinitionMap.put("locator", "id=confirm");
        objectDefinitionMap.put("parent", parentMap);

        WebElementIdentifier.ObjectDefinition objectDefinition =
                WebElementIdentifier.normalizeObjectDefinition(objectDefinitionMap, resolver);

        assertEquals(1, objectDefinition.getParentContexts().size());
        assertEquals(TestObject.ParentType.FRAME, objectDefinition.getParentContexts().get(0).getType());
        assertEquals(Arrays.asList("id=payment-iframe"), objectDefinition.getParentContexts().get(0).getValues());
    }

    @Test
    public void normalizeObjectDefinition_parentRef_includesReferencedParentChain() {
        TestObject.ParentContext frameParentContext =
                new TestObject.ParentContext(TestObject.ParentType.FRAME, Arrays.asList("id=top-frame"));
        WebElementIdentifier.ObjectDefinition shadowHostDefinition = new WebElementIdentifier.ObjectDefinition(
                Arrays.asList("cssSelector=checkout-shell"),
                Arrays.asList(frameParentContext)
        );
        Function<String, WebElementIdentifier.ObjectDefinition> resolver = key -> {
            if ("Shared.CheckoutHost".equals(key)) {
                return shadowHostDefinition;
            }
            return null;
        };

        Map<String, Object> parentMap = new LinkedHashMap<>();
        parentMap.put("type", "shadow");
        parentMap.put("ref", "Shared.CheckoutHost");

        Map<String, Object> objectDefinitionMap = new LinkedHashMap<>();
        objectDefinitionMap.put("locator", "id=confirm");
        objectDefinitionMap.put("parent", parentMap);

        WebElementIdentifier.ObjectDefinition objectDefinition =
                WebElementIdentifier.normalizeObjectDefinition(objectDefinitionMap, resolver);

        assertEquals(2, objectDefinition.getParentContexts().size());
        assertEquals(TestObject.ParentType.FRAME, objectDefinition.getParentContexts().get(0).getType());
        assertEquals(Arrays.asList("id=top-frame"), objectDefinition.getParentContexts().get(0).getValues());
        assertEquals(TestObject.ParentType.SHADOW, objectDefinition.getParentContexts().get(1).getType());
        assertEquals(Arrays.asList("cssSelector=checkout-shell"), objectDefinition.getParentContexts().get(1).getValues());
    }
}
