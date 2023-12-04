package org.ndviet.library.webui.driver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.ndviet.library.WebUI;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ndviet.library.configuration.Constants.SELENIUM_BROWSER_TYPE;
import static org.ndviet.library.configuration.Constants.SELENIUM_WEB_DRIVER_TARGET;

public class WebUITest {

    @Mock
    private WebDriver mockDriver;

    @BeforeAll
    public static void setUpClass() {
        System.setProperty(SELENIUM_WEB_DRIVER_TARGET, "local");
        System.setProperty(SELENIUM_BROWSER_TYPE, "chrome");
    }

    @BeforeEach
    public void setUp() {
        try (MockedStatic<TargetFactory> mockFactory = Mockito.mockStatic(TargetFactory.class)) {
            mockFactory.when(TargetFactory::createInstance).thenReturn(mockDriver);
        }
    }

    @AfterEach
    public void tearDown() {
        mockDriver.close();
    }

    @Test
    public void openBrowser_returnsDriver() {
        mockDriver = WebUI.openBrowser();
        assertNotNull(mockDriver);
        assertTrue(mockDriver instanceof ChromeDriver);
    }

    @Test
    public void openBrowser_withUrl_returnsDriver() {
        String url = "https://demoqa.com/";
        mockDriver = WebUI.openBrowser(url);
        mockDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        assertNotNull(mockDriver);
        assertTrue(mockDriver.getTitle().equals("DEMOQA"));
    }
}