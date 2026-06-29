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
import android.content.SharedPreferences;

import com.android.launcher3.SessionCommitReceiver;

/**
 * Controls the Elyra home mode preference.
 *
 * <p>Two modes are supported:
 * <ul>
 *   <li>{@link #MODE_DRAWER}    — standard Launcher3 behaviour with an app drawer (default)</li>
 *   <li>{@link #MODE_HOME_ONLY} — drawer swipe gesture is blocked; all apps live on home pages</li>
 * </ul>
 *
 * Hook: {@code ElyraHomeModeController.isDrawerDisabled(mLauncher)} in
 * {@code PortraitStatesTouchController.getTargetState()} before returning {@code ALL_APPS}.
 */
public final class ElyraHomeModeController {

    public static final String PREF_KEY = "elyra_home_mode";

    public static final String MODE_DRAWER    = "drawer";
    public static final String MODE_HOME_ONLY = "home_only";

    private ElyraHomeModeController() {}

    /**
     * Returns {@code true} when the user has selected Home-Only mode and the app drawer
     * swipe gesture should be suppressed.
     *
     * <p>Always returns {@code false} if {@link ElyraFeatureFlags#HOME_MODE_OPTIONS} is off.</p>
     */
    public static boolean isDrawerDisabled(Context context) {
        if (!ElyraFeatureFlags.HOME_MODE_OPTIONS) return false;
        return MODE_HOME_ONLY.equals(getMode(context));
    }

    /** Reads the persisted home mode, defaulting to {@link #MODE_DRAWER}. */
    public static String getMode(Context context) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(com.android.launcher3.LauncherFiles.SHARED_PREFERENCES_KEY,
                        Context.MODE_PRIVATE);
        return prefs.getString(PREF_KEY, MODE_DRAWER);
    }

    /**
     * Called when the user selects a new home mode from settings.
     *
     * <p>When switching to {@link #MODE_HOME_ONLY}, automatically enables
     * "add new apps to home screen" ({@link SessionCommitReceiver#ADD_ICON_PREFERENCE_KEY})
     * so future installs appear directly on home pages instead of only in the drawer.</p>
     */
    public static void onModeChanged(Context context, String newMode) {
        if (!MODE_HOME_ONLY.equals(newMode)) return;
        context.getApplicationContext()
                .getSharedPreferences(com.android.launcher3.LauncherFiles.SHARED_PREFERENCES_KEY,
                        Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_KEY, true)
                .apply();
    }
}
