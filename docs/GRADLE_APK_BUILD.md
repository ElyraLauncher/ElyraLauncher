# Gradle APK Build

## Purpose

The Gradle `:app` module builds a real installable launcher APK. The APK can be installed on a device and selected as the Home launcher.

## What It Can Test

- Gradle configuration and dependency resolution.
- Android resource packaging.
- Manifest validity and launcher intent registration.
- Basic launcher launch on a real device.

## What It Cannot Test

- Real Quickstep gesture integration.
- Real Android Recents.
- Privileged launcher behavior.
- Hidden platform APIs.
- System task management.
- ROM product integration.

## Build Command

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

## Limitations

Normal APK builds do not validate privileged Quickstep or Recents system behavior. ROM/ElyraOS integration is future work. The ROM build target remains `ElyraLauncherQuickStep`.
