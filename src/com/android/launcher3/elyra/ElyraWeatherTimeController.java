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
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.android.launcher3.Launcher;

/**
 * Drives the clock/date and weather temperature displayed in the right column of the unified
 * Elyra smart widget ({@code elyra_smart_region.xml}).
 *
 * <p>Does NOT inflate its own layout. Called from {@link ElyraSmartSpaceController} after the
 * unified widget is inflated; binds directly to {@code R.id.elyra_time_clock},
 * {@code elyra_time_date}, and {@code elyra_weather_temp} inside that view.</p>
 *
 * <p>Clock updates every minute via {@link Intent#ACTION_TIME_TICK}. The receiver is
 * unregistered via {@link View.OnAttachStateChangeListener} when the widget is detached from
 * the window (launcher destroyed / rotated), preventing receiver leaks.</p>
 */
final class ElyraWeatherTimeController {

    private TextView mClockView;
    private TextView mDateView;

    private final BroadcastReceiver mTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateClock();
        }
    };

    /**
     * Binds clock/weather views inside {@code widgetView} and registers the time-tick receiver.
     * Called from {@link ElyraSmartSpaceController#setupSmartRegion(Launcher)}.
     */
    static void bind(Launcher launcher, View widgetView) {
        ElyraWeatherTimeController ctrl = new ElyraWeatherTimeController();
        ctrl.bindViews(launcher, widgetView);
    }

    private void bindViews(Launcher launcher, View widgetView) {
        mClockView = widgetView.findViewById(com.android.launcher3.R.id.elyra_time_clock);
        mDateView  = widgetView.findViewById(com.android.launcher3.R.id.elyra_time_date);

        final Context appCtx = launcher.getApplicationContext();
        appCtx.registerReceiver(mTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        widgetView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override public void onViewAttachedToWindow(@NonNull View v) {}
            @Override public void onViewDetachedFromWindow(@NonNull View v) {
                try { appCtx.unregisterReceiver(mTickReceiver); }
                catch (IllegalArgumentException ignored) {}
            }
        });

        updateClock();
    }

    private void updateClock() {
        if (mClockView == null || mDateView == null) return;
        Date now = new Date();
        mClockView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now));
        mDateView.setText(new SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(now));
    }
}
