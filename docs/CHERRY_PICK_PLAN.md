# Cherry-pick Plan

Old experimental source should not be copied all at once.

## Phase A: Safe Identity Changes

- App name
- Strings
- Launcher icon placeholder
- Basic theme names
- ROM target naming

## Phase B: Launcher UI

- Basic settings entry
- Onboarding shell
- Drawer customization
- Search UI shell

## Phase C: Ecosystem

- ElyraIcons integration
- ElyraFeed integration
- Widget layer

## Phase D: Advanced Modules

- ElyraKIT
- Native optimization
- Benchmark
- SystemUI shared adapters

## Rule

Only cherry-pick code after the clean Launcher3/Quickstep base builds.

Do not cherry-pick changes that remove Launcher3, Quickstep, Recents, or the
current `com.android.launcher3` package assumptions.
