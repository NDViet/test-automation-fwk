package com.ndviet.library;

import com.ndviet.library.configuration.ConfigurationFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.List;

import static com.ndviet.library.configuration.Constants.SELENIUM_CHROME_ARGS;
import static com.ndviet.library.configuration.Constants.SELENIUM_ENABLE_TRACING;
import static com.ndviet.library.configuration.Constants.SELENIUM_FIREFOX_ARGS;
import static com.ndviet.library.configuration.Constants.SELENIUM_HUB_URL;

public class Browser {
    private static final Logger LOGGER = LogManager.getLogger(Browser.class);

    public static boolean getEnableTracing() {
        return Boolean.valueOf(ConfigurationFactory.getInstance().getValue(SELENIUM_ENABLE_TRACING));
    }

    protected enum Type implements BrowserType {
        CHROME {
            @Override
            public WebDriver openBrowser() {
                ChromeOptions options = new ChromeOptions();
                List<String> listArgs = ConfigurationFactory.getInstance().getListValues(SELENIUM_CHROME_ARGS);
                options.addArguments(listArgs.toArray(new String[0]));
                try {
                    String hubUrl = ConfigurationFactory.getInstance().getValue(SELENIUM_HUB_URL);
                    return new RemoteWebDriver(new URL(hubUrl), options, getEnableTracing());
                } catch (Exception e) {
                    LOGGER.error("Could not open the browser.\n" + e.getStackTrace());
                    return null;
                }
            }
        },
        FIREFOX {
            @Override
            public WebDriver openBrowser() {
                FirefoxOptions options = new FirefoxOptions();
                List<String> listArgs = ConfigurationFactory.getInstance().getListValues(SELENIUM_FIREFOX_ARGS);
                options.addArguments(listArgs.toArray(new String[0]));
                try {
                    String hubUrl = ConfigurationFactory.getInstance().getValue(SELENIUM_HUB_URL);
                    return new RemoteWebDriver(new URL(hubUrl), options, getEnableTracing());
                } catch (Exception e) {
                    LOGGER.error("Could not open the browser.\n" + e.getStackTrace());
                    return null;
                }
            }
        }
    }

    private interface BrowserType {
        WebDriver openBrowser();
    }

}
