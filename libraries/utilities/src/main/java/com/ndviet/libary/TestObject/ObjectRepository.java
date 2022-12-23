package com.ndviet.libary.TestObject;

import java.util.HashMap;
import java.util.Map;

public class ObjectRepository {
    private static final ObjectRepository m_instance = new ObjectRepository();

    public static ObjectRepository getInstance() {
        return m_instance;
    }

    public static TestObject findTestObject(String relativeObjectId) throws Exception {
        return findTestObject(relativeObjectId, new HashMap<>());
    }

    public static TestObject findTestObject(String relativeObjectId, Map variables) throws Exception {
        return new WebTestObject(relativeObjectId, variables);
    }
}
