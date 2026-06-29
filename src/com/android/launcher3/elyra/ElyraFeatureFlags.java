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

/**
 * Compile-time feature flags for the Elyra visual layer.
 * Set a flag to false to disable that surface entirely and fall back to Launcher3 defaults.
 */
public final class ElyraFeatureFlags {

    private ElyraFeatureFlags() {}

    /** Frosted semi-transparent surface behind hotseat icons. */
    public static final boolean HOTSEAT_SURFACE = true;

    /** Violet-tinted folder background with 24dp corner radius. */
    public static final boolean FOLDER_SURFACE = true;

    /** Violet-tinted all-apps bottom-sheet scrim. */
    public static final boolean ALL_APPS_SURFACE = true;

    /** Elyra-styled search bar in all-apps. */
    public static final boolean SEARCH_SURFACE = true;

    /** Elyra appearance section in launcher settings. */
    public static final boolean APPEARANCE_SETTINGS = true;
}
