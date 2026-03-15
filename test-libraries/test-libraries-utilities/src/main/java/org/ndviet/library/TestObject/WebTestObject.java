package org.ndviet.library.TestObject;

import org.ndviet.library.template.TemplateHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebTestObject extends TestObject {

    public WebTestObject(String relativeObjectId, Map variables) throws Exception {
        this.relativeObjectId = relativeObjectId;
        WebElementIdentifier.ObjectDefinition definition = WebElementIdentifier.getInstance().getObjectDefinition(relativeObjectId);
        this.setValues(processTemplates(definition.getIdentifiers(), variables));
        this.setParentContexts(processParentContexts(definition.getParentContexts(), variables));
    }

    private List<String> processTemplates(List<String> locators, Map variables) throws Exception {
        List<String> templatedLocators = new ArrayList<>();
        for (String locator : locators) {
            templatedLocators.add(TemplateHelpers.processTemplate(locator, variables));
        }
        return templatedLocators;
    }

    private List<ParentContext> processParentContexts(List<ParentContext> parentContexts, Map variables) throws Exception {
        List<ParentContext> templatedParentContexts = new ArrayList<>();
        for (ParentContext parentContext : parentContexts) {
            templatedParentContexts.add(new ParentContext(
                    parentContext.getType(),
                    processTemplates(parentContext.getValues(), variables)
            ));
        }
        return templatedParentContexts;
    }
}
