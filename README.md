# ElyraLauncher

ElyraLauncher is the official launcher project for ElyraOS.

It is a Lawnchair-style Android launcher ecosystem based on AOSP Launcher3
with Quickstep support.

## Goals

- Preserve AOSP Launcher3 architecture.
- Preserve Quickstep, Recents, Overview, Taskbar, and BubbleBar integration.
- Support Android ROM/system builds.
- Provide a Private Preview APK path only for owner UI testing.
- Build a professional ElyraLauncher ecosystem with separate icons, feed,
  docs, and website repositories.

## Main ROM Target

The main ROM target is `ElyraLauncherQuickStep`.

Expected ROM build command:

```bash
m ElyraLauncherQuickStep
```

## Build Model

### ROM/System Build

The ROM build is the source of truth.

Real Quickstep, Recents, gesture navigation, privileged launcher behavior, and
platform integration must be validated inside an Android ROM tree.

### Private Preview APK

Standalone Private Preview APK builds are only for owner UI testing and
GitHub Actions artifacts.

Private Preview APK builds cannot provide real Android Recents or Quickstep
integration because those require privileged ROM/system integration.

Build with:

```bash
./gradlew --no-daemon :app:assembleDebug
```

### Private Launcher3 APK

The experimental `:launcher-private` module attempts to build a private owner
APK from the real Launcher3/Quickstep source and resources. It is not a public
release path and does not validate real Recents or Quickstep integration.

Build attempt:

```bash
./gradlew --no-daemon :launcher-private:assembleDebug
```

## Package Naming

The Java package intentionally remains `com.android.launcher3` for now.

Launcher3 and Quickstep contain package-level assumptions that should not be
renamed until there is a dedicated, validated migration plan.

## Current Status

This repository is being rebuilt cleanly from an AOSP Launcher3 Android 16
base.

## Planned Companion Repositories

- `platform_frameworks_libs_systemui`
- `packages_apps_ElyraIcons`
- `packages_apps_ElyraFeed`
- `ElyraLauncherDocs`
- `ElyraLauncherWebsite`
