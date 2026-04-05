package org.ndviet.library.webui.selenium;

import org.junit.jupiter.api.Test;
import org.ndviet.library.TestObject.TestObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void getBy_rolePrefix_returnsRoleCssBy() {
        By by = WebElementHelpers.getBy("role=button");
        assertEquals("By.cssSelector: [role=\"button\"]", by.toString());
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

    @Test
    public void getBys_testObjectMultipleLocators_returnsOrderedByList() {
        TestObject testObject = new TestLocatorObject("id=missing", "cssSelector=.container > button");

        List<By> bys = WebElementHelpers.getBys(testObject);

        assertEquals(2, bys.size());
        assertEquals("By.id: missing", bys.get(0).toString());
        assertEquals("By.cssSelector: .container > button", bys.get(1).toString());
    }

    @Test
    public void findWebElement_primaryFails_fallbackSucceeds() {
        WebDriver driver = mock(WebDriver.class);
        WebElement expectedElement = mock(WebElement.class);
        TestObject testObject = new TestLocatorObject("id=missing", "cssSelector=.login-button");
        when(driver.findElement(any(By.class))).thenAnswer(invocation -> {
            By by = invocation.getArgument(0);
            if ("By.id: missing".equals(by.toString())) {
                throw new NoSuchElementException("Missing");
            }
            if ("By.cssSelector: .login-button".equals(by.toString())) {
                return expectedElement;
            }
            throw new IllegalStateException("Unexpected locator: " + by);
        });

        WebElement actualElement = WebElementHelpers.findWebElement(driver, testObject);

        assertSame(expectedElement, actualElement);
    }

    @Test
    public void findWebElements_primaryReturnsEmpty_fallbackReturnsElements() {
        WebDriver driver = mock(WebDriver.class);
        List<WebElement> expectedElements = Collections.singletonList(mock(WebElement.class));
        TestObject testObject = new TestLocatorObject("id=missing", "cssSelector=.card");
        when(driver.findElements(any(By.class))).thenAnswer(invocation -> {
            By by = invocation.getArgument(0);
            if ("By.id: missing".equals(by.toString())) {
                return Collections.emptyList();
            }
            if ("By.cssSelector: .card".equals(by.toString())) {
                return expectedElements;
            }
            throw new IllegalStateException("Unexpected locator: " + by);
        });

        List<WebElement> actualElements = WebElementHelpers.findWebElements(driver, testObject);

        assertSame(expectedElements, actualElements);
    }

    @Test
    public void findWebElement_withFrameParent_switchesFrameBeforeLookup() {
        WebDriver driver = mock(WebDriver.class);
        WebDriver.TargetLocator targetLocator = mock(WebDriver.TargetLocator.class);
        WebElement frameElement = mock(WebElement.class);
        WebElement targetElement = mock(WebElement.class);
        TestObject testObject = new TestLocatorObject(
                Arrays.asList("id=target"),
                Collections.singletonList(new TestObject.ParentContext(TestObject.ParentType.FRAME, Arrays.asList("id=frame-root")))
        );
        when(driver.switchTo()).thenReturn(targetLocator);
        when(targetLocator.defaultContent()).thenReturn(driver);
        when(targetLocator.frame(frameElement)).thenReturn(driver);
        when(driver.findElement(any(By.class))).thenAnswer(invocation -> {
            By by = invocation.getArgument(0);
            if ("By.id: frame-root".equals(by.toString())) {
                return frameElement;
            }
            if ("By.id: target".equals(by.toString())) {
                return targetElement;
            }
            throw new IllegalStateException("Unexpected locator: " + by);
        });

        WebElement element = WebElementHelpers.findWebElement(driver, testObject);

        assertSame(targetElement, element);
        verify(targetLocator).defaultContent();
        verify(targetLocator).frame(frameElement);
    }

    @Test
    public void findWebElement_withShadowParent_looksUpInShadowRoot() {
        WebDriver driver = mock(WebDriver.class);
        WebDriver.TargetLocator targetLocator = mock(WebDriver.TargetLocator.class);
        WebElement shadowHost = mock(WebElement.class);
        SearchContext shadowRoot = mock(SearchContext.class);
        WebElement targetElement = mock(WebElement.class);
        TestObject testObject = new TestLocatorObject(
                Arrays.asList("id=shadow-target"),
                Collections.singletonList(new TestObject.ParentContext(TestObject.ParentType.SHADOW, Arrays.asList("cssSelector=app-shell")))
        );
        when(driver.switchTo()).thenReturn(targetLocator);
        when(targetLocator.defaultContent()).thenReturn(driver);
        when(driver.findElement(any(By.class))).thenAnswer(invocation -> {
            By by = invocation.getArgument(0);
            if ("By.cssSelector: app-shell".equals(by.toString())) {
                return shadowHost;
            }
            throw new IllegalStateException("Unexpected locator: " + by);
        });
        when(shadowHost.getShadowRoot()).thenReturn(shadowRoot);
        when(shadowRoot.findElement(any(By.class))).thenAnswer(invocation -> {
            By by = invocation.getArgument(0);
            if ("By.id: shadow-target".equals(by.toString())) {
                return targetElement;
            }
            throw new IllegalStateException("Unexpected shadow locator: " + by);
        });

        WebElement element = WebElementHelpers.findWebElement(driver, testObject);

        assertSame(targetElement, element);
        verify(targetLocator).defaultContent();
    }

    private static class TestLocatorObject extends TestObject {
        private TestLocatorObject(String... locatorCandidates) {
            this(Arrays.asList(locatorCandidates), Collections.emptyList());
        }

        private TestLocatorObject(List<String> locatorCandidates, List<ParentContext> parentContexts) {
            this.relativeObjectId = "mock-object";
            this.setValues(locatorCandidates);
            this.setParentContexts(parentContexts);
        }
    }
}
