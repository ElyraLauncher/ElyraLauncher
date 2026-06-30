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
import android.view.View;

import androidx.core.content.ContextCompat;

/**
 * Applies Elyra visual styling to the All Apps drawer surface and search bar.
 *
 * Hooks:
 * <ul>
 *   <li>{@code ElyraAllAppsController.applySearchBox(mSearchContainer)} in
 *       {@code ActivityAllAppsContainerView.onFinishInflate()}</li>
 *   <li>{@code ElyraAllAppsController.resolveScrimColor(context, defaultColor)} when setting
 *       {@code mBottomSheetBackgroundColor}</li>
 * </ul>
 */
public final class ElyraAllAppsController {

    private ElyraAllAppsController() {}

    /**
     * Swaps the search box background for the Elyra-branded drawable.
     * Does nothing if {@link ElyraFeatureFlags#SEARCH_SURFACE} is disabled.
     */
    public static void applySearchBox(View searchContainer) {
        if (!ElyraFeatureFlags.SEARCH_SURFACE) return;
        searchContainer.setBackground(
                ContextCompat.getDrawable(searchContainer.getContext(),
                        ElyraDesignTokens.searchBgDrawable()));
    }

    /**
     * Returns the Elyra drawer scrim color if {@link ElyraFeatureFlags#ALL_APPS_SURFACE} is
     * enabled, or {@code defaultColor} otherwise.
     */
    public static int resolveScrimColor(Context ctx, int defaultColor) {
        if (!ElyraFeatureFlags.ALL_APPS_SURFACE) return defaultColor;
        return ElyraDesignTokens.drawerScrimColor(ctx);
    }
}
