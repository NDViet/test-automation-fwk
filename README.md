[![Gradle Build](https://github.com/ndviet/test-automation-fwk/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/ndviet/test-automation-fwk/actions/workflows/gradle.yml)

## Introduction

Test automation framework with reusable Java libraries for UI, API, Kubernetes, Office document, utility, and listener automation support.

## Documentation

* [Test Automation Framework Design](https://drive.google.com/file/d/1rBKc4p7IKA5iQXBX6F2gbWUtoq6sY1D9/view?usp=sharing)
* [Test Automation Tech Stack Decision](https://drive.google.com/file/d/125eQoai7GzwMWq6vDXe5K2Hum-WmNyzj/view?usp=sharing)

## Requirements

* Java 21+
* Gradle wrapper via `./gradlew`

## Modules

* `test-libraries-utilities`
* `test-libraries-webui`
* `test-libraries-api`
* `test-libraries-kubernetes`
* `test-libraries-office-docs`
* `test-libraries-listeners`

## Local usage

Clone and build:

```bash
git clone git@github.com:ndviet/test-automation-fwk.git
cd test-automation-fwk
./gradlew build
```

Fast local loops:

```bash
./gradlew :test-libraries-webui:test
./gradlew :test-libraries-api:test
```

Publish all framework artifacts to the local Maven cache for downstream projects:

```bash
./gradlew publishToMavenLocal
```

Build without tests:

```bash
./gradlew assemble
```

## Publishing

GitHub Packages publish workflow:

* Workflow: `.github/workflows/publish-packages-github.yml`
* `push` on `master`: publishes snapshot packages
* `push` on tag `v*`: publishes release packages and creates a GitHub Release

Publishing credentials:

* `GH_PACKAGES_USERNAME` or default `github.actor`
* `GH_PACKAGES_TOKEN` or default `GITHUB_TOKEN`

Manual publish from a workstation:

```bash
./gradlew publishAllPublicationsToGitHubPackagesRepository
```

Override the version at publish time:

```bash
./gradlew publishAllPublicationsToGitHubPackagesRepository -Pversion=26.3.0
```

## Consuming the libraries

```xml
<dependency>
    <groupId>org.ndviet</groupId>
    <artifactId>test-libraries-webui</artifactId>
    <version>${version}</version>
</dependency>
```

```xml
<dependency>
    <groupId>org.ndviet</groupId>
    <artifactId>test-libraries-utilities</artifactId>
    <version>${version}</version>
</dependency>
```

## Base test runner image

Workflow: `.github/workflows/publish-base-test-runner.yml`

The image is published to GHCR using `GH_PACKAGES_USERNAME` and `GH_PACKAGES_TOKEN`.

Build locally:

```bash
./containers/build-base-image.sh
```

The image build now seeds `/root/.m2/repository` by running `./gradlew publishToMavenLocal` inside the framework build stage.

## Notes

This repository builds framework libraries and base image layers only. Selenium Grid orchestration and downstream end-to-end test execution are handled in consumer repositories.

## Reference

* [test-automation-project](../../../test-automation-project)
