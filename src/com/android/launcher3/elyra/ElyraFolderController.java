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
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.model.data.ItemInfo;

/**
 * Applies Elyra visual styling to the folder surface, and owns the "Show folder labels" setting.
 * Hook: {@code ElyraFolderController.apply(getContext(), mBackground)} in {@code Folder(Context, AttributeSet)}.
 */
public final class ElyraFolderController {

    /** SharedPreferences key backing the "Show folder labels" setting. */
    public static final String KEY_SHOW_FOLDER_LABELS = "elyra_show_folder_labels";
    /** Default: folder labels are shown (preserves stock behavior). */
    public static final boolean SHOW_FOLDER_LABELS_DEFAULT = true;

    private ElyraFolderController() {}

    /**
     * Overrides the folder background drawable's color and corner radius with Elyra tokens.
     * The base drawable ({@code round_rect_folder}) stays unmodified — this is a runtime override.
     * Does nothing if {@link ElyraFeatureFlags#FOLDER_SURFACE} is disabled.
     */
    public static void apply(Context ctx, GradientDrawable background) {
        if (!ElyraFeatureFlags.FOLDER_SURFACE) return;
        background.setColor(ElyraDesignTokens.folderBgColor(ctx));
        background.setCornerRadius(ElyraDesignTokens.folderCornerRadius(ctx));
    }

    /** Reads the persisted "Show folder labels" preference. */
    public static boolean isFolderLabelsEnabled(Context ctx) {
        return ctx.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .getBoolean(KEY_SHOW_FOLDER_LABELS, SHOW_FOLDER_LABELS_DEFAULT);
    }

    /**
     * Applies the "Show folder labels" preference to a single workspace folder icon's label.
     * Only toggles the label text alpha (via {@link BubbleTextView#setTextVisibility(boolean)}),
     * leaving the icon fully tappable and the layout unchanged. Called from
     * {@code FolderIcon.fromXml} so freshly-bound folders honor the setting.
     */
    public static void applyLabelVisibility(BubbleTextView label, Context ctx) {
        if (label == null) return;
        label.setTextVisibility(isFolderLabelsEnabled(ctx));
    }

    /**
     * Re-applies the "Show folder labels" preference to every folder icon currently on the
     * workspace and hotseat, so a settings toggle reflects live without a restart. Safe no-op when
     * the workspace is not yet available.
     */
    public static void refreshFolderLabels(Launcher launcher) {
        if (launcher.getWorkspace() == null) return;
        boolean show = isFolderLabelsEnabled(launcher);
        launcher.getWorkspace().mapOverItems((ItemInfo info, View view) -> {
            if (view instanceof FolderIcon) {
                BubbleTextView name = ((FolderIcon) view).getFolderName();
                if (name != null) {
                    name.setTextVisibility(show);
                }
            }
            return false; // keep iterating over all items
        });
    }
}
