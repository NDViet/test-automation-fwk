package com.ndviet.listener.RobotFramework;

import java.util.Map;

public interface RFListenerInterface {
    public String onKeywordFailure(String keywordName, Map attributes);

    public String onTestCaseFailure(String testCaseName, Map attributes);

    public String onTestSuiteFailure(String testSuiteName, Map attributes);
}
