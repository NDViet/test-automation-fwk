# Test Libraries - WebUI

Keyword-driven WebUI automation library built on Selenium.

## Design

- `WebUI`: public keyword facade for downstream tests.
- `WebUIAbstract`: implementation layer for browser, waits, elements, windows, alerts, JavaScript, storage, cookies, screenshots.
- `TestObject`: locator abstraction from `test-libraries-utilities`.

## Supported Keyword Groups

- Browser lifecycle and navigation
- Waits and synchronization
- Element actions and assertions
- Mouse and keyboard interactions
- Dropdown/select handling
- Checkbox/radio handling
- Frame and window/tab switching
- Alert handling
- JavaScript execution and DOM manipulation
- Local/session storage operations
- Cookie management
- Page and full-page screenshots
- WebDriver BiDi logging and network capture helpers

## Locator Syntax

`TestObject` values support:

- `xpath=...`
- `cssSelector=...`
- `id=...`
- `name=...`
- `className=...`
- `tagName=...`
- `linkText=...`
- `partialLinkText=...`

If no prefix is used, locator is treated as XPath.

## Example Usage

```java
WebUI.openBrowser("chrome", "https://example.com");
WebUI.waitForPageLoaded(20);
WebUI.clearAndSetText(usernameInput, "demo-user");
WebUI.clearAndSetText(passwordInput, "secret");
WebUI.click(loginButton);
WebUI.verifyUrlContains("/dashboard");
WebUI.capturePageScreenshot("dashboard");
WebUI.closeBrowser();
```

## WebDriver BiDi Helpers

```java
WebUI.openBrowser("chrome", "https://example.com");

if (WebUI.isBiDiSupported()) {
    try (WebUiBiDi.BiDiLogCollector logs = WebUI.startBiDiLogCollector();
         WebUiBiDi.BiDiNetworkCollector network = WebUI.startBiDiNetworkCollector()) {
        WebUI.navigateToUrl("https://example.com/dashboard");
        network.waitForResponseStatus(200, 10);
        logs.assertNoJavaScriptErrors();
    }
}
```
