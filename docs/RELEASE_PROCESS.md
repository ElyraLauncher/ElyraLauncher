# Release Process

## Release Checklist

- Confirm the branch is based on `main`.
- Review protected Launcher3 and Quickstep source paths.
- Run CI and relevant local checks.
- Confirm ROM validation status for behavior changes.
- Confirm docs do not overstate standalone smoke APK capability.

## Tag Flow

Use signed, reviewable tags when the project is ready for tagged snapshots. Tags should map to clear release notes and known validation status.

## Release Notes

Release notes should separate ROM/system changes, standalone smoke APK artifacts, documentation changes, and known limitations.

## Smoke APK Limitation

A smoke APK can be attached only as a convenience artifact for basic UI inspection. It must not be described as an unsigned production release or as proof of real Quickstep or Recents behavior.

## ROM Integration Requirement

Launcher behavior that depends on platform privileges, Recents, Overview, gestures, Taskbar, or hidden APIs requires ROM validation with `m ElyraLauncherQuickStep`.
