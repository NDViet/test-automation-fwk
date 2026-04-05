package org.ndviet.library.webui.selenium.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class DriverManager {
    private static final Logger LOGGER = LogManager.getLogger(DriverManager.class);

    private static final ThreadLocal<DriverManager> INSTANCE = ThreadLocal.withInitial(DriverManager::new);

    private WebDriver driver = null;

    private DriverManager() {
    }

    public static DriverManager getInstance() {
        return INSTANCE.get();
    }

    public WebDriver getDriver() {
        LOGGER.info("Get driver instance: " + this.driver);
        return this.driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void quit() {
        if (this.driver != null) {
            this.driver.quit();
            this.driver = null;
        }
        INSTANCE.remove();
    }
}
