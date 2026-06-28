# ROM Build Validation

ElyraLauncherQuickStep is the ROM/system validation target for ElyraLauncher.
Standalone APK builds are only useful for basic UI smoke testing and cannot
validate real Recents, Quickstep gestures, privileged launcher behavior, or
platform hidden API integration.

## Target

Build the launcher inside an Android ROM tree with:

```bash
m ElyraLauncherQuickStep
```

The Java package remains:

```text
com.android.launcher3
```

Do not rename the package during this validation phase. Launcher3 and Quickstep
assume the AOSP package structure in several source, manifest, and privileged
integration paths.

## ROM Tree Setup

Place or sync this repository into the ROM source tree where the product can
see its `Android.bp`. The preferred path is:

```text
packages/apps/ElyraLauncher
```

If a product uses a different packages/apps path, keep the repository in a ROM
source location that Soong scans as an Android app package. The important check
is that `Android.bp` is visible from the ROM tree and the product can resolve
the `ElyraLauncherQuickStep` module name.

From the ROM tree root:

```bash
source build/envsetup.sh
lunch <device_or_product>
m ElyraLauncherQuickStep
```

After a successful ROM build, confirm the generated package in the product
output directory. The exact product name and partition path vary by ROM
configuration, but a typical system_ext privileged app output is:

```text
out/target/product/<device>/system_ext/priv-app/ElyraLauncherQuickStep/ElyraLauncherQuickStep.apk
```

If the ROM installs the module under a different output partition, use the
product's generated install list or run this from the ROM tree to confirm the
actual location:

```bash
find out/target/product/<device> -name "ElyraLauncherQuickStep.apk"
```

If the product already includes `Launcher3QuickStep`, replace that product
package entry with `ElyraLauncherQuickStep`. The Elyra target also declares
`Launcher3QuickStep` in `overrides` so a product that selects Elyra does not
install both Quickstep launcher modules with the same package.

Do not use the standalone Gradle APK output as evidence that Quickstep works.
The standalone APK is a separate smoke preview and does not replace this ROM
build target.

The experimental `:launcher-private` Gradle module is also not ROM validation.
It is a private owner APK build path that points at real Launcher3/Quickstep
source, but real Recents and Quickstep integration still require this ROM target
or future system-compatible work.

## Expected Soong Shape

`ElyraLauncherQuickStep` should stay aligned with `Launcher3QuickStep` for the
ROM validation phase:

- It reuses `Launcher3QuickStepLib`.
- It keeps `platform_apis: true`.
- It remains `privileged: true`.
- It remains `system_ext_specific: true`.
- It keeps Quickstep resources through `resource_dirs: ["quickstep/res"]`.
- It keeps `quickstep/AndroidManifest.xml`.
- It keeps `quickstep/AndroidManifest-launcher.xml` and
  `AndroidManifest-common.xml` as additional manifests.
- It requires `privapp_whitelist_com.android.launcher3`.
- It requires `launcher.quickstep.protolog.pb`.
- It keeps jacoco coverage scoped to `com.android.launcher3.*`.

Do not remove or rewrite Launcher3, Quickstep, Recents, Taskbar, or BubbleBar
source to make a standalone build pass.

## Validation Checklist

From this repository checkout, run the source-tree sanity check:

```bash
bash scripts/validate-elyraquickstep-target.sh
```

This script does not require a sourced Android build environment. It only checks
that the ROM Soong target structure is still present and that the standalone
Gradle APK path is not being treated as the real Quickstep target.

Before submitting ROM validation changes, also confirm:

```bash
git status
git diff --stat
grep -n 'ElyraLauncherQuickStep' Android.bp
grep -n 'Launcher3QuickStep' Android.bp
find . -name "Launcher.java" | head
find . -name "QuickstepLauncher.java" | head
find . -name "RecentsView.java" | head
find . -name "TaskView.java" | head
find . -name "TaskView.kt" | head
find . -name "TouchInteractionService.java" | head
```

Expected results:

- `ElyraLauncherQuickStep` remains available.
- `Launcher3QuickStep` remains available.
- `ElyraLauncherQuickStep` continues to override `Launcher3QuickStep` when the
  Elyra ROM target is selected.
- `com.android.launcher3` is not renamed.
- Quickstep and Recents source paths are still present. In this Android 16
  import, `TaskView` is
  `quickstep/src/com/android/quickstep/views/TaskView.kt`.
- No Launcher3, Quickstep, or Recents behavior is changed.

## Standalone APK Boundary

The Gradle standalone APK and the ROM target answer different questions:

- `./gradlew :app:assembleDebug` checks only the standalone Home/UI preview.
- `m ElyraLauncherQuickStep` builds the real ROM launcher package.
- Manually installing an APK cannot validate real Recents, Quickstep gestures,
  privileged launcher behavior, hidden APIs, or system task management.

Keep the standalone APK documentation in `docs/STANDALONE_APK.md`. Keep ROM
validation evidence here and in the Android ROM tree that builds
`ElyraLauncherQuickStep`.
