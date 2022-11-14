package com.ndviet.automation.libraries.webui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Waiting {
    protected interface Strategy {
        WebElement waitForElement(WebDriver driver, boolean isWait, Object obj);
    }

    protected enum WaitEnum implements Strategy {
        ELEMENT_TO_BE_CLICKABLE {
            @Override
            public WebElement waitForElement(WebDriver driver, boolean isWait, Object obj) {
                WebElement element = null;
                if(obj instanceof WebElement) {
                    element = (WebElement) obj;
                } else {
                    List<WebElement> list_element = (List<WebElement>) obj;
                    element = list_element.get(0);
                }
                getWaitDriver(driver, isWait).until(ExpectedConditions.elementToBeClickable(element));
                return element;
            }
        },
        ELEMENT_TO_BE_PRESENT {
            @Override
            public WebElement waitForElement(WebDriver driver, boolean isWait, Object obj) {
                return null;
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
    }

}
