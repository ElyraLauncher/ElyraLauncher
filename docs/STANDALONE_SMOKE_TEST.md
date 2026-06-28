# Standalone Smoke Test

## Purpose

The standalone smoke APK exists only for basic packaging, resource, and UI checks. It is useful for catching simple regressions before ROM integration work.

## What It Can Test

- Gradle configuration for the smoke module.
- Basic Android resource packaging.
- Basic manifest validity for the smoke artifact.
- Simple UI surfaces included in the smoke module.

## What It Cannot Test

- Real Quickstep gesture integration.
- Real Android Recents.
- Privileged launcher behavior.
- Hidden platform APIs.
- System task management.
- ROM product integration.

## Required Wording

Describe the artifact as a standalone smoke APK. Do not call it the production launcher and do not use it as the source of truth for Quickstep or Recents validation.
