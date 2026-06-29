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

/**
 * Entry point for Elyra theme-level overrides.
 * Future use: runtime accent color switching, dynamic surface opacity.
 */
public final class ElyraThemeController {

    private ElyraThemeController() {}

    /**
     * Apply Elyra theme customizations tied to the given context.
     * Currently a no-op; individual surface controllers handle their own styling.
     */
    public static void applyTheme(Context context) {
        // Reserved for runtime theme changes (accent color picker, opacity levels).
    }
}
