# Migration Status

ElyraLauncher is being restarted as a clean Launcher3/Quickstep-based project.

## Current State

- Repository initialized
- Elyra project rules restored
- Launcher3/Quickstep source import is in progress
- ROM target name is ElyraLauncherQuickStep

## Target State

- AOSP Launcher3 Android 16 base
- Quickstep preserved
- Recents/Overview preserved
- Taskbar/BubbleBar preserved where supported by base source
- ElyraLauncherQuickStep ROM target
- Optional standalone APK for UI testing only

## Important Limitation

Standalone APK builds cannot provide real Android Recents or Quickstep integration.

Real Quickstep requires privileged ROM/system integration.
