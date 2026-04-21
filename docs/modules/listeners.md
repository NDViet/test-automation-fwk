# Module Spec: `test-libraries-listeners`

## Purpose

`test-libraries-listeners` provides listener integrations that connect external test runners to framework capabilities, currently centered on Robot Framework plus Selenium screenshot capture.

Artifact:

- `org.ndviet:test-libraries-listeners`

## Responsibilities

- Define a Robot Framework listener contract
- Trigger failure-time screenshot capture for WebUI keyword failures
- Reset screenshot counters at test boundaries

## Public Entry Points

Primary public entry points observed in source:

- `org.ndviet.listener.RobotFramework.RFListenerInterface`
- `org.ndviet.listener.RobotFramework.RFSeleniumListener`

## Dependency Contract

Direct dependency:

- `test-libraries-webui`

This means the listener module is coupled to the Selenium screenshot implementation exposed by the WebUI module.

## Runtime Behavior

Current `RFSeleniumListener` behavior:

- listens for Robot Framework keyword completion
- if the keyword fails and `libname` is `WebUI`, it attempts to capture a page screenshot
- resets screenshot counters at the end of a test

The current implementation is narrow and operational rather than being a general extensibility layer.

## Intended Use

This module is appropriate when a consumer wants Robot Framework listener integration that reuses the framework’s screenshot behavior without re-implementing failure hooks.

## Non-Goals

- General-purpose event bus
- Multi-runner listener abstraction
- Rich reporting or artifact publishing
- Broader framework telemetry collection

## Current Risks and Gaps

- The module is specific to Robot Framework naming and lifecycle expectations.
- The main value today is failure screenshot capture, so scope is limited.
- The listener behavior is tightly coupled to WebUI naming conventions and screenshot mechanics.
