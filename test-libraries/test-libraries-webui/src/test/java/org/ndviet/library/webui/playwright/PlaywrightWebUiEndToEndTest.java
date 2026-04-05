package org.ndviet.library.webui.playwright;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.ndviet.library.TestObject.TestObject;
import org.ndviet.library.configuration.ConfigurationManager;
import org.ndviet.library.configuration.ConfigurationOrdering;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_DEFAULT_TIMEOUT;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_HEADLESS;
import static org.mockito.Mockito.when;

class PlaywrightWebUiEndToEndTest {

    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @AfterEach
    void tearDown() {
        WebUI.closeBrowser();
        ConfigurationManager.release();
    }

    @Test
    void formAuthenticationFlowWorksAgainstTheInternet() {
        ConfigurationManager.setInstance(configuration("chromium", true, 30));

        WebUI.openBrowser(BASE_URL + "/login");
        WebUI.clearAndSetText(new TestLocatorObject("id=username"), "tomsmith");
        WebUI.clearAndSetText(new TestLocatorObject("id=password"), "SuperSecretPassword!");
        WebUI.click(new TestLocatorObject("cssSelector=button[type='submit']"));

        assertTrue(WebUI.waitForUrlContains("/secure", 5));
        WebUI.verifyElementTextContains(new TestLocatorObject("id=flash"), "You logged into a secure area!");
    }

    @Test
    void browserContextsStayIndependentAgainstTheInternet() {
        ConfigurationManager.setInstance(configuration("chromium", true, 30));

        WebUI.openBrowser(BASE_URL + "/checkboxes");
        String firstContextId = WebUI.getCurrentBrowserContextId();

        String secondContextId = WebUI.createBrowserContext();
        WebUI.navigateToUrl(BASE_URL + "/dropdown");
        WebUI.switchToBrowserContext(firstContextId);

        assertTrue(WebUI.getCurrentUrl().contains("/checkboxes"));
        WebUI.switchToBrowserContext(secondContextId);
        assertEquals(List.of(firstContextId, secondContextId), WebUI.getBrowserContextIds());
        assertTrue(WebUI.getCurrentUrl().contains("/dropdown"));
    }

    private static ConfigurationOrdering configuration(String browserType, boolean headless, int timeoutInSeconds) {
        ConfigurationOrdering configuration = Mockito.mock(ConfigurationOrdering.class);
        when(configuration.getValue(PLAYWRIGHT_BROWSER_TYPE)).thenReturn(browserType);
        when(configuration.getValue(PLAYWRIGHT_HEADLESS)).thenReturn(Boolean.toString(headless));
        when(configuration.getValue(PLAYWRIGHT_DEFAULT_TIMEOUT)).thenReturn(Integer.toString(timeoutInSeconds));
        return configuration;
    }

    private static class TestLocatorObject extends TestObject {
        private TestLocatorObject(String... locatorCandidates) {
            this.relativeObjectId = "e2e-object";
            this.setValues(List.of(locatorCandidates));
        }
    }
}
