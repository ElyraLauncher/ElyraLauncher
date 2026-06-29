# Gradle APK Build Strategy

## Primary Build Command

```bash
./gradlew assembleDebug
```

## App Module Structure

Single `:app` module using `com.android.application` plugin.

## Parameters

| Parameter | Value |
|---|---|
| namespace | `com.android.launcher3` |
| applicationId | `com.elyra.launcher` |
| compileSdk | 35 |
| minSdk | 31 |
| targetSdk | 35 |
| Java source compat | Java 17 |
| Kotlin jvmTarget | 17 |

## SourceSet Strategy

```
main.java.srcDirs:
  ../src                        # Launcher3 core
  ../src_no_quickstep           # No-Quickstep overrides
  ../src_build_config           # Manual BuildConfig
  ../src_plugins                # Plugin interfaces
  ../compose/facade/disabled    # Disabled Compose stub
  ../compose/facade/core        # Compose base interface
  ../shared/src                 # Shared test utilities
  src/gradleCompat/java         # Gradle-only compat stubs

main.res.srcDirs:
  ../res                        # Full Launcher3 resources

main.proto.srcDirs:
  ../protos                     # launcher_atom.proto etc.
  ../protos_overrides           # launcher_atom_extension.proto
```

## Manifest Strategy

A Gradle-specific `app/src/main/AndroidManifest.xml` is used. It:
- Points to `com.android.launcher3.Launcher` as the HOME activity
- Includes `LauncherProvider` content provider for workspace data
- Includes necessary receivers (boot, appwidget, etc.)
- Uses `android.intent.category.HOME` and `android.intent.category.DEFAULT`
- Sets `android:exported="true"` for Android 12+ compliance
- Avoids privileged ROM-only permissions that block install on regular devices

## Dependency Strategy

| Dependency | Purpose |
|---|---|
| `com.android.application` | Gradle app plugin |
| `org.jetbrains.kotlin.android` | Kotlin support |
| `org.jetbrains.kotlin.kapt` | Dagger annotation processing |
| `com.google.protobuf` 0.9.4 | Proto generation |
| AndroidX Core KTX | Core Kotlin extensions |
| AndroidX AppCompat | Activity compat |
| AndroidX RecyclerView | App drawer lists |
| AndroidX Fragment | Fragment support |
| AndroidX DynamicAnimation | Spring animations |
| `protobuf-javalite` | Protobuf lite runtime |
| `dagger` 2.x | Dependency injection |
| `dagger-compiler` (kapt) | Dagger code generation |
| `kotlinx-coroutines-android` | Coroutines |

## Quickstep Status

**Excluded from first APK build.** Quickstep (`quickstep/src/`) requires:
- Platform-private system APIs (window management, recents task stack)
- `android.app.ITaskStackListener` AIDL
- ROM-level privileges for recents overlay

The `src_no_quickstep/` overrides provide stub implementations that allow
the core launcher to compile and run without Quickstep.

Quickstep is preserved in the repository for future integration as:
- A separate product flavor (`assembleWithQuickstepDebug`)
- Or a ROM system build via `m ElyraLauncherQuickStep`

## ROM-Only Isolation Strategy

The following are preserved but excluded from the Gradle APK build:
- `Android.bp` and all `.bp` files (AOSP build system)
- `quickstep/` source and resources
- `go/` (Go edition launcher)
- `aconfig/` (ROM-side feature flag declarations)
- Privileged permissions (`BIND_APPWIDGET`, `READ_DEVICE_CONFIG`, etc.)

## Gradle-Only Compatibility Layer

Hidden APIs and aconfig-generated classes are stubbed in `app/src/gradleCompat/java/`:
- `com.android.launcher3.Flags` — all flags return `false`
- `com.android.window.flags.Flags` — all flags return `false`
- `com.android.systemui.shared.Flags` — all flags return `false`
- `com.android.systemui.shared.system.SysUiStatsLog` — int constants only

Plugin base interfaces added to `src_plugins/`:
- `com.android.systemui.plugins.Plugin` — marker interface
- `com.android.systemui.plugins.PluginListener` — callback interface
- `com.android.systemui.plugins.annotations.ProvidesInterface` — annotation

## Success Criteria

The build is successful when:
1. `./gradlew assembleDebug` exits with code 0
2. An APK exists at `app/build/outputs/apk/debug/app-debug.apk`
3. The APK can be installed on an Android 12+ device
4. The launcher appears in the "Set default launcher" dialog

## What Is Not In Scope (First Milestone)

- Release signing
- Quickstep/Recents system integration
- ROM build (`m ElyraLauncherQuickStep`)
- GitHub Actions CI
- Privileged system behavior (widget binding, recents overlay)
