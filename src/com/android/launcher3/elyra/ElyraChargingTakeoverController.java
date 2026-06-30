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
import android.os.BatteryManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.launcher3.R;

/**
 * Monitors battery state and toggles the charging takeover section inside the
 * Elyra smart region card.
 *
 * <p>Registers an exported battery receiver when attached, then shows or hides
 * {@code R.id.elyra_charging_section} based on {@link BatteryManager} extras.</p>
 *
 * <p>The default (greeting) section is hidden while charging is active and restored
 * when the device is unplugged.</p>
 */
public final class ElyraChargingTakeoverController {

    private final Context mContext;
    private final View mSmartRegionView;

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBatteryIntent(intent);
        }
    };

    /**
     * Attaches a new controller instance to the given smart region view.
     * The receiver is registered on the application context so it survives config changes.
     */
    static void attach(Context context, View smartRegionView) {
        new ElyraChargingTakeoverController(context, smartRegionView);
    }

    private ElyraChargingTakeoverController(Context context, View smartRegionView) {
        mContext = context.getApplicationContext();
        mSmartRegionView = smartRegionView;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mContext.registerReceiver(mBatteryReceiver, filter);

        // Unregister when the smart region is detached (launcher destroyed/recreated)
        // to prevent accumulating duplicate receivers across activity instances.
        mSmartRegionView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override public void onViewAttachedToWindow(@NonNull View v) {}
            @Override public void onViewDetachedFromWindow(@NonNull View v) {
                try { mContext.unregisterReceiver(mBatteryReceiver); } catch (IllegalArgumentException ignored) {}
            }
        });

        // Read sticky battery intent immediately so we reflect current state at startup.
        Intent sticky = mContext.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (sticky != null) onBatteryIntent(sticky);
    }

    private void onBatteryIntent(Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_UNKNOWN);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;

        View defaultSection  = mSmartRegionView.findViewById(R.id.elyra_smart_default_section);
        View chargingSection = mSmartRegionView.findViewById(R.id.elyra_charging_section);
        if (defaultSection == null || chargingSection == null) return;

        if (isCharging) {
            defaultSection.setVisibility(View.GONE);
            chargingSection.setVisibility(View.VISIBLE);
            bindChargingData(intent, status);
        } else {
            defaultSection.setVisibility(View.VISIBLE);
            chargingSection.setVisibility(View.GONE);
        }
    }

    private void bindChargingData(Intent intent, int status) {
        int level  = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale  = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

        int pct = scale > 0 ? Math.round(100f * level / scale) : 0;

        TextView pctView    = mSmartRegionView.findViewById(R.id.elyra_charging_percentage);
        TextView statusView = mSmartRegionView.findViewById(R.id.elyra_charging_status);
        if (pctView != null) pctView.setText(pct + "%");

        if (statusView != null) {
            int statusRes;
            if (status == BatteryManager.BATTERY_STATUS_FULL) {
                statusRes = R.string.elyra_charging_full;
            } else if (plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                statusRes = R.string.elyra_charging_status_wireless;
            } else {
                // Differentiate fast-charge from normal by checking EXTRA_CHARGE_COUNTER
                // absence as proxy — show normal status for now.
                statusRes = R.string.elyra_charging_status_normal;
            }
            statusView.setText(statusRes);
        }
    }
}
