# Release Process

## Release Checklist

- Confirm the branch is based on `main`.
- Review protected Launcher3 and Quickstep source paths.
- Run CI and relevant local checks.
- Confirm ROM validation status for behavior changes.
- Confirm docs describe the Gradle APK as a real installable launcher APK, not as ROM validation.

## Tag Flow

Use signed, reviewable tags when the project is ready for tagged snapshots. Tags should map to clear release notes and known validation status.

## Release Notes

Release notes should separate ROM/system changes, Gradle APK build artifacts, documentation changes, and known limitations.

## Gradle APK Limitation

A Gradle debug APK can be attached as a convenience artifact for device installation and basic launcher testing. It must not be described as proof of real Quickstep or Recents behavior. ROM/ElyraOS integration is future work.

## ROM Integration Requirement

Launcher behavior that depends on platform privileges, Recents, Overview, gestures, Taskbar, or hidden APIs requires ROM validation with `m ElyraLauncherQuickStep`.
