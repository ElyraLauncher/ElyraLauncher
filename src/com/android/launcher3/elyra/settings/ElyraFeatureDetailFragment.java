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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.android.launcher3.LauncherFiles;
import com.android.launcher3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic detail screen for the Elyra launcher feature routes reached from the dashboard's
 * Personalization card: Dock, App Drawer, Folder, Appearance, Search and Edit Mode.
 *
 * <p>Rows are described declaratively per section and inflated into a single card. Each row is
 * either a real preference-backed toggle or a clearly-disabled "coming soon" row — there are no
 * dead buttons and no fake active controls. Sections whose backend is not wired yet render only
 * disabled rows so the navigation structure is complete while staying honest about availability.
 */
public final class ElyraFeatureDetailFragment extends Fragment {

    static final String ARG_SECTION = "elyra_section";

    static final int SECTION_DOCK = 1;
    static final int SECTION_DRAWER = 2;
    static final int SECTION_FOLDER = 3;
    static final int SECTION_APPEARANCE = 4;
    static final int SECTION_SEARCH = 5;
    static final int SECTION_EDIT_MODE = 6;

    private SharedPreferences mPrefs;

    /** Builds a detail fragment for the given SECTION_* constant. */
    static ElyraFeatureDetailFragment create(int section) {
        ElyraFeatureDetailFragment f = new ElyraFeatureDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION, section);
        f.setArguments(args);
        return f;
    }

    /** Header title for a section; used by ElyraSettingsActivity. */
    @StringRes
    static int titleResFor(int section) {
        switch (section) {
            case SECTION_DOCK: return R.string.elyra_section_dock_title;
            case SECTION_DRAWER: return R.string.elyra_section_drawer_title;
            case SECTION_FOLDER: return R.string.elyra_section_folder_title;
            case SECTION_APPEARANCE: return R.string.elyra_section_appearance_title;
            case SECTION_SEARCH: return R.string.elyra_section_search_title;
            case SECTION_EDIT_MODE: return R.string.elyra_section_editmode_title;
            default: return R.string.elyra_settings_section_personalize;
        }
    }

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
        return inflater.inflate(R.layout.elyra_feature_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup card = view.findViewById(R.id.elyra_feature_card);
        int section = getArguments() != null
                ? getArguments().getInt(ARG_SECTION, SECTION_DOCK) : SECTION_DOCK;

        List<Row> rows = rowsFor(section);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) {
                card.addView(makeDivider(card));
            }
            View row = inflater.inflate(R.layout.elyra_settings_row, card, false);
            bindRow(row, rows.get(i));
            card.addView(row);
        }
    }

    // ── Section content ──────────────────────────────────────────────────────────

    private List<Row> rowsFor(int section) {
        List<Row> rows = new ArrayList<>();
        switch (section) {
            case SECTION_DOCK:
                rows.add(Row.soon(R.drawable.elyra_ic_dock, R.string.elyra_row_show_dock_title));
                rows.add(Row.soon(R.drawable.elyra_ic_dock,
                        R.string.elyra_row_dock_appearance_title));
                rows.add(Row.soon(R.drawable.elyra_ic_dock,
                        R.string.elyra_row_dock_spacing_title));
                break;
            case SECTION_DRAWER:
                rows.add(Row.soon(R.drawable.elyra_ic_drawer,
                        R.string.elyra_row_drawer_style_title));
                rows.add(Row.soon(R.drawable.elyra_ic_drawer,
                        R.string.elyra_row_drawer_search_title));
                rows.add(Row.soon(R.drawable.elyra_ic_drawer,
                        R.string.elyra_row_sort_mode_title));
                break;
            case SECTION_FOLDER:
                rows.add(Row.soon(R.drawable.elyra_ic_folder,
                        R.string.elyra_row_folder_style_title));
                rows.add(Row.soon(R.drawable.elyra_ic_folder,
                        R.string.elyra_row_folder_preview_title));
                rows.add(Row.soon(R.drawable.elyra_ic_folder,
                        R.string.elyra_row_folder_label_title));
                break;
            case SECTION_APPEARANCE:
                rows.add(Row.soon(R.drawable.elyra_ic_appearance,
                        R.string.elyra_row_theme_title));
                rows.add(Row.soon(R.drawable.elyra_ic_appearance,
                        R.string.elyra_row_adaptive_surfaces_title));
                rows.add(Row.soon(R.drawable.elyra_ic_appearance,
                        R.string.elyra_row_icon_bg_title));
                break;
            case SECTION_SEARCH:
                rows.add(Row.soon(R.drawable.elyra_ic_search,
                        R.string.elyra_row_compact_search_title));
                rows.add(Row.soon(R.drawable.elyra_ic_search,
                        R.string.elyra_row_search_visibility_title));
                break;
            case SECTION_EDIT_MODE:
                rows.add(Row.soon(R.drawable.elyra_ic_grid,
                        R.string.elyra_row_workspace_edit_title));
                rows.add(Row.soon(R.drawable.elyra_ic_grid,
                        R.string.elyra_row_page_preview_title));
                break;
            default:
                break;
        }
        return rows;
    }

    // ── Binding ──────────────────────────────────────────────────────────────────

    private void bindRow(View row, Row spec) {
        ((ImageView) row.findViewById(R.id.row_icon)).setImageResource(spec.iconRes);
        ((TextView) row.findViewById(R.id.row_title)).setText(spec.titleRes);
        TextView summary = row.findViewById(R.id.row_summary);
        SwitchCompat sw = row.findViewById(R.id.row_switch);

        if (spec.prefKey != null) {
            // Real preference-backed toggle.
            summary.setText(spec.summaryRes);
            sw.setVisibility(View.VISIBLE);
            sw.setShowText(false);
            sw.setClickable(false);
            sw.setOnCheckedChangeListener(null);
            sw.setChecked(mPrefs.getBoolean(spec.prefKey, spec.prefDefault));
            sw.setOnCheckedChangeListener((btn, checked) ->
                    mPrefs.edit().putBoolean(spec.prefKey, checked).apply());
            row.setEnabled(true);
            row.setAlpha(1f);
            row.setOnClickListener(v -> sw.toggle());
        } else {
            // Disabled "coming soon" row — visible for structure, not interactive.
            summary.setText(R.string.elyra_row_coming_soon);
            sw.setVisibility(View.GONE);
            row.setEnabled(false);
            row.setClickable(false);
            row.setAlpha(0.45f);
            row.setOnClickListener(null);
        }
    }

    private View makeDivider(ViewGroup parent) {
        View divider = new View(parent.getContext());
        int height = Math.round(parent.getResources().getDisplayMetrics().density);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        lp.leftMargin = Math.round(56 * parent.getResources().getDisplayMetrics().density);
        divider.setLayoutParams(lp);
        divider.setBackgroundColor(0x14FFFFFF);
        return divider;
    }

    // ── Row spec ─────────────────────────────────────────────────────────────────

    private static final class Row {
        final int iconRes;
        @StringRes final int titleRes;
        @StringRes final int summaryRes;
        final String prefKey;       // null → disabled "coming soon" row
        final boolean prefDefault;

        private Row(int iconRes, int titleRes, int summaryRes, String prefKey,
                boolean prefDefault) {
            this.iconRes = iconRes;
            this.titleRes = titleRes;
            this.summaryRes = summaryRes;
            this.prefKey = prefKey;
            this.prefDefault = prefDefault;
        }

        static Row soon(int iconRes, @StringRes int titleRes) {
            return new Row(iconRes, titleRes, 0, null, false);
        }
    }
}
