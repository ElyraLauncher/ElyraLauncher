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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;

/**
 * Creates and initialises the Elyra home-screen widgets:
 *
 * <ul>
 *   <li><b>Smart widget</b> ({@code elyra_smart_region.xml}) — unified view containing greeting,
 *       Elyra identity, rotating subtitle, charging/notification takeover sections, and the
 *       live clock / weather column.  Placed on CellLayout 0 by
 *       {@link ElyraHomeWidgetsController}.</li>
 *   <li><b>Search trigger pill</b> ({@code elyra_search_trigger.xml}) — placed in DragLayer
 *       by {@link ElyraHomeWidgetsController}.</li>
 * </ul>
 *
 * <p>Views are inflated here but NOT attached to any parent.</p>
 */
public final class ElyraSmartSpaceController {

    /** SharedPreferences key backing the "Compact search bar" setting. */
    public static final String KEY_COMPACT_SEARCH = "elyra_compact_search";
    /** Default: the compact search pill is shown (preserves current behavior). */
    public static final boolean COMPACT_SEARCH_DEFAULT = true;

    /** Reads the persisted "Compact search bar" preference. */
    public static boolean isCompactSearchEnabled(Context ctx) {
        return ctx.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(KEY_COMPACT_SEARCH, COMPACT_SEARCH_DEFAULT);
    }

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
     * Returns {@code null} if both SMART_REGION and SEARCH_TRIGGER flags are off.
     * Called from {@link ElyraHomeWidgetsController}.
     */
    static ElyraSmartSpaceController create(Launcher launcher) {
        if (!ElyraFeatureFlags.SMART_REGION && !ElyraFeatureFlags.SEARCH_TRIGGER) return null;
        return new ElyraSmartSpaceController(launcher);
    }

    /** Unified smart widget view — caller attaches to workspace page 0. */
    View getSmartRegionView()   { return mSmartRegionView; }

    /** Search trigger pill — caller attaches to DragLayer. */
    View getSearchTriggerView() { return mSearchTriggerView; }

    private ElyraSmartSpaceController(Launcher launcher) {
        mLauncher = launcher;
        if (ElyraFeatureFlags.SMART_REGION)   setupSmartRegion(launcher);
        if (ElyraFeatureFlags.SEARCH_TRIGGER) setupSearchTrigger();
    }

    // ── Unified smart widget ──────────────────────────────────────────────────

    private void setupSmartRegion(Launcher launcher) {
        mSmartRegionView = LayoutInflater.from(launcher)
                .inflate(R.layout.elyra_smart_region, null, false);
        mSmartRegionView.setClickable(false);
        mSmartRegionView.setFocusable(false);

        initGreeting();
        startSubtitleRotation();

        // Bind live clock + weather to the right column of the unified widget.
        if (ElyraFeatureFlags.WEATHER_TIME_CARD) {
            ElyraWeatherTimeController.bind(launcher, mSmartRegionView);
        }

        if (ElyraFeatureFlags.CHARGING_TAKEOVER) {
            ElyraChargingTakeoverController.attach(launcher, mSmartRegionView);
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
    }

    private void openSearch() {
        mLauncher.getStateManager().goToState(LauncherState.ALL_APPS, true);
    }
}
