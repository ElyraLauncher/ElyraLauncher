# ElyraLauncher Source Map for Gradle

## Source Tree Summary

| Elyra path | Purpose | Gradle use | Notes |
|---|---|---|---|
| `src/` | Launcher3 core Java/Kotlin (451 Java, 96 Kotlin) | `main.java.srcDirs` | Primary source set |
| `src_no_quickstep/` | No-Quickstep overrides (5 files: Dagger, states, ProtoLog stub) | `main.java.srcDirs` | Required for no-Quickstep APK |
| `src_build_config/` | Manual `BuildConfig.java` with Launcher3-specific flags | `main.java.srcDirs` | Use with `buildFeatures.buildConfig = false` |
| `src_plugins/` | Plugin interfaces (`AllAppsRow`, `ResourceProvider`, etc.) | `main.java.srcDirs` | Plugin.java + PluginListener.java added here |
| `compose/facade/disabled/` | Disabled Compose stub (`ComposeFacade.kt`) | `main.java.srcDirs` | No Compose dependencies |
| `compose/facade/core/` | Base Compose interface (`BaseComposeFacade.kt`) | `main.java.srcDirs` | No Compose dependencies |
| `shared/src/` | Testing shared utilities | `main.java.srcDirs` | Needed for TestProtocol |
| `res/` | All launcher resources (layouts, drawables, values) | `main.res.srcDirs` | Full Launcher3 resource tree |
| `protos/` | Protobuf definitions (`launcher_atom.proto`, `launcher_trace.proto`) | `main.proto.srcDirs` | Generates `com.android.launcher3.logger.*` |
| `protos_overrides/` | Proto extensions (`launcher_atom_extension.proto`) | `main.proto.srcDirs` | Imported by `launcher_atom.proto` |
| `AndroidManifest.xml` | ROM launcher manifest (package `com.android.launcher3`) | Not used directly | References ROM-only resources |
| `AndroidManifest-common.xml` | Common components (permissions, providers, receivers) | Not merged in first build | ROM build merges with main manifest |
| `Android.bp` | ROM/AOSP build system file | Not used in Gradle | Preserved for ROM integration |
| `quickstep/` | Quickstep/Recents sources | Excluded from first APK | Preserved for future variant |
| `quickstep/res/` | Quickstep resources | Excluded from first APK | Preserved for future variant |
| `app/` | Gradle app module | APK entry point | Updated to wire real sources |
| `app/src/main/AndroidManifest.xml` | Gradle-specific launcher manifest | `main.manifest.srcFile` | Points to `com.android.launcher3.Launcher` |
| `app/src/gradleCompat/java/` | Gradle-only compat stubs | `main.java.srcDirs` | Stubs for hidden/aconfig APIs |

## Hidden API Dependencies

| Import | Source | Stub approach |
|---|---|---|
| `com.android.launcher3.Flags` | aconfig-generated | Stub in `gradleCompat/` (all methods → false) |
| `com.android.window.flags.Flags` | aconfig-generated (window) | Stub in `gradleCompat/` |
| `com.android.systemui.shared.Flags` | aconfig-generated (systemui) | Stub in `gradleCompat/` |
| `com.android.systemui.shared.system.SysUiStatsLog` | system stats log | Stub in `gradleCompat/` |
| `com.android.systemui.plugins.Plugin` | plugin interface | Added to `src_plugins/` |
| `com.android.systemui.plugins.PluginListener` | plugin callback | Added to `src_plugins/` |
| `com.android.systemui.plugins.annotations.ProvidesInterface` | plugin annotation | Added to `src_plugins/` |
| `android.window.BackEvent` | public API 34+ | Covered by compileSdk 35 |
| `android.window.OnBackAnimationCallback` | public API 34+ | Covered by compileSdk 35 |
| `android.window.OnBackInvokedDispatcher` | public API 33+ | Covered by compileSdk 35 |
| `android.hardware.display.DisplayManager` | public API | Covered by compileSdk 35 |

## Required Source Paths (First APK Build)

```
src/
src_no_quickstep/
src_build_config/
src_plugins/
compose/facade/disabled/
compose/facade/core/
shared/src/
app/src/gradleCompat/java/
```

## ROM-Only Paths (Preserved, Excluded from First APK)

```
quickstep/src/
quickstep/res/
go/
Android.bp
quickstep/Android.bp
aconfig/
```
