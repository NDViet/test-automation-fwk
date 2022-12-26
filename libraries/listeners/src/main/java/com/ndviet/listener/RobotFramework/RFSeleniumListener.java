package com.ndviet.listener.RobotFramework;

import com.ndviet.library.TakeScreenshot;

import java.util.Map;

public class RFSeleniumListener implements RFListenerInterface {
    public static final int ROBOT_LISTENER_API_VERSION = 2;

    @Override
    public String onKeywordFailure(String keywordName, Map attributes) {
        if (attributes.get("status").toString().equalsIgnoreCase("FAIL") &&
                attributes.get("libname").toString().equalsIgnoreCase("WebUI")) {
            try {
                String targetFile = TakeScreenshot.captureFullPageScreenshot();
                System.out.println("\nFailed step details: " + attributes);
                System.out.println("Kindly checkout screenshot for debugging: " + targetFile);
            } catch (Exception e) {
                System.out.println("Failed to take screenshot");
            }
        }
        return null;
    }

    @Override
    public String onTestCaseFailure(String testCaseName, Map attributes) {
        return null;
    }

    @Override
    public String onTestSuiteFailure(String testSuiteName, Map attributes) {
        return null;
    }

    public void endKeyword(String name, Map attrs) {
        onKeywordFailure(name, attrs);
    }
}
