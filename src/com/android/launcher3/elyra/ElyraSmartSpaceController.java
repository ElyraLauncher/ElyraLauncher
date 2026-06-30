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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;

/**
 * Creates and initialises the Elyra smart region card and search trigger pill.
 *
 * <p>Views are inflated and wired here but NOT attached to any parent.
 * {@link ElyraHomeWidgetsController} is responsible for placing them:
 * <ul>
 *   <li>Smart region → FrameLayout container on CellLayout 0 (workspace-bound).</li>
 *   <li>Search trigger → DragLayer, above the hotseat (dock-area, page-independent).</li>
 * </ul>
 */
public final class ElyraSmartSpaceController {

    private static final long SUBTITLE_ROTATE_MS = 5_000L;

    private static final int[] SUBTITLES = {
            R.string.elyra_subtitle_1,
            R.string.elyra_subtitle_2,
            R.string.elyra_subtitle_3,
            R.string.elyra_subtitle_4,
            R.string.elyra_subtitle_5,
    };

    private final Launcher mLauncher;
    private View mSmartRegionView;
    private View mSearchTriggerView;

    private TextView mSubtitleView;
    private int mSubtitleIndex = 0;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Runnable mSubtitleRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSubtitleView == null) return;
            mSubtitleIndex = (mSubtitleIndex + 1) % SUBTITLES.length;
            mSubtitleView.setText(SUBTITLES[mSubtitleIndex]);
            mHandler.postDelayed(this, SUBTITLE_ROTATE_MS);
        }
    };

    /**
     * Called from {@link ElyraHomeWidgetsController}. Returns {@code null} if both
     * SMART_REGION and SEARCH_TRIGGER flags are disabled.
     */
    static ElyraSmartSpaceController create(Launcher launcher) {
        if (!ElyraFeatureFlags.SMART_REGION && !ElyraFeatureFlags.SEARCH_TRIGGER) return null;
        return new ElyraSmartSpaceController(launcher);
    }

    /** Inflated smart region card — caller attaches to workspace page. */
    View getSmartRegionView()   { return mSmartRegionView; }

    /** Inflated search trigger pill — caller attaches to DragLayer. */
    View getSearchTriggerView() { return mSearchTriggerView; }

    private ElyraSmartSpaceController(Launcher launcher) {
        mLauncher = launcher;
        if (ElyraFeatureFlags.SMART_REGION)   setupSmartRegion();
        if (ElyraFeatureFlags.SEARCH_TRIGGER) setupSearchTrigger();
    }

    // ── Smart region ──────────────────────────────────────────────────────────

    private void setupSmartRegion() {
        mSmartRegionView = LayoutInflater.from(mLauncher)
                .inflate(R.layout.elyra_smart_region, null, false);

        // Non-interactive container — workspace icon touches fall through.
        mSmartRegionView.setClickable(false);
        mSmartRegionView.setFocusable(false);

        initGreeting();
        startSubtitleRotation();

        if (ElyraFeatureFlags.CHARGING_TAKEOVER) {
            ElyraChargingTakeoverController.attach(mLauncher, mSmartRegionView);
        }
        if (ElyraFeatureFlags.NOTIFICATION_TAKEOVER) {
            ElyraNotificationTakeoverController.attach(mSmartRegionView);
        }
    }

    private void initGreeting() {
        TextView greetingView = mSmartRegionView.findViewById(R.id.elyra_smart_greeting);
        if (greetingView == null) return;
        greetingView.setText(currentGreeting());
    }

    private int currentGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5  && hour < 11) return R.string.elyra_greeting_morning;
        if (hour >= 11 && hour < 15) return R.string.elyra_greeting_noon;
        if (hour >= 15 && hour < 19) return R.string.elyra_greeting_afternoon;
        return R.string.elyra_greeting_night;
    }

    private void startSubtitleRotation() {
        mSubtitleView = mSmartRegionView.findViewById(R.id.elyra_smart_subtitle);
        if (mSubtitleView == null) return;
        mHandler.postDelayed(mSubtitleRunnable, SUBTITLE_ROTATE_MS);
    }

    // ── Search trigger ────────────────────────────────────────────────────────

    private void setupSearchTrigger() {
        mSearchTriggerView = LayoutInflater.from(mLauncher)
                .inflate(R.layout.elyra_search_trigger, null, false);
        mSearchTriggerView.setOnClickListener(v -> openSearch());
        // DragLayer layout params and addView handled by ElyraHomeWidgetsController.
    }

    private void openSearch() {
        mLauncher.getStateManager().goToState(LauncherState.ALL_APPS, true);
    }
}
