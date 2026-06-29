# ElyraLauncher Gradle APK Build Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make `./gradlew assembleDebug` produce a real installable launcher APK at `app/build/outputs/apk/debug/app-debug.apk`.

**Architecture:** The app module already compiles real Launcher3 source via `../src` source sets. Three issues block the build: a smoke/preview Java file that fails to compile, the internal MSDL haptics API that has no public Maven release, and a WM Shell internal bubbles type. Fix all three with targeted deletions and minimal Gradle compat stubs, then verify the APK.

**Tech Stack:** Android Gradle Plugin 8.x, Kotlin/Java 17, Dagger 2, Protobuf Lite, AndroidX

## Global Constraints

- No `/root/...` absolute paths in any committed file
- No fake or preview launcher Activities — `StandaloneSmokeActivity` must be deleted, not stubbed
- Gradle compat stubs are compile-and-runtime shims in `app/src/gradleCompat/java/` — they must not crash at runtime (no-op is correct)
- Do not delete or touch Launcher3 core (`src/`, `quickstep/`, `shared/`, etc.)
- Branch: `feat/elyra-launcher3-ux-foundation` → rename to `feat/lawnchair-style-real-apk-build` after passing build
- Commit must be signed-off (`git commit -s`)

---

### Task 1: Delete StandaloneSmokeActivity.java

**Files:**
- Delete: `app/src/main/java/com/android/launcher3/standalone/StandaloneSmokeActivity.java`

**Interfaces:**
- Consumes: nothing from other tasks
- Produces: removes the `package R does not exist` compile errors in subsequent tasks

The file is a fake launcher UI with no Launcher3 code. It references `R.*` resources that do not exist under the app namespace, causing dozens of compile errors. Deleting the file (and its now-empty parent directory) is the only correct action.

- [ ] **Step 1: Delete the smoke file and its directory**

```bash
rm /root/ElyraLauncher/app/src/main/java/com/android/launcher3/standalone/StandaloneSmokeActivity.java
rmdir /root/ElyraLauncher/app/src/main/java/com/android/launcher3/standalone/
rmdir /root/ElyraLauncher/app/src/main/java/com/android/launcher3/ 2>/dev/null || true
rmdir /root/ElyraLauncher/app/src/main/java/com/android/ 2>/dev/null || true
rmdir /root/ElyraLauncher/app/src/main/java/com/ 2>/dev/null || true
rmdir /root/ElyraLauncher/app/src/main/java/ 2>/dev/null || true
```

- [ ] **Step 2: Verify deletion**

```bash
ls /root/ElyraLauncher/app/src/main/java/ 2>/dev/null && echo "NOT EMPTY" || echo "OK: directory gone"
```

Expected: `OK: directory gone`

- [ ] **Step 3: Stage deletion**

```bash
git -C /root/ElyraLauncher rm --cached app/src/main/java/com/android/launcher3/standalone/StandaloneSmokeActivity.java 2>/dev/null || true
git -C /root/ElyraLauncher status --short | grep StandaloneSmokeActivity
```

Expected: line shows `D` (deleted)

---

### Task 2: Add MSDL Gradle Compat Stubs

**Files:**
- Create: `app/src/gradleCompat/java/com/google/android/msdl/data/model/MSDLToken.java`
- Create: `app/src/gradleCompat/java/com/google/android/msdl/domain/InteractionProperties.java`
- Create: `app/src/gradleCompat/java/com/google/android/msdl/domain/MSDLPlayer.java`
- Create: `app/src/gradleCompat/java/com/google/android/msdl/logging/MSDLEvent.java`

**Interfaces:**
- Consumes: nothing from other tasks
- Produces: `MSDLToken`, `InteractionProperties`, `MSDLPlayer`, `MSDLEvent` types for `MSDLPlayerWrapper.java`

`com.google.android.msdl` is an internal AOSP haptics (Mechanical Sensory Design Language) API. It is not available on Maven Central or Google Maven. All usage in Launcher3 goes through `MSDLPlayerWrapper` which is already wrapped — no-op stubs at runtime are correct and safe.

Token constants used by the codebase: `DRAG_INDICATOR_DISCRETE`, `SWIPE_THRESHOLD_INDICATOR`, `TAP_HIGH_EMPHASIS`.

`MSDLPlayer` is a Kotlin class with a companion object; Java source accesses it as `MSDLPlayer.Companion.createPlayer(vibrator, executor, null)`.

- [ ] **Step 1: Create directory tree**

```bash
mkdir -p /root/ElyraLauncher/app/src/gradleCompat/java/com/google/android/msdl/data/model
mkdir -p /root/ElyraLauncher/app/src/gradleCompat/java/com/google/android/msdl/domain
mkdir -p /root/ElyraLauncher/app/src/gradleCompat/java/com/google/android/msdl/logging
```

- [ ] **Step 2: Write MSDLToken.java**

Create `app/src/gradleCompat/java/com/google/android/msdl/data/model/MSDLToken.java`:

```java
// Gradle-only compat stub for the internal AOSP MSDL haptics API.
// This shim exists solely to satisfy the compiler outside a full Android tree.
// No runtime haptic feedback is provided by this stub.
package com.google.android.msdl.data.model;

public enum MSDLToken {
    DRAG_INDICATOR_DISCRETE,
    SWIPE_THRESHOLD_INDICATOR,
    TAP_HIGH_EMPHASIS;
}
```

- [ ] **Step 3: Write InteractionProperties.java**

Create `app/src/gradleCompat/java/com/google/android/msdl/domain/InteractionProperties.java`:

```java
// Gradle-only compat stub for the internal AOSP MSDL haptics API.
package com.google.android.msdl.domain;

public class InteractionProperties {
}
```

- [ ] **Step 4: Write MSDLEvent.java**

Create `app/src/gradleCompat/java/com/google/android/msdl/logging/MSDLEvent.java`:

```java
// Gradle-only compat stub for the internal AOSP MSDL haptics API.
package com.google.android.msdl.logging;

public interface MSDLEvent {
}
```

- [ ] **Step 5: Write MSDLPlayer.java**

Create `app/src/gradleCompat/java/com/google/android/msdl/domain/MSDLPlayer.java`:

```java
// Gradle-only compat stub for the internal AOSP MSDL haptics API.
// MSDLPlayer is a Kotlin class; its companion object is accessed from Java as
// MSDLPlayer.Companion.createPlayer(...). This stub mirrors that structure.
package com.google.android.msdl.domain;

import android.os.Vibrator;
import com.google.android.msdl.data.model.MSDLToken;
import com.google.android.msdl.logging.MSDLEvent;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class MSDLPlayer {

    public static final Companion Companion = new Companion();

    public abstract void playToken(MSDLToken token, InteractionProperties properties);
    public abstract List<MSDLEvent> getHistory();

    public static class Companion {
        public MSDLPlayer createPlayer(
                Vibrator vibrator,
                Executor executor,
                Object useHapticFeedbackForToken) {
            return new MSDLPlayer() {
                @Override
                public void playToken(MSDLToken token, InteractionProperties properties) {}
                @Override
                public List<MSDLEvent> getHistory() {
                    return Collections.emptyList();
                }
                @Override
                public String toString() {
                    return "MSDLPlayer[stub]";
                }
            };
        }
    }
}
```

- [ ] **Step 6: Verify files were created**

```bash
find /root/ElyraLauncher/app/src/gradleCompat/java/com/google -type f | sort
```

Expected output (4 files):
```
.../msdl/data/model/MSDLToken.java
.../msdl/domain/InteractionProperties.java
.../msdl/domain/MSDLPlayer.java
.../msdl/logging/MSDLEvent.java
```

---

### Task 3: Add WM Shell Bubbles Gradle Compat Stub

**Files:**
- Create: `app/src/gradleCompat/java/com/android/wm/shell/shared/bubbles/BubbleAnythingFlagHelper.java`

**Interfaces:**
- Consumes: nothing from other tasks
- Produces: `BubbleAnythingFlagHelper.enableCreateAnyBubble()` for `WorkspaceItemInfo.java:182`

`com.android.wm.shell.shared.bubbles.BubbleAnythingFlagHelper` is an internal WM Shell flag helper. It has one static method: `enableCreateAnyBubble()` which returns `boolean`. Returning `false` is safe — Launcher3 uses it in an `if` guard that opts into bubble creation support; returning `false` disables that path without breaking the launcher.

No prebuilt JAR is available locally under `prebuilts/libs/`, so a compat stub is the correct strategy.

- [ ] **Step 1: Create directory**

```bash
mkdir -p /root/ElyraLauncher/app/src/gradleCompat/java/com/android/wm/shell/shared/bubbles
```

- [ ] **Step 2: Write BubbleAnythingFlagHelper.java**

Create `app/src/gradleCompat/java/com/android/wm/shell/shared/bubbles/BubbleAnythingFlagHelper.java`:

```java
// Gradle-only compat stub for the internal WM Shell bubbles flag API.
// enableCreateAnyBubble() returns false so the bubble-creation code path is
// disabled in Gradle APK builds; this does not affect normal launcher function.
package com.android.wm.shell.shared.bubbles;

public final class BubbleAnythingFlagHelper {
    private BubbleAnythingFlagHelper() {}

    public static boolean enableCreateAnyBubble() {
        return false;
    }
}
```

- [ ] **Step 3: Verify file was created**

```bash
cat /root/ElyraLauncher/app/src/gradleCompat/java/com/android/wm/shell/shared/bubbles/BubbleAnythingFlagHelper.java
```

Expected: shows the stub content without error.

---

### Task 4: Build Loop — Run assembleDebug

**Files:**
- None created; build artifacts appear under `app/build/outputs/apk/`

**Interfaces:**
- Consumes: all stubs and deletions from Tasks 1-3
- Produces: `app/build/outputs/apk/debug/app-debug.apk` on success

- [ ] **Step 1: Run first build**

```bash
cd /root/ElyraLauncher && ./gradlew assembleDebug --no-daemon --stacktrace 2>&1 | tee /tmp/build1.log | tail -20
```

Expected: `BUILD SUCCESSFUL`

If `BUILD FAILED`, proceed to Step 2. If successful, skip to Step 4.

- [ ] **Step 2: Triage new errors (only if Step 1 failed)**

```bash
grep -E "error: package|error: cannot find|error: class" /tmp/build1.log | grep -v "^Note:" | head -30
```

Classify each new error:
- Missing AOSP/internal package → add stub to `app/src/gradleCompat/java/`
- Missing public AndroidX/Google class → add Maven dependency in `app/build.gradle.kts`
- Missing Elyra SystemUI source → add module from `external/ElyraSystemUILibs`
- Fix exactly the first distinct error, then re-run Step 1.

- [ ] **Step 3: Repeat Step 1 → Step 2 until BUILD SUCCESSFUL**

Repeat the build-triage cycle, fixing one error category per iteration. Do not guess version numbers — look them up in `gradle.properties` or `gradle/libs.versions.toml` if they exist.

- [ ] **Step 4: Verify APK output**

```bash
find /root/ElyraLauncher -path "*/build/outputs/apk/*.apk" -type f
```

Expected: `./app/build/outputs/apk/debug/app-debug.apk`

---

### Task 5: Path Audit and Commit

**Files:**
- Modified: `app/src/gradleCompat/java/` (new stubs)
- Deleted: `app/src/main/java/com/android/launcher3/standalone/StandaloneSmokeActivity.java`

**Interfaces:**
- Consumes: passing build from Task 4
- Produces: a signed-off git commit; branch renamed to `feat/lawnchair-style-real-apk-build`; pushed to remote

- [ ] **Step 1: Verify no absolute /root paths**

```bash
grep -R "/root/ElyraSystemUILibs" \
  /root/ElyraLauncher/app \
  /root/ElyraLauncher/build.gradle.kts \
  /root/ElyraLauncher/settings.gradle.kts \
  /root/ElyraLauncher/external 2>/dev/null \
  /root/ElyraLauncher/prebuilts 2>/dev/null \
  /root/ElyraLauncher/.github 2>/dev/null || echo "OK: no /root paths found"
```

Expected: `OK: no /root paths found`

If any `/root/` paths are found, fix them to use `rootProject.projectDir.resolveSibling("ElyraSystemUILibs")` before committing.

- [ ] **Step 2: Stage all changes**

```bash
git -C /root/ElyraLauncher add \
  app/src/gradleCompat/java/com/google/android/msdl/ \
  app/src/gradleCompat/java/com/android/wm/shell/ \
  docs/superpowers/
git -C /root/ElyraLauncher rm --cached \
  "app/src/main/java/com/android/launcher3/standalone/StandaloneSmokeActivity.java" 2>/dev/null || true
git -C /root/ElyraLauncher status --short
```

- [ ] **Step 3: Commit**

```bash
git -C /root/ElyraLauncher commit -s -m "$(cat <<'EOF'
build: add Lawnchair-style real APK build

- StandaloneSmokeActivity removed; it was a smoke/preview file with no
  real Launcher3 code and blocked compilation via unresolvable R refs
- Add Gradle compat stubs for com.google.android.msdl (MSDL haptics,
  internal AOSP API with no public Maven release)
- Add Gradle compat stub for com.android.wm.shell.shared.bubbles
  (BubbleAnythingFlagHelper, returns false to disable bubble path)
- All stubs are no-op runtime shims in app/src/gradleCompat/java/
- No /root absolute paths introduced
- ./gradlew assembleDebug now produces app/build/outputs/apk/debug/app-debug.apk
- ROM/ElyraOS integration and Quickstep variant remain future work
EOF
)"
```

- [ ] **Step 4: Rename branch**

```bash
git -C /root/ElyraLauncher branch -m feat/lawnchair-style-real-apk-build
git -C /root/ElyraLauncher branch --show-current
```

Expected: `feat/lawnchair-style-real-apk-build`

- [ ] **Step 5: Push**

```bash
git -C /root/ElyraLauncher push -u origin feat/lawnchair-style-real-apk-build
```

Expected: remote tracking branch set and push confirmed.
