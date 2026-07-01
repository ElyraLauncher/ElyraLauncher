/*
 * Copyright (C) 2024 ElyraLauncher Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.android.launcher3.elyra.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.android.launcher3.R;

/**
 * Shell activity for all Elyra settings screens.
 *
 * Owns the header bar (back button + title) and the content FrameLayout.
 * Fragments placed here must NOT attempt to manipulate an ActionBar.
 *
 * Navigation:
 *   showDashboard() — hides header, places ElyraSettingsDashboardFragment
 *   showHomeDetail() — shows header with "Layar Utama", places ElyraHomeDetailFragment
 */
public final class ElyraSettingsActivity extends AppCompatActivity {

    /** When set to true, opens directly to {@link #showHomeDetail()} instead of the dashboard. */
    public static final String EXTRA_SHOW_HOME_DETAIL =
            "com.android.launcher3.elyra.EXTRA_SHOW_HOME_DETAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.elyra_settings_activity);

        ImageButton back = findViewById(R.id.elyra_back);
        back.setOnClickListener(v -> onBackPressed());

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            boolean hasBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
            View header = findViewById(R.id.elyra_header);
            if (header != null) {
                header.setVisibility(hasBack ? View.VISIBLE : View.GONE);
            }
        });

        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra(EXTRA_SHOW_HOME_DETAIL, false)) {
                showHomeDetail();
            } else {
                showDashboard();
            }
        }
    }

    /** Places the main dashboard; header is hidden. */
    public void showDashboard() {
        View header = findViewById(R.id.elyra_header);
        if (header != null) header.setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.elyra_content_frame, new ElyraSettingsDashboardFragment())
                .commit();
    }

    /** Places the Layar Utama detail; shows header with correct title. */
    public void showHomeDetail() {
        TextView titleView = findViewById(R.id.elyra_header_title);
        if (titleView != null) {
            titleView.setText(R.string.elyra_home_category);
        }
        View header = findViewById(R.id.elyra_header);
        if (header != null) header.setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.elyra_content_frame, new ElyraHomeDetailFragment())
                .addToBackStack("home_detail")
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
