package com.ndviet.library;

import com.ndviet.libary.TestObject.TestObject;
import com.ndviet.libary.configuration.ConfigurationFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.ndviet.libary.configuration.Constants.SELENIUM_DEFAULT_TIMEOUT;

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

    public static WebElement getWebElement(WebDriver driver, Object object) {
        WebElement element = null;
        if (object instanceof TestObject) {
            element = WebUIAbstract.findWebElement(driver, (TestObject) object);
        } else if (object instanceof WebElement) {
            element = (WebElement) object;
        } else {
            List<WebElement> list_element = (List<WebElement>) object;
            element = list_element.get(0);
        }
        return element;
    }

    public static List<WebElement> getWebElements(WebDriver driver, Object object) {
        List<WebElement> elements = new ArrayList<>();
        if (object instanceof TestObject) {
            elements = WebUIAbstract.findWebElements(driver, (TestObject) object);
        }
        return elements;
    }

    protected enum Element implements WaitElement {
        PRESENCE_OF_ELEMENT_LOCATED {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.presenceOfElementLocated(By.xpath(object.toString())));
                //return getWebElement(driver, object);
            }
        },
        ELEMENT_TO_BE_CLICKABLE {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.elementToBeClickable(By.xpath(object.toString())));
                //return getWebElement(driver, object);
            }
        },
        VISIBILITY_OF {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.visibilityOf(getWebElement(driver, object)));
                //return getWebElement(driver, object);
            }
        },
        INVISIBILITY_OF {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.invisibilityOf(getWebElement(driver, object)));
                return getWebElement(driver, object);
            }
        }
    }

    protected enum ElementText implements WaitElementText {
        TEXT_TO_BE_PRESENT_IN_ELEMENT {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText) {
                getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.textToBePresentInElement(getWebElement(driver, object), expectText));
                return getWebElement(driver, object);
            }
        }
    }

    protected enum Elements implements WaitElements {
        PRESENCE_OF_ALL_ELEMENTS_LOCATED {
            @Override
            public List<WebElement> waitForElements(WebDriver driver, Object object, boolean isWait, int timeOut) {
                getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(object.toString())));
                return getWebElements(driver, object);
            }
        },
        ELEMENT_TO_BE_CLICKABLE {
            @Override
            public List<WebElement> waitForElements(WebDriver driver, Object object, boolean isWait, int timeOut) {
                getWaitDriver(driver, isWait, timeOut).until(ExpectedConditions.elementToBeClickable(By.xpath(object.toString())));
                return getWebElements(driver, object);
            }
        }
    }

    private interface WaitElement {
        WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut);
    }

    private interface WaitElements {
        List<WebElement> waitForElements(WebDriver driver, Object object, boolean isWait, int timeOut);
    }

    private interface WaitElementText {
        WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText);
    }
}
