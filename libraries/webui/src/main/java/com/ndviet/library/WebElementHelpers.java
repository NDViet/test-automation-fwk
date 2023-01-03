package com.ndviet.library;

import com.ndviet.libary.TestObject.TestObject;
import com.ndviet.libary.string.StringHelpers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class WebElementHelpers {
    private static final Logger LOGGER = LogManager.getLogger(WebElementHelpers.class);

    public static By getBy(Object object) {
        String textObject;
        if (object instanceof TestObject) {
            textObject = ((TestObject) object).getValue();
        } else {
            textObject = object.toString();
        }
        if (textObject.startsWith("xpath=")) {
            return By.xpath(StringHelpers.replaceStringUsingRegex(textObject, "xpath=", ""));
        } else if (textObject.startsWith("cssSelector=")) {
            return By.cssSelector(StringHelpers.replaceStringUsingRegex(textObject, "cssSelector=", ""));
        } else {
            return By.xpath(textObject);
        }
    }

    public static WebElement findWebElement(WebDriver driver, TestObject testObject) {
        return driver.findElement(getBy(testObject));
    }

    public static List<WebElement> findWebElements(WebDriver driver, TestObject testObject) {
        return driver.findElements(getBy(testObject));
    }

    public static void scrollIntoView(WebDriver driver, WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
    }

    public static WebElement getWebElement(WebDriver driver, Object object) {
        WebElement element;
        if (object instanceof TestObject) {
            element = WebElementHelpers.findWebElement(driver, (TestObject) object);
        } else if (object instanceof WebElement) {
            element = isRefreshed(driver, (WebElement) object);
        } else {
            List<WebElement> list_element = (List<WebElement>) object;
            element = list_element.get(0);
        }
        scrollIntoView(driver, element);
        return element;
    }

    public static WebElement isRefreshed(WebDriver driver, WebElement element) {
        try {
            element.isEnabled();
            return element;
        } catch (StaleElementReferenceException e) {
            return refreshWebElement(driver, element);
        }
    }

    public static WebElement refreshWebElement(WebDriver driver, WebElement element) {
        String elementInfo = element.toString();
        LOGGER.info("WebElement info: " + elementInfo);
        elementInfo = elementInfo.substring(elementInfo.indexOf("->"));
        String elementLocator = elementInfo.substring(elementInfo.indexOf(": "));
        elementLocator = elementLocator.substring(2, elementLocator.length() - 1);
        LOGGER.info("Extracted WebElement locator: " + elementLocator);
        WebElement refreshedElement = null;
        if (elementInfo.contains("-> link text:")) {
            refreshedElement = driver.findElement(By.linkText(elementLocator));
        } else if (elementInfo.contains("-> name:")) {
            refreshedElement = driver.findElement(By.name(elementLocator));
        } else if (elementInfo.contains("-> id:")) {
            refreshedElement = driver.findElement(By.id(elementLocator));
        } else if (elementInfo.contains("-> xpath:")) {
            refreshedElement = driver.findElement(By.xpath(elementLocator));
        } else if (elementInfo.contains("-> class name:")) {
            refreshedElement = driver.findElement(By.className(elementLocator));
        } else if (elementInfo.contains("-> css selector:")) {
            refreshedElement = driver.findElement(By.cssSelector(elementLocator));
        } else if (elementInfo.contains("-> partial link text:")) {
            refreshedElement = driver.findElement(By.partialLinkText(elementLocator));
        } else if (elementInfo.contains("-> tag name:")) {
            refreshedElement = driver.findElement(By.tagName(elementLocator));
        } else {
            LOGGER.error("Could not refresh the WebElement.");
        }
        return refreshedElement;
    }
}
