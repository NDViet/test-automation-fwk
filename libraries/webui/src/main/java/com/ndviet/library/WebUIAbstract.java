package com.ndviet.library;

import com.ndviet.libary.TestObject.TestObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

public class WebUIAbstract {
    private static final Logger LOGGER = LogManager.getLogger(WebUIAbstract.class);

    public static WebElement findWebElement(WebDriver driver, TestObject testObject) {
        return driver.findElement(By.xpath(testObject.getValue()));
    }

    public static List<WebElement> findWebElements(WebDriver driver, TestObject testObject) {
        return driver.findElements(By.xpath(testObject.getValue()));
    }

    public static void click(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        element = Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, -1);
        element.click();
    }

    public static void setText(WebDriver driver, TestObject testObject, String text) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        element = Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, -1);
        element.sendKeys(text);
    }

    public static String getText(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        return Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, -1).getText();
    }

    public static List<String> getTexts(WebDriver driver, TestObject testObject) {
        List<WebElement> listElements = findWebElements(driver, testObject);
        List<String> listTexts = new ArrayList<>();
        for (WebElement element : listElements) {
            listTexts.add(element.getText().trim());
        }
        return listTexts;
    }

    public static void moveToElement(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        element = Waiting.Element.VISIBILITY_OF.waitForElement(driver, testObject, true, -1);
        element = Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, -1);
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();
    }

    public static void scrollToElement(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        element = Waiting.Element.VISIBILITY_OF.waitForElement(driver, element, true, -1);
        Actions action = new Actions(driver);
        action.scrollToElement(element).perform();
    }

    public static void uploadFile(WebDriver driver, TestObject testObject, String absolutePath) {
        WebElement element = Waiting.Element.VISIBILITY_OF.waitForElement(driver, testObject, true, -1);
        element.sendKeys(absolutePath);
    }

    public static void verifyElementPresent(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
    }

    public static void verifyElementVisible(WebDriver driver, TestObject testObject) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        Waiting.Element.VISIBILITY_OF.waitForElement(driver, testObject, true, -1);
    }

    public static void verifyElementTextEquals(WebDriver driver, TestObject testObject, String expectText) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        element = Waiting.ElementText.TEXT_TO_BE_PRESENT_IN_ELEMENT.waitForElement(driver, testObject, true, -1, expectText);
        String actualText = element.getText().trim();
        LOGGER.info("Actual value: " + actualText);
        LOGGER.info("Expect value: " + expectText);
        if (!actualText.equals(expectText.trim())) {
            throw new RuntimeException("Actual value: " + actualText + " does not equal the expect value: " + expectText);
        }
    }

    public static void verifyElementTextContains(WebDriver driver, TestObject testObject, String expectText) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        element = Waiting.ElementText.TEXT_TO_BE_PRESENT_IN_ELEMENT.waitForElement(driver, testObject, true, -1, expectText);
        String actualText = element.getText().trim();
        LOGGER.info("Actual value: " + actualText);
        LOGGER.info("Expect value: " + expectText);
        if (!actualText.contains(expectText.trim())) {
            throw new RuntimeException("Actual value: " + actualText + " does not contain the expect value: " + expectText);
        }
    }
}
