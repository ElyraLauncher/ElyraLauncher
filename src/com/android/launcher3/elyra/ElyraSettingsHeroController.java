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
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView mRecyclerView;

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

        // Capture the host RecyclerView for category scroll.
        if (holder.itemView.getParent() instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) holder.itemView.getParent();
        }

        mDescriptionView = (TextView) holder.findViewById(R.id.elyra_hero_description);
        if (mDescriptionView != null) {
            mDescriptionView.setText(DESCRIPTIONS[mDescriptionIndex]);
        }

        // Wire quick actions to relevant settings destinations.
        wireAction(holder, R.id.elyra_action_weather,      () -> openCategory("elyra_widget_category"));
        wireAction(holder, R.id.elyra_action_notification, () -> openCategory("elyra_notification_category"));
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

    /**
     * Scrolls the settings RecyclerView to the preference with the given key.
     * Uses a linear traversal of the preference tree to find the adapter position.
     */
    private void openCategory(String key) {
        if (mRecyclerView == null) return;
        PreferenceScreen screen = getPreferenceManager().getPreferenceScreen();
        if (screen == null) return;

        int[] counter = {0};
        int position = findPosition(screen, key, counter);
        if (position >= 0) {
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    /**
     * Depth-first traversal of the preference tree; returns the flattened adapter position
     * of the preference matching {@code key}, or -1 if not found.
     */
    private int findPosition(PreferenceGroup group, String key, int[] counter) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference pref = group.getPreference(i);
            if (!pref.isVisible()) continue;
            if (key.equals(pref.getKey())) return counter[0];
            counter[0]++;
            if (pref instanceof PreferenceGroup) {
                int found = findPosition((PreferenceGroup) pref, key, counter);
                if (found >= 0) return found;
            }
        }
        return -1;
    }
}
