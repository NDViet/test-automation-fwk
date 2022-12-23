package com.ndviet.library;

import org.openqa.selenium.WebDriver;

import java.util.LinkedHashMap;

public class RemoteDriverManagement {
    public static final RemoteDriverManagement I = new RemoteDriverManagement();
    protected LinkedHashMap<String, WebDriver> m_drivers = new LinkedHashMap<>();

    public WebDriver getCurrentDriver(String key) {
        return m_drivers.get(key);
    }

    public void removeWebDriver(String key) {
        this.m_drivers.remove(key);
    }

    public void addWebDriver(String key, WebDriver driver) {
        m_drivers.put(key, driver);
    }
}
