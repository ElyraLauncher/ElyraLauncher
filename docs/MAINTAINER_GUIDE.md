# Maintainer Guide

## Review Process

Review pull requests for scope, architecture impact, validation evidence, and documentation accuracy. Ask for ROM validation when a change touches Quickstep, Recents, Taskbar, gestures, privileged behavior, or platform APIs.

## High-Risk Changes

High-risk areas include `Android.bp`, manifests, `src/` model/provider/icon cache behavior, `quickstep/`, Taskbar, BubbleBar, protos, and resource overlays that affect device profile or workspace behavior.

## Protected Architecture

Keep Launcher3, Quickstep, Recents, Overview, Taskbar, BubbleBar, Workspace, Hotseat, Folder, All Apps, and package `com.android.launcher3` intact during this phase.

## CI Failure Handling

Treat CI as a signal, not a reason to delete platform code. If Gradle smoke checks fail because of ROM-only APIs, document the limitation and validate the ROM path instead of stubbing hidden behavior.
