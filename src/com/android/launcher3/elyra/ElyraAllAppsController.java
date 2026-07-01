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

import com.android.launcher3.LauncherFiles;

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

    /** SharedPreferences key backing the "Show drawer search" setting. */
    public static final String KEY_DRAWER_SEARCH = "elyra_drawer_search";
    /** Default: the drawer search bar is shown (preserves stock behavior). */
    public static final boolean DRAWER_SEARCH_DEFAULT = true;

    private ElyraAllAppsController() {}

    /** Reads the persisted "Show drawer search" preference. */
    public static boolean isDrawerSearchEnabled(Context ctx) {
        return ctx.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(KEY_DRAWER_SEARCH, DRAWER_SEARCH_DEFAULT);
    }

    /**
     * Applies the "Show drawer search" preference to the All Apps search bar visibility.
     *
     * <p>Only toggles the search container view; the app list, its adapter and scrolling are
     * untouched, so the drawer stays fully usable when the search bar is hidden. Safe to call on
     * every drawer open — a {@code null} container (not yet inflated) is ignored.</p>
     */
    public static void applySearchVisibility(View searchContainer, Context ctx) {
        if (searchContainer == null) return;
        searchContainer.setVisibility(isDrawerSearchEnabled(ctx) ? View.VISIBLE : View.GONE);
    }

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
