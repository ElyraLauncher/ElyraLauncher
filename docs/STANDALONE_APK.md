# Private Preview APK

The standalone APK path produces the ElyraLauncher Private Preview APK for
project-owner personal testing and GitHub Actions artifacts.

It is not the source of truth for ElyraLauncher. The ROM/System build remains
authoritative, and the main ROM target remains:

```bash
m ElyraLauncherQuickStep
```

The Private Preview APK can preview Home, Drawer, Search, Settings, Appearance,
Elyra Glass, and the ElyraIcons concept. It cannot validate real Android
Recents, real Quickstep gesture integration, privileged launcher behavior,
platform hidden APIs, or system-level task management. Those paths require an
Android ROM tree and must be validated with:

```bash
m ElyraLauncherQuickStep
```

## Build

Build the debug APK with:

```bash
./gradlew --no-daemon :app:assembleDebug
```

The output is:

```text
app/build/outputs/apk/debug/app-debug.apk
```

In GitHub Actions, the Private Preview APK workflow copies that debug build to:

```text
app/build/outputs/apk/private/ElyraLauncher-private-debug.apk
```

The workflow uploads it with this artifact name:

```text
ElyraLauncher-private-debug
```

Download it from the completed GitHub Actions run by opening the
`ElyraLauncher Private Preview APK Build` workflow run and selecting the
`ElyraLauncher-private-debug` artifact. The downloaded APK file is named:

```text
ElyraLauncher-private-debug.apk
```

The Gradle `:app` module is a Standalone Private Preview target. It does not
compile or replace the ROM `Launcher3QuickStep` or `ElyraLauncherQuickStep`
Soong targets and does not package platform hidden API stubs.

For the separate experimental Gradle path that attempts to build from the real
Launcher3/Quickstep source tree, see `docs/PRIVATE_LAUNCHER3_APK.md`.

## Home Preview

The standalone APK registers its preview activity for Android Home app picker.
This lets the APK appear as an available Home launcher option. If selected as
the default Home app, pressing the device Home button opens the ElyraLauncher
Private Preview home shell.

This is still a HOME preview only. It is not real Quickstep, does not provide
real Recents, does not exercise privileged launcher behavior, and does not
validate platform task management. Real Recents and Quickstep validation remain
ROM-only and must be done with:

```bash
m ElyraLauncherQuickStep
```

## Private Preview UI

The APK opens to a simple ElyraLauncher home shell preview with a title, a
Private Preview APK note, search bar, workspace placeholders, dock placeholders,
and app drawer/settings actions. Tapping either the home search bar or drawer
search/filter placeholder opens a Search preview with Suggested apps, Settings
results, and Widgets placeholder sections.

The All apps action opens a placeholder drawer preview with its own search/filter
placeholder and twelve preview app placeholders: Phone, Messages, Browser,
Camera, Settings, Files, Gallery, Clock, Calculator, Calendar, Contacts, and
Weather. The Settings item opens the same standalone settings shell as the home
Settings action.

The Settings action opens the standalone ElyraLauncher Settings concept preview.
It uses a grouped settings layout with a hero information card and Appearance,
Home screen, Dock, Search, and About rows. The main settings rows stay text-only
with chevrons and no icons.

Appearance opens a visual concept screen with a launcher mock preview, grouped
Tema, Elyra Glass, Ikon, and Layout sections, and lightweight preview-only
controls. Elyra Glass depth opens a calm modern seekbar with Ringan, Sedang,
Dalam, and Kustom presets.

Home screen opens a preview-only detail screen with a mock launcher card and
grouped Layout, Workspace, and Motion rows for grid size, icon size, labels,
widget area, page indicator, empty slots, animation style, and transition speed.
Dock opens a preview-only detail screen with a focused Elyra Glass dock preview
card and grouped Tampilan, Aplikasi, and Integrasi rows for dock style, height,
radius, app count, labels, suggestions, dock search, Elyra Glass, and haptic
feedback.

Search opens a preview-only detail screen with a search card, suggested apps,
settings result, widget result, and grouped Tampilan, Sumber, and Perilaku rows.
About opens a detail screen that identifies the APK as Private Preview APK,
shows the build type as Standalone private APK, and keeps
`m ElyraLauncherQuickStep` as the ROM target.

These controls are only for private UI/Home preview and do not validate real
Launcher3 workspace, dock model binding, Recents, or Quickstep behavior. Theme
mode changes update the standalone Home, All apps, Search, Settings, Appearance,
Home screen, Dock, Search settings, and About preview colors for the current
process only.

## Elyra Glass And ElyraIcons

Elyra Glass is ElyraLauncher's visual surface system for dock, search bar,
drawer, settings cards, folders, widgets, app icon containers, shortcut cards,
and visual depth. In this Private Preview APK it remains preview-only. Glass
style, icon glass, and card surface rows are placeholders.

The Ikon section includes an Icon pack row that points to ElyraIcons as the
intended future icon source. Tapping it shows `ElyraIcons preview only`.
ElyraIcons remains preview-only here: the standalone APK does not parse icon
packs, does not add ElyraIcons source, and does not embed full icon assets.
ElyraLauncher controls the launcher experience and visual treatment; ElyraIcons
will provide icon pack assets and mappings later.

Preview screens have a back button, and Android back returns from a preview to
the previous standalone shell where practical.

Search result, dock, and placeholder app taps only show preview Toast messages,
except for the drawer Settings placeholder, which opens the standalone settings
shell. These controls are visual preview placeholders only; they do not exercise
real Launcher3 model binding, Recents, Quickstep gestures, privileged launcher
behavior, or platform task management.
