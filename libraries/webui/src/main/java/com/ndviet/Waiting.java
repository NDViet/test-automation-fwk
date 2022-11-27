package com.ndviet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Waiting {
    private interface WaitCondition {
        WebElement waitForElement(WebDriver driver, boolean isWait, Object obj);
    }

    protected enum Condition implements WaitCondition {
        ELEMENT_TO_BE_CLICKABLE {
            @Override
            public WebElement waitForElement(WebDriver driver, boolean isWait, Object obj) {
                WebElement element = getWebElement(obj);
                getWaitDriver(driver, isWait).until(ExpectedConditions.elementToBeClickable(element));
                return element;
            }
        },
        VISIBILITY_OF {
            @Override
            public WebElement waitForElement(WebDriver driver, boolean isWait, Object obj) {
                WebElement element = getWebElement(obj);
                getWaitDriver(driver, isWait).until(ExpectedConditions.visibilityOf(element));
                return element;
            }
        };

        public WebDriverWait getWaitDriver(WebDriver driver, boolean isWait) {
            WebDriverWait wait = null;
            if(isWait) {
                wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            } else {
                wait = new WebDriverWait(driver, Duration.ZERO);
            }
            return wait;
        }

        public WebElement getWebElement(Object obj) {
            WebElement element = null;
            if(obj instanceof WebElement) {
                element = (WebElement) obj;
            } else {
                List<WebElement> list_element = (List<WebElement>) obj;
                element = list_element.get(0);
            }
            return element;
        }
    }

}
