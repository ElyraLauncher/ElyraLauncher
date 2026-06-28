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

The APK opens to a simple ElyraLauncher home shell preview with a title, calmer
standalone note, fake search bar, workspace placeholders, dock placeholders,
and app drawer/settings actions. Tapping either the home search bar or drawer
search/filter placeholder opens a standalone Search preview with Suggested
apps, Settings results, and Widgets placeholder sections.

The All apps action opens a placeholder drawer preview with its own search/filter
placeholder and twelve smoke-test app placeholders:
Phone, Messages, Browser, Camera, Settings, Files, Gallery, Clock, Calculator,
Calendar, Contacts, and Weather. The Settings item opens the same standalone
settings shell as the home Settings action.

The Settings action opens the standalone ElyraLauncher Settings concept
preview. It uses a grouped settings layout with a hero information card and
Appearance, Home screen, Dock, Search, and About rows. Appearance opens a
standalone visual concept screen with a launcher mock preview, grouped Tema,
Elyra Glass, Ikon, and Layout sections, and lightweight preview-only controls.
Home screen opens a standalone preview-only detail screen with a mock launcher
card and grouped Layout, Workspace, and Motion rows. These controls are only for
standalone UI smoke testing and do not validate real Launcher3 workspace,
Recents, or Quickstep behavior. Theme mode changes update the standalone Home,
All apps, Search, Settings, Appearance, and Home screen preview colors for the
current process only.

Elyra Glass is ElyraLauncher's visual surface system for dock, search bar,
drawer, settings cards, folders, widgets, app icon containers, shortcut cards,
and visual depth. In this standalone APK it is only a concept preview. Glass
style, icon glass, and card surface rows are placeholders, while Kedalaman Glass
opens a detail screen with a calm modern seekbar, percentage text, and Ringan,
Sedang, Dalam, and Kustom presets.

The Ikon section includes an Icon pack row that points to ElyraIcons as the
intended future icon source. This is preview-only: the standalone APK does not
parse icon packs, does not add ElyraIcons source, and does not embed full icon
assets. ElyraLauncher controls the launcher experience and visual treatment;
ElyraIcons will provide icon pack assets and mappings later.

Preview screens have a back button, and Android back returns from a preview to
the previous standalone shell where practical.

Search result, dock, and placeholder app taps only show preview Toast messages,
except for the drawer Settings placeholder, which opens the standalone settings
shell. These controls are visual smoke-test placeholders only; they do not
exercise real Launcher3 model binding, Recents, Quickstep gestures, privileged
launcher behavior, or platform task management.
