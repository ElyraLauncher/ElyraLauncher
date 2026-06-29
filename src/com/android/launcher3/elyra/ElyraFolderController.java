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

/**
 * Applies Elyra visual styling to the folder surface.
 * Hook: {@code ElyraFolderController.apply(getContext(), mBackground)} in {@code Folder(Context, AttributeSet)}.
 */
public final class ElyraFolderController {

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
}
