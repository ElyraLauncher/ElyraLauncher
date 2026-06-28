# GitHub Workflows

## `ci.yml`

Runs repository architecture guards, source hygiene checks, documentation checks, and optional Gradle smoke discovery. It protects the Launcher3 and Quickstep tree but does not validate ROM-only behavior.

## `build_release_apk.yml`

Builds a standalone smoke APK when Gradle support is available. Artifacts from this workflow are smoke-test outputs only and are not production launcher releases.

## `close_low_effort_issues.yml`

Marks issues that lack required diagnostic detail and asks for more information. It should be polite and conservative.

## `close_stale_issues.yml`

Marks inactive issues as stale and closes them after additional inactivity, while exempting important labels such as security, regression, Quickstep, ROM build, and maintainer-pinned items.

## `release_update.yml`

Prepares or updates release notes for tags. Release notes must include the ROM build note and standalone smoke APK limitation.
