package com.ndviet.library.TestObject;

public abstract class TestObject {
    protected String relativeObjectId;
    protected String value;

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.format("Object ID: %s - Object value: %s", this.relativeObjectId, this.value);
    }
}
