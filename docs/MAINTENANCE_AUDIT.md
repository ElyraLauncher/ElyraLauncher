# Repository Maintenance Audit

## Scope

This audit records the repository state before rebuilding documentation,
workflow, and collaboration infrastructure. It is limited to infrastructure and
documentation cleanup. Launcher3, Quickstep, Recents, Taskbar, BubbleBar, and
Soong ROM integration must remain intact.

## Must Be Removed

- Private preview workflow language and artifacts that can be mistaken for a
  project release path.
- The experimental `launcher-private` Gradle module, which attempts to package
  Launcher3 and Quickstep source as a private APK outside the ROM build.
- GitHub workflows with overlapping APK preview purpose:
  `.github/workflows/private-launcher3-apk.yml` and the old private preview APK
  workflow.
- Documentation that presents private owner APKs as project infrastructure.
- Scripts that only validate deleted private preview paths.

## Must Be Preserved

- `Android.bp` and the `ElyraLauncherQuickStep` ROM/system target.
- The `com.android.launcher3` package name.
- Launcher3 core source under `src/`, including `LauncherModel`,
  `LauncherProvider`, `IconCache`, `DeviceProfile`, Workspace, Hotseat, Folder,
  and All Apps behavior.
- Quickstep and Recents source under `quickstep/`, including
  `QuickstepLauncher`, `RecentsView`, `TaskView`, `TouchInteractionService`,
  `OverviewCommandHelper`, and `LauncherActivityInterface`.
- Taskbar, BubbleBar, shared sources, protos, resources, and tests.
- The optional `app/` Gradle smoke module only if it remains clearly separated
  from ROM/system validation.

## Must Be Rewritten

- Root project documentation must describe ElyraLauncher as a ROM-first
  Launcher3 and Quickstep project.
- Standalone APK documentation must use "smoke APK" wording and must not claim
  real Recents, Quickstep, privileged launcher, or hidden API validation.
- GitHub Actions documentation must match the new workflow names and their
  limited responsibilities.
- Contribution, security, changelog, and maintainer documentation must be added
  without invented releases or contact addresses.

## High-Risk Areas

- Any Gradle module that imports `src/` and `quickstep/src/` can accidentally
  imply that a non-privileged APK validates Quickstep. Such modules should be
  removed unless they are explicitly ROM-integrated.
- Broad text searches for "preview" produce many legitimate Launcher3 widget and
  resource references. Cleanup must target project infrastructure wording, not
  platform feature names.
- Removing `launcher-private` requires updating `settings.gradle.kts` and any
  workflow or script references to avoid broken Gradle includes.
- CI must guard architecture without deleting or stubbing hidden platform
  integration code.

## Validation Strategy

- Use source and path checks to confirm ROM architecture is still present:
  `Android.bp`, `src/`, `quickstep/`, `res/`, `AndroidManifest.xml`, and
  `AndroidManifest-common.xml`.
- Check that `Android.bp` and docs continue to mention
  `ElyraLauncherQuickStep`.
- Check that private preview workflows and misleading private APK references are
  removed.
- Run Gradle smoke discovery only when Gradle files are present. Treat Gradle as
  a smoke path, not as Recents or Quickstep validation.
- Do not run `m ElyraLauncherQuickStep` outside an Android ROM tree. Document it
  as the required ROM validation command.
