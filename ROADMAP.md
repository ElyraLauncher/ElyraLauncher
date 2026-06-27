# ElyraLauncher Roadmap

## Phase 1 — Clean Launcher3 Base

- Import clean AOSP Launcher3 Android 16 source
- Preserve Quickstep and Recents architecture
- Preserve Overview, Taskbar, and BubbleBar where supported by base source
- Add ElyraLauncherQuickStep Soong target
- Keep commit history clean

## Phase 2 — Elyra Branding

- ElyraLauncher app name
- ElyraOS strings
- Elyra launcher icon
- Basic theme naming
- Package/resource naming only where safe

## Phase 3 — Standalone Validation APK

- Add Gradle build only for UI testing
- Do not claim real Recents/Quickstep support
- Upload debug APK through GitHub Actions

## Phase 4 — Elyra Features

- Settings shell
- Onboarding shell
- Drawer customization
- Search customization
- Theme options

## Phase 5 — Companion Repositories

- platform_frameworks_libs_systemui
- packages_apps_ElyraIcons
- packages_apps_ElyraFeed
- ElyraLauncherDocs
- ElyraLauncherWebsite

## Rule

Do not add advanced features before the clean Launcher3/Quickstep base is stable.
