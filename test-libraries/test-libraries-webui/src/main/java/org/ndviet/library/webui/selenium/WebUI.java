package org.ndviet.library.webui.selenium;

import org.ndviet.library.TestObject.TestObject;
import org.ndviet.library.webui.selenium.bidi.WebUiBiDi;
import org.ndviet.library.webui.selenium.driver.DriverManager;
import org.ndviet.library.webui.selenium.driver.TargetFactory;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.BiDiSessionStatus;
import org.openqa.selenium.WindowType;

import java.util.List;
import java.util.Set;

public class WebUI {

    private WebUI() {
    }

    private static WebDriver getDriver() {
        return DriverManager.getInstance().getDriver();
    }

    public static WebDriver openBrowser() {
        DriverManager.getInstance().setDriver(TargetFactory.createInstance());
        return getDriver();
    }

    public static WebDriver openBrowser(String url) {
        DriverManager.getInstance().setDriver(TargetFactory.createInstance());
        navigateToUrl(url);
        return getDriver();
    }

    public static WebDriver openBrowser(String browser, String url) {
        DriverManager.getInstance().setDriver(TargetFactory.createInstance(browser));
        navigateToUrl(url);
        return getDriver();
    }

    public static WebDriver openBrowser(String browser, String target, String url) {
        DriverManager.getInstance().setDriver(TargetFactory.createInstance(browser, target));
        navigateToUrl(url);
        return getDriver();
    }

    public static void navigateToUrl(String url) {
        getDriver().get(url);
    }

    public static void closeBrowser() {
        DriverManager.getInstance().quit();
    }

    public static WebElement findWebElement(TestObject testObject) {
        return WebElementHelpers.findWebElement(getDriver(), testObject);
    }

    public static List<WebElement> findWebElements(TestObject testObject) {
        return WebElementHelpers.findWebElements(getDriver(), testObject);
    }

    public static void click(TestObject testObject) {
        WebUIAbstract.click(getDriver(), testObject);
    }

    public static void click(TestObject testObject, int timeout) {
        WebUIAbstract.click(getDriver(), testObject, timeout);
    }

    public static void clickByJavaScript(TestObject testObject) {
        WebUIAbstract.clickByJavaScript(getDriver(), testObject);
    }

    public static void doubleClick(TestObject testObject) {
        WebUIAbstract.doubleClick(getDriver(), testObject);
    }

    public static void rightClick(TestObject testObject) {
        WebUIAbstract.rightClick(getDriver(), testObject);
    }

    public static void setText(TestObject testObject, String text) {
        WebUIAbstract.setText(getDriver(), testObject, text);
    }

    public static void clearAndSetText(TestObject testObject, String text) {
        WebUIAbstract.clearAndSetText(getDriver(), testObject, text);
    }

    public static void appendText(TestObject testObject, String text) {
        WebUIAbstract.appendText(getDriver(), testObject, text);
    }

    public static void clearText(TestObject testObject) {
        WebUIAbstract.clearText(getDriver(), testObject);
    }

    public static void sendKeys(TestObject testObject, CharSequence... keys) {
        WebUIAbstract.sendKeys(getDriver(), testObject, keys);
    }

    public static void pressEnter(TestObject testObject) {
        WebUIAbstract.pressEnter(getDriver(), testObject);
    }

    public static void submit(TestObject testObject) {
        WebUIAbstract.submit(getDriver(), testObject);
    }

    public static String getText(TestObject testObject) {
        return WebUIAbstract.getText(getDriver(), testObject);
    }

    public static List<String> getTexts(TestObject testObject) {
        return WebUIAbstract.getTexts(getDriver(), testObject);
    }

    public static String getAttribute(TestObject testObject, String attributeName) {
        return WebUIAbstract.getAttribute(getDriver(), testObject, attributeName);
    }

    public static String getCssValue(TestObject testObject, String cssProperty) {
        return WebUIAbstract.getCssValue(getDriver(), testObject, cssProperty);
    }

    public static String getTagName(TestObject testObject) {
        return WebUIAbstract.getTagName(getDriver(), testObject);
    }

    public static String getValue(TestObject testObject) {
        return WebUIAbstract.getValue(getDriver(), testObject);
    }

    public static void moveToElement(TestObject testObject) {
        WebUIAbstract.moveToElement(getDriver(), testObject);
    }

    public static void scrollToElement(TestObject testObject) {
        WebUIAbstract.scrollToElement(getDriver(), testObject);
    }

    public static void scrollBy(int deltaX, int deltaY) {
        WebUIAbstract.scrollBy(getDriver(), deltaX, deltaY);
    }

    public static void scrollToTop() {
        WebUIAbstract.scrollToTop(getDriver());
    }

    public static void scrollToBottom() {
        WebUIAbstract.scrollToBottom(getDriver());
    }

    public static void dragAndDrop(TestObject sourceObject, TestObject targetObject) {
        WebUIAbstract.dragAndDrop(getDriver(), sourceObject, targetObject);
    }

    public static void dragAndDropByOffset(TestObject sourceObject, int xOffset, int yOffset) {
        WebUIAbstract.dragAndDropByOffset(getDriver(), sourceObject, xOffset, yOffset);
    }

    public static void uploadFile(TestObject testObject, String absolutePath) {
        WebUIAbstract.uploadFile(getDriver(), testObject, absolutePath);
    }

    public static void selectOptionByText(TestObject testObject, String text, boolean isPartialMatch) {
        WebUIAbstract.selectOptionByText(getDriver(), testObject, text, isPartialMatch);
    }

    public static void selectOptionByValue(TestObject testObject, String value) {
        WebUIAbstract.selectOptionByValue(getDriver(), testObject, value);
    }

    public static void selectOptionByIndex(TestObject testObject, int index) {
        WebUIAbstract.selectOptionByIndex(getDriver(), testObject, index);
    }

    public static String getSelectedOptionText(TestObject testObject) {
        return WebUIAbstract.getSelectedOptionText(getDriver(), testObject);
    }

    public static List<String> getAllOptionTexts(TestObject testObject) {
        return WebUIAbstract.getAllOptionTexts(getDriver(), testObject);
    }

    public static void deselectAll(TestObject testObject) {
        WebUIAbstract.deselectAll(getDriver(), testObject);
    }

    public static void check(TestObject testObject) {
        WebUIAbstract.check(getDriver(), testObject);
    }

    public static void uncheck(TestObject testObject) {
        WebUIAbstract.uncheck(getDriver(), testObject);
    }

    public static boolean isChecked(TestObject testObject) {
        return WebUIAbstract.isChecked(getDriver(), testObject);
    }

    public static WebElement waitForElementPresent(TestObject testObject, int timeout) {
        return WebUIAbstract.waitForElementPresent(getDriver(), testObject, timeout);
    }

    public static WebElement waitForElementVisible(TestObject testObject, int timeout) {
        return WebUIAbstract.waitForElementVisible(getDriver(), testObject, timeout);
    }

    public static WebElement waitForElementClickable(TestObject testObject, int timeout) {
        return WebUIAbstract.waitForElementClickable(getDriver(), testObject, timeout);
    }

    public static boolean waitForElementNotPresent(TestObject testObject, int timeout) {
        return WebUIAbstract.waitForElementNotPresent(getDriver(), testObject, timeout);
    }

    public static boolean waitForElementNotVisible(TestObject testObject, int timeout) {
        return WebUIAbstract.waitForElementNotVisible(getDriver(), testObject, timeout);
    }

    public static boolean waitForPageLoaded(int timeout) {
        return WebUIAbstract.waitForPageLoaded(getDriver(), timeout);
    }

    public static boolean waitForUrlContains(String value, int timeout) {
        return WebUIAbstract.waitForUrlContains(getDriver(), value, timeout);
    }

    public static boolean waitForTitleContains(String value, int timeout) {
        return WebUIAbstract.waitForTitleContains(getDriver(), value, timeout);
    }

    public static void verifyElementPresent(TestObject testObject) {
        WebUIAbstract.verifyElementPresent(getDriver(), testObject);
    }

    public static void verifyElementNotPresent(TestObject testObject) {
        WebUIAbstract.verifyElementNotPresent(getDriver(), testObject);
    }

    public static void verifyElementVisible(TestObject testObject) {
        WebUIAbstract.verifyElementVisible(getDriver(), testObject);
    }

    public static void verifyElementNotVisible(TestObject testObject) {
        WebUIAbstract.verifyElementNotVisible(getDriver(), testObject);
    }

    public static void verifyElementEnabled(TestObject testObject) {
        WebUIAbstract.verifyElementEnabled(getDriver(), testObject);
    }

    public static void verifyElementDisabled(TestObject testObject) {
        WebUIAbstract.verifyElementDisabled(getDriver(), testObject);
    }

    public static boolean isElementPresent(TestObject testObject, int timeout) {
        return WebUIAbstract.isElementPresent(getDriver(), testObject, timeout);
    }

    public static boolean isElementVisible(TestObject testObject, int timeout) {
        return WebUIAbstract.isElementVisible(getDriver(), testObject, timeout);
    }

    public static boolean isElementEnabled(TestObject testObject, int timeout) {
        return WebUIAbstract.isElementEnabled(getDriver(), testObject, timeout);
    }

    public static boolean isElementSelected(TestObject testObject, int timeout) {
        return WebUIAbstract.isElementSelected(getDriver(), testObject, timeout);
    }

    public static void verifyElementTextEquals(TestObject testObject, String expectText) {
        WebUIAbstract.verifyElementTextEquals(getDriver(), testObject, expectText);
    }

    public static void verifyElementTextContains(TestObject testObject, String expectText) {
        WebUIAbstract.verifyElementTextContains(getDriver(), testObject, expectText);
    }

    public static void verifyElementAttributeEquals(TestObject testObject, String attributeName, String expectedValue) {
        WebUIAbstract.verifyElementAttributeEquals(getDriver(), testObject, attributeName, expectedValue);
    }

    public static void verifyElementAttributeContains(TestObject testObject, String attributeName, String expectedValue) {
        WebUIAbstract.verifyElementAttributeContains(getDriver(), testObject, attributeName, expectedValue);
    }

    public static void refreshPage() {
        WebUIAbstract.refreshPage(getDriver());
    }

    public static void navigateBack() {
        WebUIAbstract.navigateBack(getDriver());
    }

    public static void navigateForward() {
        WebUIAbstract.navigateForward(getDriver());
    }

    public static void maximizeWindow() {
        WebUIAbstract.maximizeWindow(getDriver());
    }

    public static void fullscreenWindow() {
        WebUIAbstract.fullscreenWindow(getDriver());
    }

    public static void setImplicitWait(int timeoutInSeconds) {
        WebUIAbstract.setImplicitWait(getDriver(), timeoutInSeconds);
    }

    public static void setPageLoadTimeout(int timeoutInSeconds) {
        WebUIAbstract.setPageLoadTimeout(getDriver(), timeoutInSeconds);
    }

    public static void setScriptTimeout(int timeoutInSeconds) {
        WebUIAbstract.setScriptTimeout(getDriver(), timeoutInSeconds);
    }

    public static String getCurrentUrl() {
        return WebUIAbstract.getCurrentUrl(getDriver());
    }

    public static String getTitle() {
        return WebUIAbstract.getTitle(getDriver());
    }

    public static String getPageSource() {
        return WebUIAbstract.getPageSource(getDriver());
    }

    public static void verifyUrlEquals(String expectedUrl) {
        WebUIAbstract.verifyUrlEquals(getDriver(), expectedUrl);
    }

    public static void verifyUrlContains(String expectedUrlPart) {
        WebUIAbstract.verifyUrlContains(getDriver(), expectedUrlPart);
    }

    public static void verifyTitleEquals(String expectedTitle) {
        WebUIAbstract.verifyTitleEquals(getDriver(), expectedTitle);
    }

    public static void verifyTitleContains(String expectedTitlePart) {
        WebUIAbstract.verifyTitleContains(getDriver(), expectedTitlePart);
    }

    public static boolean isAlertPresent(int timeout) {
        return WebUIAbstract.isAlertPresent(getDriver(), timeout);
    }

    public static void acceptAlert(int timeout) {
        WebUIAbstract.acceptAlert(getDriver(), timeout);
    }

    public static void dismissAlert(int timeout) {
        WebUIAbstract.dismissAlert(getDriver(), timeout);
    }

    public static String getAlertText(int timeout) {
        return WebUIAbstract.getAlertText(getDriver(), timeout);
    }

    public static void setAlertText(String text, int timeout) {
        WebUIAbstract.setAlertText(getDriver(), text, timeout);
    }

    public static void switchToFrame(TestObject frameObject) {
        WebUIAbstract.switchToFrame(getDriver(), frameObject);
    }

    public static void switchToFrame(int frameIndex) {
        WebUIAbstract.switchToFrame(getDriver(), frameIndex);
    }

    public static void switchToFrame(String frameNameOrId) {
        WebUIAbstract.switchToFrame(getDriver(), frameNameOrId);
    }

    public static void switchToParentFrame() {
        WebUIAbstract.switchToParentFrame(getDriver());
    }

    public static void switchToDefaultContent() {
        WebUIAbstract.switchToDefaultContent(getDriver());
    }

    public static void openNewTab() {
        WebUIAbstract.openNewTab(getDriver());
    }

    public static void openNewWindow() {
        WebUIAbstract.openNewWindow(getDriver());
    }

    public static Set<String> getWindowHandles() {
        return WebUIAbstract.getWindowHandles(getDriver());
    }

    public static String getCurrentWindowHandle() {
        return WebUIAbstract.getCurrentWindowHandle(getDriver());
    }

    public static int getWindowCount() {
        return WebUIAbstract.getWindowCount(getDriver());
    }

    public static void switchToWindowByHandle(String windowHandle) {
        WebUIAbstract.switchToWindowByHandle(getDriver(), windowHandle);
    }

    public static void switchToWindowByIndex(int windowIndex) {
        WebUIAbstract.switchToWindowByIndex(getDriver(), windowIndex);
    }

    public static void switchToLatestWindow() {
        WebUIAbstract.switchToLatestWindow(getDriver());
    }

    public static void switchToWindowByTitle(String title) {
        WebUIAbstract.switchToWindowByTitle(getDriver(), title);
    }

    public static void switchToWindowByUrlContains(String urlPart) {
        WebUIAbstract.switchToWindowByUrlContains(getDriver(), urlPart);
    }

    public static boolean waitForNumberOfWindowsToBe(int expectedCount, int timeout) {
        return WebUIAbstract.waitForNumberOfWindowsToBe(getDriver(), expectedCount, timeout);
    }

    public static void closeCurrentWindow() {
        WebUIAbstract.closeCurrentWindow(getDriver());
    }

    public static void closeAllAdditionalWindows() {
        WebUIAbstract.closeAllAdditionalWindows(getDriver());
    }

    public static Object executeJavaScript(String script, Object... arguments) {
        return WebUIAbstract.executeJavaScript(getDriver(), script, arguments);
    }

    public static String getInnerTextByJavaScript(TestObject testObject) {
        return WebUIAbstract.getInnerTextByJavaScript(getDriver(), testObject);
    }

    public static void setAttributeByJavaScript(TestObject testObject, String attributeName, String attributeValue) {
        WebUIAbstract.setAttributeByJavaScript(getDriver(), testObject, attributeName, attributeValue);
    }

    public static void removeAttributeByJavaScript(TestObject testObject, String attributeName) {
        WebUIAbstract.removeAttributeByJavaScript(getDriver(), testObject, attributeName);
    }

    public static void highlightElement(TestObject testObject) {
        WebUIAbstract.highlightElement(getDriver(), testObject);
    }

    public static void addCookie(String name, String value) {
        WebUIAbstract.addCookie(getDriver(), name, value);
    }

    public static void addCookie(Cookie cookie) {
        WebUIAbstract.addCookie(getDriver(), cookie);
    }

    public static Cookie getCookie(String name) {
        return WebUIAbstract.getCookie(getDriver(), name);
    }

    public static Set<Cookie> getCookies() {
        return WebUIAbstract.getCookies(getDriver());
    }

    public static void deleteCookie(String name) {
        WebUIAbstract.deleteCookie(getDriver(), name);
    }

    public static void deleteAllCookies() {
        WebUIAbstract.deleteAllCookies(getDriver());
    }

    public static void setLocalStorageItem(String key, String value) {
        WebUIAbstract.setLocalStorageItem(getDriver(), key, value);
    }

    public static String getLocalStorageItem(String key) {
        return WebUIAbstract.getLocalStorageItem(getDriver(), key);
    }

    public static void removeLocalStorageItem(String key) {
        WebUIAbstract.removeLocalStorageItem(getDriver(), key);
    }

    public static void clearLocalStorage() {
        WebUIAbstract.clearLocalStorage(getDriver());
    }

    public static void setSessionStorageItem(String key, String value) {
        WebUIAbstract.setSessionStorageItem(getDriver(), key, value);
    }

    public static String getSessionStorageItem(String key) {
        return WebUIAbstract.getSessionStorageItem(getDriver(), key);
    }

    public static void removeSessionStorageItem(String key) {
        WebUIAbstract.removeSessionStorageItem(getDriver(), key);
    }

    public static void clearSessionStorage() {
        WebUIAbstract.clearSessionStorage(getDriver());
    }

    public static String capturePageScreenshot(String fileName) throws Exception {
        return WebUIAbstract.capturePageScreenshot(fileName);
    }

    public static String captureFullPageScreenshot(String fileName) throws Exception {
        return WebUIAbstract.captureFullPageScreenshot(fileName);
    }

    public static boolean isBiDiSupported() {
        return WebUiBiDi.isBiDiSupported(getDriver());
    }

    public static String getCurrentBiDiBrowsingContextId() {
        return WebUiBiDi.getCurrentBrowsingContextId(getDriver());
    }

    public static String createBiDiUserContext() {
        return WebUiBiDi.createUserContext(getDriver());
    }

    public static List<String> getBiDiUserContexts() {
        return WebUiBiDi.getUserContexts(getDriver());
    }

    public static void removeBiDiUserContext(String userContextId) {
        WebUiBiDi.removeUserContext(userContextId, getDriver());
    }

    public static String openNewBiDiTab() {
        return WebUiBiDi.createBrowsingContext(getDriver(), WindowType.TAB).getId();
    }

    public static String openNewBiDiTab(String userContextId) {
        return WebUiBiDi.createBrowsingContext(WindowType.TAB, userContextId, getDriver()).getId();
    }

    public static String openNewBiDiWindow() {
        return WebUiBiDi.createBrowsingContext(getDriver(), WindowType.WINDOW).getId();
    }

    public static String openNewBiDiWindow(String userContextId) {
        return WebUiBiDi.createBrowsingContext(WindowType.WINDOW, userContextId, getDriver()).getId();
    }

    public static void switchToBiDiBrowsingContext(String browsingContextId) {
        WebUiBiDi.switchToBrowsingContext(browsingContextId, getDriver());
    }

    public static void closeBiDiBrowsingContext(String browsingContextId) {
        WebUiBiDi.closeBrowsingContext(browsingContextId, getDriver());
    }

    public static BiDiSessionStatus getBiDiSessionStatus() {
        return WebUiBiDi.getBiDiSessionStatus(getDriver());
    }

    public static WebUiBiDi.BiDiLogCollector startBiDiLogCollector() {
        return WebUiBiDi.startLogCollector(getDriver());
    }

    public static WebUiBiDi.BiDiLogCollector startBiDiLogCollector(String browsingContextId) {
        return WebUiBiDi.startLogCollector(browsingContextId, getDriver());
    }

    public static WebUiBiDi.BiDiNetworkCollector startBiDiNetworkCollector() {
        return WebUiBiDi.startNetworkCollector(getDriver());
    }

    public static WebUiBiDi.BiDiNetworkCollector startBiDiNetworkCollector(String browsingContextId) {
        return WebUiBiDi.startNetworkCollector(browsingContextId, getDriver());
    }

    public static void pause(long milliseconds) {
        WebUIAbstract.pause(milliseconds);
    }
}
