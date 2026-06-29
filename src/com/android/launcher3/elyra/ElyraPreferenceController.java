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

import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;

/**
 * Bridges Elyra appearance preferences declared in {@code launcher_preferences.xml}.
 * Hook: {@code ElyraPreferenceController.configure(screen)} in
 * {@code SettingsActivity.LauncherSettingsFragment.onCreatePreferences()}.
 */
public final class ElyraPreferenceController {

    private ElyraPreferenceController() {}

    private static final String KEY_CATEGORY = "elyra_appearance_category";
    private static final String KEY_FLOATING_DOCK = "elyra_floating_dock";
    private static final String KEY_DRAWER_BLUR = "elyra_drawer_blur";

    /**
     * Configures Elyra preference entries on the given screen.
     * Hides the entire category if {@link ElyraFeatureFlags#APPEARANCE_SETTINGS} is disabled.
     * Ensures unimplemented toggles remain invisible.
     */
    public static void configure(PreferenceGroup screen) {
        Preference category = screen.findPreference(KEY_CATEGORY);
        if (category == null) return;

        if (!ElyraFeatureFlags.APPEARANCE_SETTINGS) {
            category.setVisible(false);
            return;
        }

        // Keep unimplemented toggles hidden until their runtime logic is wired.
        setHidden(screen, KEY_FLOATING_DOCK);
        setHidden(screen, KEY_DRAWER_BLUR);
    }

    private static void setHidden(PreferenceGroup screen, String key) {
        Preference pref = screen.findPreference(key);
        if (pref != null) pref.setVisible(false);
    }
}
