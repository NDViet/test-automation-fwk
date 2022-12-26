package com.ndviet.library;

import com.ndviet.libary.TestObject.TestObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class WebUI {
    public static WebElement findWebElement(TestObject testObject) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        return WebUIAbstract.findWebElement(driver, testObject);
    }

    public static List<WebElement> findWebElements(TestObject testObject) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        return WebUIAbstract.findWebElements(driver, testObject);
    }

    public static void click(TestObject testObject) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebUIAbstract.click(driver, testObject);
    }

    public static void setText(TestObject testObject, String text) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebUIAbstract.setText(driver, testObject, text);
    }

    public static String getText(TestObject testObject) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        return WebUIAbstract.getText(driver, testObject);
    }

    public static void moveToElement(TestObject testObject) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebUIAbstract.moveToElement(driver, testObject);
    }

    public static void scrollToElement(TestObject testObject) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebUIAbstract.scrollToElement(driver, testObject);
    }

    public static void uploadFile(TestObject testObject, String absolutePath) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebUIAbstract.uploadFile(driver, testObject, absolutePath);
    }

    public static void verifyElementPresent(TestObject testObject) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebUIAbstract.verifyElementPresent(driver, testObject);
    }

    public static void verifyElementVisible(TestObject testObject) throws Exception {
        WebDriver driver = BrowserManagement.getInstance().getDriver();
        WebUIAbstract.verifyElementVisible(driver, testObject);
    }
}
