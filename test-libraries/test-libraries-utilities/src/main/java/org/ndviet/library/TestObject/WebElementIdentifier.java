package org.ndviet.library.TestObject;

import org.ndviet.library.configuration.ConfigurationManager;
import org.ndviet.library.yaml.YamlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.ndviet.library.configuration.Constants.WEB_ELEMENT_IDENTIFIERS_DIRECTORY;
import static org.ndviet.library.map.MapUtils.getValueAsObject;

public class WebElementIdentifier {
    private static final Set<String> SUPPORTED_LOCATOR_TYPES = new HashSet<>(Arrays.asList(
            "xpath",
            "cssselector",
            "id",
            "name",
            "classname",
            "tagname",
            "linktext",
            "partiallinktext",
            "role"
    ));
    private static final Set<String> OBJECT_METADATA_KEYS = new HashSet<>(Arrays.asList(
            "parent",
            "parents",
            "parentref",
            "parentrefs",
            "locator",
            "locators"
    ));
    private static final Set<String> PARENT_METADATA_KEYS = new HashSet<>(Arrays.asList(
            "type",
            "locator",
            "locators",
            "ref",
            "key",
            "frameref",
            "shadowref",
            "frame",
            "shadow"
    ));
    private static LinkedHashMap m_data = new LinkedHashMap<>();
    private static WebElementIdentifier m_instance;

    public static class ObjectDefinition {
        private final List<String> identifiers;
        private final List<TestObject.ParentContext> parentContexts;

        public ObjectDefinition(List<String> identifiers, List<TestObject.ParentContext> parentContexts) {
            this.identifiers = (identifiers == null) ? new ArrayList<>() : new ArrayList<>(identifiers);
            this.parentContexts = (parentContexts == null) ? new ArrayList<>() : new ArrayList<>(parentContexts);
        }

        public List<String> getIdentifiers() {
            return new ArrayList<>(identifiers);
        }

        public List<TestObject.ParentContext> getParentContexts() {
            return new ArrayList<>(parentContexts);
        }
    }

    public WebElementIdentifier() throws Exception {
        setElementFiles();
    }

    public static WebElementIdentifier getInstance() throws Exception {
        if (m_instance == null) {
            m_instance = new WebElementIdentifier();
        }
        return m_instance;
    }

    public void setElementFiles() throws Exception {
        String directory = ConfigurationManager.getInstance().getValue(WEB_ELEMENT_IDENTIFIERS_DIRECTORY);
        setElementFiles(directory);
    }

    public void setElementFiles(String directory) throws Exception {
        m_data.putAll(YamlUtils.readAllYamlInDirectory(directory));
    }

    public String getIdentifier(String key) {
        return getIdentifiers(key).stream().findFirst().orElse(null);
    }

    public List<String> getIdentifiers(String key) {
        return getObjectDefinition(key).getIdentifiers();
    }

    public ObjectDefinition getObjectDefinition(String key) {
        return getObjectDefinition(key, new HashSet<>());
    }

    private ObjectDefinition getObjectDefinition(String key, Set<String> resolvingObjectKeys) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Object reference key must not be null or empty.");
        }
        String normalizedKey = key.trim();
        if (!resolvingObjectKeys.add(normalizedKey)) {
            throw new IllegalArgumentException(String.format("Circular parent reference detected at object key: '%s'.", normalizedKey));
        }
        try {
            Object value = getValueAsObject(m_data, normalizedKey);
            ObjectDefinition definition = normalizeObjectDefinition(value, refKey -> getObjectDefinition(refKey, resolvingObjectKeys));
            if (definition.getIdentifiers().isEmpty()) {
                throw new IllegalArgumentException(String.format("No valid locator found for object ID: '%s'.", normalizedKey));
            }
            return definition;
        } finally {
            resolvingObjectKeys.remove(normalizedKey);
        }
    }

    static ObjectDefinition normalizeObjectDefinition(Object value) {
        return normalizeObjectDefinition(value, null);
    }

    static ObjectDefinition normalizeObjectDefinition(Object value, Function<String, ObjectDefinition> objectDefinitionResolver) {
        if (value instanceof Map) {
            return normalizeObjectDefinitionFromMap((Map<?, ?>) value, objectDefinitionResolver);
        }
        return new ObjectDefinition(normalizeIdentifiers(value), new ArrayList<>());
    }

    private static ObjectDefinition normalizeObjectDefinitionFromMap(Map<?, ?> valueMap, Function<String, ObjectDefinition> objectDefinitionResolver) {
        Object parentValue = getCaseInsensitiveValue(valueMap, "parents");
        if (parentValue == null) {
            parentValue = getCaseInsensitiveValue(valueMap, "parent");
        }
        Object parentRefValue = getCaseInsensitiveValue(valueMap, "parentRefs");
        if (parentRefValue == null) {
            parentRefValue = getCaseInsensitiveValue(valueMap, "parentRef");
        }

        Object locatorValue = getCaseInsensitiveValue(valueMap, "locator");
        if (locatorValue == null) {
            locatorValue = getCaseInsensitiveValue(valueMap, "locators");
        }
        if (locatorValue == null) {
            locatorValue = filterMapWithoutKeys(valueMap, OBJECT_METADATA_KEYS);
        }

        List<String> identifiers = normalizeIdentifiers(locatorValue);
        List<TestObject.ParentContext> parentContexts = new ArrayList<>();
        parentContexts.addAll(normalizeParentContexts(parentValue, objectDefinitionResolver));
        parentContexts.addAll(normalizeParentContexts(parentRefValue, objectDefinitionResolver));
        return new ObjectDefinition(identifiers, parentContexts);
    }

    static List<String> normalizeIdentifiers(Object value) {
        List<String> identifiers = new ArrayList<>();
        appendIdentifiers(identifiers, value);
        return identifiers;
    }

    private static void appendIdentifiers(List<String> identifiers, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            String locator = ((String) value).trim();
            if (!locator.isEmpty()) {
                identifiers.add(locator);
            }
            return;
        }
        if (value instanceof List) {
            for (Object locator : (List<?>) value) {
                appendIdentifiers(identifiers, locator);
            }
            return;
        }
        if (value instanceof Map) {
            appendIdentifiersFromMap(identifiers, (Map<?, ?>) value);
            return;
        }
        String locator = value.toString().trim();
        if (!locator.isEmpty()) {
            identifiers.add(locator);
        }
    }

    private static void appendIdentifiersFromMap(List<String> identifiers, Map<?, ?> map) {
        Object primary = getCaseInsensitiveValue(map, "primary");
        Object fallbacks = getCaseInsensitiveValue(map, "fallbacks");
        if (primary != null || fallbacks != null) {
            appendIdentifiers(identifiers, primary);
            appendIdentifiers(identifiers, fallbacks);
            return;
        }

        Map<?, ?> locatorMap = filterMapWithoutKeys(map, OBJECT_METADATA_KEYS);
        if (locatorMap.isEmpty()) {
            return;
        }

        List<String> typedLocators = new ArrayList<>();
        for (Map.Entry<?, ?> entry : locatorMap.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            String locatorType = entry.getKey().toString().trim().toLowerCase();
            if (!SUPPORTED_LOCATOR_TYPES.contains(locatorType)) {
                return;
            }
            appendLocatorValueByType(typedLocators, locatorType, entry.getValue());
        }
        identifiers.addAll(typedLocators);
    }

    private static List<TestObject.ParentContext> normalizeParentContexts(Object value, Function<String, ObjectDefinition> objectDefinitionResolver) {
        List<TestObject.ParentContext> parentContexts = new ArrayList<>();
        if (value == null) {
            return parentContexts;
        }
        if (value instanceof List) {
            for (Object parent : (List<?>) value) {
                parentContexts.addAll(normalizeParentContext(parent, objectDefinitionResolver));
            }
            return parentContexts;
        }
        parentContexts.addAll(normalizeParentContext(value, objectDefinitionResolver));
        return parentContexts;
    }

    private static List<TestObject.ParentContext> normalizeParentContext(Object value, Function<String, ObjectDefinition> objectDefinitionResolver) {
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Parent context must be defined as a map with type and locator.");
        }

        Map<?, ?> map = (Map<?, ?>) value;
        Object frameValue = getCaseInsensitiveValue(map, "frame");
        Object shadowValue = getCaseInsensitiveValue(map, "shadow");
        if (frameValue != null && shadowValue != null) {
            throw new IllegalArgumentException("Parent context cannot define both frame and shadow at the same level.");
        }

        if (frameValue != null || shadowValue != null) {
            TestObject.ParentType parentType = (frameValue != null)
                    ? TestObject.ParentType.FRAME
                    : TestObject.ParentType.SHADOW;
            Object locatorValue = (frameValue != null) ? frameValue : shadowValue;
            List<String> parentLocators = normalizeIdentifiers(locatorValue);
            if (parentLocators.isEmpty()) {
                throw new IllegalArgumentException("Parent context locator must not be empty.");
            }
            return Arrays.asList(new TestObject.ParentContext(parentType, parentLocators));
        }

        Object frameRefValue = getCaseInsensitiveValue(map, "frameRef");
        Object shadowRefValue = getCaseInsensitiveValue(map, "shadowRef");
        if (frameRefValue != null && shadowRefValue != null) {
            throw new IllegalArgumentException("Parent context cannot define both frameRef and shadowRef at the same level.");
        }

        if (frameRefValue != null || shadowRefValue != null) {
            TestObject.ParentType parentType = (frameRefValue != null)
                    ? TestObject.ParentType.FRAME
                    : TestObject.ParentType.SHADOW;
            Object referenceKey = (frameRefValue != null) ? frameRefValue : shadowRefValue;
            return normalizeParentContextByReference(referenceKey, parentType, objectDefinitionResolver);
        }

        Object typeValue = getCaseInsensitiveValue(map, "type");
        if (typeValue == null) {
            throw new IllegalArgumentException("Parent context type must be defined as frame or shadow.");
        }
        TestObject.ParentType parentType = parseParentType(typeValue.toString());

        Object referenceKey = getCaseInsensitiveValue(map, "ref");
        if (referenceKey == null) {
            referenceKey = getCaseInsensitiveValue(map, "key");
        }
        if (referenceKey != null) {
            return normalizeParentContextByReference(referenceKey, parentType, objectDefinitionResolver);
        }

        Object locatorValue = getCaseInsensitiveValue(map, "locator");
        if (locatorValue == null) {
            locatorValue = getCaseInsensitiveValue(map, "locators");
        }
        if (locatorValue == null) {
            locatorValue = filterMapWithoutKeys(map, PARENT_METADATA_KEYS);
        }

        List<String> parentLocators = normalizeIdentifiers(locatorValue);
        if (parentLocators.isEmpty()) {
            throw new IllegalArgumentException("Parent context locator must not be empty.");
        }
        return Arrays.asList(new TestObject.ParentContext(parentType, parentLocators));
    }

    private static List<TestObject.ParentContext> normalizeParentContextByReference(
            Object referenceKeyValue,
            TestObject.ParentType parentType,
            Function<String, ObjectDefinition> objectDefinitionResolver
    ) {
        if (objectDefinitionResolver == null) {
            throw new IllegalArgumentException("Parent reference requires an object definition resolver.");
        }
        if (referenceKeyValue == null || referenceKeyValue.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Parent reference key must not be null or empty.");
        }
        String referenceKey = referenceKeyValue.toString().trim();
        ObjectDefinition referencedDefinition = objectDefinitionResolver.apply(referenceKey);
        if (referencedDefinition == null || referencedDefinition.getIdentifiers().isEmpty()) {
            throw new IllegalArgumentException(String.format("Could not resolve parent reference key: '%s'.", referenceKey));
        }
        List<TestObject.ParentContext> resolvedParentContexts = new ArrayList<>();
        resolvedParentContexts.addAll(referencedDefinition.getParentContexts());
        resolvedParentContexts.add(new TestObject.ParentContext(parentType, referencedDefinition.getIdentifiers()));
        return resolvedParentContexts;
    }

    private static TestObject.ParentType parseParentType(String typeValue) {
        String normalized = typeValue.trim().toLowerCase().replace("_", "").replace("-", "").replace(" ", "");
        if ("frame".equals(normalized) || "iframe".equals(normalized)) {
            return TestObject.ParentType.FRAME;
        }
        if ("shadow".equals(normalized) || "shadowdom".equals(normalized) || "shadowroot".equals(normalized)) {
            return TestObject.ParentType.SHADOW;
        }
        throw new IllegalArgumentException(String.format("Unsupported parent context type: '%s'. Expected frame or shadow.", typeValue));
    }

    private static void appendLocatorValueByType(List<String> identifiers, String locatorType, Object value) {
        if (value instanceof List) {
            for (Object item : (List<?>) value) {
                appendLocatorValueByType(identifiers, locatorType, item);
            }
            return;
        }
        if (value instanceof Map) {
            return;
        }
        String locatorValue = value.toString().trim();
        if (!locatorValue.isEmpty()) {
            identifiers.add(locatorType + "=" + locatorValue);
        }
    }

    private static Object getCaseInsensitiveValue(Map<?, ?> map, String key) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            if (entry.getKey().toString().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static Map<?, ?> filterMapWithoutKeys(Map<?, ?> source, Set<String> ignoredKeysLowercase) {
        LinkedHashMap<Object, Object> filtered = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            String normalizedKey = entry.getKey().toString().trim().toLowerCase();
            if (ignoredKeysLowercase.contains(normalizedKey)) {
                continue;
            }
            filtered.put(entry.getKey(), entry.getValue());
        }
        return filtered;
    }
}
