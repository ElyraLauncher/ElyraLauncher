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

import android.os.Handler;
import android.os.Looper;
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
 * Responsibilities:
 * <ul>
 *   <li>Immediately hides all home widgets when launcher leaves {@link LauncherState#NORMAL}
 *       (All Apps, Overview, Spring-Loaded, any other non-home state). Hidden instantly — no
 *       animation delay — so widgets never float above the drawer or overview.</li>
 *   <li>Translates greeting and weather cards with {@code -scrollX} as the workspace scrolls,
 *       so they appear to belong to workspace page 0 and scroll off-screen with it.</li>
 *   <li>Fades the search trigger during workspace page scrolling and restores it after the
 *       page settles (300 ms debounce).</li>
 *   <li>Owns the single {@code Workspace.OnScrollChangeListener} — no competing listeners.</li>
 * </ul>
 *
 * <p>Hook: {@code ElyraHomeWidgetsController.attachTo(launcher)} at the end of
 * {@code Launcher.setupViews()}.
 */
public final class ElyraHomeWidgetsController
        implements StateManager.StateListener<LauncherState> {

    private static final int SHOW_DURATION_MS    = 180;
    private static final int TRIGGER_SETTLE_MS   = 300;

    private final Launcher mLauncher;
    private final Handler  mHandler = new Handler(Looper.getMainLooper());

    /**
     * Greeting + weather: translate with workspace page 0 so they scroll with it.
     * Search trigger is NOT page-bound (lower dock area), handled separately.
     */
    private final List<View> mPageBoundViews = new ArrayList<>();

    /** All home-only views — hidden in every non-NORMAL state. */
    private final List<View> mAllHomeViews   = new ArrayList<>();

    /** The lower search trigger — also fades on page scroll. */
    private View mSearchTriggerView;

    private boolean mIsNormal = true;

    private final Runnable mTriggerFadeInRunnable = () -> {
        if (mSearchTriggerView != null && mIsNormal) {
            mSearchTriggerView.animate().alpha(1f).setDuration(150).start();
        }
    };

    // ── Entry point ───────────────────────────────────────────────────────────

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
        mLauncher = launcher;

        // Smart region (upper-left) + search trigger (lower dock area).
        ElyraSmartSpaceController smartSpace = ElyraSmartSpaceController.attachTo(launcher);
        if (smartSpace != null) {
            View smart   = smartSpace.getSmartRegionView();
            View trigger = smartSpace.getSearchTriggerView();
            if (smart != null) {
                mAllHomeViews.add(smart);
                mPageBoundViews.add(smart);   // scrolls off with page 0
            }
            if (trigger != null) {
                mAllHomeViews.add(trigger);
                mSearchTriggerView = trigger; // separate scroll-fade logic
            }
        }

        // Weather / time card (upper-right).
        if (ElyraFeatureFlags.WEATHER_TIME_CARD) {
            ElyraWeatherTimeController weather = ElyraWeatherTimeController.attach(launcher);
            View card = weather.getCardView();
            if (card != null) {
                mAllHomeViews.add(card);
                mPageBoundViews.add(card);    // scrolls off with page 0
            }
        }

        // Single registration for state gating.
        launcher.getStateManager().addStateListener(this);

        // Single workspace scroll listener — page translation + trigger fade.
        launcher.getWorkspace().setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        onWorkspaceScrolled(scrollX, oldScrollX));
    }

    // ── StateManager.StateListener ────────────────────────────────────────────

    @Override
    public void onStateTransitionStart(LauncherState toState) {
        if (toState != LauncherState.NORMAL) {
            mIsNormal = false;
            hideAll();
        }
    }

    @Override
    public void onStateTransitionComplete(LauncherState finalState) {
        if (finalState == LauncherState.NORMAL) {
            mIsNormal = true;
            showAll();
        }
    }

    // ── Visibility ────────────────────────────────────────────────────────────

    private void hideAll() {
        mHandler.removeCallbacks(mTriggerFadeInRunnable);
        for (View v : mAllHomeViews) {
            v.animate().cancel();
            v.setVisibility(View.GONE);  // instant — no animation lag over the drawer
        }
    }

    private void showAll() {
        // Apply current page translation before making views visible.
        int scrollX = mLauncher.getWorkspace().getScrollX();
        applyPageTranslation(scrollX);

        for (View v : mAllHomeViews) {
            v.animate().cancel();
            v.setAlpha(0f);
            v.setVisibility(View.VISIBLE);
            v.animate().alpha(1f).setDuration(SHOW_DURATION_MS).start();
        }
    }

    // ── Workspace scroll ──────────────────────────────────────────────────────

    private void onWorkspaceScrolled(int scrollX, int oldScrollX) {
        applyPageTranslation(scrollX);

        if (mSearchTriggerView != null && mIsNormal && scrollX != oldScrollX) {
            mSearchTriggerView.animate().cancel();
            mSearchTriggerView.setAlpha(0f);
            mHandler.removeCallbacks(mTriggerFadeInRunnable);
            mHandler.postDelayed(mTriggerFadeInRunnable, TRIGGER_SETTLE_MS);
        }
    }

    /**
     * Translates greeting and weather views by {@code -scrollX} so they appear to move
     * with workspace page 0. When the user scrolls to page 1, the views slide off-screen
     * to the left naturally — no longer visible as persistent overlays on other pages.
     */
    private void applyPageTranslation(int scrollX) {
        for (View v : mPageBoundViews) {
            v.setTranslationX(-scrollX);
        }
    }
}
