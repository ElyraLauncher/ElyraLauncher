# ElyraLauncher

ElyraLauncher is the Launcher3 and Quickstep launcher package for ElyraOS. The repository is ROM-first: the Android platform build is the source of truth, and the main system target is `ElyraLauncherQuickStep`.

The project keeps the AOSP Launcher3 architecture and Elyra-specific customization work in the same source tree. It does not replace Launcher3 with a Compose-only launcher, and it does not provide fake Recents or fake Quickstep behavior.

## Build Model

### ROM/System Build

Real launcher validation happens in an Android ROM tree. The expected command is:

```bash
m ElyraLauncherQuickStep
```

This path is required for Quickstep, Recents, gesture navigation, privileged launcher behavior, hidden platform APIs, and system task management.

### Gradle APK Build

The Gradle `:app` module builds a real installable launcher APK. The APK can be installed on a device and selected as the Home launcher.

```bash
./gradlew assembleDebug
```

## Repository Layout

- `Android.bp`: Soong integration for ROM/system builds.
- `AndroidManifest.xml` and `AndroidManifest-common.xml`: Launcher manifests used by the platform build.
- `src/`: Launcher3 core, including model, provider, icon cache, workspace, hotseat, folders, widgets, and All Apps.
- `quickstep/`: Quickstep, Recents, task views, gesture integration, Taskbar, and BubbleBar-related source.
- `res/`: Launcher resources used by the ROM target.
- `shared/`, `protos/`, `tests/`: shared source, protocol definitions, and test assets.
- `app/`: Gradle APK build module.
- `docs/`: technical project documentation.

## Development Flow

Work from topic branches based on `main`. Keep commits scoped, signed, and conventional. Infrastructure changes should not delete or stub Launcher3 or Quickstep source to make a Gradle APK build pass.

Useful checks before review:

```bash
test -f Android.bp
test -d src
test -d quickstep
test -d res
grep -R "ElyraLauncherQuickStep" Android.bp README.md VERIFICATION.md docs || true
```

If Gradle is available, build the debug APK with `./gradlew assembleDebug`. Do not use Gradle success as proof that ROM Quickstep works.

## Limitations

- Real Recents and Quickstep require ROM/system validation.
- Gradle APK builds do not validate privileged Quickstep/Recents system behavior.
- The Java package remains `com.android.launcher3` in this phase.
- Platform stubs must not be packaged into release APKs.
