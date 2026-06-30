/*
 * Copyright (C) 2024 ElyraLauncher Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.android.launcher3.elyra;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.statemanager.StateManager;

/**
 * Master controller for all Elyra home-screen widgets.
 *
 * <p>Owns the smart region card, weather/time card, and search trigger pill.
 * Implements {@link StateManager.StateListener} so all three views are hidden
 * the moment the launcher leaves {@link LauncherState#NORMAL} (All Apps, Overview,
 * Spring-Loaded) and restored when it returns to NORMAL.
 *
 * <p>Hook: {@code ElyraHomeWidgetsController.attachTo(launcher)} at the end of
 * {@code Launcher.setupViews()}.
 */
public final class ElyraHomeWidgetsController
        implements StateManager.StateListener<LauncherState> {

    private static final int HIDE_DURATION_MS = 150;
    private static final int SHOW_DURATION_MS  = 200;

    private final List<View> mHomeViews = new ArrayList<>();

    /** Called from {@code Launcher.setupViews()}. No-op when all home overlay flags are off. */
    public static void attachTo(Launcher launcher) {
        if (!ElyraFeatureFlags.SMART_REGION
                && !ElyraFeatureFlags.SEARCH_TRIGGER
                && !ElyraFeatureFlags.WEATHER_TIME_CARD) {
            return;
        }
        new ElyraHomeWidgetsController(launcher);
    }

    private ElyraHomeWidgetsController(Launcher launcher) {
        // Smart region + search trigger
        ElyraSmartSpaceController smartSpace = ElyraSmartSpaceController.attachTo(launcher);
        if (smartSpace != null) {
            View smart = smartSpace.getSmartRegionView();
            View trigger = smartSpace.getSearchTriggerView();
            if (smart != null)   mHomeViews.add(smart);
            if (trigger != null) mHomeViews.add(trigger);
        }

        // Weather / time card
        if (ElyraFeatureFlags.WEATHER_TIME_CARD) {
            ElyraWeatherTimeController weather = ElyraWeatherTimeController.attach(launcher);
            View card = weather.getCardView();
            if (card != null) mHomeViews.add(card);
        }

        // Single registration point for state gating of all home views.
        launcher.getStateManager().addStateListener(this);
    }

    // ── StateManager.StateListener ────────────────────────────────────────────

    @Override
    public void onStateTransitionStart(LauncherState toState) {
        if (toState != LauncherState.NORMAL) {
            fadeOut();
        }
    }

    @Override
    public void onStateTransitionComplete(LauncherState finalState) {
        if (finalState == LauncherState.NORMAL) {
            fadeIn();
        }
    }

    // ── Visibility helpers ────────────────────────────────────────────────────

    private void fadeOut() {
        for (View v : mHomeViews) {
            v.animate().cancel();
            v.animate()
                    .alpha(0f)
                    .setDuration(HIDE_DURATION_MS)
                    .withEndAction(() -> {
                        v.setVisibility(View.GONE);
                        v.setAlpha(1f); // reset so layout re-entry is instant
                    })
                    .start();
        }
    }

    private void fadeIn() {
        for (View v : mHomeViews) {
            v.animate().cancel();
            v.setAlpha(0f);
            v.setVisibility(View.VISIBLE);
            v.animate()
                    .alpha(1f)
                    .setDuration(SHOW_DURATION_MS)
                    .start();
        }
    }
}
