package org.ndviet.library.webui.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndviet.library.configuration.ConfigurationFactory;
import org.openqa.selenium.WebDriver;

import static org.ndviet.library.configuration.Constants.SELENIUM_WEB_DRIVER_TARGET;
import static org.ndviet.library.webui.driver.RemoteDriverFactory.createRemoteInstance;

public class TargetFactory {
    private static final Logger LOGGER = LogManager.getLogger(TargetFactory.class);

    public WebDriver createInstance(String browser) {
        String target = ConfigurationFactory.getInstance().getValue(SELENIUM_WEB_DRIVER_TARGET);
        return createInstance(target, browser);
    }

    public WebDriver createInstance(String target, String browser) {
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

    public enum Target {
        LOCAL, REMOTE;
    }
}
