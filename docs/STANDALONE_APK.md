# Standalone APK

The standalone APK exists only for basic ElyraLauncher UI smoke testing in
GitHub Actions.

It is not the source of truth for ElyraLauncher. The ROM/System build remains
authoritative, and the main ROM target remains:

```bash
m ElyraLauncherQuickStep
```

The standalone APK cannot validate real Android Recents, real Quickstep gesture
integration, privileged launcher behavior, platform hidden APIs, or
system-level task management. Those paths require an Android ROM tree and must
be validated with:

```bash
m ElyraLauncherQuickStep
```

## Build

Build the debug APK with:

```bash
./gradlew :app:assembleDebug
```

The output is:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The Gradle `:app` module is a standalone UI smoke target. It does not compile
or replace the ROM `Launcher3QuickStep` or `ElyraLauncherQuickStep` Soong
targets, does not register as a Home activity, and does not package platform
hidden API stubs.

## Smoke UI

The APK opens to a simple ElyraLauncher home shell preview with a title, warning
message, fake search bar, workspace placeholders, dock placeholders, and app
drawer/settings actions. The All apps action opens a placeholder drawer preview
with Phone, Messages, Browser, Camera, Settings, Files, Gallery, and Clock. The
Settings action opens a simple placeholder settings shell with Appearance, Home
screen, Dock, Search, and About sections. Both previews have a back button, and
Android back returns from a preview to the home shell.

Search and dock taps only show preview Toast messages. These controls are visual
smoke-test placeholders only; they do not exercise real Launcher3 model binding,
Recents, Quickstep gestures, privileged launcher behavior, or platform task
management.
