# ElyraLauncher

ElyraLauncher is the official launcher project for ElyraOS.

It is planned as a Lawnchair-style launcher ecosystem based on AOSP Launcher3 with Quickstep support.

## Goals

- Preserve Launcher3 architecture
- Preserve Quickstep and Recents integration
- Support Android ROM/system builds
- Provide a clean standalone APK path only for UI testing
- Build a professional ElyraLauncher ecosystem with icons, feed, docs, and website repositories

## Main ROM Target

```bash
m ElyraLauncherQuickStep
```

## Important Note

Standalone APK builds cannot provide real Android Recents or Quickstep integration. Real Quickstep support requires ROM/system integration.

## Planned Companion Repositories

- platform_frameworks_libs_systemui
- packages_apps_ElyraIcons
- packages_apps_ElyraFeed
- ElyraLauncherDocs
- ElyraLauncherWebsite
