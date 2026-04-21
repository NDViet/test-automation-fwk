# High-Level Project Specification

## 1. Purpose

`test-automation-fwk` is a reusable Java-based test automation framework distributed as libraries, not as a standalone test application. Its purpose is to provide shared automation building blocks that downstream test repositories can consume for:

- Web UI automation
- API automation
- Office document handling
- Common utility and configuration support
- Listener integrations
- Containerized test-runner image composition

This repository is intended to reduce duplicated automation code across consumer projects and standardize how tests configure drivers, sessions, locators, screenshots, supporting utilities, and artifact publishing.

## 2. Goals

- Provide reusable automation libraries for multiple testing domains.
- Support both Selenium and Playwright for UI automation.
- Offer a common configuration and utility foundation for all modules.
- Publish versioned Java artifacts for downstream projects through Maven-compatible repositories.
- Provide a reusable base test-runner container image for CI and containerized execution.
- Keep framework concerns separate from product-specific test suites.

## 3. Non-Goals

- Hosting executable end-to-end test suites for any specific application.
- Managing Selenium Grid orchestration in this repository.
- Providing a runnable backend or web application.
- Owning environment provisioning for consumer projects.

Downstream projects are expected to own test cases, test data, execution pipelines, environment wiring, and any product-specific page objects or API clients built on top of these libraries.

## 4. System Context

This repository sits in the middle of the automation stack:

1. Upstream dependencies provide Java, browser automation, API, office, SSH, and other support libraries.
2. `test-automation-fwk` packages those capabilities into opinionated reusable modules.
3. Consumer repositories import the published artifacts and implement real test suites, execution workflows, and environment-specific orchestration.

The repo also publishes a base container image that prepackages the framework and supports offline-first execution defaults for Gradle and Maven workflows.

## 5. Architecture Overview

The project is a multi-module Java workspace with Gradle as the primary active build and Maven metadata retained for compatibility/publishing scenarios.

Core architectural characteristics:

- Modular library design: each concern is published as a separate artifact.
- Shared foundation: utility/configuration code is centralized and reused by feature modules.
- Thread-local runtime state: configuration, API session state, and UI engine sessions are managed per-thread to support parallel test execution patterns.
- Facade-based automation APIs: higher-level static helpers wrap underlying Selenium, Playwright, Rest Assured, Apache POI, and support libraries.
- Consumer-owned execution: this repo builds libraries and images, while downstream repos own real test flows.

## 6. Module Responsibilities

### `test-libraries-utilities`

Foundation module shared by the rest of the framework.

Responsibilities:

- Configuration loading and ordering across JSON, YAML, and properties sources
- Test object and locator abstractions
- File, download, template, JSON, XML, YAML, string, list, map, math, and date/time helpers
- SSH and remote machine helpers
- Selenium hub/node support models
- Spring helper utilities

This is the primary dependency base for higher-level automation modules.

### `test-libraries-webui`

UI automation module supporting two engines through separate facades:

- `org.ndviet.library.webui.selenium.WebUI`
- `org.ndviet.library.webui.playwright.WebUI`

Responsibilities:

- Browser lifecycle and navigation
- Element actions and waits
- Screenshot capture
- Multi-locator and parent-context resolution via `TestObject`
- Selenium driver creation for local/remote targets
- Selenium BiDi support
- Playwright browser-context isolation
- Shared configuration resolution through `WebUiConfiguration`

The module is designed so consumer tests can choose Selenium or Playwright by import path rather than by separate repositories.

### `test-libraries-api`

API automation support built on Rest Assured.

Responsibilities:

- Request specification/session lifecycle management
- Authentication strategy abstraction
- Basic, bearer-token, and no-auth implementations
- Target factory support for API endpoint selection

This module depends on `test-libraries-utilities`.

### `test-libraries-office-docs`

Office document support built on Apache POI.

Responsibilities:

- Excel workbook and sheet inspection
- Cell value extraction
- Column-oriented sheet-to-map conversion helpers

This module provides basic document-processing utilities for test data validation and file-content assertions.

### `test-libraries-listeners`

Listener integration module.

Responsibilities:

- Robot Framework listener contracts
- Selenium-oriented Robot Framework listener implementation

Current scope appears narrow and integration-focused rather than being a general eventing platform.

### `test-libraries-kubernetes`

Kubernetes-facing module declared in the build and published as an artifact with the Fabric8 Kubernetes client on its API surface.

Current repository state:

- The module is included in Gradle and Maven metadata.
- The module has build output and packaging metadata.
- No `src/main/java` source tree is present in the current workspace snapshot.

This should be treated as either a placeholder module, a packaging-only module, or an incomplete implementation until source responsibilities are restored or documented.

## 7. Configuration Model

Configuration is centralized in the utilities module and consumed by higher-level modules.

Observed design points:

- `ConfigurationManager` stores per-thread configuration ordering.
- `ConfigurationFactory` creates the effective configuration chain.
- Web UI configuration supports layered resolution:
  - engine-specific key
  - shared `webui.*` key
  - built-in default

Examples of supported concerns:

- Browser type
- Default timeout
- Local vs remote target
- Screenshot directory and file type
- Headless execution
- Tracing enablement

This design allows consumer projects to override shared defaults without modifying library code.

## 8. Runtime and Concurrency Model

The framework uses thread-local state for major runtime contexts:

- configuration ordering
- API request specification/session state
- Playwright session state

Implications:

- Parallel test execution is a first-class use case.
- Consumers must initialize and release context cleanly per test or test thread.
- Static facade usage is convenient but requires discipline around lifecycle cleanup.

## 9. Build and Packaging

Primary build characteristics:

- Java 21 toolchain
- Gradle wrapper as the primary local build entry point
- Multi-module Gradle build with version catalog dependency management
- Maven publication and signing support per module
- Source and Javadoc JAR generation enabled for subprojects
- JUnit 5 test platform configured across modules

Published outputs:

- Individual Maven artifacts for each module
- GitHub Packages publication support
- Base test-runner container image published to GHCR

The repository also retains Maven POM structure, likely for compatibility with existing consumer tooling and parent-POM-based workflows.

## 10. Delivery and Release Flow

Intended release flow:

- Build and test via Gradle in CI
- Publish snapshot artifacts from `master`
- Publish release artifacts on version tags
- Optionally sign non-snapshot publications when signing keys are present
- Build and publish the common test-runner image separately from the Java artifacts

Credentials are expected through environment variables or Gradle properties rather than hardcoded project config.

## 11. External Technology Stack

Primary external technologies in the current build:

- Selenium
- Playwright
- Rest Assured
- Apache POI
- Fabric8 Kubernetes Client
- SnakeYAML
- Jackson
- JSONPath
- Apache Mina SSHD
- Spring Context Support
- Log4j
- JUnit 5
- Mockito

## 12. Intended Consumers

Primary consumers are:

- Internal or external Java-based automation projects
- CI pipelines that need a prebuilt automation framework image
- Teams that want shared abstractions for UI/API/document automation without rebuilding common helpers from scratch

## 13. Constraints and Assumptions

- Java 21 or newer is required.
- Consumers are expected to manage their own test structure and execution logic.
- The framework favors static helper APIs for ease of use.
- Remote execution support exists mainly in the WebUI/Selenium path.
- The project assumes Maven-compatible artifact consumption by downstream repos.

## 14. Known Gaps and Risks

- The Kubernetes module is declared but currently lacks checked-in source code in this workspace snapshot.
- Architecture and design references in the root README point to external Google Drive documents, which makes the repo less self-describing without local docs.
- Dual Gradle/Maven metadata increases compatibility, but also increases maintenance overhead and the chance of build drift if not kept aligned.
- Static, thread-local facade design is convenient but can hide lifecycle leaks if consumer tests do not release resources consistently.

## 15. Success Criteria

This project is successful when:

- Consumer test repositories can depend on the published libraries without copying framework code.
- UI, API, and utility modules provide stable reusable primitives for common automation tasks.
- Build and publication workflows produce consistent, versioned artifacts.
- The base test-runner image reduces setup cost for CI and local containerized execution.
- Framework scope remains focused on reusable automation infrastructure rather than application-specific test logic.
