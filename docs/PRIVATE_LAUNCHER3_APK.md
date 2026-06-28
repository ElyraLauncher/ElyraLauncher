# Private Launcher3 APK

The experimental Private Launcher3 APK is a project-owner build path for
testing ElyraLauncher from the real Launcher3/Quickstep source tree. It is
separate from the existing Private Preview APK in `app/`.

## Build Modes

- Private Preview APK: `app/` is a standalone UI/Home preview for owner testing
  and GitHub Actions artifacts.
- Private Launcher3 APK: `launcher-private/` is an experimental Gradle Android
  application module that points at the real `src/`, `quickstep/src/`, `res/`,
  and `quickstep/res/` trees.
- ROM target: `ElyraLauncherQuickStep` remains the source-of-truth system build
  target.

## Commands

Build the Private Preview APK:

```bash
./gradlew --no-daemon :app:assembleDebug
```

Audit the Private Launcher3 APK setup:

```bash
bash scripts/audit-private-launcher3-apk.sh
```

Attempt the experimental Private Launcher3 APK build:

```bash
./gradlew --no-daemon :launcher-private:assembleDebug
```

Validate the real ROM launcher in an Android ROM tree:

```bash
m ElyraLauncherQuickStep
```

## Module Shape

The `:launcher-private` module uses:

- Application ID: `com.elyra.launcher.private`
- Java/source package: `com.android.launcher3`
- Real Launcher3 source set: `src/`
- Real Quickstep source set: `quickstep/src/`
- Real Launcher3 resources: `res/`
- Real Quickstep resources: `quickstep/res/`
- Private manifest: `launcher-private/src/main/AndroidManifest.xml`

The module does not copy large Launcher3 or Quickstep source trees. It keeps the
private install package separate through Gradle `applicationId` while preserving
the Java package names used by Launcher3 internals.

Resource overlay shape mirrors Soong at a small scale: `launcher3-res` points at
`res/`, `quickstep-res` points at `quickstep/res/`, and the app depends on
`quickstep-res` instead of merging both resource directories into one source set.

## Current Blockers

This path is expected to need more AOSP build-system parity before it becomes a
reliable normal APK build. The real Quickstep tree depends on platform-only and
Soong-generated pieces, including:

- Android platform APIs and hidden APIs used by Quickstep, Recents, taskbar,
  bubbles, transitions, and system task management.
- SystemUI shared libraries and shell interfaces from
  `platform_frameworks_libs_systemui`.
- Soong-generated protolog and protobuf outputs.
- Any remaining Gradle resource-overlay differences from Soong.
- Soong filegroups such as `launcher-build-config`.
- Dagger-generated components for Launcher3 and Quickstep.

Do not replace these with fake runtime behavior, do not package platform stubs
into a release APK, and do not delete Quickstep/Recents code to force a Gradle
success. Any Gradle failure in this module is a dependency-porting task unless
the failure is clearly in the new build scaffolding.

## Runtime Boundary

The Private Launcher3 APK may appear as a Home option if it builds and installs,
but it does not prove real Recents or Quickstep integration. Real Recents,
gesture navigation, privileged launcher behavior, platform hidden APIs, and
system-level task management still require ROM/system validation or future
QuickSwitch-compatible work.

The ROM validation command remains:

```bash
m ElyraLauncherQuickStep
```

## Related Docs

- `docs/STANDALONE_APK.md` describes the `app/` Private Preview APK.
- `docs/ROM_BUILD_VALIDATION.md` describes the source-of-truth ROM build.
