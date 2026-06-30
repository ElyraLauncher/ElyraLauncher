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
 * <p>Visual result: a dark frosted-glass pill that spans ~86–90% of screen width with large
 * rounded corners ({@code elyra_dock_corner_radius}).  After setting the background, the
 * controller overrides {@code Hotseat.setPadding()} so icons are vertically centred within the
 * visible pill area and horizontally contained inside the pill's inner margin.  Without this
 * override DeviceProfile places icons at {@code top=0}, causing them to appear above the pill.</p>
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
            hotseat.setBackground(null);
            return;
        }

        Context ctx   = hotseat.getContext();
        Resources res = ctx.getResources();

        int cornerPx       = res.getDimensionPixelSize(R.dimen.elyra_dock_corner_radius);
        int horizMarginPx  = res.getDimensionPixelSize(R.dimen.elyra_dock_horizontal_margin);
        int paddingHorizPx = res.getDimensionPixelSize(R.dimen.elyra_dock_padding_horizontal);
        int topGapPx       = res.getDimensionPixelSize(R.dimen.elyra_dock_top_gap);
        int minBottomPx    = res.getDimensionPixelSize(R.dimen.elyra_dock_bottom_gap);
        // Nav bar inset defines where the pill must stop; clamp to minimum for button-nav devices.
        int bottomGapPx    = Math.max(insets.bottom, minBottomPx);

        // Dark frosted-glass pill: solid fill + hairline border.
        GradientDrawable pill = new GradientDrawable();
        pill.setShape(GradientDrawable.RECTANGLE);
        pill.setColor(ContextCompat.getColor(ctx, R.color.elyra_dock_bg));
        pill.setCornerRadius(cornerPx);
        pill.setStroke(
                (int) (res.getDisplayMetrics().density),
                Color.parseColor("#33FFFFFF"));
        hotseat.setBackground(
                new InsetDrawable(pill, horizMarginPx, topGapPx, horizMarginPx, bottomGapPx));

        // DeviceProfile.getHotseatLayoutPadding() returns top=0 for the default portrait path,
        // placing icons at y=0 of the Hotseat view — above the pill's top edge (topGapPx).
        // Override the padding so icons are vertically centred within the visible pill area
        // and horizontally contained within the pill's inner bounds.
        int pillHeightPx = dp.hotseatBarSizePx - topGapPx - bottomGapPx;
        int iconHeightPx = dp.hotseatCellHeightPx;
        if (pillHeightPx > iconHeightPx) {
            int vertOffset = (pillHeightPx - iconHeightPx) / 2;
            hotseat.setPadding(
                    horizMarginPx + paddingHorizPx,   // left: pill edge + inner margin
                    topGapPx + vertOffset,             // top: pill top + centering offset
                    horizMarginPx + paddingHorizPx,   // right: symmetric
                    bottomGapPx + vertOffset);         // bottom: nav inset + centering offset
        }
    }
}
