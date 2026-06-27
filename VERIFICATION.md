# Verification

## ROM Verification

Real Launcher3/Quickstep verification must be done inside an Android ROM tree:

```bash
m ElyraLauncherQuickStep
```

## Standalone Verification

Standalone APK builds are only for UI smoke testing.

Standalone builds cannot validate:

- real Recents
- real Quickstep
- gesture navigation integration
- privileged launcher behavior
- hidden platform APIs
- system-level task management

## Current Status

This repository is being rebuilt cleanly from an AOSP Launcher3 Android 16 base.
