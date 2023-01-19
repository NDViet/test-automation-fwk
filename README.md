[![Java CI with Maven](https://github.com/vietnd96/test-automation-fwk/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/vietnd96/test-automation-fwk/actions/workflows/maven.yml)

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

## Dependencies declaration

```xml
<!-- https://mvnrepository.com/artifact/io.github.ndviet/test-libraries-webui -->
<dependency>
    <groupId>io.github.ndviet</groupId>
    <artifactId>test-libraries-webui</artifactId>
    <version>${version}</version>
</dependency>
```

```xml
<!-- https://mvnrepository.com/artifact/io.github.ndviet/test-libraries-utilities -->
<dependency>
    <groupId>io.github.ndviet</groupId>
    <artifactId>test-libraries-utilities</artifactId>
    <version>${version}</version>
</dependency>
```

## Source code usage

1. Clone repository "test-parent-pom" and this repository in the same directory

```shell
git clone git@github.com:vietnd96/test-parent-pom.git
```

```shell
git clone git@github.com:vietnd96/test-automation-fwk.git
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

## Reference

A test project is using this common test framework.<br>

* [test-automation-project](https://github.com/vietnd96/test-automation-project)
