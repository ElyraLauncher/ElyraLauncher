# Migration Status

ElyraLauncher is being restarted as a clean Launcher3/Quickstep-based project.

## Current State

- Repository initialized
- Project rules documented
- ROM target name defined
- Source layout prepared
- Real Launcher3/Quickstep source not imported yet

## Target State

- AOSP Launcher3 Android 16 base
- Quickstep preserved
- Recents/Overview preserved
- ElyraLauncherQuickStep ROM target
- Optional standalone APK for UI testing only

## Important Limitation

Standalone APK builds cannot provide real Android Recents or Quickstep integration. Real Quickstep requires privileged ROM/system integration.
