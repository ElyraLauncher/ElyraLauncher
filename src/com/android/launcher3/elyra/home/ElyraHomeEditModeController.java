/*
 * Copyright (C) 2024 ElyraLauncher Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.android.launcher3.elyra.home;

import android.view.LayoutInflater;
import android.view.View;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.views.OptionsPopupView;

/**
 * Drives the fullscreen chrome (top Done bar + bottom action bar + extra scrim) for Elyra
 * Home Edit Mode, in lockstep with {@link LauncherState#EDIT_MODE}.
 *
 * <p>The actual workspace/hotseat scale, translation and dim are handled by
 * {@code EditModeState} itself (see {@code LauncherState#EDIT_MODE}); this controller only
 * owns the overlay chrome layered on top, so the real Launcher3 state machine — not a
 * hand-rolled popup — is what makes this feel like a real edit mode rather than a menu.
 *
 * <p>Entry point: {@link Launcher#showElyraHomeEditMode()} calls
 * {@code goToState(EDIT_MODE)}; back button, Done, and every other exit path already routes
 * back through the state manager, so this controller never needs its own exit plumbing beyond
 * reacting to state transitions.
 */
public final class ElyraHomeEditModeController implements StateManager.StateListener<LauncherState> {

    private static final long ANIM_DURATION_MS = 220L;
    /**
     * Extra dock dim on top of {@code EditModeState}'s own hotseat scale — the scale alone
     * still read as "normal home, just smaller" rather than a distinct edit mode.
     */
    private static final float DOCK_EDIT_ALPHA = 0.45f;

    private final Launcher mLauncher;
    private View mOverlay;

    /** Called from {@code Launcher.setupViews()}. */
    public static void attachTo(Launcher launcher) {
        new ElyraHomeEditModeController(launcher);
    }

    private ElyraHomeEditModeController(Launcher launcher) {
        mLauncher = launcher;
        launcher.getStateManager().addStateListener(this);
    }

    @Override
    public void onStateTransitionStart(LauncherState toState) {
        if (toState == LauncherState.EDIT_MODE) {
            showOverlay();
        }
    }

    @Override
    public void onStateTransitionComplete(LauncherState finalState) {
        if (finalState != LauncherState.EDIT_MODE) {
            hideOverlay();
        }
    }

    private void ensureInflated() {
        if (mOverlay != null) {
            return;
        }
        mOverlay = LayoutInflater.from(mLauncher).inflate(
                R.layout.elyra_home_edit_overlay, mLauncher.getDragLayer(), false);

        View scrim = mOverlay.findViewById(R.id.elyra_home_edit_scrim);
        scrim.setOnClickListener(v -> exitEditMode());

        mOverlay.findViewById(R.id.elyra_home_edit_done).setOnClickListener(v -> exitEditMode());

        mOverlay.findViewById(R.id.elyra_home_edit_action_widget).setOnClickListener(v -> {
            exitEditMode();
            OptionsPopupView.openWidgets(mLauncher);
        });
        mOverlay.findViewById(R.id.elyra_home_edit_action_wallpaper).setOnClickListener(v -> {
            exitEditMode();
            OptionsPopupView.startWallpaperPicker(v);
        });
        // Routes to the Elyra settings dashboard rather than the Layar Utama detail screen:
        // the direct detail route was unreliable (could show a blank/dark screen depending on
        // launch state). Revisit once that route is verified stable on-device.
        mOverlay.findViewById(R.id.elyra_home_edit_action_layout).setOnClickListener(v -> {
            exitEditMode();
            OptionsPopupView.startSettings(v);
        });
        mOverlay.findViewById(R.id.elyra_home_edit_action_settings).setOnClickListener(v -> {
            exitEditMode();
            OptionsPopupView.startSettings(v);
        });

        mLauncher.getDragLayer().addView(mOverlay);
    }

    private void exitEditMode() {
        mLauncher.getStateManager().goToState(LauncherState.NORMAL);
    }

    private void showOverlay() {
        ensureInflated();
        mOverlay.animate().cancel();
        mOverlay.setAlpha(0f);
        mOverlay.setVisibility(View.VISIBLE);
        mOverlay.animate().alpha(1f).setDuration(ANIM_DURATION_MS).start();

        View dock = mLauncher.getHotseat();
        dock.animate().cancel();
        dock.animate().alpha(DOCK_EDIT_ALPHA).setDuration(ANIM_DURATION_MS).start();

        View topBar = mOverlay.findViewById(R.id.elyra_home_edit_top_bar);
        topBar.animate().cancel();
        topBar.setAlpha(0f);
        topBar.setTranslationY(-topBar.getHeight() / 2f - 24f);
        topBar.animate().alpha(1f).translationY(0f).setDuration(ANIM_DURATION_MS).start();

        View bottomBar = mOverlay.findViewById(R.id.elyra_home_edit_bottom_bar);
        bottomBar.animate().cancel();
        bottomBar.setAlpha(0f);
        bottomBar.setTranslationY(bottomBar.getHeight() / 2f + 24f);
        bottomBar.animate().alpha(1f).translationY(0f).setDuration(ANIM_DURATION_MS).start();
    }

    private void hideOverlay() {
        View dock = mLauncher.getHotseat();
        dock.animate().cancel();
        dock.animate().alpha(1f).setDuration(ANIM_DURATION_MS).start();

        if (mOverlay == null || mOverlay.getVisibility() != View.VISIBLE) {
            return;
        }
        mOverlay.animate().cancel();
        mOverlay.animate().alpha(0f).setDuration(ANIM_DURATION_MS)
                .withEndAction(() -> mOverlay.setVisibility(View.GONE))
                .start();
    }
}
