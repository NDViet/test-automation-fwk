package com.ndviet.library.TestObject;

import com.ndviet.library.template.TemplateHelpers;

import java.util.Map;

public class WebTestObject extends TestObject {

    public WebTestObject(String relativeObjectId, Map variables) throws Exception {
        this.relativeObjectId = relativeObjectId;
        this.value = WebElementIdentifier.getInstance().getIdentifier(relativeObjectId);
        this.value = TemplateHelpers.processTemplate(this.value, variables);
    }
}
