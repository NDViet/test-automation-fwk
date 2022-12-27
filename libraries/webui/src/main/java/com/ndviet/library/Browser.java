package com.ndviet.library;

import com.ndviet.libary.configuration.ConfigurationFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.List;

import static com.ndviet.libary.configuration.Constants.SELENIUM_CHROME_ARGS;
import static com.ndviet.libary.configuration.Constants.SELENIUM_FIREFOX_ARGS;
import static com.ndviet.libary.configuration.Constants.SELENIUM_HUB_URL;

public class Browser {
    protected enum Type implements BrowserType {
        CHROME {
            @Override
            public WebDriver openBrowser() {
                ChromeOptions options = new ChromeOptions();
                List<String> listArgs = ConfigurationFactory.getInstance().getListValues(SELENIUM_CHROME_ARGS);
                options.addArguments(listArgs.toArray(new String[0]));
                try {
                    String hubUrl = ConfigurationFactory.getInstance().getValue(SELENIUM_HUB_URL);
                    return new RemoteWebDriver(new URL(hubUrl), options);
                } catch (Exception e) {
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
                    return new RemoteWebDriver(new URL(hubUrl), options);
                } catch (Exception e) {
                    System.out.println("" + e);
                    return null;
                }
            }
        }
    }

    private interface BrowserType {
        WebDriver openBrowser();
    }

}
