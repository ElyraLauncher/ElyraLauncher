package com.android.launcher3.standalone;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public final class StandaloneSmokeActivity extends Activity {
    private static final int SCREEN_HOME = 0;
    private static final int SCREEN_DRAWER = 1;
    private static final int SCREEN_SETTINGS = 2;
    private static final int SCREEN_SEARCH = 3;
    private static final int SCREEN_APPEARANCE = 4;
    private static final int SCREEN_GLASS_DEPTH = 5;
    private static final int SCREEN_HOME_SETTINGS = 6;
    private static final int SCREEN_DOCK_SETTINGS = 7;

    private static final int GLASS_DEPTH_LIGHT = 35;
    private static final int GLASS_DEPTH_MEDIUM = 65;
    private static final int GLASS_DEPTH_DEEP = 88;

    private int mCurrentScreen = SCREEN_HOME;
    private int mSearchReturnScreen = SCREEN_HOME;
    private boolean mDarkPreview;
    private boolean mHomeIconLabels = true;
    private boolean mHomeWidgetArea = true;
    private boolean mDockLabels;
    private boolean mDockSuggestedApps;
    private boolean mDockSearch;
    private int mGlassDepth = GLASS_DEPTH_MEDIUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeShell();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentScreen == SCREEN_SEARCH) {
            showSearchReturnShell();
            return;
        }
        if (mCurrentScreen == SCREEN_GLASS_DEPTH) {
            showAppearanceScreen();
            return;
        }
        if (mCurrentScreen == SCREEN_HOME_SETTINGS) {
            showSettingsScreen();
            return;
        }
        if (mCurrentScreen == SCREEN_DOCK_SETTINGS) {
            showSettingsScreen();
            return;
        }
        if (mCurrentScreen == SCREEN_APPEARANCE) {
            showSettingsScreen();
            return;
        }
        if (mCurrentScreen == SCREEN_SETTINGS || mCurrentScreen == SCREEN_DRAWER) {
            showHomeShell();
            return;
        }
        super.onBackPressed();
    }

    private void showHomeShell() {
        mCurrentScreen = SCREEN_HOME;
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        applySystemBarColors();
        scrollView.setBackgroundColor(backgroundColor());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        scrollView.addView(root, matchParent());

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(header, matchWidthWrapHeight());

        TextView icon = label(R.string.standalone_home_logo, 18, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        icon.setTextColor(accentColor());
        icon.setBackground(rounded(surfaceColor(), dp(16), dp(1),
                borderColor()));
        header.addView(icon, size(dp(48), dp(48)));

        LinearLayout titleGroup = new LinearLayout(this);
        titleGroup.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleGroupParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleGroupParams.leftMargin = dp(14);
        header.addView(titleGroup, titleGroupParams);

        TextView title = label(R.string.standalone_smoke_title, 24, Typeface.BOLD);
        titleGroup.addView(title, matchWidthWrapHeight());

        TextView status = label(R.string.standalone_smoke_status, 13, Typeface.NORMAL);
        status.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams statusParams = matchWidthWrapHeight();
        statusParams.topMargin = dp(2);
        titleGroup.addView(status, statusParams);

        TextView warning = label(R.string.standalone_smoke_body, 14, Typeface.NORMAL);
        warning.setGravity(Gravity.CENTER);
        warning.setTextColor(mutedTextColor());
        warning.setLineSpacing(dp(2), 1.0f);
        warning.setPadding(dp(10), 0, dp(10), 0);
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(20);
        root.addView(warning, warningParams);

        TextView search = label(R.string.standalone_search_placeholder, 15, Typeface.NORMAL);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setTextColor(mutedTextColor());
        search.setPadding(dp(18), 0, dp(18), 0);
        search.setBackground(rounded(surfaceColor(), dp(24), dp(1),
                borderColor()));
        search.setOnClickListener(view -> showSearchShell(SCREEN_HOME));
        LinearLayout.LayoutParams searchParams = matchWidth(dp(52));
        searchParams.topMargin = dp(22);
        root.addView(search, searchParams);

        TextView workspaceLabel = label(R.string.standalone_workspace_title, 13, Typeface.BOLD);
        workspaceLabel.setAllCaps(true);
        workspaceLabel.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams workspaceLabelParams = matchWidthWrapHeight();
        workspaceLabelParams.topMargin = dp(28);
        root.addView(workspaceLabel, workspaceLabelParams);

        GridLayout workspace = new GridLayout(this);
        workspace.setColumnCount(2);
        workspace.setRowCount(2);
        workspace.setPadding(dp(12), dp(12), dp(12), dp(12));
        workspace.setBackground(rounded(surfaceColor(), dp(24), dp(1),
                borderColor()));
        LinearLayout.LayoutParams workspaceParams = matchWidthWrapHeight();
        workspaceParams.topMargin = dp(10);
        root.addView(workspace, workspaceParams);

        addWorkspaceTile(workspace, R.string.standalone_workspace_tile_widgets);
        addWorkspaceTile(workspace, R.string.standalone_workspace_tile_shortcuts);
        addWorkspaceTile(workspace, R.string.standalone_workspace_tile_preview);
        addWorkspaceTile(workspace, R.string.standalone_workspace_tile_empty);

        LinearLayout dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setPadding(dp(12), dp(12), dp(12), dp(12));
        dock.setBackground(rounded(surfaceColor(), dp(28), dp(1),
                borderColor()));
        LinearLayout.LayoutParams dockParams = matchWidthWrapHeight();
        dockParams.topMargin = dp(18);
        root.addView(dock, dockParams);

        addDockItem(dock, R.string.standalone_dock_phone);
        addDockItem(dock, R.string.standalone_dock_messages);
        addDockItem(dock, R.string.standalone_dock_browser);
        addDockItem(dock, R.string.standalone_dock_camera);

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.CENTER);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionsParams = matchWidthWrapHeight();
        actionsParams.topMargin = dp(18);
        root.addView(actions, actionsParams);

        TextView drawerButton = actionButton(R.string.standalone_app_drawer_button);
        drawerButton.setOnClickListener(view -> showAppDrawerShell());
        actions.addView(drawerButton, actionButtonParams(false));
        TextView settingsButton = actionButton(R.string.standalone_settings_button);
        settingsButton.setOnClickListener(view -> showSettingsShell());
        actions.addView(settingsButton, actionButtonParams(true));

        TextView footer = label(R.string.standalone_smoke_footer, 12, Typeface.NORMAL);
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams footerParams = matchWidthWrapHeight();
        footerParams.topMargin = dp(24);
        root.addView(footer, footerParams);

        setContentView(scrollView);
    }


    private void showAppDrawerShell() {
        mCurrentScreen = SCREEN_DRAWER;
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        applySystemBarColors();
        scrollView.setBackgroundColor(backgroundColor());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        scrollView.addView(root, matchParent());

        addPreviewHeader(root, R.string.standalone_app_drawer_back,
                R.string.standalone_app_drawer_title,
                R.string.standalone_app_drawer_subtitle);

        TextView search = label(R.string.standalone_app_drawer_search_placeholder, 15,
                Typeface.NORMAL);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setTextColor(mutedTextColor());
        search.setPadding(dp(18), 0, dp(18), 0);
        search.setBackground(rounded(surfaceColor(), dp(24), dp(1),
                borderColor()));
        search.setOnClickListener(view -> showSearchShell(SCREEN_DRAWER));
        LinearLayout.LayoutParams searchParams = matchWidth(dp(52));
        searchParams.topMargin = dp(22);
        root.addView(search, searchParams);

        GridLayout apps = new GridLayout(this);
        apps.setColumnCount(4);
        apps.setPadding(dp(10), dp(10), dp(10), dp(10));
        apps.setBackground(rounded(surfaceColor(), dp(24), dp(1),
                borderColor()));
        LinearLayout.LayoutParams appsParams = matchWidthWrapHeight();
        appsParams.topMargin = dp(16);
        root.addView(apps, appsParams);

        addDrawerApp(apps, R.string.standalone_drawer_phone, "P", false);
        addDrawerApp(apps, R.string.standalone_drawer_messages, "M", false);
        addDrawerApp(apps, R.string.standalone_drawer_browser, "B", false);
        addDrawerApp(apps, R.string.standalone_drawer_camera, "C", false);
        addDrawerApp(apps, R.string.standalone_drawer_settings, "S", true);
        addDrawerApp(apps, R.string.standalone_drawer_files, "F", false);
        addDrawerApp(apps, R.string.standalone_drawer_gallery, "G", false);
        addDrawerApp(apps, R.string.standalone_drawer_clock, "C", false);
        addDrawerApp(apps, R.string.standalone_drawer_calculator, "C", false);
        addDrawerApp(apps, R.string.standalone_drawer_calendar, "C", false);
        addDrawerApp(apps, R.string.standalone_drawer_contacts, "C", false);
        addDrawerApp(apps, R.string.standalone_drawer_weather, "W", false);

        TextView warning = label(R.string.standalone_app_drawer_notice, 14, Typeface.NORMAL);
        warning.setGravity(Gravity.CENTER);
        warning.setTextColor(mutedTextColor());
        warning.setPadding(dp(10), 0, dp(10), 0);
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(16);
        root.addView(warning, warningParams);

        setContentView(scrollView);
    }

    private void showSearchShell(int returnScreen) {
        mCurrentScreen = SCREEN_SEARCH;
        mSearchReturnScreen = returnScreen;

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        applySystemBarColors();
        scrollView.setBackgroundColor(backgroundColor());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        scrollView.addView(root, matchParent());

        addPreviewHeader(root, R.string.standalone_search_back,
                R.string.standalone_search_title,
                R.string.standalone_search_subtitle);

        TextView search = label(R.string.standalone_search_placeholder, 15, Typeface.NORMAL);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setTextColor(mutedTextColor());
        search.setPadding(dp(18), 0, dp(18), 0);
        search.setBackground(rounded(surfaceColor(), dp(24), dp(1),
                borderColor()));
        LinearLayout.LayoutParams searchParams = matchWidth(dp(52));
        searchParams.topMargin = dp(22);
        root.addView(search, searchParams);

        addSearchSection(root, R.string.standalone_search_suggested_apps,
                new int[] {
                        R.string.standalone_search_phone,
                        R.string.standalone_search_messages,
                        R.string.standalone_search_browser,
                        R.string.standalone_search_camera
                });
        addSearchSection(root, R.string.standalone_search_settings_results,
                new int[] {
                        R.string.standalone_search_appearance,
                        R.string.standalone_search_home_screen,
                        R.string.standalone_search_dock,
                        R.string.standalone_search_search
                });
        addSearchSection(root, R.string.standalone_search_widgets,
                new int[] {
                        R.string.standalone_search_clock,
                        R.string.standalone_search_weather
                });

        TextView warning = label(R.string.standalone_search_notice, 14, Typeface.NORMAL);
        warning.setGravity(Gravity.CENTER);
        warning.setTextColor(mutedTextColor());
        warning.setPadding(dp(10), 0, dp(10), 0);
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(16);
        root.addView(warning, warningParams);

        setContentView(scrollView);
    }

    private void showSettingsShell() {
        showSettingsScreen();
    }

    private LinearLayout createScreenContainer() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        applySystemBarColors();
        scrollView.setBackgroundColor(backgroundColor());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        scrollView.addView(root, matchParent());
        setContentView(scrollView);
        return root;
    }

    private void createHeader(LinearLayout root, int titleResId, int subtitleResId,
            Runnable backAction) {
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(header, matchWidthWrapHeight());

        TextView backButton = actionButton(R.string.standalone_settings_back);
        backButton.setOnClickListener(view -> backAction.run());
        header.addView(backButton, new LinearLayout.LayoutParams(dp(88), dp(44)));

        LinearLayout titleGroup = new LinearLayout(this);
        titleGroup.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleGroupParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleGroupParams.leftMargin = dp(14);
        header.addView(titleGroup, titleGroupParams);

        TextView title = label(titleResId, 23, Typeface.BOLD);
        titleGroup.addView(title, matchWidthWrapHeight());

        TextView subtitle = label(subtitleResId, 13, Typeface.NORMAL);
        subtitle.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams subtitleParams = matchWidthWrapHeight();
        subtitleParams.topMargin = dp(2);
        titleGroup.addView(subtitle, subtitleParams);
    }

    private void createHeroInfoCard(LinearLayout root) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(20), dp(20), dp(20), dp(20));
        card.setBackground(rounded(surfaceColor(), dp(22), dp(1), borderColor()));

        TextView icon = label(R.string.standalone_settings_hero_icon, 14, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        icon.setTextColor(accentColor());
        icon.setBackground(rounded(accentSoftColor(), dp(12), 0, Color.TRANSPARENT));
        card.addView(icon, size(dp(32), dp(32)));

        TextView title = label(R.string.standalone_settings_hero_title, 18, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = matchWidthWrapHeight();
        titleParams.topMargin = dp(16);
        card.addView(title, titleParams);

        TextView body = label(R.string.standalone_settings_hero_body, 14, Typeface.NORMAL);
        body.setTextColor(mutedTextColor());
        body.setLineSpacing(dp(4), 1.0f);
        LinearLayout.LayoutParams bodyParams = matchWidthWrapHeight();
        bodyParams.topMargin = dp(8);
        card.addView(body, bodyParams);

        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(24);
        root.addView(card, params);
    }

    private void createStandaloneNotice(LinearLayout root) {
        TextView note = label(R.string.standalone_settings_notice, 12, Typeface.NORMAL);
        note.setGravity(Gravity.CENTER);
        note.setTextColor(mutedTextColor());
        note.setPadding(dp(10), 0, dp(10), 0);
        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(16);
        root.addView(note, params);
    }

    private LinearLayout createGroupedSection(LinearLayout root, int titleResId) {
        if (titleResId != 0) {
            TextView title = label(titleResId, 13, Typeface.BOLD);
            title.setAllCaps(true);
            title.setTextColor(mutedTextColor());
            LinearLayout.LayoutParams titleParams = matchWidthWrapHeight();
            titleParams.leftMargin = dp(4);
            titleParams.topMargin = dp(24);
            root.addView(title, titleParams);
        }

        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setPadding(0, dp(4), 0, dp(4));
        section.setBackground(rounded(surfaceColor(), dp(24), dp(1), borderColor()));
        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = titleResId == 0 ? dp(16) : dp(8);
        root.addView(section, params);
        return section;
    }

    private void createSettingsRow(LinearLayout section, int titleResId, int valueResId,
            boolean chevron, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(18), 0, dp(14), 0);
        row.setMinimumHeight(dp(54));
        row.setOnClickListener(listener);

        TextView title = label(titleResId, 15, Typeface.NORMAL);
        row.addView(title, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        if (valueResId != 0) {
            TextView value = label(valueResId, 14, Typeface.NORMAL);
            value.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            value.setTextColor(mutedTextColor());
            row.addView(value, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        if (chevron) {
            TextView arrow = label(R.string.standalone_settings_chevron, 20, Typeface.NORMAL);
            arrow.setGravity(Gravity.CENTER);
            arrow.setTextColor(mutedTextColor());
            LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(dp(22),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            arrowParams.leftMargin = dp(6);
            row.addView(arrow, arrowParams);
        }

        section.addView(row, matchWidth(dp(56)));
    }

    private void createPreviewLauncherCard(LinearLayout root) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(previewWallpaperBackground());

        TextView search = label(R.string.standalone_search_placeholder, 12, Typeface.NORMAL);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setTextColor(previewTextColor());
        search.setPadding(dp(14), 0, dp(14), 0);
        search.setBackground(rounded(previewGlassColor(), dp(18), dp(1), previewGlassBorderColor()));
        card.addView(search, matchWidth(dp(38)));

        GridLayout icons = new GridLayout(this);
        icons.setColumnCount(4);
        LinearLayout.LayoutParams iconsParams = matchWidthWrapHeight();
        iconsParams.topMargin = dp(18);
        card.addView(icons, iconsParams);
        addPreviewIcon(icons, R.string.standalone_drawer_phone, 0);
        addPreviewIcon(icons, R.string.standalone_drawer_messages, 1);
        addPreviewIcon(icons, R.string.standalone_drawer_browser, 2);
        addPreviewIcon(icons, R.string.standalone_drawer_camera, 3);
        addPreviewIcon(icons, R.string.standalone_drawer_settings, 4);
        addPreviewIcon(icons, R.string.standalone_drawer_gallery, 5);
        addPreviewIcon(icons, R.string.standalone_drawer_files, 6);
        addPreviewIcon(icons, R.string.standalone_drawer_weather, 7);

        LinearLayout dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setPadding(dp(10), dp(10), dp(10), dp(10));
        dock.setBackground(rounded(previewGlassColor(), dp(24), dp(1), previewGlassBorderColor()));
        LinearLayout.LayoutParams dockParams = matchWidthWrapHeight();
        dockParams.topMargin = dp(14);
        card.addView(dock, dockParams);
        addPreviewDockIcon(dock, 0);
        addPreviewDockIcon(dock, 1);
        addPreviewDockIcon(dock, 2);
        addPreviewDockIcon(dock, 3);

        TextView indicator = label(R.string.standalone_home_page_indicator_preview, 18,
                Typeface.BOLD);
        indicator.setGravity(Gravity.CENTER);
        indicator.setTextColor(previewTextColor());
        LinearLayout.LayoutParams indicatorParams = matchWidthWrapHeight();
        indicatorParams.topMargin = dp(10);
        card.addView(indicator, indicatorParams);

        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(22);
        root.addView(card, params);
    }

    private void createDockPreviewCard(LinearLayout root) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(previewWallpaperBackground());

        TextView hint = label(R.string.standalone_dock_preview_label, 12, Typeface.BOLD);
        hint.setGravity(Gravity.CENTER);
        hint.setTextColor(previewTextColor());
        hint.setPadding(dp(12), 0, dp(12), 0);
        hint.setBackground(rounded(previewGlassColor(), dp(16), dp(1),
                previewGlassBorderColor()));
        card.addView(hint, matchWidth(dp(34)));

        LinearLayout workspace = new LinearLayout(this);
        workspace.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        workspace.setOrientation(LinearLayout.VERTICAL);
        workspace.setPadding(dp(10), dp(58), dp(10), dp(10));
        LinearLayout.LayoutParams workspaceParams = matchWidthWrapHeight();
        workspaceParams.topMargin = dp(12);
        card.addView(workspace, workspaceParams);

        LinearLayout dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setPadding(dp(12), dp(12), dp(12), dp(10));
        dock.setBackground(rounded(previewGlassColor(), dp(26), dp(1),
                previewGlassBorderColor()));
        workspace.addView(dock, matchWidthWrapHeight());
        addPreviewDockIcon(dock, R.string.standalone_dock_phone, 0, mDockLabels);
        addPreviewDockIcon(dock, R.string.standalone_dock_messages, 1, mDockLabels);
        addPreviewDockIcon(dock, R.string.standalone_dock_browser, 2, mDockLabels);
        addPreviewDockIcon(dock, R.string.standalone_dock_camera, 3, mDockLabels);

        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(22);
        root.addView(card, params);
    }

    private void createModernSeekBarRow(LinearLayout root) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(16), dp(18), dp(16));
        card.setBackground(rounded(surfaceColor(), dp(24), dp(1), borderColor()));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setOrientation(LinearLayout.HORIZONTAL);
        card.addView(top, matchWidthWrapHeight());

        TextView title = label(R.string.standalone_glass_depth_control, 15, Typeface.BOLD);
        top.addView(title, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView value = label(mGlassDepth + "%", 14, Typeface.BOLD);
        value.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        value.setTextColor(accentColor());
        top.addView(value, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(100);
        seekBar.setProgress(mGlassDepth);
        seekBar.setProgressDrawable(createSeekBarDrawable());
        seekBar.setThumb(rounded(accentColor(), dp(8), dp(2), surfaceColor()));
        seekBar.setPadding(0, dp(10), 0, dp(8));
        LinearLayout.LayoutParams seekParams = matchWidth(dp(48));
        seekParams.topMargin = dp(8);
        card.addView(seekBar, seekParams);

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.HORIZONTAL);
        card.addView(labels, matchWidthWrapHeight());
        addSeekLabel(labels, R.string.standalone_glass_depth_light_label, Gravity.LEFT);
        addSeekLabel(labels, R.string.standalone_glass_depth_medium_label, Gravity.CENTER);
        addSeekLabel(labels, R.string.standalone_glass_depth_deep_label, Gravity.RIGHT);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGlassDepth = progress;
                value.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(StandaloneSmokeActivity.this,
                        R.string.standalone_glass_depth_preview_only, Toast.LENGTH_SHORT).show();
                showGlassDepthScreen();
            }
        });

        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(18);
        root.addView(card, params);
    }

    private void showSettingsScreen() {
        mCurrentScreen = SCREEN_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_settings_title,
                R.string.standalone_settings_subtitle, this::showHomeShell);
        createHeroInfoCard(root);

        LinearLayout section = createGroupedSection(root, 0);
        createSettingsRow(section, R.string.standalone_settings_appearance, 0, true,
                view -> showAppearanceScreen());
        createSettingsRow(section, R.string.standalone_settings_home_screen, 0, true,
                view -> showHomeSettingsScreen());
        createSettingsRow(section, R.string.standalone_settings_dock, 0, true,
                view -> showDockSettingsScreen());
        createSettingsRow(section, R.string.standalone_settings_search, 0, true,
                view -> showSearchShell(SCREEN_SETTINGS));
        createSettingsRow(section, R.string.standalone_settings_about, 0, true,
                view -> showAboutPreview());
        createStandaloneNotice(root);
    }

    private void showAppearanceScreen() {
        mCurrentScreen = SCREEN_APPEARANCE;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_appearance_title,
                R.string.standalone_settings_subtitle, this::showSettingsScreen);
        createPreviewLauncherCard(root);

        LinearLayout theme = createGroupedSection(root, R.string.standalone_appearance_theme);
        createSettingsRow(theme, R.string.standalone_appearance_color,
                R.string.standalone_appearance_color_value, true,
                view -> showPreviewToast(R.string.standalone_appearance_color));
        createSettingsRow(theme, R.string.standalone_appearance_theme_mode,
                mDarkPreview ? R.string.standalone_appearance_dark : R.string.standalone_appearance_light,
                true, view -> setThemePreview(!mDarkPreview));

        LinearLayout glass = createGroupedSection(root, R.string.standalone_elyra_glass);
        createSettingsRow(glass, R.string.standalone_glass_style,
                R.string.standalone_glass_style_value, true,
                view -> showGlassOptionToast(R.string.standalone_glass_style));
        createSettingsRow(glass, R.string.standalone_glass_depth,
                R.string.standalone_glass_depth_value, true,
                view -> showGlassDepthScreen());
        createSettingsRow(glass, R.string.standalone_icon_glass,
                R.string.standalone_icon_glass_value, true,
                view -> showGlassOptionToast(R.string.standalone_icon_glass));
        createSettingsRow(glass, R.string.standalone_card_surface,
                R.string.standalone_card_surface_value, true,
                view -> showGlassOptionToast(R.string.standalone_card_surface));

        LinearLayout icons = createGroupedSection(root, R.string.standalone_appearance_icons);
        createSettingsRow(icons, R.string.standalone_icon_pack,
                R.string.standalone_icon_pack_value, true,
                view -> Toast.makeText(this, R.string.standalone_elyraicons_preview_only,
                        Toast.LENGTH_SHORT).show());
        createSettingsRow(icons, R.string.standalone_icon_shape,
                R.string.standalone_icon_shape_value, true,
                view -> showPreviewToast(R.string.standalone_icon_shape));
        createSettingsRow(icons, R.string.standalone_themed_icons,
                R.string.standalone_themed_icons_value, true,
                view -> showPreviewToast(R.string.standalone_themed_icons));

        LinearLayout layout = createGroupedSection(root, R.string.standalone_appearance_layout);
        createSettingsRow(layout, R.string.standalone_icon_size,
                R.string.standalone_icon_size_value, true,
                view -> showPreviewToast(R.string.standalone_icon_size));
        createSettingsRow(layout, R.string.standalone_grid_density,
                R.string.standalone_grid_density_value, true,
                view -> showPreviewToast(R.string.standalone_grid_density));
    }

    private void showHomeSettingsScreen() {
        mCurrentScreen = SCREEN_HOME_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_home_settings_title,
                R.string.standalone_settings_subtitle, this::showSettingsScreen);
        createPreviewLauncherCard(root);

        LinearLayout layout = createGroupedSection(root, R.string.standalone_home_layout);
        createSettingsRow(layout, R.string.standalone_home_grid_size,
                R.string.standalone_home_grid_size_value, true,
                view -> Toast.makeText(this, R.string.standalone_home_grid_size_preview_only,
                        Toast.LENGTH_SHORT).show());
        createSettingsRow(layout, R.string.standalone_icon_size,
                R.string.standalone_icon_size_value, true,
                view -> Toast.makeText(this, R.string.standalone_home_icon_size_preview_only,
                        Toast.LENGTH_SHORT).show());
        createSettingsRow(layout, R.string.standalone_home_icon_labels,
                mHomeIconLabels ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> {
                    mHomeIconLabels = !mHomeIconLabels;
                    showHomeSettingsScreen();
                });

        LinearLayout workspace = createGroupedSection(root, R.string.standalone_home_workspace);
        createSettingsRow(workspace, R.string.standalone_home_widget_area,
                mHomeWidgetArea ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> {
                    mHomeWidgetArea = !mHomeWidgetArea;
                    showHomeSettingsScreen();
                });
        createSettingsRow(workspace, R.string.standalone_home_page_indicator,
                R.string.standalone_home_page_indicator_value, true,
                view -> showPreviewToast(R.string.standalone_home_page_indicator));
        createSettingsRow(workspace, R.string.standalone_home_empty_slots,
                R.string.standalone_home_empty_slots_value, true,
                view -> showPreviewToast(R.string.standalone_home_empty_slots));

        LinearLayout motion = createGroupedSection(root, R.string.standalone_home_motion);
        createSettingsRow(motion, R.string.standalone_home_animation_style,
                R.string.standalone_home_animation_style_value, true,
                view -> showPreviewToast(R.string.standalone_home_animation_style));
        createSettingsRow(motion, R.string.standalone_home_transition_speed,
                R.string.standalone_home_transition_speed_value, true,
                view -> showPreviewToast(R.string.standalone_home_transition_speed));

        createStandaloneNotice(root);
    }

    private void showDockSettingsScreen() {
        mCurrentScreen = SCREEN_DOCK_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_dock_settings_title,
                R.string.standalone_settings_subtitle, this::showSettingsScreen);
        createDockPreviewCard(root);

        LinearLayout appearance = createGroupedSection(root, R.string.standalone_dock_display);
        createSettingsRow(appearance, R.string.standalone_dock_style,
                R.string.standalone_dock_style_value, true,
                view -> Toast.makeText(this, R.string.standalone_dock_style_preview_only,
                        Toast.LENGTH_SHORT).show());
        createSettingsRow(appearance, R.string.standalone_dock_height,
                R.string.standalone_dock_height_value, true,
                view -> Toast.makeText(this, R.string.standalone_dock_height_preview_only,
                        Toast.LENGTH_SHORT).show());
        createSettingsRow(appearance, R.string.standalone_dock_corner_radius,
                R.string.standalone_dock_corner_radius_value, true,
                view -> Toast.makeText(this, R.string.standalone_dock_corner_radius_preview_only,
                        Toast.LENGTH_SHORT).show());

        LinearLayout apps = createGroupedSection(root, R.string.standalone_dock_apps_section);
        createSettingsRow(apps, R.string.standalone_dock_apps,
                R.string.standalone_dock_apps_value, true,
                view -> Toast.makeText(this, R.string.standalone_dock_apps_preview_only,
                        Toast.LENGTH_SHORT).show());
        createSettingsRow(apps, R.string.standalone_dock_labels,
                mDockLabels ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> {
                    mDockLabels = !mDockLabels;
                    showDockSettingsScreen();
                });
        createSettingsRow(apps, R.string.standalone_dock_suggested_apps,
                mDockSuggestedApps ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> {
                    mDockSuggestedApps = !mDockSuggestedApps;
                    showDockSettingsScreen();
                });

        LinearLayout integration = createGroupedSection(root, R.string.standalone_dock_integration);
        createSettingsRow(integration, R.string.standalone_dock_search,
                mDockSearch ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> {
                    mDockSearch = !mDockSearch;
                    showDockSettingsScreen();
                });
        createSettingsRow(integration, R.string.standalone_dock_elyra_glass,
                R.string.standalone_value_on, true, view -> showAppearanceScreen());
        createSettingsRow(integration, R.string.standalone_dock_haptic_feedback,
                R.string.standalone_value_on, true,
                view -> Toast.makeText(this, R.string.standalone_dock_haptic_preview_only,
                        Toast.LENGTH_SHORT).show());

        createStandaloneNotice(root);
    }

    private void showGlassDepthScreen() {
        mCurrentScreen = SCREEN_GLASS_DEPTH;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_glass_depth,
                R.string.standalone_elyra_glass, this::showAppearanceScreen);

        TextView description = label(R.string.standalone_glass_depth_description, 14,
                Typeface.NORMAL);
        description.setTextColor(mutedTextColor());
        description.setLineSpacing(dp(3), 1.0f);
        LinearLayout.LayoutParams descriptionParams = matchWidthWrapHeight();
        descriptionParams.topMargin = dp(22);
        root.addView(description, descriptionParams);

        createPreviewLauncherCard(root);
        createModernSeekBarRow(root);

        LinearLayout presets = createGroupedSection(root, R.string.standalone_glass_depth_presets);
        createPresetRow(presets, R.string.standalone_glass_depth_light_preset,
                GLASS_DEPTH_LIGHT);
        createPresetRow(presets, R.string.standalone_glass_depth_medium_preset,
                GLASS_DEPTH_MEDIUM);
        createPresetRow(presets, R.string.standalone_glass_depth_deep_preset,
                GLASS_DEPTH_DEEP);
        createPresetRow(presets, R.string.standalone_glass_depth_custom_preset,
                mGlassDepth);
    }

    private void showAboutPreview() {
        mCurrentScreen = SCREEN_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_settings_about,
                R.string.standalone_settings_subtitle, this::showSettingsScreen);
        TextView body = label(R.string.standalone_settings_about_body, 14, Typeface.NORMAL);
        body.setTextColor(mutedTextColor());
        body.setLineSpacing(dp(3), 1.0f);
        body.setPadding(dp(18), dp(18), dp(18), dp(18));
        body.setBackground(rounded(surfaceColor(), dp(24), dp(1), borderColor()));
        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(24);
        root.addView(body, params);
    }

    private TextView label(int resId, float textSizeSp, int typefaceStyle) {
        TextView view = new TextView(this);
        view.setText(resId);
        view.setTextColor(textColor());
        view.setTextSize(textSizeSp);
        view.setTypeface(Typeface.DEFAULT, typefaceStyle);
        return view;
    }

    private TextView label(CharSequence text, float textSizeSp, int typefaceStyle) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(textColor());
        view.setTextSize(textSizeSp);
        view.setTypeface(Typeface.DEFAULT, typefaceStyle);
        return view;
    }


    private void addPreviewHeader(LinearLayout root, int backResId, int titleResId,
            int subtitleResId) {
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(header, matchWidthWrapHeight());

        TextView backButton = actionButton(backResId);
        backButton.setOnClickListener(view -> {
            if (mCurrentScreen == SCREEN_SEARCH) {
                showSearchReturnShell();
                return;
            }
            showHomeShell();
        });
        header.addView(backButton, new LinearLayout.LayoutParams(dp(88), dp(44)));

        LinearLayout titleGroup = new LinearLayout(this);
        titleGroup.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleGroupParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleGroupParams.leftMargin = dp(14);
        header.addView(titleGroup, titleGroupParams);

        TextView title = label(titleResId, 24, Typeface.BOLD);
        titleGroup.addView(title, matchWidthWrapHeight());

        TextView status = label(subtitleResId, 13, Typeface.NORMAL);
        status.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams statusParams = matchWidthWrapHeight();
        statusParams.topMargin = dp(2);
        titleGroup.addView(status, statusParams);
    }

    private void addWorkspaceTile(GridLayout workspace, int labelResId) {
        LinearLayout tile = new LinearLayout(this);
        tile.setGravity(Gravity.CENTER);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setPadding(dp(10), dp(12), dp(10), dp(12));
        tile.setBackground(rounded(tileBackgroundColor(), dp(18), 0,
                Color.TRANSPARENT));

        View marker = new View(this);
        marker.setBackground(rounded(accentSoftColor(), dp(10), 0,
                Color.TRANSPARENT));
        tile.addView(marker, size(dp(42), dp(42)));

        TextView label = label(labelResId, 13, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams labelParams = matchWidthWrapHeight();
        labelParams.topMargin = dp(10);
        tile.addView(label, labelParams);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dp(118);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(6), dp(6), dp(6), dp(6));
        workspace.addView(tile, params);
    }

    private void addDockItem(LinearLayout dock, int labelResId) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setOnClickListener(view -> showPreviewToast(labelResId));

        TextView dot = label(R.string.standalone_dock_dot, 18, Typeface.BOLD);
        dot.setGravity(Gravity.CENTER);
        dot.setTextColor(accentColor());
        dot.setBackground(rounded(tileBackgroundColor(), dp(14), 0,
                Color.TRANSPARENT));
        item.addView(dot, size(dp(48), dp(48)));

        TextView label = label(labelResId, 11, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams labelParams = matchWidthWrapHeight();
        labelParams.topMargin = dp(6);
        item.addView(label, labelParams);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        dock.addView(item, params);
    }


    private void addDrawerApp(GridLayout apps, int labelResId, String iconText,
            boolean opensSettings) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(dp(8), dp(12), dp(8), dp(10));
        item.setBackground(rounded(tileBackgroundColor(), dp(18), 0,
                Color.TRANSPARENT));
        item.setOnClickListener(view -> {
            if (opensSettings) {
                showSettingsShell();
                return;
            }
            showPreviewToast(labelResId);
        });

        TextView icon = label(iconText, 18, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        icon.setTextColor(accentColor());
        icon.setBackground(rounded(accentSoftColor(), dp(16), 0,
                Color.TRANSPARENT));
        item.addView(icon, size(dp(52), dp(52)));

        TextView label = label(labelResId, 12, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(textColor());
        LinearLayout.LayoutParams labelParams = matchWidthWrapHeight();
        labelParams.topMargin = dp(8);
        item.addView(label, labelParams);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dp(112);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(6), dp(6), dp(6), dp(6));
        apps.addView(item, params);
    }

    private void addSettingsSection(LinearLayout root, int titleResId, int bodyResId) {
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setPadding(dp(18), dp(16), dp(18), dp(16));
        section.setBackground(rounded(surfaceColor(), dp(18), dp(1),
                borderColor()));

        TextView title = label(titleResId, 16, Typeface.BOLD);
        section.addView(title, matchWidthWrapHeight());

        TextView body = label(bodyResId, 13, Typeface.NORMAL);
        body.setTextColor(mutedTextColor());
        body.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams bodyParams = matchWidthWrapHeight();
        bodyParams.topMargin = dp(6);
        section.addView(body, bodyParams);

        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(12);
        root.addView(section, params);
    }

    private void addThemeModeSection(LinearLayout root) {
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setPadding(dp(18), dp(16), dp(18), dp(16));
        section.setBackground(rounded(surfaceColor(), dp(18), dp(1), borderColor()));

        TextView title = label(R.string.standalone_settings_theme_mode, 16, Typeface.BOLD);
        section.addView(title, matchWidthWrapHeight());

        TextView body = label(R.string.standalone_settings_theme_mode_body, 13,
                Typeface.NORMAL);
        body.setTextColor(mutedTextColor());
        body.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams bodyParams = matchWidthWrapHeight();
        bodyParams.topMargin = dp(6);
        section.addView(body, bodyParams);

        LinearLayout options = new LinearLayout(this);
        options.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams optionsParams = matchWidthWrapHeight();
        optionsParams.topMargin = dp(12);
        section.addView(options, optionsParams);

        TextView light = themeModeButton(R.string.standalone_settings_theme_light, !mDarkPreview);
        light.setOnClickListener(view -> setThemePreview(false));
        options.addView(light, actionButtonParams(false));

        TextView dark = themeModeButton(R.string.standalone_settings_theme_dark, mDarkPreview);
        dark.setOnClickListener(view -> setThemePreview(true));
        options.addView(dark, actionButtonParams(true));

        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(12);
        root.addView(section, params);
    }

    private TextView themeModeButton(int labelResId, boolean selected) {
        TextView button = label(labelResId, 14, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(14), 0, dp(14), 0);
        button.setTextColor(selected ? selectedThemeTextColor() : textColor());
        button.setBackground(rounded(selected ? accentColor() : tileBackgroundColor(),
                dp(16), dp(1), selected ? accentColor() : borderColor()));
        return button;
    }

    private void setThemePreview(boolean darkPreview) {
        if (mDarkPreview == darkPreview) {
            return;
        }
        mDarkPreview = darkPreview;
        if (mCurrentScreen == SCREEN_APPEARANCE) {
            showAppearanceScreen();
        } else if (mCurrentScreen == SCREEN_GLASS_DEPTH) {
            showGlassDepthScreen();
        } else if (mCurrentScreen == SCREEN_HOME_SETTINGS) {
            showHomeSettingsScreen();
        } else if (mCurrentScreen == SCREEN_DOCK_SETTINGS) {
            showDockSettingsScreen();
        } else {
            showSettingsScreen();
        }
    }

    private void addSearchSection(LinearLayout root, int titleResId, int[] itemResIds) {
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setPadding(dp(18), dp(16), dp(18), dp(10));
        section.setBackground(rounded(surfaceColor(), dp(18), dp(1),
                borderColor()));

        TextView title = label(titleResId, 16, Typeface.BOLD);
        section.addView(title, matchWidthWrapHeight());

        for (int itemResId : itemResIds) {
            TextView item = label(itemResId, 14, Typeface.NORMAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dp(12), 0, dp(12), 0);
            item.setBackground(rounded(tileBackgroundColor(), dp(14), 0,
                    Color.TRANSPARENT));
            item.setOnClickListener(view -> showPreviewToast(itemResId));

            LinearLayout.LayoutParams itemParams = matchWidth(dp(44));
            itemParams.topMargin = dp(10);
            section.addView(item, itemParams);
        }

        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(14);
        root.addView(section, params);
    }


    private void addPreviewIcon(GridLayout icons, int labelResId, int colorIndex) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);

        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(14), 0,
                Color.TRANSPARENT));
        item.addView(icon, size(dp(34), dp(34)));

        TextView label = label(labelResId, 9, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setTextColor(previewTextColor());
        LinearLayout.LayoutParams labelParams = matchWidthWrapHeight();
        labelParams.topMargin = dp(5);
        item.addView(label, labelParams);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dp(62);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(4), dp(4), dp(4), dp(6));
        icons.addView(item, params);
    }

    private void addPreviewDockIcon(LinearLayout dock, int colorIndex) {
        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(13), 0,
                Color.TRANSPARENT));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(36), 1f);
        params.leftMargin = dp(5);
        params.rightMargin = dp(5);
        dock.addView(icon, params);
    }

    private void addPreviewDockIcon(LinearLayout dock, int labelResId, int colorIndex,
            boolean showLabel) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);

        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(14), 0,
                Color.TRANSPARENT));
        item.addView(icon, size(dp(38), dp(38)));

        if (showLabel) {
            TextView label = label(labelResId, 9, Typeface.NORMAL);
            label.setGravity(Gravity.CENTER);
            label.setSingleLine(true);
            label.setTextColor(previewTextColor());
            LinearLayout.LayoutParams labelParams = matchWidthWrapHeight();
            labelParams.topMargin = dp(5);
            item.addView(label, labelParams);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.leftMargin = dp(4);
        params.rightMargin = dp(4);
        dock.addView(item, params);
    }

    private void addSeekLabel(LinearLayout labels, int labelResId, int gravity) {
        TextView label = label(labelResId, 12, Typeface.NORMAL);
        label.setGravity(gravity | Gravity.CENTER_VERTICAL);
        label.setTextColor(mutedTextColor());
        labels.addView(label, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
    }

    private void createPresetRow(LinearLayout section, int titleResId, int value) {
        boolean selected = isPresetSelected(titleResId, value);
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(18), 0, dp(18), 0);
        row.setMinimumHeight(dp(54));
        row.setOnClickListener(view -> {
            mGlassDepth = value;
            Toast.makeText(this, R.string.standalone_glass_depth_preview_only,
                    Toast.LENGTH_SHORT).show();
            showGlassDepthScreen();
        });

        TextView title = label(titleResId, 15, Typeface.NORMAL);
        row.addView(title, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView mark = label(selected ? R.string.standalone_settings_selected
                : R.string.standalone_settings_empty, 14, Typeface.BOLD);
        mark.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mark.setTextColor(accentColor());
        row.addView(mark, new LinearLayout.LayoutParams(dp(34),
                ViewGroup.LayoutParams.WRAP_CONTENT));
        section.addView(row, matchWidth(dp(56)));
    }

    private boolean isPresetSelected(int titleResId, int value) {
        if (titleResId == R.string.standalone_glass_depth_custom_preset) {
            return nearestPreset(mGlassDepth) == 0;
        }
        return nearestPreset(mGlassDepth) == value;
    }

    private int nearestPreset(int value) {
        if (Math.abs(value - GLASS_DEPTH_LIGHT) <= 4) {
            return GLASS_DEPTH_LIGHT;
        }
        if (Math.abs(value - GLASS_DEPTH_MEDIUM) <= 4) {
            return GLASS_DEPTH_MEDIUM;
        }
        if (Math.abs(value - GLASS_DEPTH_DEEP) <= 4) {
            return GLASS_DEPTH_DEEP;
        }
        return 0;
    }

    private void showGlassOptionToast(int labelResId) {
        Toast.makeText(this, getString(R.string.standalone_glass_option_preview_only,
                getString(labelResId)), Toast.LENGTH_SHORT).show();
    }

    private LayerDrawable createSeekBarDrawable() {
        GradientDrawable inactive = rounded(seekInactiveColor(), dp(5), 0, Color.TRANSPARENT);
        GradientDrawable active = rounded(accentColor(), dp(5), 0, Color.TRANSPARENT);
        ClipDrawable progress = new ClipDrawable(active, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        LayerDrawable drawable = new LayerDrawable(new android.graphics.drawable.Drawable[] {
                inactive, progress
        });
        drawable.setId(0, android.R.id.background);
        drawable.setId(1, android.R.id.progress);
        drawable.setLayerHeight(0, dp(8));
        drawable.setLayerHeight(1, dp(8));
        drawable.setLayerGravity(0, Gravity.CENTER_VERTICAL);
        drawable.setLayerGravity(1, Gravity.CENTER_VERTICAL);
        return drawable;
    }

    private TextView actionButton(int labelResId) {
        TextView button = label(labelResId, 14, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(textColor());
        button.setPadding(dp(14), 0, dp(14), 0);
        button.setBackground(rounded(surfaceColor(), dp(18), dp(1),
                borderColor()));
        return button;
    }

    private LinearLayout.LayoutParams actionButtonParams(boolean hasLeftMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(48), 1f);
        if (hasLeftMargin) {
            params.leftMargin = dp(12);
        }
        return params;
    }

    private LinearLayout.LayoutParams matchWidthWrapHeight() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams matchWidth(int height) {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
    }

    private LinearLayout.LayoutParams size(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private FrameLayout.LayoutParams matchParent() {
        return new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private GradientDrawable rounded(int color, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        return drawable;
    }

    private void applySystemBarColors() {
        getWindow().setStatusBarColor(backgroundColor());
        getWindow().setNavigationBarColor(backgroundColor());
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        if (mDarkPreview) {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private int backgroundColor() {
        return mDarkPreview ? Color.rgb(18, 22, 25) : getColor(R.color.smoke_background);
    }

    private int surfaceColor() {
        return mDarkPreview ? Color.rgb(31, 36, 41) : getColor(R.color.smoke_surface);
    }

    private int tileBackgroundColor() {
        return mDarkPreview ? Color.rgb(43, 50, 56) : getColor(R.color.smoke_tile_background);
    }

    private int warningBackgroundColor() {
        return mDarkPreview ? Color.rgb(47, 39, 24) : getColor(R.color.smoke_warning_background);
    }

    private int warningBorderColor() {
        return mDarkPreview ? Color.rgb(128, 101, 42) : getColor(R.color.smoke_warning_border);
    }

    private int borderColor() {
        return mDarkPreview ? Color.rgb(61, 69, 77) : getColor(R.color.smoke_border);
    }

    private int textColor() {
        return mDarkPreview ? Color.rgb(238, 242, 245) : getColor(R.color.smoke_text);
    }

    private int mutedTextColor() {
        return mDarkPreview ? Color.rgb(175, 185, 194) : getColor(R.color.smoke_text_muted);
    }

    private int accentColor() {
        return mDarkPreview ? Color.rgb(111, 211, 190) : getColor(R.color.smoke_accent);
    }

    private int accentSoftColor() {
        return mDarkPreview ? Color.rgb(38, 86, 76) : getColor(R.color.smoke_accent_soft);
    }

    private int previewTextColor() {
        return mDarkPreview ? Color.rgb(238, 242, 245) : Color.rgb(255, 255, 255);
    }

    private int previewGlassColor() {
        int light = Math.max(150, 235 - mGlassDepth);
        return mDarkPreview ? Color.rgb(38 + mGlassDepth / 10, 46 + mGlassDepth / 12, 52 + mGlassDepth / 14)
                : Color.argb(235, light, 246, 243);
    }

    private int previewGlassBorderColor() {
        return mDarkPreview ? Color.rgb(82, 96, 103) : Color.rgb(224, 239, 235);
    }

    private int previewIconColor(int index) {
        int[][] light = {
                { 72, 153, 133 },
                { 76, 132, 170 },
                { 92, 105, 170 },
                { 181, 128, 70 },
                { 93, 149, 118 },
                { 164, 111, 143 },
                { 88, 137, 177 },
                { 205, 157, 74 }
        };
        int[][] dark = {
                { 62, 142, 124 },
                { 58, 112, 148 },
                { 82, 90, 150 },
                { 158, 112, 66 },
                { 72, 124, 98 },
                { 142, 88, 120 },
                { 70, 112, 150 },
                { 170, 132, 64 }
        };
        int[] color = (mDarkPreview ? dark : light)[index % light.length];
        return Color.rgb(color[0], color[1], color[2]);
    }

    private int seekInactiveColor() {
        return mDarkPreview ? Color.rgb(67, 76, 83) : Color.rgb(218, 226, 232);
    }

    private GradientDrawable previewWallpaperBackground() {
        int[] colors = mDarkPreview
                ? new int[] { Color.rgb(24, 31, 36), Color.rgb(38, 62, 58), Color.rgb(22, 26, 30) }
                : new int[] { Color.rgb(110, 151, 169), Color.rgb(73, 122, 111), Color.rgb(238, 242, 238) };
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        drawable.setCornerRadius(dp(28));
        drawable.setStroke(dp(1), borderColor());
        return drawable;
    }

    private int selectedThemeTextColor() {
        return mDarkPreview ? Color.rgb(7, 31, 27) : Color.WHITE;
    }

    private void showPreviewToast(int labelResId) {
        Toast.makeText(this, getString(R.string.standalone_preview_toast,
                getString(labelResId)), Toast.LENGTH_SHORT).show();
    }

    private void showSearchReturnShell() {
        if (mSearchReturnScreen == SCREEN_DRAWER) {
            showAppDrawerShell();
            return;
        }
        if (mSearchReturnScreen == SCREEN_SETTINGS) {
            showSettingsScreen();
            return;
        }
        showHomeShell();
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
