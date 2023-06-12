package org.ndviet.library;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.configuration.ConfigurationFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.ndviet.library.configuration.Constants.SELENIUM_CHROME_ARGS;
import static org.ndviet.library.configuration.Constants.SELENIUM_ENABLE_TRACING;
import static org.ndviet.library.configuration.Constants.SELENIUM_FIREFOX_ARGS;
import static org.ndviet.library.configuration.Constants.SELENIUM_HUB_URL;
import static org.ndviet.library.configuration.Constants.SELENIUM_WEB_DRIVER_TARGET;

public class Browser {
    private static final Logger LOGGER = LogManager.getLogger(Browser.class);

    public static boolean getEnableTracing() {
        return Boolean.parseBoolean(ConfigurationFactory.getInstance().getValue(SELENIUM_ENABLE_TRACING));
    }

    public static boolean isRemoteWebDriver() {
        if (isEmpty(ConfigurationFactory.getInstance().getValue(SELENIUM_WEB_DRIVER_TARGET))) {
            return false;
        } else {
            return ConfigurationFactory.getInstance().getValue(SELENIUM_WEB_DRIVER_TARGET).equalsIgnoreCase(DRIVER_TYPE.REMOTE.toString());
        }
    }

    public static RemoteWebDriver getRemoteWebDriver(AbstractDriverOptions options) {
        try {
            String hubUrl = ConfigurationFactory.getInstance().getValue(SELENIUM_HUB_URL);
            return new RemoteWebDriver(new URL(hubUrl), options, getEnableTracing());
        } catch (Exception e) {
            LOGGER.error("Could not open the browser.\n" + e.getMessage());
            return null;
        }
    }

    protected enum Type implements BrowserType {
        CHROME {
            @Override
            public WebDriver openBrowser() {
                ChromeOptions options = new ChromeOptions();
                List<String> listArgs = ConfigurationFactory.getInstance().getListValues(SELENIUM_CHROME_ARGS);
                options.addArguments(listArgs.toArray(new String[0]));
                if (isRemoteWebDriver()) {
                    return getRemoteWebDriver(options);
                } else {
                    return new ChromeDriver(options);
                }
            }

            @Override
            public String getName() {
                return "chrome";
            }
        },
        FIREFOX {
            @Override
            public WebDriver openBrowser() {
                FirefoxOptions options = new FirefoxOptions();
                List<String> listArgs = ConfigurationFactory.getInstance().getListValues(SELENIUM_FIREFOX_ARGS);
                options.addArguments(listArgs.toArray(new String[0]));
                if (isRemoteWebDriver()) {
                    return getRemoteWebDriver(options);
                } else {
                    return new FirefoxDriver(options);
                }
            }

            @Override
            public String getName() {
                return "firefox";
            }
        }
    }

    public enum DRIVER_TYPE {
        LOCAL("local"),
        REMOTE("remote");
        private final String value;

        DRIVER_TYPE(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private interface BrowserType {
        WebDriver openBrowser();

        String getName();
    }
}
