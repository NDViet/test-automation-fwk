package org.ndviet.library.webui.selenium.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.configuration.ConfigurationManager;
import org.ndviet.library.webui.config.WebUiConfiguration;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

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
            throw new RuntimeException(e);
        }
    }

    public static boolean getEnableTracing() {
        return WebUiConfiguration.isSeleniumTracingEnabled();
    }
}
