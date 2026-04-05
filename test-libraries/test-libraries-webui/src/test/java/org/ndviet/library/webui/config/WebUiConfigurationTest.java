package org.ndviet.library.webui.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ndviet.library.configuration.ConfigurationManager;
import org.ndviet.library.configuration.ConfigurationOrdering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_DEFAULT_TIMEOUT;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_HEADLESS;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_SCREENSHOT_DIRECTORY;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_SCREENSHOT_FILE_TYPE;
import static org.ndviet.library.configuration.Constants.SELENIUM_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.SELENIUM_DEFAULT_TIMEOUT;
import static org.ndviet.library.configuration.Constants.SELENIUM_ENABLE_TRACING;
import static org.ndviet.library.configuration.Constants.SELENIUM_SCREENSHOT_DIRECTORY;
import static org.ndviet.library.configuration.Constants.SELENIUM_SCREENSHOT_FILE_TYPE;
import static org.ndviet.library.configuration.Constants.SELENIUM_WEB_DRIVER_TARGET;
import static org.ndviet.library.configuration.Constants.WEBUI_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.WEBUI_DEFAULT_TIMEOUT;
import static org.ndviet.library.configuration.Constants.WEBUI_ENABLE_TRACING;
import static org.ndviet.library.configuration.Constants.WEBUI_HEADLESS;
import static org.ndviet.library.configuration.Constants.WEBUI_SCREENSHOT_DIRECTORY;
import static org.ndviet.library.configuration.Constants.WEBUI_SCREENSHOT_FILE_TYPE;
import static org.ndviet.library.configuration.Constants.WEBUI_TARGET;

class WebUiConfigurationTest {

    @AfterEach
    void tearDown() {
        ConfigurationManager.release();
    }

    @Test
    void playwrightUsesGenericWebUiValuesWhenEngineSpecificValuesAreMissing() {
        ConfigurationOrdering configuration = Mockito.mock(ConfigurationOrdering.class);
        Mockito.when(configuration.getValue(WEBUI_BROWSER_TYPE)).thenReturn("webkit");
        Mockito.when(configuration.getValue(WEBUI_DEFAULT_TIMEOUT)).thenReturn("22");
        Mockito.when(configuration.getValue(WEBUI_HEADLESS)).thenReturn("false");
        Mockito.when(configuration.getValue(WEBUI_SCREENSHOT_DIRECTORY)).thenReturn("/tmp/webui-shots");
        Mockito.when(configuration.getValue(WEBUI_SCREENSHOT_FILE_TYPE)).thenReturn("jpg");
        ConfigurationManager.setInstance(configuration);

        assertEquals("webkit", WebUiConfiguration.getPlaywrightBrowserType());
        assertEquals(22, WebUiConfiguration.getPlaywrightDefaultTimeout());
        assertFalse(WebUiConfiguration.isPlaywrightHeadless());
        assertEquals("/tmp/webui-shots", WebUiConfiguration.getPlaywrightScreenshotDirectory());
        assertEquals("jpg", WebUiConfiguration.getPlaywrightScreenshotFileType());
    }

    @Test
    void engineSpecificValuesOverrideGenericWebUiValues() {
        ConfigurationOrdering configuration = Mockito.mock(ConfigurationOrdering.class);
        Mockito.when(configuration.getValue(WEBUI_BROWSER_TYPE)).thenReturn("firefox");
        Mockito.when(configuration.getValue(PLAYWRIGHT_BROWSER_TYPE)).thenReturn("chromium");
        Mockito.when(configuration.getValue(WEBUI_DEFAULT_TIMEOUT)).thenReturn("12");
        Mockito.when(configuration.getValue(PLAYWRIGHT_DEFAULT_TIMEOUT)).thenReturn("30");
        Mockito.when(configuration.getValue(WEBUI_HEADLESS)).thenReturn("true");
        Mockito.when(configuration.getValue(PLAYWRIGHT_HEADLESS)).thenReturn("false");
        Mockito.when(configuration.getValue(WEBUI_SCREENSHOT_DIRECTORY)).thenReturn("/tmp/webui");
        Mockito.when(configuration.getValue(PLAYWRIGHT_SCREENSHOT_DIRECTORY)).thenReturn("/tmp/playwright");
        Mockito.when(configuration.getValue(WEBUI_SCREENSHOT_FILE_TYPE)).thenReturn("png");
        Mockito.when(configuration.getValue(PLAYWRIGHT_SCREENSHOT_FILE_TYPE)).thenReturn("webp");
        ConfigurationManager.setInstance(configuration);

        assertEquals("chromium", WebUiConfiguration.getPlaywrightBrowserType());
        assertEquals(30, WebUiConfiguration.getPlaywrightDefaultTimeout());
        assertFalse(WebUiConfiguration.isPlaywrightHeadless());
        assertEquals("/tmp/playwright", WebUiConfiguration.getPlaywrightScreenshotDirectory());
        assertEquals("webp", WebUiConfiguration.getPlaywrightScreenshotFileType());
    }

    @Test
    void seleniumUsesUnifiedWebUiFallbacksAndEngineSpecificOverrides() {
        ConfigurationOrdering configuration = Mockito.mock(ConfigurationOrdering.class);
        Mockito.when(configuration.getValue(WEBUI_BROWSER_TYPE)).thenReturn("edge");
        Mockito.when(configuration.getValue(WEBUI_DEFAULT_TIMEOUT)).thenReturn("18");
        Mockito.when(configuration.getValue(WEBUI_TARGET)).thenReturn("remote");
        Mockito.when(configuration.getValue(WEBUI_ENABLE_TRACING)).thenReturn("true");
        Mockito.when(configuration.getValue(WEBUI_SCREENSHOT_DIRECTORY)).thenReturn("/tmp/shared-shots");
        Mockito.when(configuration.getValue(WEBUI_SCREENSHOT_FILE_TYPE)).thenReturn("jpeg");
        Mockito.when(configuration.getValue(SELENIUM_BROWSER_TYPE)).thenReturn("chrome");
        Mockito.when(configuration.getValue(SELENIUM_WEB_DRIVER_TARGET)).thenReturn("local");
        Mockito.when(configuration.getValue(SELENIUM_DEFAULT_TIMEOUT)).thenReturn("25");
        Mockito.when(configuration.getValue(SELENIUM_ENABLE_TRACING)).thenReturn("false");
        Mockito.when(configuration.getValue(SELENIUM_SCREENSHOT_DIRECTORY)).thenReturn("/tmp/selenium-shots");
        Mockito.when(configuration.getValue(SELENIUM_SCREENSHOT_FILE_TYPE)).thenReturn("png");
        ConfigurationManager.setInstance(configuration);

        assertEquals("chrome", WebUiConfiguration.getSeleniumBrowserType());
        assertEquals("local", WebUiConfiguration.getSeleniumTarget());
        assertEquals(25, WebUiConfiguration.getSeleniumDefaultTimeout());
        assertFalse(WebUiConfiguration.isSeleniumTracingEnabled());
        assertEquals("/tmp/selenium-shots", WebUiConfiguration.getSeleniumScreenshotDirectory());
        assertEquals("png", WebUiConfiguration.getSeleniumScreenshotFileType());
    }

    @Test
    void seleniumFallsBackToGenericWebUiValuesWhenEngineSpecificValuesAreMissing() {
        ConfigurationOrdering configuration = Mockito.mock(ConfigurationOrdering.class);
        Mockito.when(configuration.getValue(WEBUI_BROWSER_TYPE)).thenReturn("firefox");
        Mockito.when(configuration.getValue(WEBUI_DEFAULT_TIMEOUT)).thenReturn("16");
        Mockito.when(configuration.getValue(WEBUI_TARGET)).thenReturn("remote");
        Mockito.when(configuration.getValue(WEBUI_ENABLE_TRACING)).thenReturn("true");
        ConfigurationManager.setInstance(configuration);

        assertEquals("firefox", WebUiConfiguration.getSeleniumBrowserType());
        assertEquals("remote", WebUiConfiguration.getSeleniumTarget());
        assertEquals(16, WebUiConfiguration.getSeleniumDefaultTimeout());
        assertTrue(WebUiConfiguration.isSeleniumTracingEnabled());
    }
}
