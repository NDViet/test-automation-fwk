package com.ndviet;

import org.openqa.selenium.WebDriver;

public class Main {
    public static void main(String[] args) throws Exception {
        BrowserManagement.getInstance().goToUrl("https://google.com");
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        Element.input(driver, "//*[@aria-label=\"Tìm kiếm\"]", "Vietnam");
        Element.click(driver, "//*[@aria-label=\"Tìm trên Google\"]");
        String title = BrowserManagement.getInstance().getDriver().getTitle();
        System.out.println("Page title: " + title);
        System.out.println("Current URL: " + BrowserManagement.getInstance().getDriver().getCurrentUrl());
        BrowserManagement.getInstance().getDriver().quit();
    }
}
