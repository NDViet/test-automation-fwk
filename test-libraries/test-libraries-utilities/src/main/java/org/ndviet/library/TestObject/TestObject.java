package org.ndviet.library.TestObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class TestObject {
    public enum ParentType {
        FRAME,
        SHADOW
    }

    public static class ParentContext {
        private final ParentType type;
        private final List<String> values;

        public ParentContext(ParentType type, List<String> values) {
            this.type = Objects.requireNonNull(type, "Parent context type must not be null.");
            if (values == null || values.isEmpty()) {
                throw new IllegalArgumentException("Parent context locators must not be empty.");
            }
            this.values = Collections.unmodifiableList(new ArrayList<>(values));
        }

        public ParentType getType() {
            return type;
        }

        public List<String> getValues() {
            return values;
        }

        public String getValue() {
            return values.get(0);
        }

        @Override
        public String toString() {
            return String.format("{type=%s, values=%s}", type, values);
        }
    }

    protected String relativeObjectId;
    protected String value;
    protected List<String> values = new ArrayList<>();
    protected List<ParentContext> parentContexts = new ArrayList<>();

    public String getValue() {
        if (this.value == null && this.values != null && !this.values.isEmpty()) {
            return this.values.get(0);
        }
        return this.value;
    }

    public List<String> getValues() {
        if (this.values != null && !this.values.isEmpty()) {
            return Collections.unmodifiableList(this.values);
        }
        if (this.value == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(this.value);
    }

    protected void setValues(List<String> values) {
        if (values == null) {
            this.values = new ArrayList<>();
            this.value = null;
            return;
        }
        this.values = new ArrayList<>(values);
        if (!this.values.isEmpty()) {
            this.value = this.values.get(0);
        } else {
            this.value = null;
        }
    }

    public List<ParentContext> getParentContexts() {
        if (this.parentContexts == null || this.parentContexts.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.parentContexts);
    }

    protected void setParentContexts(List<ParentContext> parentContexts) {
        if (parentContexts == null) {
            this.parentContexts = new ArrayList<>();
            return;
        }
        this.parentContexts = new ArrayList<>(parentContexts);
    }

    @Override
    public String toString() {
        return String.format("Object ID: %s - Object values: %s - Parent contexts: %s",
                this.relativeObjectId, getValues(), getParentContexts());
    }
}
