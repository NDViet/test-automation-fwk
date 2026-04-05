package org.ndviet.library.webui.selenium.bidi;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.bidi.BiDiSessionStatus;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.CreateContextParameters;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

public class WebUiBiDiTest {

    @Test
    public void isBiDiSupported_withoutHasBiDi_returnsFalse() {
        WebDriver driver = Mockito.mock(WebDriver.class);
        assertFalse(WebUiBiDi.isBiDiSupported(driver));
    }

    @Test
    public void isBiDiSupported_withBiDiUnavailable_returnsFalse() {
        WebDriver driver = Mockito.mock(WebDriver.class, Mockito.withSettings().extraInterfaces(HasBiDi.class));
        HasBiDi hasBiDi = (HasBiDi) driver;
        Mockito.when(hasBiDi.getBiDi()).thenThrow(new BiDiException("BiDi unavailable"));

        assertFalse(WebUiBiDi.isBiDiSupported(driver));
    }

    @Test
    public void isBiDiSupported_withBiDiAvailable_returnsTrue() {
        WebDriver driver = Mockito.mock(WebDriver.class, Mockito.withSettings().extraInterfaces(HasBiDi.class));
        HasBiDi hasBiDi = (HasBiDi) driver;
        Mockito.when(hasBiDi.getBiDi()).thenReturn(Mockito.mock(BiDi.class));

        assertTrue(WebUiBiDi.isBiDiSupported(driver));
    }

    @Test
    public void getBiDiSessionStatus_returnsSessionStatusFromBiDi() {
        WebDriver driver = Mockito.mock(WebDriver.class, Mockito.withSettings().extraInterfaces(HasBiDi.class));
        HasBiDi hasBiDi = (HasBiDi) driver;
        BiDi bidi = Mockito.mock(BiDi.class);
        BiDiSessionStatus expected = new BiDiSessionStatus(true, "ready");
        Mockito.when(hasBiDi.getBiDi()).thenReturn(bidi);
        Mockito.when(bidi.getBidiSessionStatus()).thenReturn(expected);

        BiDiSessionStatus actual = WebUiBiDi.getBiDiSessionStatus(driver);
        assertTrue(actual.isReady());
        assertEquals("ready", actual.getMessage());
    }

    @Test
    public void getBiDiSessionStatus_withoutHasBiDi_throwsException() {
        WebDriver driver = Mockito.mock(WebDriver.class);
        assertThrows(IllegalStateException.class, () -> WebUiBiDi.getBiDiSessionStatus(driver));
    }

    @Test
    public void startLogCollector_defaultsToCurrentBrowsingContext() {
        WebDriver driver = Mockito.mock(WebDriver.class);
        Mockito.when(driver.getWindowHandle()).thenReturn("context-1");

        try (MockedConstruction<LogInspector> ignored = Mockito.mockConstruction(
                LogInspector.class,
                (mock, context) -> assertEquals("context-1", context.arguments().get(0)))) {
            try (WebUiBiDi.BiDiLogCollector collector = WebUiBiDi.startLogCollector(driver)) {
                assertTrue(collector.getConsoleMessages().isEmpty());
            }
        }
    }

    @Test
    public void startNetworkCollector_defaultsToCurrentBrowsingContext() {
        WebDriver driver = Mockito.mock(WebDriver.class);
        Mockito.when(driver.getWindowHandle()).thenReturn("context-2");

        try (MockedConstruction<Network> ignored = Mockito.mockConstruction(
                Network.class,
                (mock, context) -> assertEquals("context-2", context.arguments().get(0)))) {
            try (WebUiBiDi.BiDiNetworkCollector collector = WebUiBiDi.startNetworkCollector(driver)) {
                assertTrue(collector.getRequests().isEmpty());
            }
        }
    }

    @Test
    public void createBrowsingContext_withUserContext_switchesDriverToCreatedContext() {
        WebDriver driver = Mockito.mock(WebDriver.class);
        WebDriver.TargetLocator targetLocator = Mockito.mock(WebDriver.TargetLocator.class);
        Mockito.when(driver.switchTo()).thenReturn(targetLocator);
        Mockito.when(driver.getWindowHandle()).thenReturn("current-context");

        try (MockedConstruction<BrowsingContext> ignored = Mockito.mockConstruction(
                BrowsingContext.class,
                (mock, context) -> {
                    CreateContextParameters parameters = (CreateContextParameters) context.arguments().get(1);
                    Map<String, Object> values = parameters.toMap();
                    assertEquals("current-context", values.get("referenceContext"));
                    assertEquals("parallel-user-context", values.get("userContext"));
                    Mockito.when(mock.getId()).thenReturn("new-context");
                })) {
            BrowsingContext browsingContext =
                    WebUiBiDi.createBrowsingContext(WindowType.TAB, "parallel-user-context", driver);

            assertEquals("new-context", browsingContext.getId());
            Mockito.verify(targetLocator).window("new-context");
        }
    }
}
