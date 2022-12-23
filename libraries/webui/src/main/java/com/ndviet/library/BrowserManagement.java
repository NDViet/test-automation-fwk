package com.ndviet.library;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Collections;

public class BrowserManagement {
    public static WebDriver m_driver = null;
    public static BrowserManagement m_instance = null;
    public static WebDriverWait m_wait = null;

    BrowserManagement() {
        m_driver = Browser.Type.CHROME.openBrowser(Collections.EMPTY_MAP);
        m_driver.manage().window().maximize();
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

    public void goToUrl(String url) {
        m_driver.get(url);
    }

}
