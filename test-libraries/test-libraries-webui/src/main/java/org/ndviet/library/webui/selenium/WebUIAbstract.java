package org.ndviet.library.webui.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.TestObject.TestObject;
import org.ndviet.library.webui.selenium.driver.TargetFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WebUIAbstract {
    private static final Logger LOGGER = LogManager.getLogger(WebUIAbstract.class);

    private WebUIAbstract() {
    }

    private static int normalizeTimeout(int timeout) {
        return timeout >= 0 ? timeout : Waiting.m_defaultTimeOut;
    }

    private static WebElement getClickableElement(WebDriver driver, TestObject testObject, int timeout) {
        return Waiting.Element.ELEMENT_TO_BE_CLICKABLE.waitForElement(driver, testObject, true, timeout);
    }

    private static WebElement getVisibleElement(WebDriver driver, TestObject testObject, int timeout) {
        return Waiting.Element.VISIBILITY_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, timeout);
    }

    private static WebElement getPresentElement(WebDriver driver, TestObject testObject, int timeout) {
        return Waiting.Element.PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, timeout);
    }

    private static Alert waitForAlert(WebDriver driver, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(normalizeTimeout(timeout))).until(ExpectedConditions.alertIsPresent());
    }

    public static void click(WebDriver driver, TestObject testObject) {
        click(driver, testObject, -1);
    }

    public static void click(WebDriver driver, TestObject testObject, int timeout) {
        try {
            WebElement element = getClickableElement(driver, testObject, timeout);
            WebElementHelpers.scrollIntoView(driver, element);
            element.click();
        } catch (Exception e) {
            LOGGER.error(testObject + " could not click successfully.", e);
            throw e;
        }
    }

    public static void clickByJavaScript(WebDriver driver, TestObject testObject) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        executeJavaScript(driver, "arguments[0].click();", element);
    }

    public static void doubleClick(WebDriver driver, TestObject testObject) {
        WebElement element = getClickableElement(driver, testObject, -1);
        new Actions(driver).doubleClick(element).perform();
    }

    public static void rightClick(WebDriver driver, TestObject testObject) {
        WebElement element = getClickableElement(driver, testObject, -1);
        new Actions(driver).contextClick(element).perform();
    }

    public static void setText(WebDriver driver, TestObject testObject, String text) {
        try {
            WebElement element = getClickableElement(driver, testObject, -1);
            WebElementHelpers.scrollIntoView(driver, element);
            element.sendKeys(text);
        } catch (Exception e) {
            LOGGER.error(testObject + " could not set text successfully.", e);
            throw e;
        }
    }

    public static void clearAndSetText(WebDriver driver, TestObject testObject, String text) {
        WebElement element = getClickableElement(driver, testObject, -1);
        WebElementHelpers.scrollIntoView(driver, element);
        element.clear();
        element.sendKeys(text);
    }

    public static void appendText(WebDriver driver, TestObject testObject, String text) {
        WebElement element = getClickableElement(driver, testObject, -1);
        WebElementHelpers.scrollIntoView(driver, element);
        element.sendKeys(text);
    }

    public static void clearText(WebDriver driver, TestObject testObject) {
        WebElement element = getClickableElement(driver, testObject, -1);
        WebElementHelpers.scrollIntoView(driver, element);
        element.clear();
    }

    public static void sendKeys(WebDriver driver, TestObject testObject, CharSequence... keys) {
        WebElement element = getClickableElement(driver, testObject, -1);
        WebElementHelpers.scrollIntoView(driver, element);
        element.sendKeys(keys);
    }

    public static void pressEnter(WebDriver driver, TestObject testObject) {
        sendKeys(driver, testObject, Keys.ENTER);
    }

    public static void submit(WebDriver driver, TestObject testObject) {
        WebElement element = getClickableElement(driver, testObject, -1);
        element.submit();
    }

    public static String getText(WebDriver driver, TestObject testObject) {
        String text = getVisibleElement(driver, testObject, -1).getText();
        LOGGER.info("Text in element: {}", text);
        return text;
    }

    public static List<String> getTexts(WebDriver driver, TestObject testObject) {
        List<WebElement> listElements = Waiting.Elements.PRESENCE_OF_ALL_ELEMENTS_LOCATED.waitForElements(driver, testObject, true, -1);
        List<String> listTexts = new ArrayList<>();
        for (WebElement element : listElements) {
            listTexts.add(element.getText().trim());
        }
        LOGGER.info("List texts: {}", listTexts);
        return listTexts;
    }

    public static String getAttribute(WebDriver driver, TestObject testObject, String attributeName) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        return element.getAttribute(attributeName);
    }

    public static String getCssValue(WebDriver driver, TestObject testObject, String cssProperty) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        return element.getCssValue(cssProperty);
    }

    public static String getTagName(WebDriver driver, TestObject testObject) {
        WebElement element = getPresentElement(driver, testObject, -1);
        return element.getTagName();
    }

    public static String getValue(WebDriver driver, TestObject testObject) {
        return getAttribute(driver, testObject, "value");
    }

    public static void moveToElement(WebDriver driver, TestObject testObject) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        new Actions(driver).moveToElement(element).perform();
    }

    public static void scrollToElement(WebDriver driver, TestObject testObject) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        new Actions(driver).scrollToElement(element).perform();
    }

    public static void scrollBy(WebDriver driver, int deltaX, int deltaY) {
        new Actions(driver).scrollByAmount(deltaX, deltaY).perform();
    }

    public static void scrollToTop(WebDriver driver) {
        executeJavaScript(driver, "window.scrollTo(0, 0);");
    }

    public static void scrollToBottom(WebDriver driver) {
        executeJavaScript(driver, "window.scrollTo(0, document.body.scrollHeight);");
    }

    public static void dragAndDrop(WebDriver driver, TestObject sourceObject, TestObject targetObject) {
        WebElement source = getVisibleElement(driver, sourceObject, -1);
        WebElement target = getVisibleElement(driver, targetObject, -1);
        new Actions(driver).dragAndDrop(source, target).perform();
    }

    public static void dragAndDropByOffset(WebDriver driver, TestObject sourceObject, int xOffset, int yOffset) {
        WebElement source = getVisibleElement(driver, sourceObject, -1);
        new Actions(driver).dragAndDropBy(source, xOffset, yOffset).perform();
    }

    public static void uploadFile(WebDriver driver, TestObject testObject, String absolutePath) {
        if (TargetFactory.isRemoteTarget()) {
            ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
        }
        WebElement element = getPresentElement(driver, testObject, -1);
        element.sendKeys(absolutePath);
    }

    public static void selectOptionByText(WebDriver driver, TestObject testObject, String text, boolean isPartialMatch) {
        WebElement element = getClickableElement(driver, testObject, -1);
        Select select = new Select(element);
        if (isPartialMatch) {
            for (WebElement option : select.getOptions()) {
                if (option.getText().contains(text)) {
                    select.selectByVisibleText(option.getText());
                    return;
                }
            }
            throw new RuntimeException("Option with text containing '" + text + "' was not found.");
        }
        select.selectByVisibleText(text);
    }

    public static void selectOptionByValue(WebDriver driver, TestObject testObject, String value) {
        WebElement element = getClickableElement(driver, testObject, -1);
        new Select(element).selectByValue(value);
    }

    public static void selectOptionByIndex(WebDriver driver, TestObject testObject, int index) {
        WebElement element = getClickableElement(driver, testObject, -1);
        new Select(element).selectByIndex(index);
    }

    public static String getSelectedOptionText(WebDriver driver, TestObject testObject) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        return new Select(element).getFirstSelectedOption().getText();
    }

    public static List<String> getAllOptionTexts(WebDriver driver, TestObject testObject) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        List<WebElement> options = new Select(element).getOptions();
        List<String> texts = new ArrayList<>();
        for (WebElement option : options) {
            texts.add(option.getText());
        }
        return texts;
    }

    public static void deselectAll(WebDriver driver, TestObject testObject) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        new Select(element).deselectAll();
    }

    public static boolean isChecked(WebDriver driver, TestObject testObject) {
        WebElement element = getPresentElement(driver, testObject, -1);
        return element.isSelected();
    }

    public static void check(WebDriver driver, TestObject testObject) {
        WebElement element = getClickableElement(driver, testObject, -1);
        if (!element.isSelected()) {
            element.click();
        }
    }

    public static void uncheck(WebDriver driver, TestObject testObject) {
        WebElement element = getClickableElement(driver, testObject, -1);
        if (element.isSelected()) {
            element.click();
        }
    }

    public static WebElement waitForElementPresent(WebDriver driver, TestObject testObject, int timeout) {
        return getPresentElement(driver, testObject, timeout);
    }

    public static WebElement waitForElementVisible(WebDriver driver, TestObject testObject, int timeout) {
        return getVisibleElement(driver, testObject, timeout);
    }

    public static WebElement waitForElementClickable(WebDriver driver, TestObject testObject, int timeout) {
        return getClickableElement(driver, testObject, timeout);
    }

    public static boolean waitForElementNotPresent(WebDriver driver, TestObject testObject, int timeout) {
        return Waiting.Condition.NOT_PRESENCE_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, timeout, null);
    }

    public static boolean waitForElementNotVisible(WebDriver driver, TestObject testObject, int timeout) {
        return Waiting.Condition.INVISIBILITY_OF_ELEMENT_LOCATED.waitForElement(driver, testObject, true, timeout, null);
    }

    public static boolean waitForPageLoaded(WebDriver driver, int timeout) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(normalizeTimeout(timeout))).until(webDriver -> {
                Object state = ((JavascriptExecutor) webDriver).executeScript("return document.readyState");
                return "complete".equals(state);
            });
        } catch (TimeoutException e) {
            LOGGER.error("Page was not fully loaded within timeout.", e);
            return false;
        }
    }

    public static boolean waitForUrlContains(WebDriver driver, String value, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(normalizeTimeout(timeout))).until(ExpectedConditions.urlContains(value));
    }

    public static boolean waitForTitleContains(WebDriver driver, String value, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(normalizeTimeout(timeout))).until(ExpectedConditions.titleContains(value));
    }

    public static void verifyElementPresent(WebDriver driver, TestObject testObject) {
        getPresentElement(driver, testObject, -1);
    }

    public static void verifyElementNotPresent(WebDriver driver, TestObject testObject) {
        if (!waitForElementNotPresent(driver, testObject, -1)) {
            throw new RuntimeException(testObject + " is still present in DOM.");
        }
    }

    public static void verifyElementVisible(WebDriver driver, TestObject testObject) {
        getVisibleElement(driver, testObject, -1);
    }

    public static void verifyElementNotVisible(WebDriver driver, TestObject testObject) {
        if (!waitForElementNotVisible(driver, testObject, -1)) {
            throw new RuntimeException(testObject + " is still visible.");
        }
    }

    public static boolean isElementPresent(WebDriver driver, TestObject testObject, int timeout) {
        try {
            getPresentElement(driver, testObject, timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isElementVisible(WebDriver driver, TestObject testObject, int timeout) {
        try {
            getVisibleElement(driver, testObject, timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isElementEnabled(WebDriver driver, TestObject testObject, int timeout) {
        try {
            return getPresentElement(driver, testObject, timeout).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isElementSelected(WebDriver driver, TestObject testObject, int timeout) {
        try {
            return getPresentElement(driver, testObject, timeout).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public static void verifyElementEnabled(WebDriver driver, TestObject testObject) {
        if (!isElementEnabled(driver, testObject, -1)) {
            throw new RuntimeException(testObject + " is disabled.");
        }
    }

    public static void verifyElementDisabled(WebDriver driver, TestObject testObject) {
        if (isElementEnabled(driver, testObject, -1)) {
            throw new RuntimeException(testObject + " is enabled.");
        }
    }

    public static void verifyElementTextEquals(WebDriver driver, TestObject testObject, String expectText) {
        String actualText = getText(driver, testObject);
        if (!actualText.equals(expectText)) {
            throw new RuntimeException("Actual value: " + actualText + " does not equal expected value: " + expectText);
        }
        LOGGER.info("Value '{}' equals expected value '{}'.", actualText, expectText);
    }

    public static void verifyElementTextContains(WebDriver driver, TestObject testObject, String expectText) {
        String actualText = getText(driver, testObject);
        if (!actualText.contains(expectText)) {
            throw new RuntimeException("Actual value: " + actualText + " does not contain expected value: " + expectText);
        }
        LOGGER.info("Value '{}' contains expected value '{}'.", actualText, expectText);
    }

    public static void verifyElementAttributeEquals(WebDriver driver, TestObject testObject, String attributeName, String expectedValue) {
        String actualValue = getAttribute(driver, testObject, attributeName);
        if (!expectedValue.equals(actualValue)) {
            throw new RuntimeException("Actual attribute value: " + actualValue + " does not equal expected value: " + expectedValue);
        }
    }

    public static void verifyElementAttributeContains(WebDriver driver, TestObject testObject, String attributeName, String expectedValue) {
        String actualValue = getAttribute(driver, testObject, attributeName);
        if (actualValue == null || !actualValue.contains(expectedValue)) {
            throw new RuntimeException("Actual attribute value: " + actualValue + " does not contain expected value: " + expectedValue);
        }
    }

    public static void refreshPage(WebDriver driver) {
        driver.navigate().refresh();
    }

    public static void navigateBack(WebDriver driver) {
        driver.navigate().back();
    }

    public static void navigateForward(WebDriver driver) {
        driver.navigate().forward();
    }

    public static void maximizeWindow(WebDriver driver) {
        driver.manage().window().maximize();
    }

    public static void fullscreenWindow(WebDriver driver) {
        driver.manage().window().fullscreen();
    }

    public static void setImplicitWait(WebDriver driver, int timeoutInSeconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutInSeconds));
    }

    public static void setPageLoadTimeout(WebDriver driver, int timeoutInSeconds) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeoutInSeconds));
    }

    public static void setScriptTimeout(WebDriver driver, int timeoutInSeconds) {
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(timeoutInSeconds));
    }

    public static String getCurrentUrl(WebDriver driver) {
        return driver.getCurrentUrl();
    }

    public static String getTitle(WebDriver driver) {
        return driver.getTitle();
    }

    public static String getPageSource(WebDriver driver) {
        return driver.getPageSource();
    }

    public static void verifyUrlEquals(WebDriver driver, String expectedUrl) {
        String actualUrl = getCurrentUrl(driver);
        if (!expectedUrl.equals(actualUrl)) {
            throw new RuntimeException("Actual URL: " + actualUrl + " does not equal expected URL: " + expectedUrl);
        }
    }

    public static void verifyUrlContains(WebDriver driver, String expectedUrlPart) {
        String actualUrl = getCurrentUrl(driver);
        if (!actualUrl.contains(expectedUrlPart)) {
            throw new RuntimeException("Actual URL: " + actualUrl + " does not contain expected value: " + expectedUrlPart);
        }
    }

    public static void verifyTitleEquals(WebDriver driver, String expectedTitle) {
        String actualTitle = getTitle(driver);
        if (!expectedTitle.equals(actualTitle)) {
            throw new RuntimeException("Actual title: " + actualTitle + " does not equal expected title: " + expectedTitle);
        }
    }

    public static void verifyTitleContains(WebDriver driver, String expectedTitlePart) {
        String actualTitle = getTitle(driver);
        if (!actualTitle.contains(expectedTitlePart)) {
            throw new RuntimeException("Actual title: " + actualTitle + " does not contain expected value: " + expectedTitlePart);
        }
    }

    public static boolean isAlertPresent(WebDriver driver, int timeout) {
        try {
            waitForAlert(driver, timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void acceptAlert(WebDriver driver, int timeout) {
        waitForAlert(driver, timeout).accept();
    }

    public static void dismissAlert(WebDriver driver, int timeout) {
        waitForAlert(driver, timeout).dismiss();
    }

    public static String getAlertText(WebDriver driver, int timeout) {
        return waitForAlert(driver, timeout).getText();
    }

    public static void setAlertText(WebDriver driver, String text, int timeout) {
        waitForAlert(driver, timeout).sendKeys(text);
    }

    public static void switchToFrame(WebDriver driver, TestObject frameObject) {
        WebElement frame = getPresentElement(driver, frameObject, -1);
        driver.switchTo().frame(frame);
    }

    public static void switchToFrame(WebDriver driver, int frameIndex) {
        driver.switchTo().frame(frameIndex);
    }

    public static void switchToFrame(WebDriver driver, String frameNameOrId) {
        driver.switchTo().frame(frameNameOrId);
    }

    public static void switchToParentFrame(WebDriver driver) {
        driver.switchTo().parentFrame();
    }

    public static void switchToDefaultContent(WebDriver driver) {
        driver.switchTo().defaultContent();
    }

    public static void openNewTab(WebDriver driver) {
        driver.switchTo().newWindow(WindowType.TAB);
    }

    public static void openNewWindow(WebDriver driver) {
        driver.switchTo().newWindow(WindowType.WINDOW);
    }

    public static Set<String> getWindowHandles(WebDriver driver) {
        return driver.getWindowHandles();
    }

    public static String getCurrentWindowHandle(WebDriver driver) {
        return driver.getWindowHandle();
    }

    public static int getWindowCount(WebDriver driver) {
        return getWindowHandles(driver).size();
    }

    public static void switchToWindowByHandle(WebDriver driver, String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    public static void switchToWindowByIndex(WebDriver driver, int windowIndex) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (windowIndex < 0 || windowIndex >= handles.size()) {
            throw new RuntimeException("Invalid window index: " + windowIndex + ". Current number of windows: " + handles.size());
        }
        driver.switchTo().window(handles.get(windowIndex));
    }

    public static void switchToLatestWindow(WebDriver driver) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (handles.isEmpty()) {
            throw new RuntimeException("No browser windows available.");
        }
        driver.switchTo().window(handles.get(handles.size() - 1));
    }

    public static void switchToWindowByTitle(WebDriver driver, String title) {
        String currentHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getTitle().equals(title)) {
                return;
            }
        }
        driver.switchTo().window(currentHandle);
        throw new RuntimeException("No window found with title: " + title);
    }

    public static void switchToWindowByUrlContains(WebDriver driver, String urlPart) {
        String currentHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getCurrentUrl().contains(urlPart)) {
                return;
            }
        }
        driver.switchTo().window(currentHandle);
        throw new RuntimeException("No window found with URL containing: " + urlPart);
    }

    public static boolean waitForNumberOfWindowsToBe(WebDriver driver, int expectedCount, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(normalizeTimeout(timeout))).until(
                ExpectedConditions.numberOfWindowsToBe(expectedCount));
    }

    public static void closeCurrentWindow(WebDriver driver) {
        driver.close();
    }

    public static void closeAllAdditionalWindows(WebDriver driver) {
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (handles.size() <= 1) {
            return;
        }
        String primaryHandle = driver.getWindowHandle();
        for (String handle : handles) {
            if (primaryHandle.equals(handle)) {
                continue;
            }
            driver.switchTo().window(handle);
            closeCurrentWindow(driver);
        }
        driver.switchTo().window(primaryHandle);
    }

    public static Object executeJavaScript(WebDriver driver, String script, Object... arguments) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        return executor.executeScript(script, arguments);
    }

    public static String getInnerTextByJavaScript(WebDriver driver, TestObject testObject) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        Object value = executeJavaScript(driver, "return arguments[0].innerText;", element);
        return value == null ? null : value.toString();
    }

    public static void setAttributeByJavaScript(WebDriver driver, TestObject testObject, String attributeName, String attributeValue) {
        WebElement element = getPresentElement(driver, testObject, -1);
        executeJavaScript(driver, "arguments[0].setAttribute(arguments[1], arguments[2]);", element, attributeName, attributeValue);
    }

    public static void removeAttributeByJavaScript(WebDriver driver, TestObject testObject, String attributeName) {
        WebElement element = getPresentElement(driver, testObject, -1);
        executeJavaScript(driver, "arguments[0].removeAttribute(arguments[1]);", element, attributeName);
    }

    public static void highlightElement(WebDriver driver, TestObject testObject) {
        WebElement element = getVisibleElement(driver, testObject, -1);
        executeJavaScript(driver, "arguments[0].style.border='2px solid #FF0000';", element);
    }

    public static void addCookie(WebDriver driver, String name, String value) {
        driver.manage().addCookie(new Cookie(name, value));
    }

    public static void addCookie(WebDriver driver, Cookie cookie) {
        driver.manage().addCookie(cookie);
    }

    public static Cookie getCookie(WebDriver driver, String name) {
        return driver.manage().getCookieNamed(name);
    }

    public static Set<Cookie> getCookies(WebDriver driver) {
        return driver.manage().getCookies();
    }

    public static void deleteCookie(WebDriver driver, String name) {
        driver.manage().deleteCookieNamed(name);
    }

    public static void deleteAllCookies(WebDriver driver) {
        driver.manage().deleteAllCookies();
    }

    public static void setLocalStorageItem(WebDriver driver, String key, String value) {
        executeJavaScript(driver, "window.localStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    public static String getLocalStorageItem(WebDriver driver, String key) {
        Object value = executeJavaScript(driver, "return window.localStorage.getItem(arguments[0]);", key);
        return value == null ? null : value.toString();
    }

    public static void removeLocalStorageItem(WebDriver driver, String key) {
        executeJavaScript(driver, "window.localStorage.removeItem(arguments[0]);", key);
    }

    public static void clearLocalStorage(WebDriver driver) {
        executeJavaScript(driver, "window.localStorage.clear();");
    }

    public static void setSessionStorageItem(WebDriver driver, String key, String value) {
        executeJavaScript(driver, "window.sessionStorage.setItem(arguments[0], arguments[1]);", key, value);
    }

    public static String getSessionStorageItem(WebDriver driver, String key) {
        Object value = executeJavaScript(driver, "return window.sessionStorage.getItem(arguments[0]);", key);
        return value == null ? null : value.toString();
    }

    public static void removeSessionStorageItem(WebDriver driver, String key) {
        executeJavaScript(driver, "window.sessionStorage.removeItem(arguments[0]);", key);
    }

    public static void clearSessionStorage(WebDriver driver) {
        executeJavaScript(driver, "window.sessionStorage.clear();");
    }

    public static String capturePageScreenshot(String fileName) throws Exception {
        return TakeScreenshot.capturePageScreenshot(fileName);
    }

    public static String captureFullPageScreenshot(String fileName) throws Exception {
        return TakeScreenshot.captureFullPageScreenshot(fileName);
    }

    public static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
