# Testing

## Source Checks

Use structural checks before review:

```bash
test -f Android.bp
test -f AndroidManifest.xml
test -f AndroidManifest-common.xml
test -d src
test -d quickstep
test -d res
grep -R "ElyraLauncherQuickStep" Android.bp README.md VERIFICATION.md docs || true
```

## Manual Launcher Checks

On a ROM build, verify home launch, workspace persistence, icon loading, folders, widgets, All Apps, settings entry points, Overview, Recents, gestures, and Taskbar behavior where supported by the device form factor.

## ROM Checks

In an Android source tree, run:

```bash
m ElyraLauncherQuickStep
```

ROM validation is required for real Quickstep and Recents changes.

## CI Checks

CI should guard architecture, source hygiene, documentation wording, and optional Gradle smoke behavior. CI does not replace device or ROM validation for platform features.

## Smoke APK Checks

If Gradle is available, use the standalone APK only for basic UI and resource smoke testing:

```bash
./gradlew --version
./gradlew tasks
./gradlew --no-daemon :app:assembleDebug
```

Do not use the smoke APK as evidence that real Recents or Quickstep works.
