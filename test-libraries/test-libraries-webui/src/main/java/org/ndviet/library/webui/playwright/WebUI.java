package org.ndviet.library.webui.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.TestObject.TestObject;
import org.ndviet.library.TestObject.TestObject.ParentContext;
import org.ndviet.library.file.FileHelpers;
import org.ndviet.library.webui.config.WebUiConfiguration;
import org.openqa.selenium.Keys;
import org.openqa.selenium.bidi.BiDiSessionStatus;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.ndviet.library.configuration.Constants.DEFAULT_SCREENSHOT_COUNT;

public class WebUI {
    private static final Logger LOGGER = LogManager.getLogger(WebUI.class);
    private static final ThreadLocal<PlaywrightSession> SESSION = new ThreadLocal<>();
    private static int screenshotCount = DEFAULT_SCREENSHOT_COUNT;

    private WebUI() {
    }

    private static PlaywrightSession requireSession() {
        PlaywrightSession session = SESSION.get();
        if (session == null) {
            throw new IllegalStateException("Playwright browser is not opened. Call WebUI.openBrowser() first.");
        }
        return session;
    }

    private static PlaywrightSession requireActiveContextSession() {
        PlaywrightSession session = requireSession();
        if (session.page == null) {
            throw new IllegalStateException("Playwright browser is not opened. Call WebUI.openBrowser() first.");
        }
        return session;
    }

    private static Page getPage() {
        return requireActiveContextSession().page;
    }

    private static BrowserContext getContext() {
        return requireActiveContextSession().getContext();
    }

    private static Frame getCurrentFrame() {
        return requireActiveContextSession().frame;
    }

    private static int defaultTimeoutInSeconds() {
        return WebUiConfiguration.getPlaywrightDefaultTimeout();
    }

    private static double timeoutInMillis(int timeout) {
        return (timeout >= 0 ? timeout : defaultTimeoutInSeconds()) * 1000d;
    }

    private static boolean isHeadless() {
        return WebUiConfiguration.isPlaywrightHeadless();
    }

    private static String resolveBrowserName(String browser) {
        if (browser != null && !browser.isBlank()) {
            return browser;
        }
        return WebUiConfiguration.getPlaywrightBrowserType();
    }

    private static BrowserType resolveBrowserType(Playwright playwright, String browser) {
        String normalizedBrowser = resolveBrowserName(browser).toLowerCase(Locale.ROOT);
        return switch (normalizedBrowser) {
            case "chrome", "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "safari", "webkit" -> playwright.webkit();
            case "edge", "msedge" -> playwright.chromium();
            default -> throw new IllegalArgumentException("Unsupported Playwright browser type: " + normalizedBrowser);
        };
    }

    private static Browser.NewContextOptions newContextOptions() {
        return new Browser.NewContextOptions();
    }

    private static void attachDialogListener(Page page, PlaywrightSession session) {
        page.onDialog(dialog -> {
            PendingDialog pendingDialog = new PendingDialog(dialog);
            session.pendingDialog = pendingDialog;
            try {
                if (!pendingDialog.await(defaultTimeoutInSeconds())) {
                    dialog.dismiss();
                    return;
                }
                if (pendingDialog.action == DialogAction.ACCEPT) {
                    if (pendingDialog.promptText != null) {
                        dialog.accept(pendingDialog.promptText);
                    } else {
                        dialog.accept();
                    }
                } else {
                    dialog.dismiss();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                dialog.dismiss();
                throw new RuntimeException(e);
            } finally {
                session.pendingDialog = null;
            }
        });
    }

    private static void configureSessionPage(Page page) {
        page.setDefaultTimeout(timeoutInMillis(-1));
        page.setDefaultNavigationTimeout(timeoutInMillis(-1));
    }

    private static PlaywrightSession createSession(String browserName) {
        Playwright playwright = Playwright.create();
        Browser browser = resolveBrowserType(playwright, browserName)
                .launch(new BrowserType.LaunchOptions().setHeadless(isHeadless()));
        PlaywrightSession session = new PlaywrightSession(playwright, browser);
        session.createBrowserContext();
        return session;
    }

    public static Page openBrowser() {
        closeBrowser();
        PlaywrightSession session = createSession(null);
        SESSION.set(session);
        return session.page;
    }

    public static Page openBrowser(String url) {
        Page page = openBrowser();
        navigateToUrl(url);
        return page;
    }

    public static Page openBrowser(String browser, String url) {
        closeBrowser();
        PlaywrightSession session = createSession(browser);
        SESSION.set(session);
        navigateToUrl(url);
        return session.page;
    }

    public static Page openBrowser(String browser, String target, String url) {
        if (target != null && !target.isBlank() && !"local".equalsIgnoreCase(target)) {
            throw new UnsupportedOperationException("Playwright WebUI currently supports only the local target.");
        }
        return openBrowser(browser, url);
    }

    public static void navigateToUrl(String url) {
        getPage().navigate(url);
    }

    public static void closeBrowser() {
        PlaywrightSession session = SESSION.get();
        if (session != null) {
            session.close();
            SESSION.remove();
        }
    }

    public static String createBrowserContext() {
        return requireSession().createBrowserContext();
    }

    public static String getCurrentBrowserContextId() {
        return requireSession().getCurrentContextId();
    }

    public static List<String> getBrowserContextIds() {
        return requireSession().getContextIds();
    }

    public static void switchToBrowserContext(String browserContextId) {
        requireSession().switchToContext(browserContextId);
    }

    public static void closeBrowserContext() {
        requireSession().closeCurrentContext();
    }

    public static void closeBrowserContext(String browserContextId) {
        requireSession().closeContext(browserContextId);
    }

    public static Locator findWebElement(TestObject testObject) {
        return resolveLocator(testObject, null, 0d);
    }

    public static List<Locator> findWebElements(TestObject testObject) {
        for (String selector : selectorsFor(testObject.getValues())) {
            Locator locator = scopedLocator(resolveScope(testObject), selector);
            List<Locator> matches = locator.all();
            if (!matches.isEmpty()) {
                return matches;
            }
        }
        return List.of();
    }

    public static void click(TestObject testObject) {
        getClickableLocator(testObject, -1).click();
    }

    public static void click(TestObject testObject, int timeout) {
        getClickableLocator(testObject, timeout).click(new Locator.ClickOptions().setTimeout(timeoutInMillis(timeout)));
    }

    public static void clickByJavaScript(TestObject testObject) {
        getVisibleLocator(testObject, -1).evaluate("element => element.click()");
    }

    public static void doubleClick(TestObject testObject) {
        getClickableLocator(testObject, -1).dblclick();
    }

    public static void rightClick(TestObject testObject) {
        getClickableLocator(testObject, -1).click(new Locator.ClickOptions().setButton(com.microsoft.playwright.options.MouseButton.RIGHT));
    }

    public static void setText(TestObject testObject, String text) {
        getClickableLocator(testObject, -1).type(text == null ? "" : text);
    }

    public static void clearAndSetText(TestObject testObject, String text) {
        getClickableLocator(testObject, -1).fill(text == null ? "" : text);
    }

    public static void appendText(TestObject testObject, String text) {
        getClickableLocator(testObject, -1).type(text == null ? "" : text);
    }

    public static void clearText(TestObject testObject) {
        getClickableLocator(testObject, -1).fill("");
    }

    public static void sendKeys(TestObject testObject, CharSequence... keys) {
        Locator locator = getClickableLocator(testObject, -1);
        locator.focus();
        for (CharSequence key : keys) {
            if (key == null) {
                continue;
            }
            if (key instanceof Keys seleniumKey) {
                locator.press(toPlaywrightKey(seleniumKey));
                continue;
            }
            locator.type(key.toString());
        }
    }

    public static void pressEnter(TestObject testObject) {
        getClickableLocator(testObject, -1).press("Enter");
    }

    public static void submit(TestObject testObject) {
        getPresentLocator(testObject, -1).evaluate("""
                element => {
                    const form = element.form || element.closest('form');
                    if (!form) {
                        throw new Error('Element is not attached to a form.');
                    }
                    if (form.requestSubmit) {
                        form.requestSubmit();
                        return;
                    }
                    form.submit();
                }
                """);
    }

    public static String getText(TestObject testObject) {
        return getVisibleLocator(testObject, -1).innerText();
    }

    public static List<String> getTexts(TestObject testObject) {
        List<String> texts = new ArrayList<>();
        for (Locator locator : findWebElements(testObject)) {
            texts.add(locator.innerText().trim());
        }
        return texts;
    }

    public static String getAttribute(TestObject testObject, String attributeName) {
        return getVisibleLocator(testObject, -1).getAttribute(attributeName);
    }

    public static String getCssValue(TestObject testObject, String cssProperty) {
        Object value = getVisibleLocator(testObject, -1)
                .evaluate("(element, property) => window.getComputedStyle(element).getPropertyValue(property)", cssProperty);
        return value == null ? null : value.toString();
    }

    public static String getTagName(TestObject testObject) {
        Object tagName = getPresentLocator(testObject, -1)
                .evaluate("element => element.tagName.toLowerCase()");
        return tagName == null ? null : tagName.toString();
    }

    public static String getValue(TestObject testObject) {
        return getClickableLocator(testObject, -1).inputValue();
    }

    public static void moveToElement(TestObject testObject) {
        getVisibleLocator(testObject, -1).hover();
    }

    public static void scrollToElement(TestObject testObject) {
        getVisibleLocator(testObject, -1).scrollIntoViewIfNeeded();
    }

    public static void scrollBy(int deltaX, int deltaY) {
        getPage().evaluate("(offset) => window.scrollBy(offset.x, offset.y)", new ScrollOffset(deltaX, deltaY));
    }

    public static void scrollToTop() {
        getPage().evaluate("() => window.scrollTo(0, 0)");
    }

    public static void scrollToBottom() {
        getPage().evaluate("() => window.scrollTo(0, document.body.scrollHeight)");
    }

    public static void dragAndDrop(TestObject sourceObject, TestObject targetObject) {
        getVisibleLocator(sourceObject, -1).dragTo(getVisibleLocator(targetObject, -1));
    }

    public static void dragAndDropByOffset(TestObject sourceObject, int xOffset, int yOffset) {
        Locator source = getVisibleLocator(sourceObject, -1);
        com.microsoft.playwright.options.BoundingBox box = source.boundingBox();
        if (box == null) {
            throw new RuntimeException("Cannot calculate bounding box for drag and drop source.");
        }
        double startX = box.x + (box.width / 2);
        double startY = box.y + (box.height / 2);
        getPage().mouse().move(startX, startY);
        getPage().mouse().down();
        getPage().mouse().move(startX + xOffset, startY + yOffset);
        getPage().mouse().up();
    }

    public static void uploadFile(TestObject testObject, String absolutePath) {
        getPresentLocator(testObject, -1).setInputFiles(Paths.get(absolutePath));
    }

    public static void selectOptionByText(TestObject testObject, String text, boolean isPartialMatch) {
        Locator locator = getClickableLocator(testObject, -1);
        if (!isPartialMatch) {
            locator.selectOption(new SelectOption().setLabel(text));
            return;
        }
        Object value = locator.evaluate("""
                (select, expectedText) => {
                    const option = Array.from(select.options).find(item => item.text.includes(expectedText));
                    return option ? option.value : null;
                }
                """, text);
        if (value == null) {
            throw new RuntimeException("Option with text containing '" + text + "' was not found.");
        }
        locator.selectOption(value.toString());
    }

    public static void selectOptionByValue(TestObject testObject, String value) {
        getClickableLocator(testObject, -1).selectOption(value);
    }

    public static void selectOptionByIndex(TestObject testObject, int index) {
        getClickableLocator(testObject, -1).selectOption(new SelectOption().setIndex(index));
    }

    public static String getSelectedOptionText(TestObject testObject) {
        Object value = getVisibleLocator(testObject, -1).evaluate("""
                select => select.selectedOptions.length > 0 ? select.selectedOptions[0].text : null
                """);
        return value == null ? null : value.toString();
    }

    public static List<String> getAllOptionTexts(TestObject testObject) {
        Object values = getVisibleLocator(testObject, -1).evaluate("""
                select => Array.from(select.options).map(option => option.text)
                """);
        return castStringList(values);
    }

    public static void deselectAll(TestObject testObject) {
        getVisibleLocator(testObject, -1).evaluate("""
                select => {
                    Array.from(select.options).forEach(option => option.selected = false);
                    select.dispatchEvent(new Event('input', { bubbles: true }));
                    select.dispatchEvent(new Event('change', { bubbles: true }));
                }
                """);
    }

    public static void check(TestObject testObject) {
        getClickableLocator(testObject, -1).check();
    }

    public static void uncheck(TestObject testObject) {
        getClickableLocator(testObject, -1).uncheck();
    }

    public static boolean isChecked(TestObject testObject) {
        return getPresentLocator(testObject, -1).isChecked();
    }

    public static Locator waitForElementPresent(TestObject testObject, int timeout) {
        return getPresentLocator(testObject, timeout);
    }

    public static Locator waitForElementVisible(TestObject testObject, int timeout) {
        return getVisibleLocator(testObject, timeout);
    }

    public static Locator waitForElementClickable(TestObject testObject, int timeout) {
        return getClickableLocator(testObject, timeout);
    }

    public static boolean waitForElementNotPresent(TestObject testObject, int timeout) {
        return waitForCondition(() -> {
            for (String selector : selectorsFor(testObject.getValues())) {
                if (scopedLocator(resolveScope(testObject), selector).count() > 0) {
                    return false;
                }
            }
            return true;
        }, timeout);
    }

    public static boolean waitForElementNotVisible(TestObject testObject, int timeout) {
        return waitForCondition(() -> {
            for (String selector : selectorsFor(testObject.getValues())) {
                if (scopedLocator(resolveScope(testObject), selector).first().isVisible()) {
                    return false;
                }
            }
            return true;
        }, timeout);
    }

    public static boolean waitForPageLoaded(int timeout) {
        try {
            getPage().waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(timeoutInMillis(timeout)));
            return true;
        } catch (PlaywrightException e) {
            LOGGER.error("Page was not fully loaded within timeout.", e);
            return false;
        }
    }

    public static boolean waitForUrlContains(String value, int timeout) {
        return waitForCondition(() -> getCurrentUrl().contains(value), timeout);
    }

    public static boolean waitForTitleContains(String value, int timeout) {
        return waitForCondition(() -> getTitle().contains(value), timeout);
    }

    public static void verifyElementPresent(TestObject testObject) {
        waitForElementPresent(testObject, -1);
    }

    public static void verifyElementNotPresent(TestObject testObject) {
        if (!waitForElementNotPresent(testObject, -1)) {
            throw new RuntimeException(testObject + " is still present in DOM.");
        }
    }

    public static void verifyElementVisible(TestObject testObject) {
        waitForElementVisible(testObject, -1);
    }

    public static void verifyElementNotVisible(TestObject testObject) {
        if (!waitForElementNotVisible(testObject, -1)) {
            throw new RuntimeException(testObject + " is still visible.");
        }
    }

    public static void verifyElementEnabled(TestObject testObject) {
        if (!isElementEnabled(testObject, -1)) {
            throw new RuntimeException(testObject + " is disabled.");
        }
    }

    public static void verifyElementDisabled(TestObject testObject) {
        if (isElementEnabled(testObject, -1)) {
            throw new RuntimeException(testObject + " is enabled.");
        }
    }

    public static boolean isElementPresent(TestObject testObject, int timeout) {
        try {
            waitForElementPresent(testObject, timeout);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean isElementVisible(TestObject testObject, int timeout) {
        try {
            return waitForElementVisible(testObject, timeout).isVisible();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean isElementEnabled(TestObject testObject, int timeout) {
        try {
            return waitForElementPresent(testObject, timeout).isEnabled();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean isElementSelected(TestObject testObject, int timeout) {
        try {
            return waitForElementPresent(testObject, timeout).isChecked();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void verifyElementTextEquals(TestObject testObject, String expectText) {
        String actualText = getText(testObject);
        if (!Objects.equals(actualText, expectText)) {
            throw new RuntimeException("Actual value: " + actualText + " does not equal expected value: " + expectText);
        }
    }

    public static void verifyElementTextContains(TestObject testObject, String expectText) {
        String actualText = getText(testObject);
        if (actualText == null || !actualText.contains(expectText)) {
            throw new RuntimeException("Actual value: " + actualText + " does not contain expected value: " + expectText);
        }
    }

    public static void verifyElementAttributeEquals(TestObject testObject, String attributeName, String expectedValue) {
        String actualValue = getAttribute(testObject, attributeName);
        if (!Objects.equals(actualValue, expectedValue)) {
            throw new RuntimeException("Actual attribute value: " + actualValue + " does not equal expected value: " + expectedValue);
        }
    }

    public static void verifyElementAttributeContains(TestObject testObject, String attributeName, String expectedValue) {
        String actualValue = getAttribute(testObject, attributeName);
        if (actualValue == null || !actualValue.contains(expectedValue)) {
            throw new RuntimeException("Actual attribute value: " + actualValue + " does not contain expected value: " + expectedValue);
        }
    }

    public static void refreshPage() {
        getPage().reload();
    }

    public static void navigateBack() {
        getPage().goBack();
    }

    public static void navigateForward() {
        getPage().goForward();
    }

    public static void maximizeWindow() {
        getPage().evaluate("() => { try { window.moveTo(0, 0); window.resizeTo(screen.availWidth, screen.availHeight); } catch (error) { return null; } }");
    }

    public static void fullscreenWindow() {
        getPage().evaluate("""
                () => {
                    const element = document.documentElement;
                    if (element.requestFullscreen) {
                        return element.requestFullscreen();
                    }
                    return null;
                }
                """);
    }

    public static void setImplicitWait(int timeoutInSeconds) {
        getPage().setDefaultTimeout(timeoutInMillis(timeoutInSeconds));
    }

    public static void setPageLoadTimeout(int timeoutInSeconds) {
        getPage().setDefaultNavigationTimeout(timeoutInMillis(timeoutInSeconds));
    }

    public static void setScriptTimeout(int timeoutInSeconds) {
        getContext().setDefaultTimeout(timeoutInMillis(timeoutInSeconds));
    }

    public static String getCurrentUrl() {
        return getPage().url();
    }

    public static String getTitle() {
        return getPage().title();
    }

    public static String getPageSource() {
        return getPage().content();
    }

    public static void verifyUrlEquals(String expectedUrl) {
        String actualUrl = getCurrentUrl();
        if (!Objects.equals(actualUrl, expectedUrl)) {
            throw new RuntimeException("Actual URL: " + actualUrl + " does not equal expected URL: " + expectedUrl);
        }
    }

    public static void verifyUrlContains(String expectedUrlPart) {
        String actualUrl = getCurrentUrl();
        if (actualUrl == null || !actualUrl.contains(expectedUrlPart)) {
            throw new RuntimeException("Actual URL: " + actualUrl + " does not contain expected value: " + expectedUrlPart);
        }
    }

    public static void verifyTitleEquals(String expectedTitle) {
        String actualTitle = getTitle();
        if (!Objects.equals(actualTitle, expectedTitle)) {
            throw new RuntimeException("Actual title: " + actualTitle + " does not equal expected title: " + expectedTitle);
        }
    }

    public static void verifyTitleContains(String expectedTitlePart) {
        String actualTitle = getTitle();
        if (actualTitle == null || !actualTitle.contains(expectedTitlePart)) {
            throw new RuntimeException("Actual title: " + actualTitle + " does not contain expected value: " + expectedTitlePart);
        }
    }

    public static boolean isAlertPresent(int timeout) {
        return awaitPendingDialog(timeout) != null;
    }

    public static void acceptAlert(int timeout) {
        PendingDialog pendingDialog = requirePendingDialog(timeout);
        pendingDialog.action = DialogAction.ACCEPT;
        pendingDialog.release();
    }

    public static void dismissAlert(int timeout) {
        PendingDialog pendingDialog = requirePendingDialog(timeout);
        pendingDialog.action = DialogAction.DISMISS;
        pendingDialog.release();
    }

    public static String getAlertText(int timeout) {
        return requirePendingDialog(timeout).dialog.message();
    }

    public static void setAlertText(String text, int timeout) {
        requirePendingDialog(timeout).promptText = text;
    }

    public static void switchToFrame(TestObject frameObject) {
        Locator frameLocator = getPresentLocator(frameObject, -1);
        ElementHandle handle = frameLocator.elementHandle();
        if (handle == null || handle.contentFrame() == null) {
            throw new RuntimeException("Unable to switch to frame: " + frameObject);
        }
        requireSession().frame = handle.contentFrame();
    }

    public static void switchToFrame(int frameIndex) {
        List<Frame> frames = new ArrayList<>(getPage().frames());
        if (frames.size() <= frameIndex + 1) {
            throw new RuntimeException("Invalid frame index: " + frameIndex);
        }
        requireSession().frame = frames.get(frameIndex + 1);
    }

    public static void switchToFrame(String frameNameOrId) {
        for (Frame frame : getPage().frames()) {
            if (frameNameOrId.equals(frame.name())) {
                requireSession().frame = frame;
                return;
            }
        }
        throw new RuntimeException("No frame found with name or id: " + frameNameOrId);
    }

    public static void switchToParentFrame() {
        Frame frame = getCurrentFrame();
        requireSession().frame = frame == null ? null : frame.parentFrame();
    }

    public static void switchToDefaultContent() {
        requireSession().frame = null;
    }

    public static void openNewTab() {
        requireSession().page = getContext().newPage();
        requireSession().frame = null;
    }

    public static void openNewWindow() {
        openNewTab();
    }

    public static List<Page> getWindowHandles() {
        return new ArrayList<>(getContext().pages());
    }

    public static Page getCurrentWindowHandle() {
        return getPage();
    }

    public static int getWindowCount() {
        return getWindowHandles().size();
    }

    public static void switchToWindowByHandle(Page windowHandle) {
        requireSession().page = windowHandle;
        requireSession().frame = null;
    }

    public static void switchToWindowByIndex(int windowIndex) {
        List<Page> pages = getWindowHandles();
        if (windowIndex < 0 || windowIndex >= pages.size()) {
            throw new RuntimeException("Invalid window index: " + windowIndex + ". Current number of windows: " + pages.size());
        }
        switchToWindowByHandle(pages.get(windowIndex));
    }

    public static void switchToLatestWindow() {
        List<Page> pages = getWindowHandles();
        if (pages.isEmpty()) {
            throw new RuntimeException("No browser windows available.");
        }
        switchToWindowByHandle(pages.get(pages.size() - 1));
    }

    public static void switchToWindowByTitle(String title) {
        for (Page page : getWindowHandles()) {
            if (Objects.equals(page.title(), title)) {
                switchToWindowByHandle(page);
                return;
            }
        }
        throw new RuntimeException("No window found with title: " + title);
    }

    public static void switchToWindowByUrlContains(String urlPart) {
        for (Page page : getWindowHandles()) {
            if (page.url().contains(urlPart)) {
                switchToWindowByHandle(page);
                return;
            }
        }
        throw new RuntimeException("No window found with URL containing: " + urlPart);
    }

    public static boolean waitForNumberOfWindowsToBe(int expectedCount, int timeout) {
        return waitForCondition(() -> getWindowCount() == expectedCount, timeout);
    }

    public static void closeCurrentWindow() {
        Page currentPage = getPage();
        currentPage.close();
        List<Page> remainingPages = getWindowHandles();
        requireSession().page = remainingPages.isEmpty() ? null : remainingPages.get(remainingPages.size() - 1);
        requireSession().frame = null;
    }

    public static void closeAllAdditionalWindows() {
        List<Page> pages = getWindowHandles();
        if (pages.size() <= 1) {
            return;
        }
        Page primaryPage = pages.get(0);
        for (int index = 1; index < pages.size(); index++) {
            pages.get(index).close();
        }
        requireSession().page = primaryPage;
        requireSession().frame = null;
    }

    public static Object executeJavaScript(String script, Object... arguments) {
        if (arguments == null || arguments.length == 0) {
            return getPage().evaluate(script);
        }
        throw new UnsupportedOperationException(
                "Playwright executeJavaScript currently supports only zero-argument scripts. "
                        + "Use the dedicated JavaScript helper methods for element-based operations.");
    }

    public static String getInnerTextByJavaScript(TestObject testObject) {
        Object value = getVisibleLocator(testObject, -1).evaluate("element => element.innerText");
        return value == null ? null : value.toString();
    }

    public static void setAttributeByJavaScript(TestObject testObject, String attributeName, String attributeValue) {
        getPresentLocator(testObject, -1)
                .evaluate("(element, data) => element.setAttribute(data.name, data.value)",
                        new AttributeChange(attributeName, attributeValue));
    }

    public static void removeAttributeByJavaScript(TestObject testObject, String attributeName) {
        getPresentLocator(testObject, -1).evaluate("(element, attribute) => element.removeAttribute(attribute)", attributeName);
    }

    public static void highlightElement(TestObject testObject) {
        getVisibleLocator(testObject, -1).highlight();
    }

    public static void addCookie(String name, String value) {
        String currentUrl = getCurrentUrl();
        if (currentUrl == null || currentUrl.isBlank() || "about:blank".equals(currentUrl)) {
            throw new IllegalStateException("Navigate to an application URL before adding Playwright cookies by name/value.");
        }
        getContext().addCookies(List.of(new Cookie(name, value).setUrl(currentUrl)));
    }

    public static void addCookie(Cookie cookie) {
        getContext().addCookies(List.of(cookie));
    }

    public static Cookie getCookie(String name) {
        return getCookies().stream()
                .filter(cookie -> Objects.equals(cookie.name, name))
                .findFirst()
                .orElse(null);
    }

    public static List<Cookie> getCookies() {
        return new ArrayList<>(getContext().cookies());
    }

    public static void deleteCookie(String name) {
        BrowserContext.ClearCookiesOptions options = new BrowserContext.ClearCookiesOptions();
        options.setName(name);
        getContext().clearCookies(options);
    }

    public static void deleteAllCookies() {
        getContext().clearCookies();
    }

    public static void setLocalStorageItem(String key, String value) {
        getPage().evaluate("(item) => window.localStorage.setItem(item.key, item.value)", new StorageItem(key, value));
    }

    public static String getLocalStorageItem(String key) {
        Object value = getPage().evaluate("(itemKey) => window.localStorage.getItem(itemKey)", key);
        return value == null ? null : value.toString();
    }

    public static void removeLocalStorageItem(String key) {
        getPage().evaluate("(itemKey) => window.localStorage.removeItem(itemKey)", key);
    }

    public static void clearLocalStorage() {
        getPage().evaluate("() => window.localStorage.clear()");
    }

    public static void setSessionStorageItem(String key, String value) {
        getPage().evaluate("(item) => window.sessionStorage.setItem(item.key, item.value)", new StorageItem(key, value));
    }

    public static String getSessionStorageItem(String key) {
        Object value = getPage().evaluate("(itemKey) => window.sessionStorage.getItem(itemKey)", key);
        return value == null ? null : value.toString();
    }

    public static void removeSessionStorageItem(String key) {
        getPage().evaluate("(itemKey) => window.sessionStorage.removeItem(itemKey)", key);
    }

    public static void clearSessionStorage() {
        getPage().evaluate("() => window.sessionStorage.clear()");
    }

    public static String capturePageScreenshot(String fileName) throws Exception {
        Path targetPath = getScreenshotPath(fileName);
        getPage().screenshot(new Page.ScreenshotOptions().setPath(targetPath));
        return targetPath.toString();
    }

    public static String captureFullPageScreenshot(String fileName) throws Exception {
        Path targetPath = getScreenshotPath(fileName);
        getPage().screenshot(new Page.ScreenshotOptions().setPath(targetPath).setFullPage(true));
        return targetPath.toString();
    }

    public static boolean isBiDiSupported() {
        return false;
    }

    public static BiDiSessionStatus getBiDiSessionStatus() {
        throw new UnsupportedOperationException("BiDi APIs are only supported by the Selenium-backed WebUI facade.");
    }

    public static org.ndviet.library.webui.selenium.bidi.WebUiBiDi.BiDiLogCollector startBiDiLogCollector() {
        throw new UnsupportedOperationException("BiDi APIs are only supported by the Selenium-backed WebUI facade.");
    }

    public static org.ndviet.library.webui.selenium.bidi.WebUiBiDi.BiDiLogCollector startBiDiLogCollector(String browsingContextId) {
        throw new UnsupportedOperationException("BiDi APIs are only supported by the Selenium-backed WebUI facade.");
    }

    public static org.ndviet.library.webui.selenium.bidi.WebUiBiDi.BiDiNetworkCollector startBiDiNetworkCollector() {
        throw new UnsupportedOperationException("BiDi APIs are only supported by the Selenium-backed WebUI facade.");
    }

    public static org.ndviet.library.webui.selenium.bidi.WebUiBiDi.BiDiNetworkCollector startBiDiNetworkCollector(String browsingContextId) {
        throw new UnsupportedOperationException("BiDi APIs are only supported by the Selenium-backed WebUI facade.");
    }

    public static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static boolean waitForCondition(BooleanSupplier condition, int timeout) {
        long deadline = System.currentTimeMillis() + (long) timeoutInMillis(timeout);
        while (System.currentTimeMillis() <= deadline) {
            try {
                if (condition.getAsBoolean()) {
                    return true;
                }
            } catch (RuntimeException ignored) {
                // Keep polling until timeout expires.
            }
            pause(200);
        }
        try {
            return condition.getAsBoolean();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static PendingDialog awaitPendingDialog(int timeout) {
        long deadline = System.currentTimeMillis() + (long) timeoutInMillis(timeout);
        while (System.currentTimeMillis() <= deadline) {
            PendingDialog pendingDialog = requireSession().pendingDialog;
            if (pendingDialog != null) {
                return pendingDialog;
            }
            pause(100);
        }
        return requireSession().pendingDialog;
    }

    private static PendingDialog requirePendingDialog(int timeout) {
        PendingDialog pendingDialog = awaitPendingDialog(timeout);
        if (pendingDialog == null) {
            throw new RuntimeException("No alert is present.");
        }
        return pendingDialog;
    }

    private static Locator getPresentLocator(TestObject testObject, int timeout) {
        return resolveLocator(testObject, WaitForSelectorState.ATTACHED, timeoutInMillis(timeout));
    }

    private static Locator getVisibleLocator(TestObject testObject, int timeout) {
        return resolveLocator(testObject, WaitForSelectorState.VISIBLE, timeoutInMillis(timeout));
    }

    private static Locator getClickableLocator(TestObject testObject, int timeout) {
        Locator locator = getVisibleLocator(testObject, timeout);
        if (!waitForCondition(locator::isEnabled, timeout)) {
            throw new RuntimeException(testObject + " is not enabled.");
        }
        return locator;
    }

    private static Locator resolveLocator(TestObject testObject, WaitForSelectorState state, double timeout) {
        RuntimeException lastException = null;
        Object scope = resolveScope(testObject);
        for (String selector : selectorsFor(testObject.getValues())) {
            Locator locator = scopedLocator(scope, selector).first();
            try {
                if (state != null) {
                    locator.waitFor(new Locator.WaitForOptions().setState(state).setTimeout(timeout));
                }
                return locator;
            } catch (RuntimeException exception) {
                lastException = exception;
                LOGGER.warn("Locator lookup failed, trying fallback locator: {}", selector);
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        throw new RuntimeException("No valid locator candidates found for: " + testObject);
    }

    private static Object resolveScope(TestObject testObject) {
        List<ParentContext> parentContexts = testObject.getParentContexts();
        if (parentContexts.isEmpty()) {
            Frame frame = getCurrentFrame();
            return frame == null ? getPage() : frame;
        }

        Object scope = getPage();
        for (ParentContext parentContext : parentContexts) {
            if (parentContext.getType() == TestObject.ParentType.FRAME) {
                scope = resolveFrameScope(scope, parentContext);
            } else {
                scope = resolveShadowScope(scope, parentContext);
            }
        }
        return scope;
    }

    private static Frame resolveFrameScope(Object scope, ParentContext parentContext) {
        Locator frameLocator = resolveParentLocator(scope, parentContext);
        ElementHandle handle = frameLocator.elementHandle();
        if (handle == null || handle.contentFrame() == null) {
            throw new RuntimeException("Unable to resolve frame parent context: " + parentContext);
        }
        return handle.contentFrame();
    }

    private static Locator resolveShadowScope(Object scope, ParentContext parentContext) {
        return resolveParentLocator(scope, parentContext);
    }

    private static Locator resolveParentLocator(Object scope, ParentContext parentContext) {
        RuntimeException lastException = null;
        for (String selector : selectorsFor(parentContext.getValues())) {
            Locator locator = scopedLocator(scope, selector).first();
            try {
                locator.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.ATTACHED)
                        .setTimeout(timeoutInMillis(-1)));
                return locator;
            } catch (RuntimeException exception) {
                lastException = exception;
                LOGGER.warn("Parent locator lookup failed, trying fallback locator: {}", selector);
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        throw new RuntimeException("No valid parent locator candidates found for: " + parentContext);
    }

    private static Locator scopedLocator(Object scope, String selector) {
        if (scope instanceof Page page) {
            return page.locator(selector);
        }
        if (scope instanceof Frame frame) {
            return frame.locator(selector);
        }
        if (scope instanceof Locator locator) {
            return locator.locator(selector);
        }
        throw new IllegalArgumentException("Unsupported Playwright search scope: " + scope);
    }

    private static List<String> selectorsFor(Collection<String> locatorValues) {
        List<String> selectors = new ArrayList<>();
        for (String locatorValue : locatorValues) {
            if (locatorValue != null && !locatorValue.isBlank()) {
                selectors.add(toSelector(locatorValue));
            }
        }
        if (selectors.isEmpty()) {
            throw new IllegalArgumentException("Locator must not be null or blank.");
        }
        return selectors;
    }

    static String toSelector(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Locator must not be null or blank.");
        }
        String normalized = value.trim();
        String lowercase = normalized.toLowerCase(Locale.ROOT);
        if (lowercase.startsWith("xpath=")) {
            return "xpath=" + normalized.substring(6);
        }
        if (lowercase.startsWith("cssselector=")) {
            return "css=" + normalized.substring(12);
        }
        if (lowercase.startsWith("id=")) {
            return "[id=\"" + escapeQuoted(normalized.substring(3)) + "\"]";
        }
        if (lowercase.startsWith("name=")) {
            return "[name=\"" + escapeQuoted(normalized.substring(5)) + "\"]";
        }
        if (lowercase.startsWith("classname=")) {
            return "[class~=\"" + escapeQuoted(normalized.substring(10)) + "\"]";
        }
        if (lowercase.startsWith("tagname=")) {
            return normalized.substring(8);
        }
        if (lowercase.startsWith("linktext=")) {
            return "a:has-text(\"" + escapeQuoted(normalized.substring(9)) + "\")";
        }
        if (lowercase.startsWith("partiallinktext=")) {
            return "a:has-text(\"" + escapeQuoted(normalized.substring(16)) + "\")";
        }
        if (lowercase.startsWith("role=")) {
            return "role=" + normalized.substring(5);
        }
        if (normalized.startsWith("/") || normalized.startsWith("(")) {
            return "xpath=" + normalized;
        }
        return normalized;
    }

    private static String escapeQuoted(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String toPlaywrightKey(Keys key) {
        return switch (key) {
            case ENTER, RETURN -> "Enter";
            case TAB -> "Tab";
            case ESCAPE -> "Escape";
            case BACK_SPACE -> "Backspace";
            case DELETE -> "Delete";
            case SPACE -> "Space";
            case ARROW_UP -> "ArrowUp";
            case ARROW_DOWN -> "ArrowDown";
            case ARROW_LEFT -> "ArrowLeft";
            case ARROW_RIGHT -> "ArrowRight";
            case HOME -> "Home";
            case END -> "End";
            case PAGE_UP -> "PageUp";
            case PAGE_DOWN -> "PageDown";
            case INSERT -> "Insert";
            case SHIFT -> "Shift";
            case CONTROL, LEFT_CONTROL -> "Control";
            case ALT, LEFT_ALT -> "Alt";
            case META, COMMAND -> "Meta";
            default -> key.name();
        };
    }

    private static List<String> castStringList(Object values) {
        if (!(values instanceof List<?> listValues)) {
            return List.of();
        }
        List<String> stringValues = new ArrayList<>();
        for (Object value : listValues) {
            stringValues.add(value == null ? null : value.toString());
        }
        return stringValues;
    }

    private static String getScreenshotDirectory() {
        return WebUiConfiguration.getPlaywrightScreenshotDirectory();
    }

    private static String getScreenshotFileType() {
        return WebUiConfiguration.getPlaywrightScreenshotFileType();
    }

    private static Path getScreenshotPath(String fileName) throws Exception {
        FileHelpers.isDirectory(getScreenshotDirectory(), true);
        String fileType = getScreenshotFileType();
        if (fileName == null) {
            return File.createTempFile("screenshot_", String.format("_SS.%s", fileType), new File(getScreenshotDirectory())).toPath();
        }
        Path path = Paths.get(getScreenshotDirectory(),
                String.format("%s_SS_%s.%s", fileName, screenshotCount, fileType));
        screenshotCount++;
        return path;
    }

    private enum DialogAction {
        ACCEPT,
        DISMISS
    }

    private static final class PendingDialog {
        private final Dialog dialog;
        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile DialogAction action = DialogAction.DISMISS;
        private volatile String promptText;

        private PendingDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        private boolean await(int timeoutInSeconds) throws InterruptedException {
            return latch.await(timeoutInSeconds, TimeUnit.SECONDS);
        }

        private void release() {
            latch.countDown();
        }
    }

    private static final class PlaywrightSession {
        private final Playwright playwright;
        private final Browser browser;
        private final Map<String, PlaywrightBrowserContext> contexts = new LinkedHashMap<>();
        private String currentContextId;
        private volatile Page page;
        private volatile Frame frame;
        private volatile PendingDialog pendingDialog;

        private PlaywrightSession(Playwright playwright, Browser browser) {
            this.playwright = playwright;
            this.browser = browser;
        }

        private BrowserContext getContext() {
            PlaywrightBrowserContext browserContext = getCurrentBrowserContext();
            if (browserContext == null) {
                throw new IllegalStateException("Playwright browser context is not available for the current thread.");
            }
            return browserContext.context;
        }

        private String createBrowserContext() {
            String browserContextId = UUID.randomUUID().toString();
            BrowserContext browserContext = browser.newContext(newContextOptions());
            Page contextPage = browserContext.newPage();
            configureSessionPage(contextPage);
            attachDialogListener(contextPage, this);
            PlaywrightBrowserContext holder = new PlaywrightBrowserContext(browserContextId, browserContext, contextPage);
            browserContext.onPage(newPage -> {
                configureSessionPage(newPage);
                attachDialogListener(newPage, this);
                holder.page = newPage;
                if (Objects.equals(currentContextId, browserContextId)) {
                    page = newPage;
                    frame = null;
                }
            });
            contexts.put(browserContextId, holder);
            currentContextId = browserContextId;
            page = contextPage;
            frame = null;
            return browserContextId;
        }

        private String getCurrentContextId() {
            return currentContextId;
        }

        private List<String> getContextIds() {
            return new ArrayList<>(contexts.keySet());
        }

        private void switchToContext(String browserContextId) {
            PlaywrightBrowserContext browserContext = contexts.get(browserContextId);
            if (browserContext == null) {
                throw new IllegalArgumentException("Playwright browser context was not found: " + browserContextId);
            }
            currentContextId = browserContextId;
            page = browserContext.page;
            frame = null;
        }

        private void closeCurrentContext() {
            if (currentContextId == null) {
                return;
            }
            closeContext(currentContextId);
        }

        private void closeContext(String browserContextId) {
            PlaywrightBrowserContext browserContext = contexts.remove(browserContextId);
            if (browserContext == null) {
                return;
            }
            browserContext.context.close();
            if (!Objects.equals(currentContextId, browserContextId)) {
                return;
            }
            if (contexts.isEmpty()) {
                currentContextId = null;
                page = null;
                frame = null;
                return;
            }
            String nextContextId = new ArrayList<>(contexts.keySet()).get(contexts.size() - 1);
            switchToContext(nextContextId);
        }

        private PlaywrightBrowserContext getCurrentBrowserContext() {
            if (currentContextId == null) {
                return null;
            }
            return contexts.get(currentContextId);
        }

        private void close() {
            try {
                List<PlaywrightBrowserContext> browserContexts = new ArrayList<>(contexts.values());
                for (PlaywrightBrowserContext browserContext : browserContexts) {
                    browserContext.context.close();
                }
                contexts.clear();
                currentContextId = null;
                page = null;
                frame = null;
            } finally {
                try {
                    browser.close();
                } finally {
                    playwright.close();
                }
            }
        }
    }

    private static final class PlaywrightBrowserContext {
        private final String id;
        private final BrowserContext context;
        private volatile Page page;

        private PlaywrightBrowserContext(String id, BrowserContext context, Page page) {
            this.id = id;
            this.context = context;
            this.page = page;
        }
    }

    private record ScrollOffset(int x, int y) {
    }

    private record AttributeChange(String name, String value) {
    }

    private record StorageItem(String key, String value) {
    }
}
