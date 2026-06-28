#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ANDROID_BP="${ROOT_DIR}/Android.bp"
SETTINGS_GRADLE="${ROOT_DIR}/settings.gradle.kts"
APP_GRADLE="${ROOT_DIR}/app/build.gradle.kts"

fail() {
    printf "FAIL: %s\n" "$1" >&2
    exit 1
}

pass() {
    printf "OK: %s\n" "$1"
}

contains() {
    local pattern="$1"
    local file="$2"
    grep -Eq "$pattern" "$file"
}

extract_module_block() {
    local module_name="$1"
    awk -v module="$module_name" '
        function brace_delta(line, tmp, opens, closes) {
            tmp = line
            opens = gsub(/\{/, "", tmp)
            tmp = line
            closes = gsub(/\}/, "", tmp)
            return opens - closes
        }
        $0 ~ /^[[:space:]]*android_app[[:space:]]*\{/ {
            in_block = 1
            depth = brace_delta($0)
            block = $0 ORS
            next
        }
        in_block {
            block = block $0 ORS
            depth += brace_delta($0)
            if (depth == 0) {
                if (block ~ "name:[[:space:]]*\"" module "\"") {
                    printf "%s", block
                    found = 1
                    exit
                }
                in_block = 0
                block = ""
            }
        }
        END {
            if (!found) {
                exit 1
            }
        }
    ' "$ANDROID_BP"
}

[[ -f "$ANDROID_BP" ]] || fail "Android.bp is missing from the repository root"
pass "Android.bp exists"

contains "name:[[:space:]]*\"Launcher3QuickStep\"" "$ANDROID_BP" \
    || fail "Launcher3QuickStep target is missing from Android.bp"
pass "Launcher3QuickStep target exists"

contains "name:[[:space:]]*\"ElyraLauncherQuickStep\"" "$ANDROID_BP" \
    || fail "ElyraLauncherQuickStep target is missing from Android.bp"
pass "ElyraLauncherQuickStep target exists"

elyra_block="$(extract_module_block "ElyraLauncherQuickStep")" \
    || fail "Could not parse ElyraLauncherQuickStep android_app block"

printf "%s" "$elyra_block" | grep -Eq "static_libs:[[:space:]]*\[[^]]*\"Launcher3QuickStepLib\"" \
    || fail "ElyraLauncherQuickStep no longer reuses Launcher3QuickStepLib"
pass "ElyraLauncherQuickStep reuses Launcher3QuickStepLib"

printf "%s" "$elyra_block" | grep -Eq "platform_apis:[[:space:]]*true" \
    || fail "ElyraLauncherQuickStep must keep platform_apis: true for ROM integration"
pass "ElyraLauncherQuickStep keeps platform_apis: true"

printf "%s" "$elyra_block" | grep -Eq "privileged:[[:space:]]*true" \
    || fail "ElyraLauncherQuickStep must remain privileged for ROM integration"
pass "ElyraLauncherQuickStep remains privileged"

printf "%s" "$elyra_block" | grep -Eq "system_ext_specific:[[:space:]]*true" \
    || fail "ElyraLauncherQuickStep must remain system_ext_specific"
pass "ElyraLauncherQuickStep remains system_ext_specific"

if printf "%s" "$elyra_block" | grep -Eq "overrides:[[:space:]]*\["; then
    printf "%s" "$elyra_block" | grep -Eq "\"Launcher3QuickStep\"" \
        || fail "ElyraLauncherQuickStep overrides section exists but does not include Launcher3QuickStep"
    pass "ElyraLauncherQuickStep override section includes Launcher3QuickStep"
else
    fail "ElyraLauncherQuickStep override section is missing"
fi

printf "%s" "$elyra_block" | grep -Eq "\"quickstep/AndroidManifest-launcher.xml\"" \
    || fail "ElyraLauncherQuickStep must keep the Quickstep launcher manifest"
pass "ElyraLauncherQuickStep keeps Quickstep launcher manifest"

printf "%s" "$elyra_block" | grep -Eq "manifest:[[:space:]]*\"quickstep/AndroidManifest.xml\"" \
    || fail "ElyraLauncherQuickStep must keep quickstep/AndroidManifest.xml"
pass "ElyraLauncherQuickStep keeps Quickstep manifest"

if [[ -f "$SETTINGS_GRADLE" ]]; then
    contains "include\(\":app\"\)" "$SETTINGS_GRADLE" \
        || fail "Gradle standalone app module is missing from settings.gradle.kts"
    pass "Gradle standalone app is present as :app"
fi

if [[ -f "$APP_GRADLE" ]]; then
    contains "namespace[[:space:]]*=[[:space:]]*\"com\.android\.launcher3\.standalone\"" "$APP_GRADLE" \
        || fail "Standalone Gradle app namespace should remain separate from com.android.launcher3"
    contains "applicationId[[:space:]]*=[[:space:]]*\"com\.android\.launcher3\.standalone\"" "$APP_GRADLE" \
        || fail "Standalone Gradle app applicationId should remain separate from the ROM package"
    if grep -Eq "ElyraLauncherQuickStep|Launcher3QuickStep" "$APP_GRADLE"; then
        fail "Standalone Gradle app should not define or depend on ROM Quickstep targets"
    fi
    pass "Standalone Gradle app is separate from ROM Quickstep target"
fi

pass "Source-tree ROM target sanity check completed"
