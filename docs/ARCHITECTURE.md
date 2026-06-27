# ElyraLauncher Architecture

## Main Repository

This repository contains the main ElyraLauncher source.

It is planned to be based on AOSP Launcher3 with Quickstep support.

## Build Modes

### ROM/System Build

The ROM build is the source of truth.

```bash
m ElyraLauncherQuickStep
```

### Standalone APK Build

Standalone APK is only for UI testing and CI artifacts.

It must not include real Quickstep runtime integration or hidden platform APIs.

## Source Areas

- `src/` — Launcher3 core source
- `res/` — Launcher resources
- `quickstep/` — Quickstep and Recents integration
- `systemUI/` — SystemUI compatibility area
- `wmshell/` — WM Shell compatibility area
- `hidden-api/` — compile-time hidden API handling area
- `protos/` — protocol buffers
- `tools/` — developer tools
- `flags/` — feature flags
- `tests/` — tests
