package com.ndviet.automation.keywords.webui;

import com.ndviet.automation.libraries.webui.BrowserManagement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.library.AnnotationLibrary;

import java.net.URL;
import java.util.Arrays;

@RobotKeywords
public class WebUI extends AnnotationLibrary {
    public WebUI() {
        super(Arrays.asList("com/ndviet/automation/keywords/webui/WebUI.class"));
    }

    @RobotKeyword
    public void openBrowser() throws Exception {

    }
}
