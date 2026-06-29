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

import androidx.core.content.ContextCompat;

import com.android.launcher3.R;

/**
 * Resolves Elyra design token resource IDs at runtime.
 * Use this class rather than hard-coding R.color / R.dimen values in controllers.
 */
public final class ElyraDesignTokens {

    private ElyraDesignTokens() {}

    public static int hotseatBgColor(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.elyra_dock_bg);
    }

    public static int folderBgColor(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.elyra_folder_bg);
    }

    public static int drawerScrimColor(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.elyra_drawer_scrim);
    }

    public static float folderCornerRadius(Context ctx) {
        return ctx.getResources().getDimension(R.dimen.elyra_folder_corner_radius);
    }

    public static int hotseatBgDrawable() {
        return R.drawable.elyra_hotseat_bg;
    }

    public static int searchBgDrawable() {
        return R.drawable.elyra_search_bg;
    }
}
