# ElyraLauncher Roadmap

## Current Phase

The current phase is repository professionalization around the existing Launcher3 and Quickstep source tree. The goal is to make documentation, GitHub workflows, and collaboration rules match the ROM-first project model.

## Short-Term Priorities

- Keep `ElyraLauncherQuickStep` building as the ROM/system target.
- Preserve Launcher3 core, Quickstep, Recents, Taskbar, and BubbleBar architecture.
- Keep standalone Gradle work limited to smoke testing.
- Add honest CI guards for architecture and documentation.
- Establish clear issue, pull request, release, and maintainer processes.

## Medium-Term Priorities

- Apply ElyraLauncher branding through safe resources and configuration.
- Add small Elyra-specific UX improvements without breaking Launcher3 contracts.
- Improve ROM-tree validation notes for supported ElyraOS devices.
- Build integration points for ElyraSystemUILibs and ElyraIcons as separate, reviewed changes.

## Long-Term Priorities

- Maintain ElyraLauncher as the default ElyraOS launcher.
- Keep Quickstep behavior aligned with platform changes.
- Add companion repositories for icons, feed, documentation site, and website work when the launcher base is stable.
- Expand automated checks without hiding platform integration failures.

## Non-Goals

- Replacing Launcher3 with a Compose-only launcher.
- Faking Recents, Overview, or Quickstep behavior in a standalone APK.
- Renaming `com.android.launcher3` without a dedicated migration plan.
- Shipping platform stubs inside release APKs.
- Claiming production readiness before ROM validation is complete.
