---
title: ElyraLauncher — Lawnchair-style Gradle APK Build
date: 2026-06-29
status: approved
branch: feat/elyra-launcher3-ux-foundation → feat/lawnchair-style-real-apk-build
---

## Goal

Make `./gradlew assembleDebug` produce a real installable launcher APK under
`app/build/outputs/apk/debug/*.apk`.

## Current State (as of 2026-06-29)

Branch `feat/elyra-launcher3-ux-foundation` already has:
- `app/` module with `applicationId = "com.elyra.launcher"`, real Launcher3 source sets
- `modules/animationlib`, `modules/iconloaderlib`, `modules/launcher-flags-compat`
- Correct `AndroidManifest.xml` (HOME intent, no privileged-only perms)

Build fails with three error categories:
1. `com.google.android.msdl.*` — internal AOSP haptics API, no public Maven release
2. `com.android.wm.shell.shared.bubbles` — WM Shell internal type
3. `StandaloneSmokeActivity.java` — smoke/preview file, must be removed

## Design Decisions

### Dependency classification rule
| Source | Strategy |
|--------|----------|
| AOSP/internal class | `prebuilts/libs/*.jar` compileOnly |
| Elyra-owned SystemUI source | `external/ElyraSystemUILibs` source link |
| Public AndroidX/Google class | Maven dependency |
| Tiny unavailable compile-only API | Gradle compat stub in `app/src/gradleCompat/java/` |

### Fix 1 — Delete StandaloneSmokeActivity.java
Smoke file, not part of real launcher. Remove entirely.

### Fix 2 — MSDL stubs
`com.google.android.msdl` is an internal Google haptics library unavailable outside
AOSP or Maven. Add minimal compile-only stubs under `app/src/gradleCompat/java/`.
Stubs must be clearly marked as Gradle-only shims with no runtime logic.

### Fix 3 — WM Shell bubbles
Prefer `prebuilts/libs/WindowManagerShell-16.jar` (Lawnchair strategy) if available.
Only stub if JAR does not resolve the error or causes conflicts.

### Paths
- No `/root/...` absolute paths anywhere
- `prebuilts/libs/` for AOSP JARs
- `external/ElyraSystemUILibs` → portable relative symlink `../ElyraSystemUILibs`

## Success Criteria
- `./gradlew assembleDebug` exits 0
- APK exists at `app/build/outputs/apk/debug/app-debug.apk`
- No `/root/` paths in committed files
- Commit signed-off, branch pushed as `feat/lawnchair-style-real-apk-build`
