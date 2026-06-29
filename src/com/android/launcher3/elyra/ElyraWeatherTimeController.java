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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.android.launcher3.Launcher;
import com.android.launcher3.R;

/**
 * Manages the upper-right weather / time card on the home screen.
 *
 * <p>Tapping the card toggles between the weather state (default, placeholder data)
 * and the time state (live clock + date). The clock updates every minute via
 * {@link Intent#ACTION_TIME_TICK}. Weather data is placeholder until an API is wired.</p>
 *
 * <p>Created and owned by {@link ElyraSmartSpaceController}. Hook lives in
 * {@code Launcher.setupViews()} via {@code ElyraSmartSpaceController.attachTo(this)}.</p>
 */
public final class ElyraWeatherTimeController {

    private boolean mShowingTime = false;

    private View mCardView;
    private View mWeatherState;
    private View mTimeState;
    private TextView mClockView;
    private TextView mDateView;

    private final BroadcastReceiver mTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateClock();
        }
    };

    /**
     * Inflates the card, adds it to DragLayer in the upper-right corner, and
     * registers time-tick events. Called from {@link ElyraSmartSpaceController}.
     */
    static ElyraWeatherTimeController attach(Launcher launcher) {
        ElyraWeatherTimeController ctrl = new ElyraWeatherTimeController();
        ctrl.setup(launcher);
        return ctrl;
    }

    private void setup(Launcher launcher) {
        mCardView = LayoutInflater.from(launcher)
                .inflate(R.layout.elyra_weather_time_card, null, false);

        int statusBarH = getStatusBarHeight(launcher);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.TOP | Gravity.END;
        lp.topMargin = statusBarH + dpToPx(launcher, 8);
        lp.rightMargin = dpToPx(launcher, 16);

        launcher.getDragLayer().addView(mCardView, lp);

        mWeatherState = mCardView.findViewById(R.id.elyra_weather_state);
        mTimeState    = mCardView.findViewById(R.id.elyra_time_state);
        mClockView    = mCardView.findViewById(R.id.elyra_time_clock);
        mDateView     = mCardView.findViewById(R.id.elyra_time_date);

        mCardView.setOnClickListener(v -> toggleState());

        // Register time tick on application context to avoid leaking the card view.
        Context appCtx = launcher.getApplicationContext();
        appCtx.registerReceiver(mTickReceiver,
                new IntentFilter(Intent.ACTION_TIME_TICK));

        updateClock();
    }

    private void toggleState() {
        mShowingTime = !mShowingTime;
        if (mShowingTime) {
            mWeatherState.setVisibility(View.GONE);
            mTimeState.setVisibility(View.VISIBLE);
            updateClock();
        } else {
            mTimeState.setVisibility(View.GONE);
            mWeatherState.setVisibility(View.VISIBLE);
        }
    }

    private void updateClock() {
        if (mClockView == null || mDateView == null) return;
        Date now = new Date();
        mClockView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now));
        mDateView.setText(new SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(now));
    }

    private static int getStatusBarHeight(Launcher launcher) {
        int id = launcher.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        return id > 0 ? launcher.getResources().getDimensionPixelSize(id) : 0;
    }

    private static int dpToPx(Launcher launcher, int dp) {
        return Math.round(dp * launcher.getResources().getDisplayMetrics().density);
    }
}
