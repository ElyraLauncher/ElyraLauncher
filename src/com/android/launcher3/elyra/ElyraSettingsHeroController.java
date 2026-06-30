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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.launcher3.R;

/**
 * Renders the "Pusat Elyra" hero card at the top of the settings screen.
 *
 * <p>Added to the PreferenceScreen programmatically with {@code setOrder(Integer.MIN_VALUE)}
 * in {@link ElyraPreferenceController#configure}.  Uses a custom layout
 * ({@code elyra_settings_hero_card.xml}) that replaces the standard preference chrome.</p>
 *
 * <p>Contains an info pill, the "Pusat Elyra" title, and a rotating description that swaps
 * every {@link #DESCRIPTION_ROTATE_MS} ms.  Quick-action buttons and a fake weather preview
 * that previously pointed to hidden preference categories have been removed.</p>
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

        // Cancel any in-flight rotation from a previous bind (recycled or re-opened settings).
        mRotating = false;
        mHandler.removeCallbacks(mRotateRunnable);

        mDescriptionView = (TextView) holder.findViewById(R.id.elyra_hero_description);
        if (mDescriptionView != null) {
            mDescriptionView.setText(DESCRIPTIONS[mDescriptionIndex]);
        }

        startRotation();
    }

    private void startRotation() {
        if (mRotating) return;
        mRotating = true;
        mHandler.postDelayed(mRotateRunnable, DESCRIPTION_ROTATE_MS);
    }
}
