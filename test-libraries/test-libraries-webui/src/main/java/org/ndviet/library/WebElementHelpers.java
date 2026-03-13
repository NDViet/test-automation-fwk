package org.ndviet.library;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.TestObject.TestObject;
import org.ndviet.library.string.StringHelpers;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;

public class WebElementHelpers {
    private static final Logger LOGGER = LogManager.getLogger(WebElementHelpers.class);

    public static By getBy(Object object) {
        String textObject;
        if (object instanceof TestObject) {
            textObject = ((TestObject) object).getValue();
        } else {
            textObject = object.toString();
        }
        if (textObject == null || textObject.isBlank()) {
            throw new IllegalArgumentException("Locator must not be null or blank.");
        }

        String normalizedLocator = textObject.trim();
        String lowercase = normalizedLocator.toLowerCase(Locale.ROOT);
        if (lowercase.startsWith("xpath=")) {
            return By.xpath(StringHelpers.replaceStringUsingRegex(normalizedLocator, "(?i)^xpath=", ""));
        }
        if (lowercase.startsWith("cssselector=")) {
            return By.cssSelector(StringHelpers.replaceStringUsingRegex(normalizedLocator, "(?i)^cssSelector=", ""));
        }
        if (lowercase.startsWith("id=")) {
            return By.id(StringHelpers.replaceStringUsingRegex(normalizedLocator, "(?i)^id=", ""));
        }
        if (lowercase.startsWith("name=")) {
            return By.name(StringHelpers.replaceStringUsingRegex(normalizedLocator, "(?i)^name=", ""));
        }
        if (lowercase.startsWith("classname=")) {
            return By.className(StringHelpers.replaceStringUsingRegex(normalizedLocator, "(?i)^className=", ""));
        }
        if (lowercase.startsWith("tagname=")) {
            return By.tagName(StringHelpers.replaceStringUsingRegex(normalizedLocator, "(?i)^tagName=", ""));
        }
        if (lowercase.startsWith("linktext=")) {
            return By.linkText(StringHelpers.replaceStringUsingRegex(normalizedLocator, "(?i)^linkText=", ""));
        }
        if (lowercase.startsWith("partiallinktext=")) {
            return By.partialLinkText(StringHelpers.replaceStringUsingRegex(normalizedLocator, "(?i)^partialLinkText=", ""));
        }
        return By.xpath(normalizedLocator);
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
            List<?> listElement = (List<?>) object;
            element = (WebElement) listElement.get(0);
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
