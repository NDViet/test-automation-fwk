package org.ndviet.library.webui.selenium.driver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.ndviet.library.webui.selenium.WebUI;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.ndviet.library.configuration.Constants.SELENIUM_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.SELENIUM_WEB_DRIVER_TARGET;

public class WebUITest {

    private WebDriver mockDriver;
    private WebDriver.Options mockOptions;
    private WebDriver.Timeouts mockTimeouts;
    private MockedStatic<TargetFactory> mockFactory;

    @BeforeAll
    public static void setUpClass() {
        System.setProperty(SELENIUM_WEB_DRIVER_TARGET, "local");
        System.setProperty(SELENIUM_BROWSER_TYPE, "chrome");
    }

    @BeforeEach
    public void setUp() {
        mockDriver = Mockito.mock(WebDriver.class);
        mockOptions = Mockito.mock(WebDriver.Options.class);
        mockTimeouts = Mockito.mock(WebDriver.Timeouts.class);
        Mockito.when(mockDriver.manage()).thenReturn(mockOptions);
        Mockito.when(mockOptions.timeouts()).thenReturn(mockTimeouts);
        Mockito.when(mockTimeouts.implicitlyWait(Duration.ofSeconds(10))).thenReturn(mockTimeouts);

        mockFactory = Mockito.mockStatic(TargetFactory.class);
        mockFactory.when(TargetFactory::createInstance).thenReturn(mockDriver);
    }

    @AfterEach
    public void tearDown() {
        if (mockFactory != null) {
            mockFactory.close();
        }
        if (mockDriver != null) {
            mockDriver.quit();
        }
        DriverManager.getInstance().setDriver(null);
    }

    @Test
    public void openBrowser_returnsDriver() {
        WebDriver returnedDriver = WebUI.openBrowser();
        assertNotNull(returnedDriver);
        assertSame(mockDriver, returnedDriver);
    }

    @Test
    public void openBrowser_withUrl_returnsDriver() {
        String url = "https://demoqa.com/";
        Mockito.when(mockDriver.getTitle()).thenReturn("DEMOQA");

        WebDriver returnedDriver = WebUI.openBrowser(url);
        returnedDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        assertNotNull(returnedDriver);
        assertSame(mockDriver, returnedDriver);
        assertEquals("DEMOQA", returnedDriver.getTitle());
        Mockito.verify(mockDriver).get(url);
    }
}
