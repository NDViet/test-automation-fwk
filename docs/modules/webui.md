# Module Spec: `test-libraries-webui`

## Purpose

`test-libraries-webui` provides reusable UI automation facades for Selenium and Playwright while reusing the shared configuration and locator abstractions from `test-libraries-utilities`.

Artifact:

- `org.ndviet:test-libraries-webui`

## Responsibilities

- Browser lifecycle and navigation
- Waits and synchronization
- Element actions and assertions
- Screenshot capture
- Local and remote Selenium driver support
- Selenium BiDi integration
- Playwright browser-context isolation
- Shared WebUI configuration resolution

## Public Entry Points

Primary public entry points observed in source:

- `org.ndviet.library.webui.selenium.WebUI`
- `org.ndviet.library.webui.playwright.WebUI`
- `org.ndviet.library.webui.config.WebUiConfiguration`
- `org.ndviet.library.webui.selenium.WebUIAbstract`
- `org.ndviet.library.webui.selenium.Waiting`
- `org.ndviet.library.webui.selenium.TakeScreenshot`
- `org.ndviet.library.webui.selenium.bidi.WebUiBiDi`
- `org.ndviet.library.webui.selenium.driver.DriverManager`
- `org.ndviet.library.webui.selenium.driver.RemoteDriverFactory`
- `org.ndviet.library.webui.selenium.driver.TargetFactory`

## Dependency Contract

Direct dependencies:

- `test-libraries-utilities`
- Selenium Java
- Playwright
- AShot

## Configuration Contract

This module resolves engine configuration through layered keys:

- Selenium-first keys such as `selenium.browser.type`
- Playwright-first keys such as `playwright.browser.type`
- shared WebUI keys such as `webui.browser.type`
- built-in defaults when no configured value is present

Notable keys:

- `webui.browser.type`
- `webui.default.timeOut`
- `webui.screenshot.directory`
- `webui.screenshot.fileType`
- `webui.target`
- `webui.enableTracing`
- `webui.headless`
- `selenium.*`
- `playwright.*`

## Locator Model

This module consumes `TestObject` from the utilities module and supports:

- prefixed locators such as `xpath=...`, `cssSelector=...`, `id=...`
- default XPath behavior when no prefix is present
- `primary` plus fallback locators
- frame and shadow parent chains

This is a key part of the module contract. Consumer projects should treat YAML-backed locator repositories as the default way to define reusable UI element references.

## Runtime Model

Selenium path:

- Driver creation is managed through `DriverManager`, browser factories, and remote-target helpers.
- Shared Selenium helper methods are implemented in `WebUIAbstract`.

Playwright path:

- Runtime state is held in a thread-local `PlaywrightSession`.
- Multiple Playwright browser contexts can be created and switched explicitly.
- Current implementation supports only the local Playwright target; non-local target values are rejected.

## Consumer Expectations

Consumer test code is expected to:

- open and close browser/session state explicitly
- keep locator definitions externalized
- choose engine by import path
- release resources cleanly in parallel or repeated test runs

## Non-Goals

- Providing a full page-object model for consumer applications
- Managing Selenium Grid orchestration in this repository
- Supporting remote Playwright execution in the current implementation

## Current Risks and Gaps

- Static facade APIs are convenient but can obscure lifecycle leaks.
- Selenium and Playwright share the same module but expose different runtime models.
- Playwright support is intentionally narrower than Selenium in target/execution flexibility.
