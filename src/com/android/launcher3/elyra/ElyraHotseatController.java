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

import android.view.View;

import androidx.core.content.ContextCompat;

/**
 * Applies Elyra visual styling to the Hotseat (dock) surface.
 * Hook: {@code ElyraHotseatController.apply(this)} at the end of {@code Hotseat(Context, AttributeSet, int)}.
 */
public final class ElyraHotseatController {

    private ElyraHotseatController() {}

    /**
     * Sets the Elyra frosted dock background on the given hotseat view.
     * Does nothing if {@link ElyraFeatureFlags#HOTSEAT_SURFACE} is disabled.
     */
    public static void apply(View hotseat) {
        if (!ElyraFeatureFlags.HOTSEAT_SURFACE) return;
        hotseat.setBackground(
                ContextCompat.getDrawable(hotseat.getContext(),
                        ElyraDesignTokens.hotseatBgDrawable()));
    }
}
