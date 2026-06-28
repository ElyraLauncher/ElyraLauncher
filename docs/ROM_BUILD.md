# ROM Build

## Android Tree Placement

Place ElyraLauncher in the Android source tree where the ROM product expects launcher packages. The exact path may vary by tree layout, but the Soong package must expose the launcher target from this repository.

## Module Target

The main ROM/system target is:

```text
ElyraLauncherQuickStep
```

## Build Command

From a configured Android build environment, run:

```bash
m ElyraLauncherQuickStep
```

Do not run this command from a standalone repository checkout that has not sourced Android build environment setup.

## Why ROM Build Is Required

Quickstep and Recents are tied to platform APIs, privileged permissions, task management, SystemUI relationships, and framework behavior. A normal APK build cannot prove those paths work. The ROM build is the only authoritative validation path for real launcher integration.
