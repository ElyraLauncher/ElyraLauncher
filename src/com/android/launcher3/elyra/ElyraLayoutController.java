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

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.data.ItemInfo;

/**
 * Owns the Elyra workspace layout settings: the home page indicator and workspace icon labels.
 *
 * <p>Both are applied to normal home only, via {@link #apply(Launcher)} called from the launcher's
 * NORMAL-state hooks. Edit mode independently drives the page indicator (it is lifted/forced
 * visible while editing), so this controller never fights it. Icon labels are toggled by setting
 * the label text alpha on workspace app/shortcut icons — folder labels (a separate FolderIcon
 * concern owned by {@link ElyraFolderController}) and dock icons are left untouched.</p>
 */
public final class ElyraLayoutController {

    /** SharedPreferences key backing the "Page indicator" setting. */
    public static final String KEY_PAGE_INDICATOR = "elyra_page_indicator";
    /** Default ON: preserves the current page indicator behavior. */
    public static final boolean PAGE_INDICATOR_DEFAULT = true;

    /** SharedPreferences key backing the "Icon labels" setting. */
    public static final String KEY_ICON_LABELS = "elyra_icon_labels";
    /** Default ON: preserves the current workspace label behavior. */
    public static final boolean ICON_LABELS_DEFAULT = true;

    private ElyraLayoutController() {}

    /** Reads the persisted "Page indicator" preference. */
    public static boolean isPageIndicatorEnabled(Context ctx) {
        return ctx.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(KEY_PAGE_INDICATOR, PAGE_INDICATOR_DEFAULT);
    }

    /** Reads the persisted "Icon labels" preference. */
    public static boolean isIconLabelsEnabled(Context ctx) {
        return ctx.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(KEY_ICON_LABELS, ICON_LABELS_DEFAULT);
    }

    /** Applies both layout preferences to normal home. */
    public static void apply(Launcher launcher) {
        applyPageIndicator(launcher);
        applyIconLabels(launcher);
    }

    /**
     * Applies the "Page indicator" preference to the workspace page indicator's visibility in
     * normal home. Edit mode forces the indicator visible independently, so this only governs
     * normal home.
     */
    public static void applyPageIndicator(Launcher launcher) {
        if (launcher.getWorkspace() == null) {
            return;
        }
        View indicator = (View) launcher.getWorkspace().getPageIndicator();
        if (indicator == null) {
            return;
        }
        indicator.setVisibility(isPageIndicatorEnabled(launcher) ? View.VISIBLE : View.GONE);
    }

    /**
     * Applies the "Icon labels" preference to every workspace app/shortcut icon label by toggling
     * its text alpha ({@link BubbleTextView#setTextVisibility(boolean)}). Dock (hotseat) icons and
     * folder icons are excluded, so drag/drop, folders and the dock are unaffected.
     */
    public static void applyIconLabels(Launcher launcher) {
        if (launcher.getWorkspace() == null) {
            return;
        }
        boolean show = isIconLabelsEnabled(launcher);
        launcher.getWorkspace().mapOverItems((ItemInfo info, View view) -> {
            if (view instanceof BubbleTextView && info != null
                    && info.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                ((BubbleTextView) view).setTextVisibility(show);
            }
            return false; // keep iterating over all items
        });
    }
}
