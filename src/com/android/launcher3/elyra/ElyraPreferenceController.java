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

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;

import com.android.launcher3.InvariantDeviceProfile;

/**
 * Bridges Elyra appearance preferences declared in {@code launcher_preferences.xml}.
 * Hook: {@code ElyraPreferenceController.configure(screen)} in
 * {@code SettingsActivity.LauncherSettingsFragment.onCreatePreferences()}.
 */
public final class ElyraPreferenceController {

    private ElyraPreferenceController() {}

    private static final String KEY_APPEARANCE_CATEGORY = "elyra_appearance_category";
    private static final String KEY_HOME_CATEGORY        = "elyra_home_category";
    private static final String KEY_FOLDER_CATEGORY      = "elyra_folder_category";
    private static final String KEY_FLOATING_DOCK        = "elyra_floating_dock";
    private static final String KEY_DRAWER_BLUR          = "elyra_drawer_blur";
    private static final String KEY_HOME_MODE            = "elyra_home_mode";
    private static final String KEY_GRID_PRESET          = "elyra_grid_preset";
    private static final String KEY_FOLDER_STYLE         = "elyra_folder_style";

    /**
     * Configures Elyra preference entries on the given screen.
     * Hides the entire appearance category if {@link ElyraFeatureFlags#APPEARANCE_SETTINGS} is
     * disabled. Hides each feature section individually if its flag is off.
     */
    public static void configure(PreferenceGroup screen) {
        final Context ctx = screen.getContext();

        // Legacy appearance category
        Preference appearanceCategory = screen.findPreference(KEY_APPEARANCE_CATEGORY);
        if (appearanceCategory != null) {
            if (!ElyraFeatureFlags.APPEARANCE_SETTINGS) {
                appearanceCategory.setVisible(false);
            } else {
                setHidden(screen, KEY_FLOATING_DOCK);
                setHidden(screen, KEY_DRAWER_BLUR);
            }
        }

        // Home screen category: mode + grid preset
        Preference homeCategory = screen.findPreference(KEY_HOME_CATEGORY);
        if (homeCategory != null) {
            if (!ElyraFeatureFlags.HOME_MODE_OPTIONS && !ElyraFeatureFlags.GRID_PRESETS) {
                homeCategory.setVisible(false);
            } else {
                configureHomeMode(screen, ctx);
                configureGridPreset(screen, ctx);
            }
        }

        // Folder category: style
        Preference folderCategory = screen.findPreference(KEY_FOLDER_CATEGORY);
        if (folderCategory != null) {
            if (!ElyraFeatureFlags.FOLDER_STYLE_OPTIONS) {
                folderCategory.setVisible(false);
            } else {
                configureFolderStyle(screen, ctx);
            }
        }
    }

    private static void configureHomeMode(PreferenceGroup screen, Context ctx) {
        ListPreference pref = screen.findPreference(KEY_HOME_MODE);
        if (pref == null) return;
        if (!ElyraFeatureFlags.HOME_MODE_OPTIONS) {
            pref.setVisible(false);
            return;
        }
        // Reflect the currently stored value in the summary
        updateListSummary(pref);
        pref.setOnPreferenceChangeListener((p, newValue) -> {
            updateListSummary((ListPreference) p, (String) newValue);
            return true;
        });
    }

    private static void configureGridPreset(PreferenceGroup screen, Context ctx) {
        ListPreference pref = screen.findPreference(KEY_GRID_PRESET);
        if (pref == null) return;
        if (!ElyraFeatureFlags.GRID_PRESETS) {
            pref.setVisible(false);
            return;
        }

        // Seed the displayed value from the IDP's current grid name so the summary is accurate.
        String currentGrid = InvariantDeviceProfile.INSTANCE.get(ctx).dbFile;
        // The pref value is the grid name (e.g. "5_by_5"), not the db file name.
        // Leave the persistent value as-is; SettingsActivity binds to shared prefs automatically.

        updateListSummary(pref);
        pref.setOnPreferenceChangeListener((p, newValue) -> {
            String gridName = (String) newValue;
            updateListSummary((ListPreference) p, gridName);
            // Trigger the standard grid migration + workspace rebuild.
            InvariantDeviceProfile.INSTANCE.get(ctx).setCurrentGrid(ctx, gridName);
            return true;
        });
    }

    private static void configureFolderStyle(PreferenceGroup screen, Context ctx) {
        ListPreference pref = screen.findPreference(KEY_FOLDER_STYLE);
        if (pref == null) return;

        updateListSummary(pref);
        pref.setOnPreferenceChangeListener((p, newValue) -> {
            updateListSummary((ListPreference) p, (String) newValue);
            // Re-run grid init so IDP rebuilds DeviceProfiles with the new folder grid.
            InvariantDeviceProfile.INSTANCE.get(ctx).onConfigChanged(ctx);
            return true;
        });
    }

    // -------------------------------------------------------------------------

    private static void setHidden(PreferenceGroup screen, String key) {
        Preference pref = screen.findPreference(key);
        if (pref != null) pref.setVisible(false);
    }

    private static void updateListSummary(ListPreference pref) {
        CharSequence entry = pref.getEntry();
        if (entry != null) pref.setSummary(entry);
    }

    private static void updateListSummary(ListPreference pref, String value) {
        int idx = pref.findIndexOfValue(value);
        if (idx >= 0) pref.setSummary(pref.getEntries()[idx]);
    }
}
