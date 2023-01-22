package org.ndviet.library.TestObject;

import java.util.HashMap;
import java.util.Map;

public class ObjectRepository {
    public static TestObject findTestObject(String relativeObjectId) throws Exception {
        return findTestObject(relativeObjectId, new HashMap<>());
    }

    public static TestObject findTestObject(String relativeObjectId, Map variables) throws Exception {
        TestObject to;
        try {
            to = new WebTestObject(relativeObjectId, variables);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Could not find test object with ID: '%s'. Kindly check the relativeObjectId is correct!", relativeObjectId));
        }
        return to;
    }
}
