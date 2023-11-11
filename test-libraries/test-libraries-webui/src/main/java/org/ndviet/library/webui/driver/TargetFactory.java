package org.ndviet.library.webui.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.configuration.ConfigurationManager;
import org.openqa.selenium.WebDriver;

import static org.ndviet.library.configuration.Constants.SELENIUM_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.SELENIUM_WEB_DRIVER_TARGET;
import static org.ndviet.library.webui.driver.RemoteDriverFactory.createRemoteInstance;

public class TargetFactory {
    private static final Logger LOGGER = LogManager.getLogger(TargetFactory.class);

    public static WebDriver createInstance() {
        String browser = ConfigurationManager.getInstance().getValue(SELENIUM_BROWSER_TYPE);
        String target = ConfigurationManager.getInstance().getValue(SELENIUM_WEB_DRIVER_TARGET);
        return createInstance(browser, target);
    }

    public static WebDriver createInstance(String browser) {
        String target = ConfigurationManager.getInstance().getValue(SELENIUM_WEB_DRIVER_TARGET);
        return createInstance(browser, target);
    }

    public static WebDriver createInstance(String browser, String target) {
        Target instance = Target.valueOf(target.toUpperCase());
        switch (instance) {
            case LOCAL:
                return BrowserFactory.valueOf(browser.toUpperCase()).createLocalDriver();
            case REMOTE:
                return createRemoteInstance(browser);
            default:
                throw new RuntimeException("Target is still not support!");
        }
    }

    public static boolean isRemoteTarget() {
        String target = ConfigurationManager.getInstance().getValue(SELENIUM_WEB_DRIVER_TARGET);
        return Target.valueOf(target.toUpperCase()) == Target.REMOTE;
    }

    public enum Target {
        LOCAL, REMOTE;
    }
}
