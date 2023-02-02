package org.ndviet.library;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.TestObject.TestObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

public class WebUIAbstract {
    private static final Logger LOGGER = LogManager.getLogger(WebUIAbstract.class);

    public static void click(WebDriver driver, TestObject testObject) {
        click(driver, testObject, -1);
    }

    public static void click(WebDriver driver, TestObject testObject, int timeout) {
        try {
            WebElement element = Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, timeout);
            WebElementHelpers.scrollIntoView(driver, element);
            element.click();
        } catch (Exception e) {
            LOGGER.error(testObject + " could not click successfully.");
            throw e;
        }
    }

    public static void setText(WebDriver driver, TestObject testObject, String text) {
        try {
            WebElement element = Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, -1);
            WebElementHelpers.scrollIntoView(driver, element);
            element.sendKeys(text);
        } catch (Exception e) {
            LOGGER.error(testObject + " could not set text successfully.");
            throw e;
        }
    }

    public static String getText(WebDriver driver, TestObject testObject) {
        String text = Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, -1).getText();
        LOGGER.info("Text in element: " + text);
        return text;
    }

    public static List<String> getTexts(WebDriver driver, TestObject testObject) {
        List<WebElement> listElements = Waiting.Elements.PRESENCE_OF_ALL_ELEMENTS_LOCATED.waitForElements(driver, testObject, true, -1);
        List<String> listTexts = new ArrayList<>();
        for (WebElement element : listElements) {
            listTexts.add(element.getText().trim());
        }
        LOGGER.info("List texts: " + listTexts);
        return listTexts;
    }

    public static void moveToElement(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, -1);
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();
    }

    public static void scrollToElement(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.VISIBILITY_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        Actions action = new Actions(driver);
        action.scrollToElement(element).perform();
    }

    public static void uploadFile(WebDriver driver, TestObject testObject, String absolutePath) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        element.sendKeys(absolutePath);
    }

    public static void verifyElementPresent(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
    }

    public static void verifyElementNotPresent(WebDriver driver, TestObject testObject) {
        Waiting.Condition.NOT_PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1, null);
    }

    public static void verifyElementVisible(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.VISIBILITY_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
    }

    public static void verifyElementNotVisible(WebDriver driver, TestObject testObject) {
        Waiting.Condition.INVISIBILITY_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1, null);
    }

    public static void verifyElementTextEquals(WebDriver driver, TestObject testObject, String expectText) {
        LOGGER.info("Verify text is present in element should equal: " + expectText);
        try {
            Waiting.Condition.TEXT_TO_BE_PRESENT_IN_ELEMENT.waitForElement(driver, testObject, true, -1, expectText);
            String actualText = WebElementHelpers.getWebElement(driver, testObject).getText();
            if (actualText.equals(expectText)) {
                LOGGER.info("Value: " + actualText + " is present in element and equal to: " + expectText);
            } else {
                throw new RuntimeException("Actual value: " + actualText + " does not equal the expect value: " + expectText);
            }
        } catch (Exception e) {
            LOGGER.error("Expect value: " + expectText + " is not present in element");
            throw e;
        }
    }

    public static void verifyElementTextContains(WebDriver driver, TestObject testObject, String expectText) {
        LOGGER.info("Verify text is present in element should contain: " + expectText);
        try {
            Waiting.Condition.TEXT_TO_BE_PRESENT_IN_ELEMENT.waitForElement(driver, testObject, true, -1, expectText);
            String actualText = WebElementHelpers.getWebElement(driver, testObject).getText();
            if (actualText.contains(expectText)) {
                LOGGER.info("Value: " + actualText + " is present in element and contain: " + expectText);
            } else {
                throw new RuntimeException("Actual value: " + actualText + " does not contain the expect value: " + expectText);
            }
        } catch (Exception e) {
            LOGGER.error("Expect value: " + expectText + " is not present in element");
            throw e;
        }
    }
}
