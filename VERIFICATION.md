# Verification

## ROM Verification

Real Launcher3/Quickstep verification must be done inside an Android ROM tree:

```bash
m ElyraLauncherQuickStep
```

This is the only validation path for real Recents, Quickstep gesture
integration, privileged launcher behavior, and system-level task management.

## Standalone Verification

Standalone APK builds are only for UI smoke testing.

Standalone builds cannot validate:

- real Recents
- real Quickstep
- gesture navigation integration
- privileged launcher behavior
- hidden platform APIs
- system-level task management

## Package Naming

The Java package remains `com.android.launcher3` in this phase.

Package renaming must not be treated as a verification shortcut because
Launcher3 and Quickstep rely on existing package assumptions.

## Current Status

This repository is being rebuilt cleanly from an AOSP Launcher3 Android 16
base.
