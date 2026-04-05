package org.ndviet.library.webui.config;

import org.ndviet.library.configuration.ConfigurationManager;

import java.io.File;

import static org.ndviet.library.configuration.Constants.CURRENT_WORKING_DIR;
import static org.ndviet.library.configuration.Constants.SCREENSHOT_DIR;
import static org.ndviet.library.configuration.Constants.SELENIUM_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.SELENIUM_DEFAULT_TIMEOUT;
import static org.ndviet.library.configuration.Constants.SELENIUM_ENABLE_TRACING;
import static org.ndviet.library.configuration.Constants.SELENIUM_SCREENSHOT_DIRECTORY;
import static org.ndviet.library.configuration.Constants.SELENIUM_SCREENSHOT_FILE_TYPE;
import static org.ndviet.library.configuration.Constants.SELENIUM_WEB_DRIVER_TARGET;
import static org.ndviet.library.configuration.Constants.TARGET_DIR;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_DEFAULT_TIMEOUT;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_HEADLESS;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_SCREENSHOT_DIRECTORY;
import static org.ndviet.library.configuration.Constants.PLAYWRIGHT_SCREENSHOT_FILE_TYPE;
import static org.ndviet.library.configuration.Constants.WEBUI_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.WEBUI_DEFAULT_TIMEOUT;
import static org.ndviet.library.configuration.Constants.WEBUI_ENABLE_TRACING;
import static org.ndviet.library.configuration.Constants.WEBUI_HEADLESS;
import static org.ndviet.library.configuration.Constants.WEBUI_SCREENSHOT_DIRECTORY;
import static org.ndviet.library.configuration.Constants.WEBUI_SCREENSHOT_FILE_TYPE;
import static org.ndviet.library.configuration.Constants.WEBUI_TARGET;

public final class WebUiConfiguration {

    private WebUiConfiguration() {
    }

    public static String getSeleniumBrowserType() {
        return firstNonBlank(SELENIUM_BROWSER_TYPE, WEBUI_BROWSER_TYPE, "chrome");
    }

    public static String getPlaywrightBrowserType() {
        return firstNonBlank(PLAYWRIGHT_BROWSER_TYPE, WEBUI_BROWSER_TYPE, "chromium");
    }

    public static String getSeleniumTarget() {
        return firstNonBlank(SELENIUM_WEB_DRIVER_TARGET, WEBUI_TARGET, "local");
    }

    public static int getSeleniumDefaultTimeout() {
        return Integer.parseInt(firstNonBlank(SELENIUM_DEFAULT_TIMEOUT, WEBUI_DEFAULT_TIMEOUT, "10"));
    }

    public static int getPlaywrightDefaultTimeout() {
        return Integer.parseInt(firstNonBlank(PLAYWRIGHT_DEFAULT_TIMEOUT, WEBUI_DEFAULT_TIMEOUT, "10"));
    }

    public static boolean isPlaywrightHeadless() {
        return Boolean.parseBoolean(firstNonBlank(PLAYWRIGHT_HEADLESS, WEBUI_HEADLESS, "true"));
    }

    public static boolean isSeleniumTracingEnabled() {
        return Boolean.parseBoolean(firstNonBlank(SELENIUM_ENABLE_TRACING, WEBUI_ENABLE_TRACING, "false"));
    }

    public static String getSeleniumScreenshotDirectory() {
        return firstNonBlank(SELENIUM_SCREENSHOT_DIRECTORY, WEBUI_SCREENSHOT_DIRECTORY, defaultScreenshotDirectory());
    }

    public static String getPlaywrightScreenshotDirectory() {
        return firstNonBlank(PLAYWRIGHT_SCREENSHOT_DIRECTORY, WEBUI_SCREENSHOT_DIRECTORY, defaultScreenshotDirectory());
    }

    public static String getSeleniumScreenshotFileType() {
        return firstNonBlank(SELENIUM_SCREENSHOT_FILE_TYPE, WEBUI_SCREENSHOT_FILE_TYPE, "png");
    }

    public static String getPlaywrightScreenshotFileType() {
        return firstNonBlank(PLAYWRIGHT_SCREENSHOT_FILE_TYPE, WEBUI_SCREENSHOT_FILE_TYPE, "png");
    }

    private static String firstNonBlank(String primaryKey, String secondaryKey, String defaultValue) {
        String primaryValue = ConfigurationManager.getInstance().getValue(primaryKey);
        if (primaryValue != null && !primaryValue.isBlank()) {
            return primaryValue;
        }
        String secondaryValue = ConfigurationManager.getInstance().getValue(secondaryKey);
        if (secondaryValue != null && !secondaryValue.isBlank()) {
            return secondaryValue;
        }
        return defaultValue;
    }

    private static String defaultScreenshotDirectory() {
        return System.getProperty(CURRENT_WORKING_DIR) + File.separator + TARGET_DIR + File.separator + SCREENSHOT_DIR;
    }
}
