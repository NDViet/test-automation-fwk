package com.ndviet;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.library.AnnotationLibrary;

import java.util.Arrays;

@RobotKeywords
public class WebUI extends AnnotationLibrary {
    public WebUI() {
        super(Arrays.asList("com/ndviet/WebUI.class"));
    }

    @RobotKeyword
    public void openBrowser(String url) throws Exception {
        BrowserManagement.getInstance().goToUrl(url);
    }

    @RobotKeyword
    public void closeBrowser() {
        BrowserManagement.getInstance().getDriver().quit();
    }

    @RobotKeyword
    public void click() {

    }

    @RobotKeyword
    public void setText() {

    }

    @RobotKeyword
    public void verifyElementPresent() {

    }

    @RobotKeyword
    public void verifyElementNotPresent() {

    }

    @RobotKeyword
    public void verifyElementHasAttribute() {

    }

    @RobotKeyword
    public void verifyElementNotHasAttribute() {

    }

    @RobotKeyword
    public void verifyElementAttributeValue() {

    }

    @RobotKeyword
    public void verifyElementText() {

    }

    @RobotKeyword
    public void verifyElementVisible() {

    }

    @RobotKeyword
    public void verifyElementNotVisible() {

    }
}
