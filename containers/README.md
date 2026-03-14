# Container Assets for test-automation-fwk

This folder is scoped to building and publishing the common framework image only.

## What is included

- `base-test-runner/Dockerfile`: image layer for `test-automation-fwk` libraries, built on top of `test-automation-java-base`.
- `base-test-runner/entrypoint.sh`: runtime entrypoint to seed Maven cache and execute container commands.
- `build-base-image.sh`: local image build helper.

## Scope

This repository does not provide Selenium Grid or test-run orchestration scripts.
Containerized test execution is implemented by each downstream project repository.

## Build local image

```bash
./test-automation-fwk/containers/build-base-image.sh
```

Override target image tag:

```bash
./test-automation-fwk/containers/build-base-image.sh ndviet/test-automation-java-common:local
```

Override source base image:

```bash
JAVA_BASE_IMAGE=ndviet/test-automation-java-base:25.3.0 \
  ./test-automation-fwk/containers/build-base-image.sh
```
