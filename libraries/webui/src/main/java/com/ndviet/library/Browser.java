package com.ndviet.library;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Map;

public class Browser {
    protected enum Type implements BrowserType {
        CHROME {
            @Override
            public WebDriver openBrowser(Map prefs) {
                ChromeOptions options = new ChromeOptions();
                //options.addArguments("--proxy-server=http://10.10.10.10:8080", "--incognito", "high-dpi-support=0.8", "force-device-scale-factor=0.8");
                options.addArguments("--incognito", "high-dpi-support=0.8", "force-device-scale-factor=0.8");
                try {
                    WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444"), options);
                    return driver;
                } catch (Exception e) {
                    return null;
                }
            }
        },
        FIREFOX {
            @Override
            public WebDriver openBrowser(Map prefs) {
                FirefoxOptions options = new FirefoxOptions();
                return new FirefoxDriver(options);
            }
        }
    }

    private interface BrowserType {
        WebDriver openBrowser(Map prefs);
    }

}
