# Module Specifications

This directory contains module-level specifications for each publishable library in `test-automation-fwk`.

## Dependency Shape

- `test-libraries-utilities`: foundation module used by other feature modules
- `test-libraries-webui`: depends on `test-libraries-utilities`
- `test-libraries-api`: depends on `test-libraries-utilities`
- `test-libraries-listeners`: depends on `test-libraries-webui`
- `test-libraries-office-docs`: standalone
- `test-libraries-kubernetes`: currently packaging-only in the checked-in workspace snapshot

## Specs

- [Utilities](utilities.md)
- [WebUI](webui.md)
- [API](api.md)
- [Office Docs](office-docs.md)
- [Listeners](listeners.md)
- [Kubernetes](kubernetes.md)
