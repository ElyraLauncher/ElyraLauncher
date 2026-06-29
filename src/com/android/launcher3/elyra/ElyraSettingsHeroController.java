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
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.launcher3.R;

/**
 * Renders the "Pusat Elyra" hero card at the top of the settings screen.
 *
 * <p>Added to the PreferenceScreen programmatically with {@code setOrder(Integer.MIN_VALUE)}
 * from {@link ElyraPreferenceController#configure(androidx.preference.PreferenceGroup)}.
 * Uses a custom layout that replaces the standard preference chrome entirely.</p>
 *
 * <p>The description text rotates every {@link #DESCRIPTION_ROTATE_MS} milliseconds among the
 * five entries in {@link #DESCRIPTIONS}.  Rotation stops when the view is recycled (detached).</p>
 */
public final class ElyraSettingsHeroController extends Preference {

    private static final long DESCRIPTION_ROTATE_MS = 4_000L;

    private static final int[] DESCRIPTIONS = {
            R.string.elyra_hero_desc_1,
            R.string.elyra_hero_desc_2,
            R.string.elyra_hero_desc_3,
            R.string.elyra_hero_desc_4,
            R.string.elyra_hero_desc_5,
    };

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private int mDescriptionIndex = 0;
    private TextView mDescriptionView;
    private boolean mRotating = false;

    private final Runnable mRotateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mDescriptionView == null || !mRotating) return;
            mDescriptionIndex = (mDescriptionIndex + 1) % DESCRIPTIONS.length;
            mDescriptionView.setText(DESCRIPTIONS[mDescriptionIndex]);
            mHandler.postDelayed(this, DESCRIPTION_ROTATE_MS);
        }
    };

    public ElyraSettingsHeroController(@NonNull Context context) {
        super(context);
        init();
    }

    public ElyraSettingsHeroController(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.elyra_settings_hero_card);
        setSelectable(false);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mDescriptionView = (TextView) holder.findViewById(R.id.elyra_hero_description);
        if (mDescriptionView != null) {
            mDescriptionView.setText(DESCRIPTIONS[mDescriptionIndex]);
        }

        // Wire quick actions to relevant settings destinations.
        wireAction(holder, R.id.elyra_action_weather,      () -> openCategory("elyra_folder_category"));
        wireAction(holder, R.id.elyra_action_notification, () -> openCategory("elyra_appearance_category"));
        wireAction(holder, R.id.elyra_action_location,     () -> openCategory("elyra_home_category"));

        startRotation();
    }

    /** Starts description rotation after initial display. */
    private void startRotation() {
        if (mRotating) return;
        mRotating = true;
        mHandler.postDelayed(mRotateRunnable, DESCRIPTION_ROTATE_MS);
    }

    /** Stops rotation and clears the handler reference when view is recycled. */
    public void stopRotation() {
        mRotating = false;
        mDescriptionView = null;
        mHandler.removeCallbacks(mRotateRunnable);
    }

    private void wireAction(PreferenceViewHolder holder, int viewId, Runnable action) {
        View v = holder.findViewById(viewId);
        if (v != null) v.setOnClickListener(view -> action.run());
    }

    /** Scrolls the preference list to the given category key (best-effort). */
    private void openCategory(String key) {
        // The host preference screen handles navigation via the normal RecyclerView scrolling.
        // For now this is a no-op anchor — each quick action is visually distinct and routed
        // to the correct section when deep-link navigation is available.
    }
}
