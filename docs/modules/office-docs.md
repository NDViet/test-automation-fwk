# Module Spec: `test-libraries-office-docs`

## Purpose

`test-libraries-office-docs` provides lightweight office-document helpers for automation scenarios that need to inspect spreadsheet content.

Artifact:

- `org.ndviet:test-libraries-office-docs`

## Responsibilities

- Read workbook sheet names
- Read worksheet cell values
- Convert a worksheet into a column-oriented map structure

## Public Entry Points

Primary public entry point observed in source:

- `org.ndviet.library.excel.ExcelHelpers`

## Dependency Contract

Direct dependencies:

- Apache POI
- Apache POI OOXML

## Supported Capabilities

Current implementation supports:

- listing sheet names from an Excel workbook
- selecting the first sheet implicitly
- selecting a sheet by name
- extracting cell values as strings
- building a `LinkedHashMap<header, List<values>>` representation

## Runtime Model

- The module is implemented as static utility methods.
- The current implementation opens workbooks directly from file paths.
- The API is read-oriented; there is no workbook write/edit surface in the checked-in source.

## Constraints

Observed implementation details imply the following:

- the helper uses `XSSFWorkbook`, so the current implementation is oriented to OOXML Excel files
- return types use raw `List` and `LinkedHashMap` in some methods
- the current implementation is intentionally minimal and does not expose richer spreadsheet modeling

## Non-Goals

- Full office-suite automation
- Spreadsheet authoring workflows
- Rich formatting, styling, or formula evaluation APIs
- Word or PowerPoint handling in the checked-in source

## Current Risks and Gaps

- Public APIs are narrow and generic.
- Type signatures could be stronger.
- Workbook resource handling is minimal in the current implementation.
