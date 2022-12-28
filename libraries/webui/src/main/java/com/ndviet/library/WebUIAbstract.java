package com.ndviet.library;

import com.ndviet.libary.TestObject.TestObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class WebUIAbstract {
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

    public static void verifyElementText(WebDriver driver, TestObject testObject, String expectText) {
        WebElement element = Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, -1);
        element = Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, -1);
        String actualText = element.getText().trim();
        if (!actualText.equals(expectText.trim())) {
            throw new RuntimeException(actualText + " does not match the expect value " + expectText);
        }
    }
}
