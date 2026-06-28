#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

fail() {
  echo "ERROR: $*" >&2
  exit 1
}

check_path() {
  local path="$1"
  test -e "$path" || fail "Missing required path: $path"
  echo "OK: $path"
}

echo "Auditing experimental private Launcher3 APK path..."

check_path "src"
check_path "quickstep/src"
check_path "res"
check_path "quickstep/res"
check_path "AndroidManifest.xml"
check_path "AndroidManifest-common.xml"
check_path "quickstep/AndroidManifest.xml"
check_path "quickstep/AndroidManifest-launcher.xml"
check_path "Android.bp"
check_path "launcher-private/build.gradle.kts"
check_path "launcher-private/src/main/AndroidManifest.xml"
check_path "launcher-private/quickstep-res/build.gradle.kts"
check_path "launcher-private/launcher3-res/build.gradle.kts"

grep -q 'include(":launcher-private")' settings.gradle.kts \
  || fail "settings.gradle.kts does not include :launcher-private"

grep -q 'include(":launcher-private:quickstep-res")' settings.gradle.kts \
  || fail "settings.gradle.kts does not include :launcher-private:quickstep-res"

grep -q 'include(":launcher-private:launcher3-res")' settings.gradle.kts \
  || fail "settings.gradle.kts does not include :launcher-private:launcher3-res"

grep -q 'name: "ElyraLauncherQuickStep"' Android.bp \
  || fail "Android.bp is missing ElyraLauncherQuickStep"

grep -q 'name: "Launcher3QuickStep"' Android.bp \
  || fail "Android.bp is missing Launcher3QuickStep"

grep -q 'platform_apis: true' Android.bp \
  || fail "Android.bp no longer exposes platform_apis for ROM Quickstep targets"

if [ ! -d platform_frameworks_libs_systemui ]; then
  fail "Missing platform_frameworks_libs_systemui path; initialize submodules for private Launcher3 APK work"
fi

if ! find platform_frameworks_libs_systemui -mindepth 1 -maxdepth 3 -print -quit | grep -q .; then
  echo "WARN: platform_frameworks_libs_systemui exists but is empty; run git submodule update --init --recursive before expecting a full private Launcher3 APK compile."
else
  echo "OK: platform_frameworks_libs_systemui has content"
fi

echo "Audit complete. This does not prove Quickstep/Recents runtime support."
echo "Real validation remains: m ElyraLauncherQuickStep"
