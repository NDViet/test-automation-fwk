package org.ndviet.library.webui.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.ndviet.library.webui.selenium.driver.DriverManager;
import org.ndviet.library.webui.selenium.driver.TargetFactory;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertSame;

class SeleniumFacadeWebUITest {

    private WebDriver mockDriver;
    private MockedStatic<TargetFactory> mockFactory;

    @BeforeEach
    void setUp() {
        mockDriver = Mockito.mock(WebDriver.class);
        mockFactory = Mockito.mockStatic(TargetFactory.class);
        mockFactory.when(TargetFactory::createInstance).thenReturn(mockDriver);
    }

    @AfterEach
    void tearDown() {
        if (mockFactory != null) {
            mockFactory.close();
        }
        DriverManager.getInstance().setDriver(null);
    }

    @Test
    void seleniumFacadeOpensDriverFromEnginePackage() {
        WebDriver returnedDriver = WebUI.openBrowser();

        assertSame(mockDriver, returnedDriver);
        assertSame(mockDriver, DriverManager.getInstance().getDriver());
    }
}
