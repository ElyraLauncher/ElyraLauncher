# GitHub Workflows

## `ci.yml`

Runs repository architecture guards, source hygiene checks, documentation checks, and Gradle APK build validation. It protects the Launcher3 and Quickstep tree but does not validate ROM-only behavior.

## `build_apk.yml`

Builds a real installable launcher APK via `./gradlew assembleDebug`. The APK can be installed and selected as the Home launcher. Artifacts are uploaded as `ElyraLauncher-debug-apk`.

## `close_low_effort_issues.yml`

Marks issues that lack required diagnostic detail and asks for more information. It should be polite and conservative.

## `close_stale_issues.yml`

Marks inactive issues as stale and closes them after additional inactivity, while exempting important labels such as security, regression, Quickstep, ROM build, and maintainer-pinned items.

## `release_update.yml`

Prepares or updates release notes for tags. Release notes must include the ROM build note and Gradle APK build limitations.
