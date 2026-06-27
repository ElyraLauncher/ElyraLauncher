#!/usr/bin/env bash
set -euo pipefail

echo "==> Bootstrapping ElyraLauncher clean repository..."

mkdir -p \
  docs \
  .github/workflows \
  src \
  res \
  quickstep \
  systemUI \
  wmshell \
  hidden-api \
  protos \
  tools \
  flags \
  tests

cat > README.md <<'EOF_README'
# ElyraLauncher

ElyraLauncher is the official launcher project for ElyraOS.

It is planned as a Lawnchair-style launcher ecosystem based on AOSP Launcher3 with Quickstep support.

## Goals

- Preserve Launcher3 architecture
- Preserve Quickstep and Recents integration
- Support Android ROM/system builds
- Provide a clean standalone APK path only for UI testing
- Build a professional ElyraLauncher ecosystem with icons, feed, docs, and website repositories

## Main ROM Target

```bash
m ElyraLauncherQuickStep
```

## Important Note

Standalone APK builds cannot provide real Android Recents or Quickstep integration. Real Quickstep support requires ROM/system integration.

## Planned Companion Repositories

- platform_frameworks_libs_systemui
- packages_apps_ElyraIcons
- packages_apps_ElyraFeed
- ElyraLauncherDocs
- ElyraLauncherWebsite
EOF_README

cat > AGENTS.md <<'EOF_AGENTS'
# ElyraLauncher Agent Rules

ElyraLauncher is a Lawnchair-style Android launcher ecosystem project based on AOSP Launcher3 with Quickstep support.

## Project Goal

Create a clean, professional Launcher3/Quickstep-based launcher for ElyraOS.

This is not a fake launcher rewrite.
This is not a simple Compose-only launcher.
This project must preserve Launcher3 and Quickstep architecture.

## Main ROM Target

ElyraLauncherQuickStep

Expected ROM build command:

```bash
m ElyraLauncherQuickStep
```

## Architecture Rules

- ROM/System build is the source of truth.
- Android.bp is required for ROM integration.
- Gradle standalone APK is only for UI testing and GitHub Actions.
- Standalone APK must not claim to support real Recents or Quickstep.
- Real Recents/Quickstep must be validated inside an Android ROM tree.

## Must Preserve

Do not remove:

- Launcher3 core
- Quickstep core
- RecentsView
- TaskView
- TouchInteractionService
- QuickstepLauncher
- LauncherModel
- LauncherProvider
- IconCache
- DeviceProfile
- OverviewCommandHelper
- LauncherActivityInterface
- Taskbar
- BubbleBar

## First Phase Restrictions

Do not add these in the first phase:

- ElyraKIT
- native module
- benchmark module
- samples
- website source
- docs website source
- ElyraFeed source
- ElyraIcons source
- complex Compose feature layer

Those will be created later as separate repos or separate clean commits.

## Planned Companion Repositories

- platform_frameworks_libs_systemui
- packages_apps_ElyraIcons
- packages_apps_ElyraFeed
- ElyraLauncherDocs
- ElyraLauncherWebsite

## Commit Style

Use clean commits:

- rebase(launcher3): import clean Android 16 Launcher3 base
- build(soong): add ElyraLauncherQuickStep target
- brand: apply ElyraLauncher identity
- docs: define ElyraLauncher ecosystem architecture
- ci: add initial validation workflow
EOF_AGENTS

cat > Android.bp <<'EOF_ANDROID_BP'
// ElyraLauncher Soong build entry.
//
// Real Launcher3/Quickstep source must be imported before this target can be
// built inside an Android ROM tree.
//
// Target ROM build command:
//
//     m ElyraLauncherQuickStep
//
// This file intentionally documents the final ROM target early so the project
// direction stays clear from the first commits.

package {
    default_applicable_licenses: ["ElyraLauncher_license"],
}

license {
    name: "ElyraLauncher_license",
    visibility: [":__subpackages__"],
    license_kinds: [
        "SPDX-license-identifier-Apache-2.0",
    ],
}

// TODO: Convert this placeholder into a real android_app target after importing
// AOSP Launcher3/Quickstep source.
//
// Expected final target:
//
// android_app {
//     name: "ElyraLauncherQuickStep",
//     srcs: [
//         "src/**/*.java",
//         "src/**/*.kt",
//         "quickstep/**/*.java",
//         "quickstep/**/*.kt",
//     ],
//     manifest: "AndroidManifest.xml",
//     platform_apis: true,
//     privileged: true,
//     certificate: "platform",
//     overrides: [
//         "Home",
//         "Launcher3",
//         "Launcher3QuickStep",
//     ],
//     system_ext_specific: true,
// }
EOF_ANDROID_BP

cat > AndroidManifest.xml <<'EOF_MANIFEST_MAIN'
<!--
    ElyraLauncher main manifest placeholder.

    The real manifest must be imported from AOSP Launcher3/Quickstep and adapted
    carefully for ElyraOS ROM integration.
-->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
EOF_MANIFEST_MAIN

cat > AndroidManifest-common.xml <<'EOF_MANIFEST_COMMON'
<!-- Common Launcher3 manifest placeholder. -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
EOF_MANIFEST_COMMON

cat > AndroidManifest-launcher3.xml <<'EOF_MANIFEST_LAUNCHER3'
<!-- Launcher3 manifest placeholder. -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
EOF_MANIFEST_LAUNCHER3

cat > AndroidManifest-quickstep.xml <<'EOF_MANIFEST_QUICKSTEP'
<!-- Quickstep manifest placeholder. Real Quickstep requires ROM/system integration. -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
EOF_MANIFEST_QUICKSTEP

cat > CleanSpec.mk <<'EOF_CLEANSPEC'
# ElyraLauncher clean spec placeholder.
EOF_CLEANSPEC

cat > proguard.flags <<'EOF_PROGUARD'
# ElyraLauncher common ProGuard flags placeholder.
EOF_PROGUARD

cat > proguard-launcher3.pro <<'EOF_PROGUARD_LAUNCHER3'
# Launcher3/Quickstep ProGuard flags placeholder.
EOF_PROGUARD_LAUNCHER3

cat > ROADMAP.md <<'EOF_ROADMAP'
# ElyraLauncher Roadmap

## Phase 1 — Clean Launcher3 Base

- Import clean AOSP Launcher3 Android 16 source
- Preserve Quickstep and Recents architecture
- Add ElyraLauncherQuickStep Soong target
- Keep commit history clean

## Phase 2 — Elyra Branding

- ElyraLauncher app name
- ElyraOS strings
- Elyra launcher icon
- Basic theme naming

## Phase 3 — Standalone Validation APK

- Add Gradle build only for UI testing
- Do not claim real Recents/Quickstep support
- Upload debug APK through GitHub Actions

## Phase 4 — Elyra Features

- Settings shell
- Onboarding shell
- Drawer customization
- Search customization
- Theme options

## Phase 5 — Companion Repositories

- platform_frameworks_libs_systemui
- packages_apps_ElyraIcons
- packages_apps_ElyraFeed
- ElyraLauncherDocs
- ElyraLauncherWebsite
EOF_ROADMAP

cat > VERIFICATION.md <<'EOF_VERIFICATION'
# Verification

## ROM Verification

Real Launcher3/Quickstep verification must be done inside an Android ROM tree:

```bash
m ElyraLauncherQuickStep
```

## Standalone Verification

Standalone APK builds are only for UI smoke testing. They cannot validate real Recents or Quickstep.

## Current Status

This repository currently contains the clean project foundation. Real AOSP Launcher3/Quickstep source still needs to be imported.
EOF_VERIFICATION

cat > docs/MIGRATION_STATUS.md <<'EOF_MIGRATION'
# Migration Status

ElyraLauncher is being restarted as a clean Launcher3/Quickstep-based project.

## Current State

- Repository initialized
- Project rules documented
- ROM target name defined
- Source layout prepared
- Real Launcher3/Quickstep source not imported yet

## Target State

- AOSP Launcher3 Android 16 base
- Quickstep preserved
- Recents/Overview preserved
- ElyraLauncherQuickStep ROM target
- Optional standalone APK for UI testing only

## Important Limitation

Standalone APK builds cannot provide real Android Recents or Quickstep integration. Real Quickstep requires privileged ROM/system integration.
EOF_MIGRATION

cat > docs/ARCHITECTURE.md <<'EOF_ARCHITECTURE'
# ElyraLauncher Architecture

## Main Repository

This repository contains the main ElyraLauncher source.

It is planned to be based on AOSP Launcher3 with Quickstep support.

## Build Modes

### ROM/System Build

The ROM build is the source of truth.

```bash
m ElyraLauncherQuickStep
```

### Standalone APK Build

Standalone APK is only for UI testing and CI artifacts.

It must not include real Quickstep runtime integration or hidden platform APIs.

## Source Areas

- `src/` — Launcher3 core source
- `res/` — Launcher resources
- `quickstep/` — Quickstep and Recents integration
- `systemUI/` — SystemUI compatibility area
- `wmshell/` — WM Shell compatibility area
- `hidden-api/` — compile-time hidden API handling area
- `protos/` — protocol buffers
- `tools/` — developer tools
- `flags/` — feature flags
- `tests/` — tests
EOF_ARCHITECTURE

cat > docs/COMPANION_REPOS.md <<'EOF_COMPANION'
# Companion Repositories

ElyraLauncher is planned as an ecosystem.

## Planned Repositories

### platform_frameworks_libs_systemui

Shared SystemUI and Quickstep compatibility code.

### packages_apps_ElyraIcons

Elyra icon pack and monochrome/adaptive icon resources.

### packages_apps_ElyraFeed

Optional feed provider integration for ElyraLauncher.

### ElyraLauncherDocs

Full documentation website source or documentation content.

### ElyraLauncherWebsite

Landing page, download page, screenshots, and release notes.

## Rule

Do not mix all companion repository code into the main launcher repository.
EOF_COMPANION

cat > docs/CHERRY_PICK_PLAN.md <<'EOF_CHERRY_PICK'
# Cherry-pick Plan

Old experimental source should not be copied all at once.

## Phase A — Safe Identity Changes

- App name
- Strings
- Launcher icon placeholder
- Basic theme names
- ROM target naming

## Phase B — Launcher UI

- Basic settings entry
- Onboarding shell
- Drawer customization
- Search UI shell

## Phase C — Ecosystem

- ElyraIcons integration
- ElyraFeed integration
- Widget layer

## Phase D — Advanced Modules

- ElyraKIT
- Native optimization
- Benchmark
- SystemUI shared adapters

## Rule

Only cherry-pick code after the clean Launcher3/Quickstep base builds.
EOF_CHERRY_PICK

cat > .github/workflows/build.yml <<'EOF_WORKFLOW'
name: ElyraLauncher Validation

on:
  push:
    branches:
      - main
  pull_request:
  workflow_dispatch:

jobs:
  repository-sanity:
    name: Repository sanity
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Show repository layout
        run: |
          echo "ElyraLauncher repository sanity check"
          find . -maxdepth 3 -type f | sort

      - name: Validate required files
        run: |
          test -f README.md
          test -f AGENTS.md
          test -f Android.bp
          test -f AndroidManifest.xml
          test -f AndroidManifest-common.xml
          test -f AndroidManifest-launcher3.xml
          test -f AndroidManifest-quickstep.xml
          test -f CleanSpec.mk
          test -f proguard.flags
          test -f proguard-launcher3.pro
          test -f ROADMAP.md
          test -f VERIFICATION.md
          test -f docs/MIGRATION_STATUS.md
          test -f docs/ARCHITECTURE.md
          test -f docs/COMPANION_REPOS.md
          test -f docs/CHERRY_PICK_PLAN.md

      - name: Explain current limitation
        run: |
          echo "This repository currently contains the clean ElyraLauncher foundation."
          echo "Full Launcher3/Quickstep validation requires Android ROM tree:"
          echo "m ElyraLauncherQuickStep"
EOF_WORKFLOW

cat > .gitignore <<'EOF_GITIGNORE'
.gradle/
build/
out/
*.apk
*.apks
*.aab
*.keystore
*.jks
local.properties
.DS_Store
.idea/
*.iml
EOF_GITIGNORE

echo "==> Bootstrap complete."
echo
echo "Next commands:"
echo "  git status"
echo "  git add ."
echo "  git commit -m \"build: create initial ElyraLauncher source layout\""
echo "  git push"
