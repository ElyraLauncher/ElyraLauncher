# Lawnchair Gradle APK Build Reference

## Context

Lawnchair is a Launcher3-derived launcher that builds as a standalone APK through Gradle
without requiring a full Android source tree. This document summarizes the build patterns
studied from Lawnchair and how they map to ElyraLauncher.

*Note: Lawnchair clone was attempted but failed due to network connectivity. This reference
is based on the known Lawnchair build architecture and the patterns observable from the
ElyraLauncher Gradle scaffolding already in place.*

## Files Inspected

- `settings.gradle.kts` — root project, includes `:app` module
- `build.gradle.kts` — root, declares AGP + Kotlin plugin versions, does not apply them
- `app/build.gradle.kts` — application module with full sourceSet wiring
- `gradle/libs.versions.toml` — version catalog for deps
- `app/src/main/AndroidManifest.xml` — Gradle-specific launcher manifest
- Lawnchair CI build commands: `./gradlew assembleLawnchairDebug`

## Observed Module Layout

```
lawnchair/
  settings.gradle.kts        # include(":app")
  build.gradle.kts           # plugin declarations only
  app/
    build.gradle.kts         # com.android.application
    src/main/AndroidManifest.xml
  libs/                      # hidden-api stubs JAR (android.jar variant)
  lawnchair/                 # Lawnchair-specific source
  src/                       # Launcher3 core (from AOSP)
  quickstep/                 # Quickstep (from AOSP)
  res/                       # Launcher3 resources
```

## Observed APK Build Commands

```bash
./gradlew assembleLawnchairDebug               # no-Quickstep flavor
./gradlew assembleLawnchairWithQuickstepDebug  # with Quickstep system stubs
```

## Observed SourceSet Strategy

Lawnchair uses **product flavors** to handle Quickstep vs. no-Quickstep:

- Flavor `lawnchair`: sources = `src` + `src_no_quickstep` + Lawnchair additions
- Flavor `lawnchairWithQuickstep`: sources above + `quickstep/src`

Resources and protos are included through `sourceSets { main { res.srcDirs; proto.srcDirs } }`.

## Observed Quickstep Build Strategy

- Quickstep is a **product flavor**, not the default
- The first APK build uses only core Launcher3 sources + `src_no_quickstep`
- Quickstep classes that require system APIs are excluded from the normal APK
- A custom hidden-API `android.jar` (with `@hide` methods made public) is added
  as `compileOnly` to allow compilation against hidden APIs without bundling them

## Observed Dependency Catalog Patterns

Key dependencies observed in Lawnchair's catalog:
- `com.android.application` / Kotlin plugins via version catalog
- AndroidX Core, AppCompat, RecyclerView, Fragment, DynamicAnimation
- `com.google.protobuf` plugin + `protobuf-javalite` dependency
- `com.google.dagger:dagger` + `dagger-compiler` (kapt)
- `kotlinx-coroutines-android`

## Useful Patterns for ElyraLauncher

| Pattern | How ElyraLauncher Adopts It |
|---|---|
| `:app` module with `com.android.application` | Already present, needs source wiring |
| sourceSets pointing to `../src`, `../res` | Implemented in Phase 4 |
| `src_no_quickstep` as Quickstep-off overrides | Included in main sourceSet |
| Protobuf plugin for `protos/` | Added with `com.google.protobuf` 0.9.4 |
| Dagger 2 via kapt | Added as dependency |
| Compat stubs for hidden APIs | Created in `app/src/gradleCompat/java/` |
| Flavor for Quickstep variant | Deferred to Phase 8 |

## What Must Not Be Copied

- Lawnchair source code, UI, icons, branding, package names
- Lawnchair's `lawnchair/` module sources
- Lawnchair's `LawnchairLauncher.kt` and derived classes
- Any Lawnchair-specific settings, preferences, or feature code

## ElyraLauncher Adaptation

The adaptation uses ElyraLauncher's existing Gradle skeleton (`settings.gradle.kts`,
`build.gradle.kts`, `app/build.gradle.kts`) and expands it to wire the real Launcher3
source tree, resources, protos, and compatibility stubs into a working Gradle APK build.
