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
 * Detail screen for "Layar Utama" settings — no hero card, only the 3 quick rows.
 * The activity header ("Layar Utama" title + back button) is managed by ElyraSettingsActivity.
 */
public final class ElyraHomeDetailFragment extends Fragment {

    private SharedPreferences mPrefs;

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
        return inflater.inflate(R.layout.elyra_home_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Apply the Elyra "Adaptive surfaces" appearance setting to this detail card.
        com.android.launcher3.elyra.ElyraAppearanceController.applyCardSurface(
                view.findViewById(R.id.card_quick));

        setupNotificationDotsRow(view);
        setupAddToHomeRow(view);
        setupRotationRow(view);

        // Granular home-layout rows. These are shown for structure but disabled until their
        // runtime backends are wired, so no fake active control is exposed.
        bindSoonRow(view.findViewById(R.id.row_grid),
                R.drawable.elyra_ic_grid, R.string.elyra_row_grid_title);
        bindSoonRow(view.findViewById(R.id.row_icon_size),
                R.drawable.elyra_ic_grid, R.string.elyra_row_icon_size_title);
        bindSoonRow(view.findViewById(R.id.row_labels),
                R.drawable.elyra_ic_home, R.string.elyra_row_labels_title);
        bindSoonRow(view.findViewById(R.id.row_page_indicator),
                R.drawable.elyra_ic_home, R.string.elyra_row_page_indicator_title);
    }

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

    private void bindSoonRow(View row, int iconRes, int titleRes) {
        if (row == null) return;
        ((ImageView) row.findViewById(R.id.row_icon)).setImageResource(iconRes);
        ((TextView) row.findViewById(R.id.row_title)).setText(titleRes);
        ((TextView) row.findViewById(R.id.row_summary)).setText(R.string.elyra_row_coming_soon);
        row.findViewById(R.id.row_switch).setVisibility(View.GONE);
        row.setEnabled(false);
        row.setClickable(false);
        row.setAlpha(0.45f);
    }

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
}
