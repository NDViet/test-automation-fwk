package com.ndviet.library;

import com.ndviet.libary.TestObject.TestObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class Element {
    public static WebElement findElementByXpath(WebDriver driver, String xpath) {
        return driver.findElement(By.xpath(xpath));
    }

    public static void click(TestObject testObject) {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebElement element = findElementByXpath(driver, testObject.getValue());
        element = Waiting.Condition.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, true, element);
        element.click();
    }

    public static void setText(TestObject testObject, String text) {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebElement element = findElementByXpath(driver, testObject.getValue());
        element = Waiting.Condition.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, true, element);
        element.sendKeys(text);
    }

    public static void mouseOver(WebDriver driver, String xpath) {
        WebElement element = findElementByXpath(driver, xpath);
        element = Waiting.Condition.VISIBILITY_OF.waitForElement(driver, true, element);
        Actions action = new Actions(driver);
        action.moveToElement(element);
    }

    public static void uploadFile(WebDriver driver, String xpath, String absolutePath) {
        WebElement element = findElementByXpath(driver, xpath);
        element = Waiting.Condition.VISIBILITY_OF.waitForElement(driver, true, element);
        element.sendKeys(absolutePath);
    }
}
