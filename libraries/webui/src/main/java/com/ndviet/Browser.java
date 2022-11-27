package com.ndviet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

public class Browser {
    private interface BrowserType {
        WebDriver openBrowser(Map prefs);
    }

    protected enum Type implements BrowserType {
        CHROME {
            @Override
            public WebDriver openBrowser(Map prefs) {
                ChromeOptions options = new ChromeOptions();
                return new ChromeDriver(options);
            }
        },
        FIREFOX {
            @Override
            public WebDriver openBrowser(Map prefs) {
                FirefoxOptions options = new FirefoxOptions();
                return new FirefoxDriver(options);
            }
        };
    }
}
