## Summary

Rebuild ElyraLauncher repository infrastructure around the real ROM-first Launcher3 and Quickstep architecture. This branch removes misleading old preview build paths, rewrites project documentation, adds maintainer documentation, adds GitHub collaboration templates, and replaces old workflows with honest architecture and smoke validation.

## What changed

- Added a maintenance audit before cleanup.
- Removed preview-only/private APK infrastructure that could be mistaken for real launcher validation.
- Rebuilt root documentation for the ROM-first project model.
- Rebuilt `docs/` with architecture, ROM build, development, testing, workflow, release, smoke APK, brand, and maintainer guides.
- Added issue forms, PR template, labeler config, release config, funding placeholder, and Renovate policy.
- Added GitHub Actions for architecture checks, docs checks, optional Gradle smoke validation, issue maintenance, and release notes.

## What was removed

- Old private/preview APK workflows.
- The experimental Gradle packaging module that attempted to package Launcher3/Quickstep outside the ROM build.
- Stale private APK documentation and scripts.
- Duplicate planning documents replaced by focused maintainer guides.

## What was preserved

- Launcher3 architecture under `src/`.
- Quickstep and Recents under `quickstep/`.
- Taskbar, BubbleBar, Overview, Workspace, Hotseat, Folder, All Apps, shared sources, protos, tests, and resources.
- `Android.bp`.
- `ElyraLauncherQuickStep` as the ROM/system target.
- Package `com.android.launcher3`.

## Documentation added

- `CONTRIBUTING.md`
- `SECURITY.md`
- `CODE_OF_CONDUCT.md`
- `CHANGELOG.md`
- `docs/ARCHITECTURE.md`
- `docs/ROM_BUILD.md`
- `docs/DEVELOPMENT.md`
- `docs/TESTING.md`
- `docs/GITHUB_WORKFLOWS.md`
- `docs/RELEASE_PROCESS.md`
- `docs/STANDALONE_SMOKE_TEST.md`
- `docs/BRAND_INSPIRATION.md`
- `docs/MAINTAINER_GUIDE.md`

## GitHub infrastructure added

- Bug report and feature request issue forms.
- Pull request template with Launcher3/Quickstep and ROM checks.
- Labeler and release configuration.
- Funding placeholder.
- Conservative Renovate policy.
- CI, smoke APK, issue maintenance, stale issue, and release note workflows.

## Validation performed

- Verified required Launcher3/Quickstep paths and `Android.bp`.
- Verified `ElyraLauncherQuickStep` references in Soong and docs.
- Verified old preview and misleading wording scans return no matches.
- Verified required root docs and `docs/` files exist.
- Verified required `.github` templates and workflows exist.
- Ran `./gradlew --version`.
- Ran `./gradlew tasks`.

## Known limitations

- `m ElyraLauncherQuickStep` was not run because this checkout is not an Android ROM tree.
- Local YAML parser validation was not available because Ruby is not installed and PyYAML is not installed. Workflow files were checked by structure and review.
- Standalone APK artifacts remain smoke-test-only and do not validate real Recents, Quickstep, privileged launcher behavior, hidden APIs, or system task management.

## ROM build note

Full launcher validation must be performed inside a configured Android source tree with:

```bash
m ElyraLauncherQuickStep
```
