package com.ndviet.listener.RobotFramework;

import com.ndviet.library.TakeScreenshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class RFSeleniumListener implements RFListenerInterface {
    public static final int ROBOT_LISTENER_API_VERSION = 2;
    private static final Logger LOGGER = LogManager.getLogger(RFSeleniumListener.class);

    @Override
    public String onKeywordFailure(String keywordName, Map attributes) {
        if (attributes.get("status").toString().equalsIgnoreCase("FAIL") &&
                attributes.get("libname").toString().equalsIgnoreCase("WebUI")) {
            try {
                TakeScreenshot.captureFullPageScreenshot(null);
            } catch (Exception e) {
                LOGGER.error("Failed to take screenshot for debugging.");
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

    public void endTest(String name, Map attrs) {
        TakeScreenshot.resetScreenshotCount();
    }
}
