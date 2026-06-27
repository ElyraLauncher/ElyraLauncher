# ElyraLauncher Roadmap

## Phase 1: Clean Launcher3 Base

- Import clean AOSP Launcher3 Android 16 source.
- Preserve Quickstep and Recents architecture.
- Preserve Overview, Taskbar, and BubbleBar where supported by base source.
- Add the `ElyraLauncherQuickStep` Soong target.
- Keep commit history clean.

## Phase 2: Elyra Branding

- ElyraLauncher app name.
- ElyraOS strings.
- Elyra launcher icon.
- Basic theme naming.
- Package and resource naming only where safe.
- Keep the Java package as `com.android.launcher3` until a validated
  migration plan exists.

## Phase 3: Standalone Validation APK

- Add a Gradle build only for UI testing.
- Do not claim real Recents or Quickstep support.
- Upload debug APK artifacts through GitHub Actions.

## Phase 4: Elyra Features

- Settings shell.
- Onboarding shell.
- Drawer customization.
- Search customization.
- Theme options.

## Phase 5: Companion Repositories

- `platform_frameworks_libs_systemui`
- `packages_apps_ElyraIcons`
- `packages_apps_ElyraFeed`
- `ElyraLauncherDocs`
- `ElyraLauncherWebsite`

## Rule

Do not add advanced features before the clean Launcher3/Quickstep base is
stable.
