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

    /** Optional intent extra requesting a specific landing route (see ROUTE_* below). */
    public static final String EXTRA_ROUTE = "com.android.launcher3.elyra.EXTRA_ROUTE";
    /** Land directly on the Home Screen (layout) detail, with the dashboard behind it. */
    public static final String ROUTE_HOME = "home";

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
            showDashboard();
            // Deep-link: the edit-mode "Layout" action lands on the Home Screen detail with the
            // dashboard placed behind it, so Back returns to the dashboard then to home.
            if (ROUTE_HOME.equals(getIntent().getStringExtra(EXTRA_ROUTE))) {
                showHomeDetail();
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

    /** Places a feature detail screen (Dock / Drawer / Folder / Appearance / Search / Edit Mode). */
    public void showFeatureDetail(int section) {
        TextView titleView = findViewById(R.id.elyra_header_title);
        if (titleView != null) {
            titleView.setText(ElyraFeatureDetailFragment.titleResFor(section));
        }
        View header = findViewById(R.id.elyra_header);
        if (header != null) header.setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.elyra_content_frame, ElyraFeatureDetailFragment.create(section))
                .addToBackStack("feature_" + section)
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
