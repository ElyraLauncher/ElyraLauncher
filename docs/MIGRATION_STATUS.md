# Migration Status

ElyraLauncher is being restarted as a clean Launcher3/Quickstep-based
project.

## Current State

- Repository initialized
- Elyra project rules restored
- Launcher3/Quickstep source import is in progress
- ROM target name is ElyraLauncherQuickStep

## Imported Launcher3/Quickstep Source

The clean AOSP Launcher3 Android 16 base has been imported.

Important source areas:

- `src/`: Launcher3 core source
- `res/`: Launcher resources
- `quickstep/`: Quickstep, Recents, Overview, Taskbar, and gesture integration
- `protos/`: Launcher protocol buffers
- `tests/`: Launcher tests

Important preserved classes:

- `src/com/android/launcher3/Launcher.java`
- `quickstep/src/com/android/launcher3/uioverrides/QuickstepLauncher.java`
- `quickstep/src/com/android/quickstep/views/RecentsView.java`
- `quickstep/src/com/android/quickstep/views/TaskView.java`
- `quickstep/src/com/android/quickstep/TouchInteractionService.java`

## Target State

- AOSP Launcher3 Android 16 base
- Quickstep preserved
- Recents/Overview preserved
- Taskbar/BubbleBar preserved where supported by base source
- ElyraLauncherQuickStep ROM target
- Optional standalone APK for UI testing only

## Soong Target Status

ElyraOS exposes the ROM build target:

```bash
m ElyraLauncherQuickStep
```

The Java package is intentionally kept as `com.android.launcher3` in this
phase to avoid breaking Launcher3/Quickstep assumptions.

## Important Limitation

Standalone APK builds cannot provide real Android Recents or Quickstep
integration.

Real Quickstep requires privileged ROM/system integration.
