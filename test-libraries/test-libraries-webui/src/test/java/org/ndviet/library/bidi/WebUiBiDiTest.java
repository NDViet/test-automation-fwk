package org.ndviet.library.bidi;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.bidi.BiDiSessionStatus;
import org.openqa.selenium.bidi.HasBiDi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
