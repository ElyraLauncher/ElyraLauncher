# ElyraLauncher Next Steps

## Current Stable State

The current stable state is intentionally narrow.

It records that the repository is still centered on the clean Launcher3 base
and its preserved Quickstep integration points.

### Imported Base

- Clean AOSP Launcher3 Android 16 base imported.

### Preserved Quickstep Source

- Quickstep source preserved.

### Preserved Recents Components

- `RecentsView` preserved.
- `TaskView` preserved.

### Preserved Service Components

- `TouchInteractionService` preserved.

### Preserved Launcher Components

- `QuickstepLauncher` preserved.

### ROM Target

- `ElyraLauncherQuickStep` Soong target added.

### Java Package

- Java package intentionally still kept as `com.android.launcher3`.

## Do Not Do Yet

The next phase must not expand the project beyond metadata formatting.

### Package Restrictions

- Do not rename Java package.

### Launcher3 Restrictions

- Do not remove Quickstep.
- Do not remove Recents.
- Do not remove Overview.
- Do not remove Taskbar.
- Do not remove BubbleBar.

### Deferred Modules

- Do not add ElyraKIT.
- Do not add native module.
- Do not add benchmark.
- Do not add feed provider.
- Do not add icon pack source.

### UI Scope

- Do not redesign UI yet.

### Standalone APK Scope

- Do not force Gradle standalone APK to support real Quickstep.

## Next Phase 1: Formatting Cleanup

### Goal

Normalize metadata formatting without touching Launcher3/Quickstep source.

### Metadata Files

- `Android.bp`
- `README.md`
- `AGENTS.md`
- `ROADMAP.md`
- `VERIFICATION.md`
- `docs/*.md`
- `.github/workflows/build.yml`

### Source Directories Out Of Scope

- `src/`
- `quickstep/`
- `res/`
- `protos/`
- `tests/`
- `shared/`
- `compose/`
- `go/`

### Required Validation

Run repository status checks after formatting changes.

Run diff checks after formatting changes.

Verify the metadata files remain readable multi-line documents.

Verify the standalone APK warning remains visible.

Verify the Java package note remains visible.

Verify the ROM target remains `ElyraLauncherQuickStep`.

## Commit

Use this commit message:

```text
chore(format): normalize project metadata formatting
```
