# ElyraLauncher

ElyraLauncher is the official launcher project for ElyraOS.

It is a Lawnchair-style Android launcher ecosystem based on AOSP Launcher3 with Quickstep support.

## Goals

- Preserve AOSP Launcher3 architecture
- Preserve Quickstep, Recents, Overview, Taskbar, and BubbleBar integration
- Support Android ROM/system builds
- Provide a standalone APK path only for basic UI testing
- Build a professional ElyraLauncher ecosystem with icons, feed, docs, and website repositories

## Main ROM Target

```bash
m ElyraLauncherQuickStep
```

## Build Model

### ROM/System Build

The ROM build is the source of truth.

Real Quickstep, Recents, gesture navigation, privileged launcher behavior, and platform integration must be validated inside an Android ROM tree.

### Standalone APK

Standalone APK builds are only for UI smoke testing and GitHub Actions artifacts.

Standalone APK builds cannot provide real Android Recents or Quickstep integration because those require privileged ROM/system integration.

## Current Status

This repository is being rebuilt cleanly from an AOSP Launcher3 Android 16 base.

## Planned Companion Repositories

- platform_frameworks_libs_systemui
- packages_apps_ElyraIcons
- packages_apps_ElyraFeed
- ElyraLauncherDocs
- ElyraLauncherWebsite
