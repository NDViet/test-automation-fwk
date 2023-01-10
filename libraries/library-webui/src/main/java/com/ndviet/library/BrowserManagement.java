package com.ndviet.library;

import com.ndviet.library.configuration.ConfigurationFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

import static com.ndviet.library.configuration.Constants.SELENIUM_BROWSER_TYPE;

public class BrowserManagement {
    private static WebDriver m_driver = null;
    private static BrowserManagement m_instance = null;
    private static String m_browser = null;

    BrowserManagement() {
        m_browser = ConfigurationFactory.getInstance().getValue(SELENIUM_BROWSER_TYPE);
    }

    public static BrowserManagement getInstance() {
        if (m_instance == null) {
            m_instance = new BrowserManagement();
        }
        return m_instance;
    }

    public WebDriver getDriver() {
        return m_driver;
    }

    public void openBrowser() {
        if ("FIREFOX".equalsIgnoreCase(m_browser)) {
            m_driver = Browser.Type.FIREFOX.openBrowser();
        } else {
            m_driver = Browser.Type.CHROME.openBrowser();
        }
        m_driver.manage().window().maximize();
    }

    public void openBrowser(String url) {
        openBrowser();
        goToUrl(url);
    }

    public void openBrowser(String browser, String url) {
        m_browser = browser;
        openBrowser(url);
    }

    public void openNewTab() {
        ((JavascriptExecutor) m_driver).executeScript("window.open();");
    }

    public void goToUrl(String url) {
        m_driver.get(url);
    }

    public void closeWindowIndex(int index) {
        List<String> windowHandles = new ArrayList<>(m_driver.getWindowHandles());
        if (windowHandles.size() == 1) {
            closeBrowser();
        } else {
            m_driver.switchTo().window(windowHandles.get(index)).close();
        }
    }

    public void closeBrowser() {
        m_driver.quit();
        m_instance = null;
    }

}
