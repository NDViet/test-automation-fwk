# Module Spec: `test-libraries-utilities`

## Purpose

`test-libraries-utilities` is the framework foundation module. It provides cross-cutting helpers and shared abstractions used by higher-level modules such as WebUI and API.

Artifact:

- `org.ndviet:test-libraries-utilities`

## Responsibilities

- Configuration loading and ordering
- Test object and locator repository support
- File, download, template, JSON, XML, YAML, string, list, map, math, and date/time helpers
- SSH and remote machine helpers
- Selenium hub and node support models
- Spring helper utilities

## Public Entry Points

Primary public entry points observed in source:

- `org.ndviet.library.configuration.ConfigurationManager`
- `org.ndviet.library.configuration.ConfigurationFactory`
- `org.ndviet.library.TestObject.ObjectRepository`
- `org.ndviet.library.TestObject.WebElementIdentifier`
- `org.ndviet.library.TestObject.WebTestObject`
- `org.ndviet.library.template.TemplateHelpers`
- `org.ndviet.library.file.FileHelpers`
- `org.ndviet.library.file.DownloadHelpers`
- `org.ndviet.library.yaml.YamlUtils`
- `org.ndviet.library.json.JsonUtils`
- `org.ndviet.library.xml.XmlUtils`
- `org.ndviet.library.ssh.SshUtils`
- `org.ndviet.library.machine.RemoteMachine`
- `org.ndviet.library.machine.SeleniumHub`
- `org.ndviet.library.machine.SeleniumNode`

## Configuration Contract

This module owns the core configuration surface for the framework. Notable keys defined in `Constants` include:

- `configuration.base`
- `configuration.ordering*`
- `testObjectRepository.directory`
- `webElementIdentifiers.directory`
- `testData.directory`
- `machine.*`
- `selenium.hub.*`
- `selenium.node.*`

Current model:

- `ConfigurationManager` stores the effective configuration chain in a `ThreadLocal`.
- `ConfigurationFactory` bootstraps configuration ordering from properties.
- Higher-level modules resolve their own keys through this shared configuration layer.

## Locator and Test Object Model

The utilities module provides the locator abstraction used by WebUI:

- scalar locators
- typed locators such as `xpath`, `cssSelector`, `id`, `name`, `role`
- `primary` plus `fallbacks`
- parent-context chains for `frame` and `shadow`
- template substitution via variables

Consumer projects are expected to keep reusable locator definitions in YAML files and resolve them through `ObjectRepository` / `WebElementIdentifier`.

## Runtime Model

- Configuration state is thread-local.
- Test object resolution is static/facade-oriented.
- Helper classes are predominantly static utility APIs.

This is convenient for test code, but it assumes consumers cleanly initialize and release per-thread state when running tests in parallel.

## External Dependencies

Key external dependencies declared by this module:

- SnakeYAML
- Apache Mina SSHD
- Commons IO
- JSONPath
- Jackson Databind
- FreeMarker
- Spring Context Support
- Commons Lang

## Non-Goals

- Owning test execution logic
- Providing domain-specific page objects or API clients
- Acting as a full dependency injection container for test code

## Current Risks and Gaps

- The module mixes many unrelated helper categories, so ownership boundaries are broad.
- Much of the API is static, which simplifies usage but makes lifecycle discipline more important in parallel runs.
- Configuration bootstrap behavior is implicit; consumers need local docs or examples to use it correctly.
