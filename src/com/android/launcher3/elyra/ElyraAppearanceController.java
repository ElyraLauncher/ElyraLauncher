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
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.android.launcher3.LauncherFiles;
import com.android.launcher3.R;

/**
 * Owns the Elyra "Adaptive surfaces" appearance setting and applies it to Elyra-owned surfaces.
 *
 * <p>Scope is deliberately limited to surfaces fully controlled by Elyra code (the settings
 * dashboard and detail cards). Adaptive mode draws a tonal, slightly-raised card; flat mode draws
 * a near-background fill defined by a hairline instead of elevation. Stock Launcher3 theming is
 * never touched, so normal home, edit mode, dock, drawer and folder are unaffected.</p>
 */
public final class ElyraAppearanceController {

    /** SharedPreferences key backing the "Adaptive surfaces" setting. */
    public static final String KEY_ADAPTIVE_SURFACES = "elyra_adaptive_surfaces";
    /** Default: adaptive/tonal surfaces (preserves the current visual behavior). */
    public static final boolean ADAPTIVE_SURFACES_DEFAULT = true;

    private static final float CARD_RADIUS_DP = 20f;

    private ElyraAppearanceController() {}

    /** Reads the persisted "Adaptive surfaces" preference. */
    public static boolean isAdaptiveSurfaces(Context ctx) {
        return ctx.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(KEY_ADAPTIVE_SURFACES, ADAPTIVE_SURFACES_DEFAULT);
    }

    /**
     * Applies the current "Adaptive surfaces" style to an Elyra settings card container.
     * Safe to call repeatedly and on a {@code null} view (ignored).
     */
    public static void applyCardSurface(View card) {
        if (card == null) {
            return;
        }
        Context ctx = card.getContext();
        Resources res = ctx.getResources();
        float radius = CARD_RADIUS_DP * res.getDisplayMetrics().density;

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(radius);
        if (isAdaptiveSurfaces(ctx)) {
            bg.setColor(ContextCompat.getColor(ctx, R.color.elyra_surface_adaptive));
        } else {
            bg.setColor(ContextCompat.getColor(ctx, R.color.elyra_surface_flat));
            bg.setStroke((int) res.getDisplayMetrics().density,
                    ContextCompat.getColor(ctx, R.color.elyra_surface_flat_stroke));
        }
        card.setBackground(bg);
    }
}
