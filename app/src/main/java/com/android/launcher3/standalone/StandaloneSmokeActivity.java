package com.android.launcher3.standalone;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public final class StandaloneSmokeActivity extends Activity {
    private static final int SCREEN_HOME = 0;
    private static final int SCREEN_DRAWER = 1;
    private static final int SCREEN_SETTINGS = 2;
    private static final int SCREEN_SEARCH = 3;

    private int mCurrentScreen = SCREEN_HOME;
    private int mSearchReturnScreen = SCREEN_HOME;
    private boolean mDarkPreview;

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
        if (mCurrentScreen != SCREEN_HOME) {
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
        warning.setTextColor(mutedTextColor());
        warning.setLineSpacing(dp(2), 1.0f);
        warning.setPadding(dp(16), dp(14), dp(16), dp(14));
        warning.setBackground(rounded(warningBackgroundColor(), dp(18),
                dp(1), warningBorderColor()));
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(24);
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
        warning.setTextColor(mutedTextColor());
        warning.setLineSpacing(dp(2), 1.0f);
        warning.setPadding(dp(16), dp(14), dp(16), dp(14));
        warning.setBackground(rounded(warningBackgroundColor(), dp(18),
                dp(1), warningBorderColor()));
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
        warning.setTextColor(mutedTextColor());
        warning.setLineSpacing(dp(2), 1.0f);
        warning.setPadding(dp(16), dp(14), dp(16), dp(14));
        warning.setBackground(rounded(warningBackgroundColor(), dp(18),
                dp(1), warningBorderColor()));
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(16);
        root.addView(warning, warningParams);

        setContentView(scrollView);
    }

    private void showSettingsShell() {
        mCurrentScreen = SCREEN_SETTINGS;
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        applySystemBarColors();
        scrollView.setBackgroundColor(backgroundColor());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        scrollView.addView(root, matchParent());

        addPreviewHeader(root, R.string.standalone_settings_back,
                R.string.standalone_settings_title,
                R.string.standalone_settings_subtitle);

        TextView warning = label(R.string.standalone_settings_notice, 14, Typeface.NORMAL);
        warning.setTextColor(mutedTextColor());
        warning.setLineSpacing(dp(2), 1.0f);
        warning.setPadding(dp(16), dp(14), dp(16), dp(14));
        warning.setBackground(rounded(warningBackgroundColor(), dp(18),
                dp(1), warningBorderColor()));
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(24);
        root.addView(warning, warningParams);

        addThemeModeSection(root);
        addSettingsSection(root, R.string.standalone_settings_appearance,
                R.string.standalone_settings_placeholder);
        addSettingsSection(root, R.string.standalone_settings_home_screen,
                R.string.standalone_settings_placeholder);
        addSettingsSection(root, R.string.standalone_settings_dock,
                R.string.standalone_settings_placeholder);
        addSettingsSection(root, R.string.standalone_settings_search,
                R.string.standalone_settings_placeholder);
        addSettingsSection(root, R.string.standalone_settings_about,
                R.string.standalone_settings_about_body);

        setContentView(scrollView);
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
        showSettingsShell();
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
        showHomeShell();
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
