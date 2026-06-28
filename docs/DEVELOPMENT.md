# Development

## Branch Naming

Use short topic branches based on `main`, for example:

- `docs/rewrite-verification`
- `ci/architecture-guard`
- `fix(quickstep)/gesture-regression`
- `build/soong-target-cleanup`

## Code Organization

Keep Launcher3 core work in `src/`, Quickstep and Recents work in `quickstep/`, shared code in `shared/`, resources in `res/`, and platform integration in Soong files. Do not move protected source to satisfy a standalone build.

## Resource Naming

Prefer clear Elyra-specific names only for new Elyra resources. Do not rename inherited Launcher3 resources unless the change is part of a reviewed migration and all references are updated.

## Safe Launcher3 Extension Rules

Extend existing Launcher3 contracts instead of bypassing them. Keep model, database, icon cache, workspace, hotseat, folder, widget, and All Apps behavior compatible with upstream assumptions. Changes touching Quickstep, Recents, Taskbar, or gestures need ROM validation.
