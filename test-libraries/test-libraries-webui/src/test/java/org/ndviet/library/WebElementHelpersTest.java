package org.ndviet.library;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WebElementHelpersTest {

    @Test
    public void getBy_xpathPrefix_returnsXpathBy() {
        By by = WebElementHelpers.getBy("xpath=//div[@id='root']");
        assertEquals("By.xpath: //div[@id='root']", by.toString());
    }

    @Test
    public void getBy_cssSelectorPrefix_returnsCssBy() {
        By by = WebElementHelpers.getBy("cssSelector=.container > button");
        assertEquals("By.cssSelector: .container > button", by.toString());
    }

    @Test
    public void getBy_idPrefix_returnsIdBy() {
        By by = WebElementHelpers.getBy("id=username");
        assertEquals("By.id: username", by.toString());
    }

    @Test
    public void getBy_caseInsensitivePrefix_returnsExpectedBy() {
        By by = WebElementHelpers.getBy("LiNkTeXt=Sign in");
        assertEquals("By.linkText: Sign in", by.toString());
    }

    @Test
    public void getBy_default_returnsXpathBy() {
        By by = WebElementHelpers.getBy("//button[text()='Login']");
        assertEquals("By.xpath: //button[text()='Login']", by.toString());
    }

    @Test
    public void getBy_blankLocator_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> WebElementHelpers.getBy(" "));
    }
}
