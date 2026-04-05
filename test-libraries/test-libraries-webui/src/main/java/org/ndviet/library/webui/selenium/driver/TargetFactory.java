package org.ndviet.library.webui.selenium.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.webui.config.WebUiConfiguration;
import org.openqa.selenium.WebDriver;
import static org.ndviet.library.webui.selenium.driver.RemoteDriverFactory.createRemoteInstance;

public class TargetFactory {
    private static final Logger LOGGER = LogManager.getLogger(TargetFactory.class);

    public static WebDriver createInstance() {
        return createInstance(WebUiConfiguration.getSeleniumBrowserType(), WebUiConfiguration.getSeleniumTarget());
    }

    public static WebDriver createInstance(String browser) {
        return createInstance(browser, WebUiConfiguration.getSeleniumTarget());
    }

    public static WebDriver createInstance(String browser, String target) {
        String normalizedBrowser = browser == null || browser.isBlank() ? "chrome" : browser;
        String normalizedTarget = target == null || target.isBlank() ? "local" : target;
        Target instance = Target.valueOf(normalizedTarget.toUpperCase());
        switch (instance) {
            case LOCAL:
                return BrowserFactory.valueOf(normalizedBrowser.toUpperCase()).createLocalDriver();
            case REMOTE:
                return createRemoteInstance(normalizedBrowser);
            default:
                throw new RuntimeException("Target is still not support!");
        }
    }

    public static boolean isRemoteTarget() {
        String normalizedTarget = WebUiConfiguration.getSeleniumTarget();
        return Target.valueOf(normalizedTarget.toUpperCase()) == Target.REMOTE;
    }

    public enum Target {
        LOCAL, REMOTE;
    }
}
