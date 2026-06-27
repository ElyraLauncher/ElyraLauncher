# ElyraLauncher Architecture

## Main Repository

This repository contains the main ElyraLauncher source.

It is based on AOSP Launcher3 with Quickstep support and preserves the
Launcher3/Quickstep architecture.

## Build Modes

### ROM/System Build

The ROM build is the source of truth.

```bash
m ElyraLauncherQuickStep
```

Real Quickstep, Recents, gesture navigation, privileged launcher behavior, and
platform integration must be tested in a full Android ROM tree.

### Standalone APK Build

Standalone APK is only for UI testing and CI artifacts.

It must not claim to support real Quickstep runtime integration, real Recents,
or hidden platform APIs.

## Package Naming

The Java package remains `com.android.launcher3` in this phase.

Launcher3 and Quickstep package assumptions should be preserved until a
dedicated migration plan is validated.

## Source Areas

- `src/`: Launcher3 core source
- `res/`: Launcher resources
- `quickstep/`: Quickstep and Recents integration
- `systemUI/`: SystemUI compatibility area
- `wmshell/`: WM Shell compatibility area
- `hidden-api/`: compile-time hidden API handling area
- `protos/`: protocol buffers
- `tools/`: developer tools
- `flags/`: feature flags
- `tests/`: tests

## Boundary

The main launcher repository should not contain the full source of companion
repositories.

Companion repositories must stay separate and be integrated only through
documented interfaces.
