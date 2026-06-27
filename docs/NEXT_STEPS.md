# ElyraLauncher Next Steps

## Current Stable State

- Clean AOSP Launcher3 Android 16 base imported
- Quickstep source preserved
- RecentsView preserved
- TaskView preserved
- TouchInteractionService preserved
- QuickstepLauncher preserved
- ElyraLauncherQuickStep Soong target added
- Java package intentionally still kept as `com.android.launcher3`

## Do Not Do Yet

- Do not rename Java package
- Do not remove Quickstep
- Do not remove Recents
- Do not remove Overview
- Do not remove Taskbar
- Do not remove BubbleBar
- Do not add ElyraKIT
- Do not add native module
- Do not add benchmark
- Do not add feed provider
- Do not add icon pack source
- Do not redesign UI yet
- Do not force Gradle standalone APK to support real Quickstep

## Next Phase 1 — Formatting Cleanup

Goal:

Normalize metadata formatting without touching Launcher3/Quickstep source.

Files:

- `Android.bp`
- `README.md`
- `AGENTS.md`
- `ROADMAP.md`
- `VERIFICATION.md`
- `docs/*.md`
- `.github/workflows/build.yml`

Commit:

```text
chore(format): normalize project metadata formatting
