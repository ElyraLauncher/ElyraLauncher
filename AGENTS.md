# ElyraLauncher Agent Rules

ElyraLauncher is a Lawnchair-style Android launcher ecosystem project based on AOSP Launcher3 with Quickstep support.

## Project Goal

Create a clean, professional Launcher3/Quickstep-based launcher for ElyraOS.

This is not a fake launcher rewrite.
This is not a simple Compose-only launcher.
This project must preserve Launcher3 and Quickstep architecture.

## Main ROM Target

ElyraLauncherQuickStep

Expected ROM build command:

```bash
m ElyraLauncherQuickStep
```

## Architecture Rules

- ROM/System build is the source of truth.
- Android.bp is required for ROM integration.
- Gradle standalone APK is only for UI testing and GitHub Actions.
- Standalone APK must not claim to support real Recents or Quickstep.
- Real Recents/Quickstep must be validated inside an Android ROM tree.

## Must Preserve

Do not remove:

- Launcher3 core
- Quickstep core
- RecentsView
- TaskView
- TouchInteractionService
- QuickstepLauncher
- LauncherModel
- LauncherProvider
- IconCache
- DeviceProfile
- OverviewCommandHelper
- LauncherActivityInterface
- Taskbar
- BubbleBar

## First Phase Restrictions

Do not add these in the first phase:

- ElyraKIT
- native module
- benchmark module
- samples
- website source
- docs website source
- ElyraFeed source
- ElyraIcons source
- complex Compose feature layer

Those will be created later as separate repos or separate clean commits.

## Planned Companion Repositories

- platform_frameworks_libs_systemui
- packages_apps_ElyraIcons
- packages_apps_ElyraFeed
- ElyraLauncherDocs
- ElyraLauncherWebsite

## Commit Style

Use clean commits:

- rebase(launcher3): import clean Android 16 Launcher3 base
- build(soong): add ElyraLauncherQuickStep target
- brand: apply ElyraLauncher identity
- docs: define ElyraLauncher ecosystem architecture
- ci: add initial validation workflow
