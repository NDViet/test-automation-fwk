package com.ndviet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BrowserManagement {
    public static WebDriver m_driver = null;
    public static BrowserManagement m_instance = null;
    public static WebDriverWait m_wait = null;

    BrowserManagement() {
        m_driver = new ChromeDriver();
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

    public WebElement findElementByXpath(String xpath) {
        return m_driver.findElement(By.xpath(xpath));
    }

    public void click(String xpath) {
        WebElement element = findElementByXpath(xpath);
        element = Waiting.WaitEnum.ELEMENT_TO_BE_CLICKABLE.waitForElement(m_driver, true, element);
        element.click();
    }

    public void input(String xpath, String text) {
        WebElement element = findElementByXpath(xpath);
        element = Waiting.WaitEnum.ELEMENT_TO_BE_CLICKABLE.waitForElement(m_driver, true, element);
        element.sendKeys(text);
    }

    public static void main(String[] args) throws Exception {
        BrowserManagement.getInstance().goToUrl("https://google.com");
        BrowserManagement.getInstance().input("//*[@aria-label=\"Tìm kiếm\"]", "Vietnam");
        BrowserManagement.getInstance().click("//*[@aria-label=\"Tìm trên Google\"]");
        String title = m_driver.getTitle();
        System.out.println("Page title: " + title);
        System.out.println("Current URL: " + m_driver.getCurrentUrl());
        m_driver.quit();
    }
}
