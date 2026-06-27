# ElyraLauncher Agent Rules

ElyraLauncher is a Lawnchair-style Android launcher ecosystem project based on
AOSP Launcher3 with Quickstep support.

## Project Goal

Create a clean, professional Launcher3/Quickstep-based launcher for ElyraOS.

This is not a fake launcher rewrite.
This is not a simple Compose-only launcher.
This project must preserve Launcher3 and Quickstep architecture.

## Main ROM Target

The main ROM target is:

```text
ElyraLauncherQuickStep
```

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
- Do not package platform stubs into a release APK.
- Do not fake hidden API behavior.
- Do not remove platform integration code just to make standalone builds pass.
- Do not convert this project into a fake minimal launcher.
- Do not replace Launcher3 with Compose-only source.
- Do not rename package `com.android.launcher3` in this phase.

## Must Preserve

Do not remove Launcher3 or Quickstep architecture.

Do not remove these Launcher3 areas:

- Launcher3 core
- `LauncherModel`
- `LauncherProvider`
- `IconCache`
- `DeviceProfile`

Do not remove these Quickstep and Recents areas:

- Quickstep core
- `RecentsView`
- `TaskView`
- `TouchInteractionService`
- `QuickstepLauncher`
- `OverviewCommandHelper`
- `LauncherActivityInterface`

Do not remove these system integration areas:

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

Those will be created later as separate repositories or separate clean commits.

## Planned Companion Repositories

- `platform_frameworks_libs_systemui`
- `packages_apps_ElyraIcons`
- `packages_apps_ElyraFeed`
- `ElyraLauncherDocs`
- `ElyraLauncherWebsite`

## Commit Style

Use clean commits:

- `rebase(launcher3): import clean Android 16 Launcher3 base`
- `build(soong): add ElyraLauncherQuickStep target`
- `brand: apply ElyraLauncher identity`
- `docs: define ElyraLauncher ecosystem architecture`
- `ci: add initial validation workflow`

## Important Rule For Agents

Before changing source code, inspect the existing Launcher3/Quickstep
structure.

Do not rewrite unrelated files.
Do not delete source to hide build errors.
Do not remove Quickstep/Recents just to make Gradle build pass.
Do not package hidden API stubs into release APKs.
Do not claim standalone APK supports real Recents or Quickstep.

## Standalone APK Rule

Standalone APK builds are only for basic UI smoke testing.

Standalone builds cannot validate:

- real Android Recents
- Quickstep gesture integration
- privileged launcher behavior
- platform hidden APIs
- system-level task management

Full validation must be done inside an Android ROM tree using:

```bash
m ElyraLauncherQuickStep
```

