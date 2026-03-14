ARG JAVA_BASE_IMAGE=ndviet/test-automation-java-base:latest

FROM ${JAVA_BASE_IMAGE} AS framework-builder

WORKDIR /opt/framework-src
COPY test-parent-pom /opt/framework-src/test-parent-pom
COPY test-automation-fwk /opt/framework-src/test-automation-fwk

# Seed local .m2 from base image cache before building framework artifacts.
RUN mkdir -p /root/.m2/repository \
    && cp -a /opt/m2-seed/repository/. /root/.m2/repository/

ARG SKIP_FRAMEWORK_TESTS=true
RUN mvn -B -f /opt/framework-src/test-automation-fwk/pom.xml \
    -DskipTests=${SKIP_FRAMEWORK_TESTS} \
    clean install

FROM ${JAVA_BASE_IMAGE}

COPY --from=framework-builder /root/.m2/repository /opt/m2-seed/repository

ENV WORKSPACE_DIR=/workspace
WORKDIR /workspace

COPY test-automation-fwk/containers/base-test-runner/entrypoint.sh /usr/local/bin/test-runner-entrypoint.sh
RUN chmod +x /usr/local/bin/test-runner-entrypoint.sh

ENTRYPOINT ["/usr/local/bin/test-runner-entrypoint.sh"]
CMD ["bash"]
