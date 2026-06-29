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

import java.util.Arrays;

/**
 * Controls the Elyra folder style preference.
 *
 * Supported styles affect the open-folder popup grid dimensions (rows × columns):
 * <ul>
 *   <li>standard — uses the grid-option defaults from device_profiles.xml</li>
 *   <li>large    — 4 × 4</li>
 *   <li>wide     — 2 rows × 4 columns (horizontal emphasis)</li>
 *   <li>tall     — 4 rows × 2 columns (vertical emphasis)</li>
 * </ul>
 *
 * Hook: {@code ElyraFolderStyleController.patchIDP(context, this)} added at the end of
 * {@code InvariantDeviceProfile.initGrid(Context, Info, DisplayOption)}, immediately after
 * {@code applyPartnerDeviceProfileOverrides()}.
 */
public final class ElyraFolderStyleController {

    public static final String PREF_KEY = "elyra_folder_style";

    public static final String STYLE_STANDARD = "standard";
    public static final String STYLE_LARGE    = "large";
    public static final String STYLE_WIDE     = "wide";
    public static final String STYLE_TALL     = "tall";

    private ElyraFolderStyleController() {}

    /**
     * Reads the folder style preference and, if non-standard, overwrites
     * {@code idp.numFolderRows} and {@code idp.numFolderColumns} with new arrays so that
     * DeviceProfile instances built afterwards use the correct folder grid size.
     *
     * <p>This must be called AFTER the XML-based grid fields are assigned in
     * {@code InvariantDeviceProfile.initGrid()} and BEFORE the DeviceProfile list is built.</p>
     *
     * <p>Does nothing if {@link ElyraFeatureFlags#FOLDER_STYLE_OPTIONS} is disabled.</p>
     */
    public static void patchIDP(Context context, com.android.launcher3.InvariantDeviceProfile idp) {
        if (!ElyraFeatureFlags.FOLDER_STYLE_OPTIONS) return;

        String style = getStyle(context);
        if (STYLE_STANDARD.equals(style)) return;

        int rows = getRows(style, idp.numFolderRows[0]);
        int cols = getColumns(style, idp.numFolderColumns[0]);

        // Create new arrays so we do not mutate the GridOption's own arrays.
        int[] newRows = new int[idp.numFolderRows.length];
        int[] newCols = new int[idp.numFolderColumns.length];
        Arrays.fill(newRows, rows);
        Arrays.fill(newCols, cols);
        idp.numFolderRows = newRows;
        idp.numFolderColumns = newCols;
    }

    /** Reads the persisted folder style, defaulting to {@link #STYLE_STANDARD}. */
    public static String getStyle(Context context) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(com.android.launcher3.LauncherFiles.SHARED_PREFERENCES_KEY,
                        Context.MODE_PRIVATE);
        return prefs.getString(PREF_KEY, STYLE_STANDARD);
    }

    /** Returns the folder row count for the given style. */
    public static int getRows(String style, int defaultRows) {
        switch (style) {
            case STYLE_LARGE: return 4;
            case STYLE_WIDE:  return 2;
            case STYLE_TALL:  return 4;
            default:          return defaultRows;
        }
    }

    /** Returns the folder column count for the given style. */
    public static int getColumns(String style, int defaultCols) {
        switch (style) {
            case STYLE_LARGE: return 4;
            case STYLE_WIDE:  return 4;
            case STYLE_TALL:  return 2;
            default:          return defaultCols;
        }
    }
}
