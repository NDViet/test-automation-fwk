package org.ndviet.library.webui.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.configuration.ConfigurationManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

import static org.ndviet.library.configuration.Constants.SELENIUM_ENABLE_TRACING;
import static org.ndviet.library.configuration.Constants.SELENIUM_HUB_URL;

public class RemoteDriverFactory {
    private static final Logger LOGGER = LogManager.getLogger(RemoteDriverFactory.class);

    public static WebDriver createRemoteInstance(String browser) {
        MutableCapabilities capability = BrowserFactory.valueOf(browser.toUpperCase()).getOptions();
        try {
            String hubUrl = ConfigurationManager.getInstance().getValue(SELENIUM_HUB_URL);
            return new RemoteWebDriver(new URL(hubUrl), capability, getEnableTracing());
        } catch (Exception e) {
            LOGGER.error("Could not open the browser.\n" + e.getMessage());
            return null;
        }
    }

    public static boolean getEnableTracing() {
        return Boolean.parseBoolean(ConfigurationManager.getInstance().getValue(SELENIUM_ENABLE_TRACING));
    }
}
