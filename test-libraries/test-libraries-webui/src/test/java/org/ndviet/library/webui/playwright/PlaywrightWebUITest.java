package org.ndviet.library.webui.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.ndviet.library.TestObject.TestObject;
import org.ndviet.library.configuration.ConfigurationManager;
import org.ndviet.library.configuration.ConfigurationOrdering;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_DEFAULT_TIMEOUT;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_HEADLESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaywrightWebUITest {

    private static final String BASE_URL = "https://the-internet.herokuapp.com/";

    private MockedStatic<Playwright> playwrightFactory;

    @AfterEach
    void tearDown() {
        WebUI.closeBrowser();
        ConfigurationManager.release();
        if (playwrightFactory != null) {
            playwrightFactory.close();
        }
    }

    @Test
    void toSelectorTranslatesSupportedLocatorPrefixes() {
        assertEquals("xpath=//button", WebUI.toSelector("xpath=//button"));
        assertEquals("css=.submit", WebUI.toSelector("cssSelector=.submit"));
        assertEquals("[id=\"login\"]", WebUI.toSelector("id=login"));
        assertEquals("[name=\"username\"]", WebUI.toSelector("name=username"));
        assertEquals("[class~=\"flash success\"]", WebUI.toSelector("className=flash success"));
        assertEquals("form", WebUI.toSelector("tagName=form"));
        assertEquals("a:has-text(\"Form Authentication\")", WebUI.toSelector("linkText=Form Authentication"));
        assertEquals("a:has-text(\"Authentication\")", WebUI.toSelector("partialLinkText=Authentication"));
        assertEquals("role=button", WebUI.toSelector("role=button"));
        assertEquals("xpath=//div[@data-testid='card']", WebUI.toSelector("//div[@data-testid='card']"));
    }

    @Test
    void toSelectorRejectsBlankLocators() {
        assertThrows(IllegalArgumentException.class, () -> WebUI.toSelector(" "));
    }

    @Test
    void openBrowserUsesConfigurationDrivenBrowserTypeHeadlessAndTimeouts() {
        MockPlaywrightRuntime runtime = new MockPlaywrightRuntime("configured", 1);
        mockPlaywrightFactory(Map.of("configured", runtime));
        ConfigurationManager.setInstance(configuration("firefox", false, 22));

        Page page = WebUI.openBrowser(BASE_URL);

        assertSame(runtime.page(0), page);
        verify(runtime.playwright).firefox();
        ArgumentCaptor<BrowserType.LaunchOptions> launchOptions = ArgumentCaptor.forClass(BrowserType.LaunchOptions.class);
        verify(runtime.firefoxType).launch(launchOptions.capture());
        assertEquals(Boolean.FALSE, launchOptions.getValue().headless);
        verify(runtime.page(0)).setDefaultTimeout(22000d);
        verify(runtime.page(0)).setDefaultNavigationTimeout(22000d);
        verify(runtime.page(0)).navigate(BASE_URL);
    }

    @ParameterizedTest
    @CsvSource({
            "chrome,chromium",
            "chromium,chromium",
            "firefox,firefox",
            "safari,webkit",
            "webkit,webkit",
            "edge,chromium",
            "msedge,chromium"
    })
    void openBrowserMapsBrowserAliasesToExpectedPlaywrightFactories(String browserAlias, String expectedFactory) {
        MockPlaywrightRuntime runtime = new MockPlaywrightRuntime("browser-alias", 1);
        mockPlaywrightFactory(Map.of("browser-alias", runtime));
        ConfigurationManager.setInstance(configuration("chromium", true, 10));

        WebUI.openBrowser(browserAlias, BASE_URL);

        assertSame(runtime.page(0), WebUI.getCurrentWindowHandle());
        verifyExpectedFactory(runtime, expectedFactory);
        verify(runtime.page(0)).navigate(BASE_URL);
    }

    @Test
    void openBrowserRejectsUnsupportedPlaywrightBrowserType() {
        MockPlaywrightRuntime runtime = new MockPlaywrightRuntime("unsupported-browser", 1);
        mockPlaywrightFactory(Map.of("unsupported-browser", runtime));
        ConfigurationManager.setInstance(configuration("chromium", true, 10));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> WebUI.openBrowser("opera", BASE_URL));

        assertTrue(exception.getMessage().contains("Unsupported Playwright browser type"));
    }

    @Test
    void openBrowserRejectsNonLocalTargets() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> WebUI.openBrowser("chromium", "remote", BASE_URL));

        assertTrue(exception.getMessage().contains("only the local target"));
    }

    @Test
    void elementActionsUseLocatorFallbacksForTypicalEndToEndFlows() {
        MockPlaywrightRuntime runtime = new MockPlaywrightRuntime("actions", 1);
        mockPlaywrightFactory(Map.of("actions", runtime));
        ConfigurationManager.setInstance(configuration("chromium", true, 5));
        WebUI.openBrowser();

        TestObject loginButton = new TestLocatorObject("id=missing-login", "cssSelector=button[type='submit']");
        TestObject usernameField = new TestLocatorObject("id=username");
        TestObject flashMessage = new TestLocatorObject("id=flash");

        Locator missingButton = mockResolvedLocator(runtime.page(0), "[id=\"missing-login\"]");
        doThrow(new RuntimeException("Missing primary locator")).when(missingButton).waitFor(any());
        Locator workingButton = mockResolvedLocator(runtime.page(0), "css=button[type='submit']");
        Locator username = mockResolvedLocator(runtime.page(0), "[id=\"username\"]");
        Locator flash = mockResolvedLocator(runtime.page(0), "[id=\"flash\"]");
        when(flash.innerText()).thenReturn("You logged into a secure area!");

        WebUI.click(loginButton);
        WebUI.clearAndSetText(usernameField, "tomsmith");

        assertEquals("You logged into a secure area!", WebUI.getText(flashMessage));
        verify(workingButton).click();
        verify(username).fill("tomsmith");
    }

    @Test
    void browserContextLifecycleSupportsSwitchingAndClosingContexts() {
        MockPlaywrightRuntime runtime = new MockPlaywrightRuntime("contexts", 2);
        mockPlaywrightFactory(Map.of("contexts", runtime));
        ConfigurationManager.setInstance(configuration("chromium", true, 5));

        WebUI.openBrowser(BASE_URL + "checkboxes");
        String primaryContextId = WebUI.getCurrentBrowserContextId();

        String secondaryContextId = WebUI.createBrowserContext();
        assertNotEquals(primaryContextId, secondaryContextId);
        assertEquals(List.of(primaryContextId, secondaryContextId), WebUI.getBrowserContextIds());

        WebUI.navigateToUrl(BASE_URL + "dropdown");
        verify(runtime.page(1)).navigate(BASE_URL + "dropdown");

        WebUI.switchToBrowserContext(primaryContextId);
        WebUI.navigateToUrl(BASE_URL + "login");
        verify(runtime.page(0)).navigate(BASE_URL + "login");

        WebUI.closeBrowserContext(secondaryContextId);
        verify(runtime.context(1)).close();
        assertEquals(List.of(primaryContextId), WebUI.getBrowserContextIds());
        assertEquals(primaryContextId, WebUI.getCurrentBrowserContextId());

        WebUI.closeBrowserContext();
        verify(runtime.context(0)).close();
        assertTrue(WebUI.getBrowserContextIds().isEmpty());
        assertNull(WebUI.getCurrentBrowserContextId());
    }

    @Test
    void parallelWorkersKeepPlaywrightSessionsAndBrowserContextsIsolated() throws Exception {
        MockPlaywrightRuntime workerOneRuntime = new MockPlaywrightRuntime("worker-one", 2);
        MockPlaywrightRuntime workerTwoRuntime = new MockPlaywrightRuntime("worker-two", 2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<WorkerResult> workerOne = executor.submit(workerScenario("alpha", workerOneRuntime, ready, start));
            Future<WorkerResult> workerTwo = executor.submit(workerScenario("beta", workerTwoRuntime, ready, start));

            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            WorkerResult first = workerOne.get(10, TimeUnit.SECONDS);
            WorkerResult second = workerTwo.get(10, TimeUnit.SECONDS);

            assertEquals(2, first.contextIds().size());
            assertEquals(2, second.contextIds().size());
            Set<String> allContextIds = new LinkedHashSet<>();
            allContextIds.addAll(first.contextIds());
            allContextIds.addAll(second.contextIds());
            assertEquals(4, allContextIds.size());
            assertNotEquals(first.primaryContextId(), second.primaryContextId());
            assertNotEquals(first.secondaryContextId(), second.secondaryContextId());

            verify(workerOneRuntime.page(0)).navigate(BASE_URL + "alpha");
            verify(workerOneRuntime.page(1)).navigate(BASE_URL + "alpha/secondary");
            verify(workerOneRuntime.page(0)).navigate(BASE_URL + "alpha/primary");
            verify(workerTwoRuntime.page(0)).navigate(BASE_URL + "beta");
            verify(workerTwoRuntime.page(1)).navigate(BASE_URL + "beta/secondary");
            verify(workerTwoRuntime.page(0)).navigate(BASE_URL + "beta/primary");
        } finally {
            executor.shutdownNow();
        }
    }

    private Callable<WorkerResult> workerScenario(String workerPath, MockPlaywrightRuntime runtime,
                                                  CountDownLatch ready, CountDownLatch start) {
        return () -> {
            ConfigurationManager.setInstance(configuration("chromium", true, 5));
            ready.countDown();
            assertTrue(start.await(5, TimeUnit.SECONDS));
            try {
                installMockSession(runtime);
                WebUI.navigateToUrl(BASE_URL + workerPath);
                String primaryContextId = WebUI.getCurrentBrowserContextId();
                String secondaryContextId = WebUI.createBrowserContext();
                WebUI.navigateToUrl(BASE_URL + workerPath + "/secondary");
                WebUI.switchToBrowserContext(primaryContextId);
                WebUI.navigateToUrl(BASE_URL + workerPath + "/primary");
                return new WorkerResult(primaryContextId, secondaryContextId, new ArrayList<>(WebUI.getBrowserContextIds()));
            } finally {
                WebUI.closeBrowser();
                ConfigurationManager.release();
            }
        };
    }

    private static void installMockSession(MockPlaywrightRuntime runtime) throws Exception {
        Field sessionField = WebUI.class.getDeclaredField("SESSION");
        sessionField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<Object> sessions = (ThreadLocal<Object>) sessionField.get(null);

        Class<?> sessionClass = Class.forName(WebUI.class.getName() + "$PlaywrightSession");
        Constructor<?> constructor = sessionClass.getDeclaredConstructor(Playwright.class, Browser.class);
        constructor.setAccessible(true);
        Object session = constructor.newInstance(runtime.playwright, runtime.browser);

        Method createBrowserContext = sessionClass.getDeclaredMethod("createBrowserContext");
        createBrowserContext.setAccessible(true);
        createBrowserContext.invoke(session);
        sessions.set(session);
    }

    private void mockPlaywrightFactory(Map<String, MockPlaywrightRuntime> runtimes) {
        playwrightFactory = Mockito.mockStatic(Playwright.class);
        AtomicInteger nextRuntimeIndex = new AtomicInteger();
        List<MockPlaywrightRuntime> orderedRuntimes = new ArrayList<>(runtimes.values());
        playwrightFactory.when(Playwright::create).thenAnswer(invocation -> {
            int currentIndex = Math.min(nextRuntimeIndex.getAndIncrement(), orderedRuntimes.size() - 1);
            return orderedRuntimes.get(currentIndex).playwright;
        });
    }

    private static void verifyExpectedFactory(MockPlaywrightRuntime runtime, String expectedFactory) {
        switch (expectedFactory) {
            case "chromium" -> verify(runtime.playwright).chromium();
            case "firefox" -> verify(runtime.playwright).firefox();
            case "webkit" -> verify(runtime.playwright).webkit();
            default -> throw new IllegalArgumentException("Unexpected expected factory: " + expectedFactory);
        }
    }

    private static Locator mockResolvedLocator(Page page, String selector) {
        Locator locator = Mockito.mock(Locator.class);
        when(page.locator(selector)).thenReturn(locator);
        when(locator.first()).thenReturn(locator);
        doNothing().when(locator).waitFor(any());
        when(locator.isEnabled()).thenReturn(true);
        return locator;
    }

    private static ConfigurationOrdering configuration(String browserType, boolean headless, int timeoutInSeconds) {
        ConfigurationOrdering configuration = Mockito.mock(ConfigurationOrdering.class);
        when(configuration.getValue(PLAYWRIGHT_BROWSER_TYPE)).thenReturn(browserType);
        when(configuration.getValue(PLAYWRIGHT_HEADLESS)).thenReturn(Boolean.toString(headless));
        when(configuration.getValue(PLAYWRIGHT_DEFAULT_TIMEOUT)).thenReturn(Integer.toString(timeoutInSeconds));
        return configuration;
    }

    private record WorkerResult(String primaryContextId, String secondaryContextId, List<String> contextIds) {
    }

    private static final class MockPlaywrightRuntime {
        private final String name;
        private final Playwright playwright;
        private final BrowserType chromiumType;
        private final BrowserType firefoxType;
        private final BrowserType webkitType;
        private final Browser browser;
        private final List<BrowserContext> contexts;
        private final List<Page> pages;
        private final AtomicInteger nextContextIndex = new AtomicInteger();

        private MockPlaywrightRuntime(String name, int contextCount) {
            this.name = name;
            playwright = Mockito.mock(Playwright.class, "playwright-" + name);
            chromiumType = Mockito.mock(BrowserType.class, "chromium-" + name);
            firefoxType = Mockito.mock(BrowserType.class, "firefox-" + name);
            webkitType = Mockito.mock(BrowserType.class, "webkit-" + name);
            browser = Mockito.mock(Browser.class, "browser-" + name);
            contexts = new ArrayList<>();
            pages = new ArrayList<>();

            when(playwright.chromium()).thenReturn(chromiumType);
            when(playwright.firefox()).thenReturn(firefoxType);
            when(playwright.webkit()).thenReturn(webkitType);
            when(chromiumType.launch(any())).thenReturn(browser);
            when(firefoxType.launch(any())).thenReturn(browser);
            when(webkitType.launch(any())).thenReturn(browser);

            for (int index = 0; index < contextCount; index++) {
                BrowserContext context = Mockito.mock(BrowserContext.class, name + "-context-" + index);
                Page page = Mockito.mock(Page.class, name + "-page-" + index);
                contexts.add(context);
                pages.add(page);
                when(context.newPage()).thenReturn(page);
                when(page.url()).thenReturn("about:blank");
            }
            when(browser.newContext(any())).thenAnswer(invocation -> contexts.get(nextContextIndex.getAndIncrement()));
        }

        private BrowserContext context(int index) {
            return contexts.get(index);
        }

        private Page page(int index) {
            return pages.get(index);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    private static class TestLocatorObject extends TestObject {
        private TestLocatorObject(String... locatorCandidates) {
            this.relativeObjectId = "mock-object";
            this.setValues(List.of(locatorCandidates));
        }
    }
}
