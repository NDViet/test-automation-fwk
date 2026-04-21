# Module Spec: `test-libraries-api`

## Purpose

`test-libraries-api` provides a reusable API testing facade on top of Rest Assured and the framework’s shared configuration model.

Artifact:

- `org.ndviet:test-libraries-api`

## Responsibilities

- Request specification creation
- Per-thread API session lifecycle management
- Authentication strategy selection
- Default request configuration from framework properties
- Convenience helpers for REST and GraphQL calls

## Public Entry Points

Primary public entry points observed in source:

- `org.ndviet.library.RestAssured`
- `org.ndviet.library.api.client.TargetFactory`
- `org.ndviet.library.api.client.ApiSessionManager`
- `org.ndviet.library.api.auth.AuthenticationStrategy`
- `org.ndviet.library.api.auth.AuthenticationStrategyFactory`
- `org.ndviet.library.api.auth.AuthenticationType`

## Dependency Contract

Direct dependencies:

- `test-libraries-utilities`
- Rest Assured

## Configuration Contract

Notable supported keys from shared constants:

- `api.base.url`
- `api.base.path`
- `api.graphql.path`
- `api.default.contentType`
- `api.default.accept`
- `api.headers`
- `api.auth.type`
- `api.auth.bearer.token`
- `api.auth.basic.username`
- `api.auth.basic.password`
- `api.relaxedHttpsValidation`

Behavior:

- `TargetFactory` builds the effective `RequestSpecification`.
- `AuthenticationStrategyFactory` resolves auth mode from configuration or explicit input.
- GraphQL defaults to `/graphql` when `api.graphql.path` is unset.

## Runtime Model

- `ApiSessionManager` stores the current `RequestSpecification` in a `ThreadLocal`.
- `RestAssured.openSession*` methods initialize per-thread state.
- `RestAssured.closeSession()` releases the thread-local session.

This design supports parallel tests, but callers must close sessions cleanly to avoid stale state.

## Supported Usage Pattern

Expected consumer flow:

1. Open a session using configuration or an explicit base URL/auth strategy.
2. Reuse the thread-bound request specification through convenience methods such as `given()`, `get()`, `post()`, and `graphQl()`.
3. Close the session after the test or test fixture completes.

## Non-Goals

- API schema generation
- Client code generation from OpenAPI/GraphQL schemas
- Built-in retry, polling, or circuit-breaking semantics
- Domain-specific API wrappers for consumer systems

## Current Risks and Gaps

- Session lifecycle is implicit and static.
- The module currently focuses on request setup and convenience calls, not richer API-test orchestration.
- Consumers still need to own assertions, test data design, and higher-level client abstractions.
