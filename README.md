[![Java CI with Maven](https://github.com/ndviet/test-automation-fwk/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/ndviet/test-automation-fwk/actions/workflows/maven.yml)

## Introduction

Test automation framework with rich test supporting libraries are written in Java.<br>
This implementation is on top of some open-source libraries, utilities. For list of details, kindly check Insights >
Dependency graph.<br>

## Documentation

* [Test Automation Framework Design](https://drive.google.com/file/d/1rBKc4p7IKA5iQXBX6F2gbWUtoq6sY1D9/view?usp=sharing)
* [Test Automation Tech Stack Decision](https://drive.google.com/file/d/125eQoai7GzwMWq6vDXe5K2Hum-WmNyzj/view?usp=sharing)

## List dependency repositories

* [test-parent-pom](../../../test-parent-pom)

## System requires

Java 17+ [Tested in [17.0.2 (build 17.0.2+8)](https://jdk.java.net/archive/)].<br>
Maven 3.8.4+.

## Publish base test runner image

Workflow: `.github/workflows/publish-base-test-runner.yml`

Required repository secrets:

- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`

Optional repository variable:

- `DOCKERHUB_JAVA_COMMON_IMAGE` (default fallback is `ndviet/test-automation-java-common`)
- `DOCKERHUB_JAVA_BASE_IMAGE` (default fallback is `ndviet/test-automation-java-base`)

Manual trigger supports:

- `image_name`
- `push_image` (set `false` for build-only validation)

Workflow tagging behavior:

- `push` on `master`: publishes `ndviet/test-automation-java-common:<revision>-SNAPSHOT`
- `push` on tag `v*`: publishes release tags
  - `ndviet/test-automation-java-common:latest`
  - `ndviet/test-automation-java-common:<revision-without-SNAPSHOT>`

Build base relationship:

- `test-automation-java-common` is built `FROM test-automation-java-base`
- Tag strategy for source base image matches the same revision policy (`master` -> `-SNAPSHOT`, `v*` -> release)

## Publish Maven package to GitHub Packages

Workflow: `.github/workflows/publish-maven-github.yml`

Required permissions:

- `packages: write` (provided by workflow permissions)

Workflow version behavior:

- `push` on `master`: deploys `<revision>-SNAPSHOT`
- `push` on tag `v*`: deploys release `<revision-without-SNAPSHOT>`

## Dependencies declaration

```xml
<!-- https://mvnrepository.com/artifact/org.ndviet/test-libraries-webui -->
<dependency>
    <groupId>org.ndviet</groupId>
    <artifactId>test-libraries-webui</artifactId>
    <version>${version}</version>
</dependency>
```

```xml
<!-- https://mvnrepository.com/artifact/org.ndviet/test-libraries-utilities -->
<dependency>
    <groupId>org.ndviet</groupId>
    <artifactId>test-libraries-utilities</artifactId>
    <version>${version}</version>
</dependency>
```

## Source code usage

1. Clone repository "test-parent-pom" and this repository in the same directory

```shell
git clone git@github.com:ndviet/test-parent-pom.git
```

```shell
git clone git@github.com:ndviet/test-automation-fwk.git
```

2. Build source code in each repository following the order

- test-parent-pom

```shell
cd test-parent-pom
mvn clean install
```

- test-automation-fwk

```shell
cd test-automation-fwk
mvn clean install
```

## Local build and test workflow for any change

Run from `test-automation-project` root.

1. Ensure `test-parent-pom` is available in local Maven cache (`~/.m2`) or resolvable from GitHub Maven registry.
   Optional local install:

```shell
mvn -f test-parent-pom/pom.xml -DskipTests clean install
```

2. Run full framework build + tests:

```shell
mvn -f test-automation-fwk/pom.xml -DskipTests=false clean verify
```

3. Fast loop for WebUI-only changes:

```shell
mvn -f test-automation-fwk/test-libraries/test-libraries-webui/pom.xml -DskipTests=false clean test
```

4. Rebuild runner image used by downstream test repos:

```shell
./test-automation-fwk/containers/build-base-image.sh
```

If you are validating only compilation without tests:

```shell
mvn -f test-automation-fwk/pom.xml -DskipTests clean install
```

Note: this repository only builds framework libraries and base image layers.
Selenium Grid orchestration and end-to-end test execution are handled by downstream project repositories.

## Reference

A test project is using this common test framework.<br>

* [test-automation-project](../../../test-automation-project)
