package com.ndviet;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.library.AnnotationLibrary;

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
