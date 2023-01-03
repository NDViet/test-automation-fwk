package com.ndviet.library.TestObject;

import java.util.HashMap;
import java.util.Map;

public class ObjectRepository {
    public static TestObject findTestObject(String relativeObjectId) throws Exception {
        return findTestObject(relativeObjectId, new HashMap<>());
    }

    public static TestObject findTestObject(String relativeObjectId, Map variables) throws Exception {
        return new WebTestObject(relativeObjectId, variables);
    }
}
