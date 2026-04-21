# Change Specifications

This directory contains lightweight change specifications used to make spec-driven development operational for consumer-visible module changes.

## When a Change Spec Is Required

Add a change spec for any pull request that changes the consumer-visible behavior of a published module, especially when it changes:

- public APIs under `src/main`
- configuration keys or configuration resolution behavior
- runtime behavior or lifecycle behavior
- artifact packaging that affects consumers
- module responsibilities or documented limitations

In this repository, a PR is treated as requiring a change spec when it changes files such as:

- `test-libraries/test-libraries-*/src/main/**`
- `test-libraries/test-libraries-*/build.gradle`
- `test-libraries/test-libraries-*/pom.xml`

## When a Change Spec Is Usually Not Required

A change spec is usually not required for:

- test-only changes under `src/test`
- internal refactors that do not change consumer-visible behavior
- typo fixes and documentation-only edits
- CI or repository-maintenance changes unrelated to module behavior

If a PR falls into one of these cases, mark the PR template item as `N/A` and explain why.

## File Naming

Create a new markdown file using this pattern:

`docs/changes/YYYY-MM-DD-module-short-name.md`

Examples:

- `docs/changes/2026-04-21-webui-playwright-contexts.md`
- `docs/changes/2026-04-21-api-auth-defaults.md`

## Workflow

1. Copy [docs/changes/_template.md](./_template.md).
2. Fill in the proposed behavior before or alongside implementation.
3. Link the change spec in the pull request.
4. Update the relevant module spec if the change alters the long-term module contract.
5. Add or update tests that prove the acceptance criteria.

## Enforcement

- Pull requests use a checklist that asks for the linked change spec or a justified `N/A`.
- A GitHub Actions guard fails PRs that touch consumer-visible module files without adding a change spec file in this directory.
