/*
 * Copyright (C) 2024 ElyraLauncher Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.android.launcher3.elyra.dock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.elyra.ElyraFeatureFlags;

/**
 * Manages the Elyra dock (Hotseat) visual surface.
 *
 * <p>The Hotseat view is full-width with height = {@code hotseatBarSizePx}, which includes the
 * navigation bar inset at the bottom.  A static XML drawable cannot account for the device-
 * specific nav bar height, so this controller creates the pill {@link InsetDrawable} dynamically
 * each time {@code Hotseat.setInsets()} is called.</p>
 *
 * <p>Result: a dark frosted-glass rounded-rectangle pill that floats above the nav bar, with
 * 12dp horizontal margins, 4dp top gap, and a nav-bar-sized bottom gap — icons are vertically
 * centred inside the visible pill area.</p>
 *
 * <p>Hook: {@code Hotseat.setInsets(Rect)} calls
 * {@link #onInsetsChanged(View, Rect, DeviceProfile)} as its last statement.</p>
 */
public final class ElyraDockController {

    private ElyraDockController() {}

    /**
     * Applies the dock pill background with device-correct insets.
     *
     * @param hotseat full-width Hotseat view (height = hotseatBarSizePx)
     * @param insets  window insets at the time {@code setInsets} was called
     * @param dp      active DeviceProfile
     */
    public static void onInsetsChanged(View hotseat, Rect insets, DeviceProfile dp) {
        if (!ElyraFeatureFlags.HOTSEAT_SURFACE) return;
        if (dp.isVerticalBarLayout()) {
            // Landscape sidebar layout: keep transparent, no pill.
            hotseat.setBackground(null);
            return;
        }

        Context ctx    = hotseat.getContext();
        Resources res  = ctx.getResources();
        float density  = res.getDisplayMetrics().density;

        int cornerPx   = res.getDimensionPixelSize(R.dimen.elyra_dock_corner_radius);
        int leftPx     = Math.round(12 * density);
        int rightPx    = Math.round(12 * density);
        int topPx      = Math.round(4  * density);
        // Bottom inset = navigation bar height so the pill sits ABOVE the nav bar.
        // Minimum 4dp so there is always a small gap at the bottom.
        int bottomPx   = Math.max(insets.bottom, Math.round(4 * density));

        // Pill shape: dark frosted dark-navy fill + hairline white stroke.
        GradientDrawable pill = new GradientDrawable();
        pill.setShape(GradientDrawable.RECTANGLE);
        pill.setColor(ContextCompat.getColor(ctx, R.color.elyra_dock_bg));
        pill.setCornerRadius(cornerPx);
        pill.setStroke(Math.round(1 * density), Color.parseColor("#1AFFFFFF"));

        hotseat.setBackground(new InsetDrawable(pill, leftPx, topPx, rightPx, bottomPx));
    }
}
