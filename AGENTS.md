# ElyraLauncher Agent Rules

ElyraLauncher is a ROM-first Launcher3 and Quickstep launcher for ElyraOS. Agents must preserve the AOSP Launcher3 architecture and keep `ElyraLauncherQuickStep` as the main ROM/system target.

## Protected Architecture

Do not remove or replace:

- `Android.bp`
- `ElyraLauncherQuickStep`
- package `com.android.launcher3`
- Launcher3 core under `src/`
- Quickstep and Recents under `quickstep/`
- `LauncherModel`, `LauncherProvider`, `IconCache`, `DeviceProfile`
- Workspace, Hotseat, Folder, All Apps
- `QuickstepLauncher`, `RecentsView`, `TaskView`, `TouchInteractionService`
- `OverviewCommandHelper`, `LauncherActivityInterface`
- Taskbar and BubbleBar integration

## Forbidden Directions

- Do not replace Launcher3 with a Compose-only UI.
- Do not create fake Recents or fake Quickstep behavior.
- Do not delete platform code to make Gradle pass.
- Do not package hidden API stubs into release APKs.
- Do not claim a standalone smoke APK validates real Recents or Quickstep.
- Do not add unrelated companion projects in this repository phase.

## Testing Expectations

Run the checks relevant to the files changed. Documentation and workflow changes should at least verify required files, protected paths, and forbidden wording. Source changes should include Gradle or ROM-tree validation when available.

Do not run `m ElyraLauncherQuickStep` unless the workspace is inside a configured Android source tree. When ROM validation is required but unavailable, state that limitation clearly.

## Final Response Expectations

Report the branch, commits, validation commands, skipped commands with reasons, known limitations, and the exact next Git commands. Keep the response factual and do not overstate the standalone APK path.
