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

import static com.android.app.animation.Interpolators.EMPHASIZED_ACCELERATE;
import static com.android.app.animation.Interpolators.EMPHASIZED_DECELERATE;

import android.graphics.Insets;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.views.OptionsPopupView;

/**
 * Drives the fullscreen chrome (top Done/Group bar + bottom action bar + dim scrim) for Elyra
 * Home Edit Mode, in lockstep with {@link LauncherState#EDIT_MODE}.
 *
 * <p><b>Architecture.</b> The workspace scale/translation/dim and the dock fade are owned by
 * {@code EditModeState} and the standard Launcher3 state-transition animation — not by this
 * class. This controller only layers the edit chrome on top. Crucially, the chrome must NOT
 * consume horizontal touch over the workspace: the dim scrim is deliberately non-clickable so
 * finger swipes fall through to the real {@code Workspace}, which is fully page-scrollable in
 * {@code EDIT_MODE} ({@code Workspace#workspaceInScrollableState}). Only the Done/Group pills and
 * the four action buttons are interactive. Exit is via Done or Back (both route through the state
 * manager); there is intentionally no tap-to-exit scrim, as that would eat swipes.
 *
 * <p>On entry an extra empty workspace page is added ({@code Workspace#addExtraEmptyScreens}) so
 * the user can swipe to a blank page; on exit it is stripped again if unused
 * ({@code Workspace#removeExtraEmptyScreen}).
 *
 * <p>Entry point: {@link Launcher#showElyraHomeEditMode()} calls {@code goToState(EDIT_MODE)}.
 */
public final class ElyraHomeEditModeController implements StateManager.StateListener<LauncherState> {

    private static final long ANIM_DURATION_MS = 240L;
    private static final float BAR_SLIDE_DP = 32f;

    private final Launcher mLauncher;
    private final float mBarSlidePx;
    private View mOverlay;

    /** Called from {@code Launcher.setupViews()}. */
    public static void attachTo(Launcher launcher) {
        new ElyraHomeEditModeController(launcher);
    }

    private ElyraHomeEditModeController(Launcher launcher) {
        mLauncher = launcher;
        mBarSlidePx = BAR_SLIDE_DP * launcher.getResources().getDisplayMetrics().density;
        launcher.getStateManager().addStateListener(this);
    }

    @Override
    public void onStateTransitionStart(LauncherState toState) {
        // Drive chrome from transition START so it animates in lockstep with the workspace
        // state animation (avoids the disjoint "chrome settles after workspace" jitter).
        if (toState == LauncherState.EDIT_MODE) {
            enterEditMode();
        } else {
            exitEditMode();
        }
    }

    @Override
    public void onStateTransitionComplete(LauncherState finalState) {
        // No-op: visuals are driven from transition start.
    }

    private void ensureInflated() {
        if (mOverlay != null) {
            return;
        }
        mOverlay = LayoutInflater.from(mLauncher).inflate(
                R.layout.elyra_home_edit_overlay, mLauncher.getDragLayer(), false);

        // Span under the system bars so the dim scrim is one continuous edge-to-edge surface.
        // Without this, DragLayer (an InsettableFrameLayout) adds status/nav insets as MARGINS to
        // the overlay, leaving undimmed bands behind the status and navigation bars.
        if (mOverlay.getLayoutParams() instanceof InsettableFrameLayout.LayoutParams) {
            ((InsettableFrameLayout.LayoutParams) mOverlay.getLayoutParams()).ignoreInsets = true;
        }

        // NOTE: the scrim is intentionally NOT given a click listener — it must not consume
        // workspace swipes. Exit is Done / Back only.
        mOverlay.findViewById(R.id.elyra_home_edit_done).setOnClickListener(v -> {
            v.setEnabled(false);
            exitToNormal();
        });

        mOverlay.findViewById(R.id.elyra_home_edit_action_widget).setOnClickListener(v -> {
            exitToNormal();
            OptionsPopupView.openWidgets(mLauncher);
        });
        mOverlay.findViewById(R.id.elyra_home_edit_action_wallpaper).setOnClickListener(v -> {
            exitToNormal();
            OptionsPopupView.startWallpaperPicker(v);
        });
        // Routes to the Elyra settings dashboard rather than the Layar Utama detail screen:
        // the direct detail route was unreliable (could show a blank/dark screen). Revisit once
        // that route is verified stable on-device.
        mOverlay.findViewById(R.id.elyra_home_edit_action_layout).setOnClickListener(v -> {
            exitToNormal();
            OptionsPopupView.startSettings(v);
        });
        mOverlay.findViewById(R.id.elyra_home_edit_action_settings).setOnClickListener(v -> {
            exitToNormal();
            OptionsPopupView.startSettings(v);
        });

        applyWindowInsets();
        mLauncher.getDragLayer().addView(mOverlay);
    }

    /**
     * Pushes the system-bar insets onto ONLY the top and bottom bars, leaving the scrim
     * edge-to-edge so the dim stays one continuous surface (no bands behind the system bars).
     * The status-bar inset collapses to 0 once the bar is hidden in edit mode, letting the top
     * pills rise close to the screen edge.
     */
    private void applyWindowInsets() {
        View topBar = mOverlay.findViewById(R.id.elyra_home_edit_top_bar);
        View bottomBar = mOverlay.findViewById(R.id.elyra_home_edit_bottom_bar);
        final int topBase = topBar.getPaddingTop();
        final int bottomBase = bottomBar.getPaddingBottom();
        mOverlay.setOnApplyWindowInsetsListener((v, insets) -> {
            Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            topBar.setPadding(topBar.getPaddingLeft(), topBase + bars.top,
                    topBar.getPaddingRight(), topBar.getPaddingBottom());
            bottomBar.setPadding(bottomBar.getPaddingLeft(), bottomBar.getPaddingTop(),
                    bottomBar.getPaddingRight(), bottomBase + bars.bottom);
            return insets;
        });
    }

    private void exitToNormal() {
        mLauncher.getStateManager().goToState(LauncherState.NORMAL);
    }

    private void enterEditMode() {
        ensureInflated();
        setStatusBarHidden(true);
        // Real blank page to swipe to (stripped again on exit if unused).
        mLauncher.getWorkspace().addExtraEmptyScreens();

        View done = mOverlay.findViewById(R.id.elyra_home_edit_done);
        done.setEnabled(true);

        mOverlay.animate().cancel();
        mOverlay.setVisibility(View.VISIBLE);
        mOverlay.requestApplyInsets();
        mOverlay.setAlpha(0f);
        mOverlay.animate().alpha(1f).setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_DECELERATE).start();

        View topBar = mOverlay.findViewById(R.id.elyra_home_edit_top_bar);
        topBar.animate().cancel();
        topBar.setAlpha(0f);
        topBar.setTranslationY(-mBarSlidePx);
        topBar.animate().alpha(1f).translationY(0f).setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_DECELERATE).withLayer().start();

        View bottomBar = mOverlay.findViewById(R.id.elyra_home_edit_bottom_bar);
        bottomBar.animate().cancel();
        bottomBar.setAlpha(0f);
        bottomBar.setTranslationY(mBarSlidePx);
        bottomBar.animate().alpha(1f).translationY(0f).setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_DECELERATE).withLayer().start();
    }

    private void exitEditMode() {
        if (mOverlay == null || mOverlay.getVisibility() != View.VISIBLE) {
            return;
        }
        setStatusBarHidden(false);
        // Remove the extra empty page (and any other now-empty trailing pages).
        mLauncher.getWorkspace().removeExtraEmptyScreen(true /* stripEmptyScreens */);

        mOverlay.animate().cancel();
        mOverlay.animate().alpha(0f).setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_ACCELERATE).withLayer()
                .withEndAction(() -> mOverlay.setVisibility(View.GONE))
                .start();
    }

    /**
     * Hides/shows the status bar via {@link WindowInsetsController} (API 30+; minSdk here is 31).
     * Only the status bar is toggled — the navigation bar is left to the system so gesture nav /
     * back keep working. Restoring on exit returns the previous system-bar state.
     */
    private void setStatusBarHidden(boolean hidden) {
        WindowInsetsController controller = mLauncher.getWindow().getInsetsController();
        if (controller == null) {
            return;
        }
        if (hidden) {
            controller.setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            controller.hide(WindowInsets.Type.statusBars());
        } else {
            controller.show(WindowInsets.Type.statusBars());
        }
    }
}
