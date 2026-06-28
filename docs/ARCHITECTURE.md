# Architecture

ElyraLauncher is based on AOSP Launcher3 with Quickstep support. The repository keeps the platform launcher model intact and layers Elyra-specific identity, resources, and integration work on top of the existing architecture.

## Launcher3 Base

Launcher3 core remains under `src/` and includes the launcher model, provider, icon cache, device profile, workspace, hotseat, folders, widgets, and All Apps. These components are not optional scaffolding; they are the launcher.

## Quickstep Relationship

Quickstep and Recents live under `quickstep/`. This area contains the launcher-side relationship with Overview, task surfaces, gestures, taskbar behavior, and related system integration. `QuickstepLauncher`, `RecentsView`, `TaskView`, and `TouchInteractionService` must remain platform-integrated.

## ROM/System Integration

The authoritative target is `ElyraLauncherQuickStep` in `Android.bp`. ROM validation is required because Quickstep and Recents depend on privileged installation, platform APIs, SystemUI relationships, and task-management behavior.

## Elyra Customization Layer

Elyra customization should be applied through safe resources, configuration, branding, and reviewed extensions. Avoid changes that bypass Launcher3 model contracts or fork Quickstep behavior without ROM validation.

## Integration Plan

ElyraSystemUILibs and ElyraIcons should be integrated through separate, reviewed changes. Their purpose is to support system UI relationships and icon identity without embedding unrelated companion projects into this repository.

## Standalone Smoke Limitation

The standalone smoke APK can check basic UI packaging only. It cannot validate real Recents, Quickstep gestures, privileged launcher behavior, hidden APIs, or system-level task management.
