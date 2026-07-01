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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.Flags;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.R;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.util.DisplayController;

/**
 * Main Elyra settings dashboard — shows Pusat Elyra hero + all section rows.
 * Hosted inside ElyraSettingsActivity; no ActionBar manipulation needed.
 */
public final class ElyraSettingsDashboardFragment extends Fragment {

    private static final long ROTATE_MS = 4_000L;
    private static final int[] DESCRIPTIONS = {
            R.string.elyra_hero_desc_1,
            R.string.elyra_hero_desc_2,
            R.string.elyra_hero_desc_3,
            R.string.elyra_hero_desc_4,
            R.string.elyra_hero_desc_5,
    };

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private int mDescIndex = 0;
    private TextView mDescView;
    private boolean mRotating;
    private SharedPreferences mPrefs;

    private final Runnable mRotate = new Runnable() {
        @Override
        public void run() {
            if (mDescView == null || !mRotating) return;
            mDescIndex = (mDescIndex + 1) % DESCRIPTIONS.length;
            mDescView.setText(DESCRIPTIONS[mDescIndex]);
            mHandler.postDelayed(this, ROTATE_MS);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = requireContext().getSharedPreferences(
                LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.elyra_settings_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDescView = view.findViewById(R.id.elyra_hero_description);
        if (mDescView != null) {
            mDescView.setText(DESCRIPTIONS[mDescIndex]);
        }

        setupHomeRow(view);
        setupNotificationDotsRow(view);
        setupAddToHomeRow(view);
        setupRotationRow(view);
        setupFeatureRows(view);
    }

    // ── Personalisasi (feature routes) ───────────────────────────────────────────

    private void setupFeatureRows(View root) {
        bindNavRow(root.findViewById(R.id.row_sec_dock),
                R.drawable.elyra_ic_dock,
                R.string.elyra_section_dock_title,
                R.string.elyra_section_dock_summary,
                () -> openFeature(ElyraFeatureDetailFragment.SECTION_DOCK));
        bindNavRow(root.findViewById(R.id.row_sec_drawer),
                R.drawable.elyra_ic_drawer,
                R.string.elyra_section_drawer_title,
                R.string.elyra_section_drawer_summary,
                () -> openFeature(ElyraFeatureDetailFragment.SECTION_DRAWER));
        bindNavRow(root.findViewById(R.id.row_sec_folder),
                R.drawable.elyra_ic_folder,
                R.string.elyra_section_folder_title,
                R.string.elyra_section_folder_summary,
                () -> openFeature(ElyraFeatureDetailFragment.SECTION_FOLDER));
        bindNavRow(root.findViewById(R.id.row_sec_appearance),
                R.drawable.elyra_ic_appearance,
                R.string.elyra_section_appearance_title,
                R.string.elyra_section_appearance_summary,
                () -> openFeature(ElyraFeatureDetailFragment.SECTION_APPEARANCE));
        bindNavRow(root.findViewById(R.id.row_sec_search),
                R.drawable.elyra_ic_search,
                R.string.elyra_section_search_title,
                R.string.elyra_section_search_summary,
                () -> openFeature(ElyraFeatureDetailFragment.SECTION_SEARCH));
        bindNavRow(root.findViewById(R.id.row_sec_editmode),
                R.drawable.elyra_ic_grid,
                R.string.elyra_section_editmode_title,
                R.string.elyra_section_editmode_summary,
                () -> openFeature(ElyraFeatureDetailFragment.SECTION_EDIT_MODE));
    }

    private void openFeature(int section) {
        ((ElyraSettingsActivity) requireActivity()).showFeatureDetail(section);
    }

    // ── Pengaturan utama ────────────────────────────────────────────────────────

    private void setupHomeRow(View root) {
        bindNavRow(root.findViewById(R.id.row_home),
                R.drawable.elyra_ic_home,
                R.string.elyra_home_category,
                R.string.elyra_home_category_summary,
                this::openHomeSettings);
    }

    private void openHomeSettings() {
        ((ElyraSettingsActivity) requireActivity()).showHomeDetail();
    }

    // ── Pengaturan cepat ────────────────────────────────────────────────────────

    private void setupNotificationDotsRow(View root) {
        View row = root.findViewById(R.id.row_notification_dots);
        View divider = root.findViewById(R.id.divider_notif_add);
        if (!BuildConfig.NOTIFICATION_DOTS_ENABLED) {
            if (row != null) row.setVisibility(View.GONE);
            if (divider != null) divider.setVisibility(View.GONE);
            return;
        }
        bindNavRow(row,
                R.drawable.elyra_ic_notification,
                R.string.notification_dots_title,
                R.string.elyra_row_notif_dots_summary,
                () -> startActivity(new Intent("android.settings.NOTIFICATION_SETTINGS")));
    }

    private void setupAddToHomeRow(View root) {
        bindToggleRow(root.findViewById(R.id.row_add_to_home),
                R.drawable.elyra_ic_home,
                R.string.auto_add_shortcuts_label,
                R.string.auto_add_shortcuts_description,
                "pref_add_icon_to_home", true);
    }

    private void setupRotationRow(View root) {
        View row = root.findViewById(R.id.row_rotation);
        View divider = root.findViewById(R.id.divider_add_rotation);
        DisplayController.Info info =
                DisplayController.INSTANCE.get(requireContext()).getInfo();
        boolean show = !Flags.oneGridSpecs() && !info.isTablet(info.realBounds);
        if (!show) {
            if (row != null) row.setVisibility(View.GONE);
            if (divider != null) divider.setVisibility(View.GONE);
            return;
        }
        bindToggleRow(row,
                R.drawable.elyra_ic_grid,
                R.string.allow_rotation_title,
                R.string.allow_rotation_desc,
                RotationHelper.ALLOW_ROTATION_PREFERENCE_KEY,
                RotationHelper.getAllowRotationDefaultValue(info));
    }

    // ── Row binders ─────────────────────────────────────────────────────────────

    private void bindNavRow(View row, int iconRes, int titleRes, int summaryRes,
            Runnable onClick) {
        if (row == null) return;
        ((ImageView) row.findViewById(R.id.row_icon)).setImageResource(iconRes);
        ((TextView) row.findViewById(R.id.row_title)).setText(titleRes);
        ((TextView) row.findViewById(R.id.row_summary)).setText(summaryRes);
        row.findViewById(R.id.row_switch).setVisibility(View.GONE);
        row.setOnClickListener(v -> onClick.run());
    }

    private void bindToggleRow(View row, int iconRes, int titleRes, int summaryRes,
            String prefKey, boolean defaultValue) {
        if (row == null) return;
        ((ImageView) row.findViewById(R.id.row_icon)).setImageResource(iconRes);
        ((TextView) row.findViewById(R.id.row_title)).setText(titleRes);
        ((TextView) row.findViewById(R.id.row_summary)).setText(summaryRes);
        SwitchCompat sw = row.findViewById(R.id.row_switch);
        sw.setShowText(false);
        sw.setClickable(false);
        sw.setOnCheckedChangeListener(null);
        sw.setChecked(mPrefs.getBoolean(prefKey, defaultValue));
        sw.setOnCheckedChangeListener((btn, checked) ->
                mPrefs.edit().putBoolean(prefKey, checked).apply());
        row.setOnClickListener(v -> sw.toggle());
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────────

    @Override
    public void onResume() {
        super.onResume();
        mRotating = true;
        mHandler.postDelayed(mRotate, ROTATE_MS);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRotating = false;
        mHandler.removeCallbacks(mRotate);
    }
}
