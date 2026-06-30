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
 * <p>The Hotseat view spans the full screen width at height {@code hotseatBarSizePx}, which
 * includes the navigation bar inset.  A static XML drawable cannot account for the
 * device-specific nav bar height, so this controller creates the pill background programmatically
 * each time {@code Hotseat.setInsets()} fires.</p>
 *
 * <p>Visual result: a dark frosted-glass rounded-rectangle pill with
 * {@code elyra_dock_horizontal_margin} side insets, {@code elyra_dock_top_gap} top inset, and a
 * nav-bar-sized bottom inset so the pill floats cleanly above the navigation area.  Icons remain
 * centred vertically within the visible pill by {@code DeviceProfile.getHotseatLayoutPadding()}.</p>
 *
 * <p>Hook: {@code Hotseat.setInsets(Rect)} calls
 * {@link #onInsetsChanged(View, Rect, DeviceProfile)} as its last statement.</p>
 */
public final class ElyraDockController {

    private ElyraDockController() {}

    /**
     * Rebuilds and applies the dock pill background with device-correct insets.
     * Called every time the window insets change (device rotation, nav bar mode switch,
     * multi-window resize).
     *
     * @param hotseat full-width Hotseat view (height = hotseatBarSizePx)
     * @param insets  window insets reported by {@code Hotseat.setInsets()}
     * @param dp      active DeviceProfile
     */
    public static void onInsetsChanged(View hotseat, Rect insets, DeviceProfile dp) {
        if (!ElyraFeatureFlags.HOTSEAT_SURFACE) return;

        if (dp.isVerticalBarLayout()) {
            // Landscape sidebar layout: clear the pill, no background.
            hotseat.setBackground(null);
            return;
        }

        Context ctx   = hotseat.getContext();
        Resources res = ctx.getResources();

        int cornerPx  = res.getDimensionPixelSize(R.dimen.elyra_dock_corner_radius);
        int leftPx    = res.getDimensionPixelSize(R.dimen.elyra_dock_horizontal_margin);
        int rightPx   = leftPx;
        int topPx     = res.getDimensionPixelSize(R.dimen.elyra_dock_top_gap);
        // Bottom inset = nav bar height so pill sits above gesture/button area.
        // Clamp to at least elyra_dock_bottom_gap for devices reporting zero nav bar height.
        int minBottomPx = res.getDimensionPixelSize(R.dimen.elyra_dock_bottom_gap);
        int bottomPx  = Math.max(insets.bottom, minBottomPx);

        // Dark frosted-glass pill: solid fill + visible hairline border.
        GradientDrawable pill = new GradientDrawable();
        pill.setShape(GradientDrawable.RECTANGLE);
        pill.setColor(ContextCompat.getColor(ctx, R.color.elyra_dock_bg));
        pill.setCornerRadius(cornerPx);
        // Slightly brighter stroke for premium soft-edge definition.
        pill.setStroke(
                (int) (res.getDisplayMetrics().density),   // 1dp in px
                Color.parseColor("#33FFFFFF"));

        hotseat.setBackground(new InsetDrawable(pill, leftPx, topPx, rightPx, bottomPx));
    }
}
