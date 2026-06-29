# Verification

## Architecture Checks

Run these checks from the repository root to confirm the protected Launcher3 and Quickstep structure is present:

```bash
test -f Android.bp
test -f AndroidManifest.xml
test -f AndroidManifest-common.xml
test -d src
test -d quickstep
test -d res
grep -R "ElyraLauncherQuickStep" Android.bp README.md VERIFICATION.md docs || true
grep -R "package com.android.launcher3" src quickstep || true
```

Check for protected classes before merging broad source changes:

```bash
find src quickstep -name "QuickstepLauncher.*" -o -name "RecentsView.*" -o -name "TaskView.*" -o -name "TouchInteractionService.*"
```

## GitHub Actions Checks

GitHub Actions should run architecture guards, source hygiene checks, documentation checks, and Gradle APK builds. These workflows protect repository structure; they do not replace ROM validation.

## Gradle APK Build

If Gradle files are present, build the real installable launcher APK with:

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

The Gradle APK can catch basic resource, manifest, and packaging problems. It cannot validate real Recents, Quickstep gestures, privileged launcher behavior, hidden platform APIs, or system task management.

## ROM Build Validation

Real validation must happen inside an Android source tree that includes ElyraLauncher in the expected package location. Build the ROM target with:

```bash
m ElyraLauncherQuickStep
```

This is the required validation path for Quickstep and Recents because those components rely on platform APIs, privileged installation, framework integration, SystemUI relationships, and task-management behavior that are unavailable to a normal APK.
