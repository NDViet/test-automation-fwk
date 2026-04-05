package org.ndviet.library.webui.selenium;

import org.ndviet.library.TestObject.TestObject;
import org.ndviet.library.webui.config.WebUiConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Waiting {
    public static int m_defaultTimeOut = WebUiConfiguration.getSeleniumDefaultTimeout();

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

    private static WebElement locateElement(WebDriver driver, Object object) {
        if (object instanceof TestObject) {
            return WebElementHelpers.findWebElement(driver, (TestObject) object);
        }
        return driver.findElement(WebElementHelpers.getBy(object));
    }

    private static List<WebElement> locateElements(WebDriver driver, Object object) {
        if (object instanceof TestObject) {
            return WebElementHelpers.findWebElements(driver, (TestObject) object);
        }
        return driver.findElements(WebElementHelpers.getBy(object));
    }

    protected enum Element implements WaitElement {
        PRESENCE_OF_ELEMENT_LOCATED {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> locateElement(webDriver, object));
            }
        },
        ELEMENT_TO_BE_CLICKABLE {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    WebElement element = locateElement(webDriver, object);
                    if (element.isDisplayed() && element.isEnabled()) {
                        return element;
                    }
                    return null;
                });
            }
        },
        VISIBILITY_OF {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    WebElement element = locateElement(webDriver, object);
                    if (element.isDisplayed()) {
                        return element;
                    }
                    return null;
                });
            }
        },
        VISIBILITY_OF_ELEMENT_LOCATED {
            @Override
            public WebElement waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    WebElement element = locateElement(webDriver, object);
                    if (element.isDisplayed()) {
                        return element;
                    }
                    return null;
                });
            }
        }
    }

    protected enum Elements implements WaitElements {
        PRESENCE_OF_ALL_ELEMENTS_LOCATED {
            @Override
            public List<WebElement> waitForElements(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    List<WebElement> elements = locateElements(webDriver, object);
                    if (elements != null && !elements.isEmpty()) {
                        return elements;
                    }
                    return null;
                });
            }
        },
        VISIBILITY_OF_ALL_ELEMENTS_LOCATED_BY {
            @Override
            public List<WebElement> waitForElements(WebDriver driver, Object object, boolean isWait, int timeOut) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    List<WebElement> elements = locateElements(webDriver, object);
                    if (elements == null || elements.isEmpty()) {
                        return null;
                    }
                    for (WebElement element : elements) {
                        if (!element.isDisplayed()) {
                            return null;
                        }
                    }
                    return elements;
                });
            }
        }
    }

    protected enum Condition implements WaitElementCondition {
        INVISIBILITY_OF_ELEMENT_LOCATED {
            @Override
            public boolean waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    try {
                        WebElement element = locateElement(webDriver, object);
                        return !element.isDisplayed();
                    } catch (Exception ignored) {
                        return true;
                    }
                });
            }
        },
        NOT_PRESENCE_OF_ELEMENT_LOCATED {
            @Override
            public boolean waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    try {
                        List<WebElement> elements = locateElements(webDriver, object);
                        return elements == null || elements.isEmpty();
                    } catch (Exception ignored) {
                        return true;
                    }
                });
            }
        },
        TEXT_TO_BE_PRESENT_IN_ELEMENT_LOCATED {
            @Override
            public boolean waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    try {
                        String actualText = locateElement(webDriver, object).getText();
                        return actualText != null && actualText.contains(expectText);
                    } catch (Exception ignored) {
                        return false;
                    }
                });
            }
        },
        TEXT_TO_BE_PRESENT_IN_ELEMENT {
            @Override
            public boolean waitForElement(WebDriver driver, Object object, boolean isWait, int timeOut, String expectText) {
                return getWaitDriver(driver, isWait, timeOut).until(webDriver -> {
                    try {
                        String actualText = locateElement(webDriver, object).getText();
                        return actualText != null && actualText.contains(expectText);
                    } catch (Exception ignored) {
                        return false;
                    }
                });
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
