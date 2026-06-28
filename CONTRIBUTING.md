# Contributing

## Issue Quality

Good issues include the affected launcher area, build type, device, Android version, ROM/base, reproduction steps, expected behavior, actual behavior, logs, and screenshots or recordings when useful. For Recents or Quickstep issues, state whether the behavior was observed in a ROM build or only in a standalone smoke APK.

## Pull Request Process

Open pull requests from topic branches based on `main`. Keep changes scoped and explain the Launcher3, Quickstep, ROM build, and standalone smoke impact. Do not mix infrastructure cleanup with behavior changes unless the coupling is unavoidable.

## Testing Requirements

Run checks that match the change. At minimum, preserve the required source directories and Soong target. For smoke APK work, Gradle checks are useful but do not prove system launcher behavior. For Quickstep, Recents, Taskbar, or privileged integration changes, ROM validation with `m ElyraLauncherQuickStep` is the authoritative path.

## Commit Messages

Use conventional commit subjects such as `docs:`, `ci:`, `build(soong):`, `chore(github):`, or `fix(quickstep):`. Include a clear body for non-trivial changes. Signed commits are preferred for project maintenance work.

## ROM-First Constraints

The repository must remain a real Launcher3 and Quickstep source tree. Do not remove protected source, fake platform behavior, or describe standalone APK output as real Quickstep or Recents validation.
