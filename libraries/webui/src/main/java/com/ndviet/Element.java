package com.ndviet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Element {
    public static WebElement findElementByXpath(WebDriver driver, String xpath) {
        return driver.findElement(By.xpath(xpath));
    }

    public static void click(WebDriver driver, String xpath) {
        WebElement element = findElementByXpath(driver, xpath);
        element = Waiting.Condition.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, true, element);
        element.click();
    }

    public static void input(WebDriver driver, String xpath, String text) {
        WebElement element = findElementByXpath(driver, xpath);
        element = Waiting.Condition.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, true, element);
        element.sendKeys(text);
    }
}
