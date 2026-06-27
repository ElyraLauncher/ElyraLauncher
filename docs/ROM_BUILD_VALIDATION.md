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
see its `Android.bp`, for example:

```text
packages/apps/ElyraLauncher
```

From the ROM tree root:

```bash
source build/envsetup.sh
lunch <device_or_product>
m ElyraLauncherQuickStep
```

If the product already includes `Launcher3QuickStep`, replace that product
package entry with `ElyraLauncherQuickStep`. The Elyra target also declares
`Launcher3QuickStep` in `overrides` so a product that selects Elyra does not
install both Quickstep launcher modules with the same package.

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

Before submitting ROM validation changes, confirm:

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
- `com.android.launcher3` is not renamed.
- Quickstep and Recents source paths are still present. In this Android 16
  import, `TaskView` is
  `quickstep/src/com/android/quickstep/views/TaskView.kt`.
- No Launcher3, Quickstep, or Recents behavior is changed.
