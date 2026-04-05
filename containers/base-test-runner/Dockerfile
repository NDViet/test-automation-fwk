ARG JAVA_BASE_IMAGE=ghcr.io/ndviet/test-automation-java-base:latest

FROM ${JAVA_BASE_IMAGE} AS framework-builder

WORKDIR /opt/framework-src
COPY test-automation-fwk /opt/framework-src/test-automation-fwk

# Seed local .m2 from base image cache before building framework artifacts.
RUN mkdir -p /root/.m2/repository \
    && cp -a /opt/m2-seed/repository/. /root/.m2/repository/

ARG SKIP_FRAMEWORK_TESTS=true
RUN if [ "${SKIP_FRAMEWORK_TESTS}" = "true" ]; then \
      /opt/framework-src/test-automation-fwk/gradlew --no-daemon publishToMavenLocal -x test; \
    else \
      /opt/framework-src/test-automation-fwk/gradlew --no-daemon publishToMavenLocal; \
    fi

FROM ${JAVA_BASE_IMAGE}

COPY --from=framework-builder /root/.m2/repository /opt/m2-seed/repository

ENV WORKSPACE_DIR=/workspace
WORKDIR /workspace

COPY test-automation-fwk/containers/base-test-runner/entrypoint.sh /usr/local/bin/test-runner-entrypoint.sh
RUN chmod +x /usr/local/bin/test-runner-entrypoint.sh

ENTRYPOINT ["/usr/local/bin/test-runner-entrypoint.sh"]
CMD ["bash"]
