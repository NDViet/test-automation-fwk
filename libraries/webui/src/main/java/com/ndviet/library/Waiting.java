package com.ndviet.library;

import com.ndviet.libary.configuration.ConfigurationFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.ndviet.libary.configuration.Constants.SELENIUM_DEFAULT_TIMEOUT;
import static com.ndviet.library.WebElementHelpers.getBy;

public class Waiting {
    private static final String configDefaultTimeOut = ConfigurationFactory.getInstance().getValue(SELENIUM_DEFAULT_TIMEOUT);
    public static int m_defaultTimeOut = (configDefaultTimeOut == null) ? 10 : Integer.parseInt(configDefaultTimeOut);

    public static WebDriverWait getWaitDriver(WebDriver driver, boolean isWait) {
        return getWaitDriver(driver, isWait, m_defaultTimeOut);
    }

    public static WebDriverWait getWaitDriver(WebDriver driver, boolean isWait, int timeOut) {
        WebDriverWait wait;
        if (isWait) {
            if (timeOut >= 0) {
                wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
            } else {
                wait = getWaitDriver(driver, true);
            }
        } else {
            wait = new WebDriverWait(driver, Duration.ZERO);
        }
        return wait;
    }

    protected enum Element implements WaitElement {
        PRESENCE_OF_ELEMENT_LOCATED {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(
                        ExpectedConditions.refreshed(
                                ExpectedConditions.presenceOfElementLocated(getBy(object))));
            }
        },
        ELEMENT_TO_BE_CLICKABLE {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(
                        ExpectedConditions.refreshed(
                                ExpectedConditions.elementToBeClickable(
                                        ExpectedConditions.visibilityOf(
                                                ExpectedConditions.presenceOfElementLocated(getBy(object)).apply(driver)
                                        ).apply(driver))));
            }
        },
        VISIBILITY_OF {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(
                        ExpectedConditions.visibilityOf(
                                ExpectedConditions.presenceOfElementLocated(getBy(object)).apply(driver)));
            }
        },
    }

    protected enum Elements implements WaitElements {
        PRESENCE_OF_ALL_ELEMENTS_LOCATED {
            @Override
            public List<WebElement> waitForElements(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.presenceOfAllElementsLocatedBy(getBy(object)));
            }
        },
        VISIBILITY_OF_ALL_ELEMENTS_LOCATED_BY {
            @Override
            public List<WebElement> waitForElements(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(getBy(object)));
            }
        }
    }

    protected enum Condition implements WaitElementCondition {
        INVISIBILITY_OF_ELEMENT_LOCATED {
            @Override
            public boolean waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText) {
                return getWaitDriver(driver, isWait, timeOut).until(
                        ExpectedConditions.refreshed(
                                ExpectedConditions.invisibilityOfElementLocated(getBy(object))));
            }
        },
        TEXT_TO_BE_PRESENT_IN_ELEMENT_LOCATED {
            @Override
            public boolean waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText) {
                return getWaitDriver(driver, isWait, timeOut).until(
                        ExpectedConditions.refreshed(
                                ExpectedConditions.textToBePresentInElementLocated(getBy(object), expectText)));
            }
        },
        TEXT_TO_BE_PRESENT_IN_ELEMENT {
            @Override
            public boolean waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText) {
                return getWaitDriver(driver, isWait, timeOut).until(
                        ExpectedConditions.refreshed(
                                ExpectedConditions.textToBePresentInElement(
                                        ExpectedConditions.presenceOfElementLocated(getBy(object)).apply(driver), expectText)));
            }
        },
    }

    private interface WaitElement {
        WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut);
    }

    private interface WaitElements {
        List<WebElement> waitForElements(WebDriver driver, Object object, boolean isWait, int timeOut);
    }

    private interface WaitElementCondition {
        boolean waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText);
    }
}
