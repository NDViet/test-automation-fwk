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
- `role=...` (translated to CSS selector `[role="..."]`)

If no prefix is used, locator is treated as XPath.

## Self-Healing Locators (Primary + Fallbacks)

`TestObject` is backward-compatible:

- Existing scalar locator entries still work.
- New multi-locator entries can define `primary` and `fallbacks`.
- The framework tries locators in order until one works.
- Optional `parent`/`parents` can define context path for `iframe` and `shadow` roots.

YAML examples:

```yaml
Login Page:
  # Existing format (still supported)
  Username: id=username

  # New format for self-healing
  Login Button:
    primary: cssSelector=button[type='submit']
    fallbacks:
      - id=login-btn
      - xpath=//button[normalize-space()='Login']

  # Optional typed style (same execution order)
  Login Button Typed:
    primary:
      cssSelector: button[type='submit']
    fallbacks:
      - id: login-btn
      - role: button

  # Nested under iframe then shadow root
  Secure Action:
    locator:
      primary: role=button
      fallbacks:
        - xpath=//button[normalize-space()='Confirm']
    parents:
      - type: frame
        locator:
          primary: id=payment-iframe
          fallbacks:
            - cssSelector=iframe[data-testid='payment']
      - type: shadow
        locator:
          primary: cssSelector=checkout-shell

  # Parent shorthand also supported
  Quick Action:
    locator: id=quick-action
    parent:
      frame: id=quick-frame

  # Reuse shared parent by key (avoid duplicated parent locator blocks)
  Shared:
    Payment Frame:
      locator: id=payment-iframe
    Checkout Host:
      locator:
        primary: cssSelector=checkout-shell
      parent:
        type: frame
        ref: Shared.Payment Frame

  Confirm:
    locator: id=confirm
    parents:
      - type: frame
        ref: Shared.Payment Frame
      - type: shadow
        ref: Shared.Checkout Host

  # Shorthand references
  Confirm Shorthand:
    locator: id=confirm
    parent:
      frameRef: Shared.Payment Frame
```

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
