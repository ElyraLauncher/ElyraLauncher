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

    private int mCurrentScreen = SCREEN_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeShell();
    }

    @Override
    public void onBackPressed() {
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
        scrollView.setBackgroundColor(getColor(R.color.smoke_background));

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
        icon.setTextColor(getColor(R.color.smoke_accent));
        icon.setBackground(rounded(getColor(R.color.smoke_surface), dp(16), dp(1),
                getColor(R.color.smoke_border)));
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
        status.setTextColor(getColor(R.color.smoke_text_muted));
        LinearLayout.LayoutParams statusParams = matchWidthWrapHeight();
        statusParams.topMargin = dp(2);
        titleGroup.addView(status, statusParams);

        TextView warning = label(R.string.standalone_smoke_body, 14, Typeface.NORMAL);
        warning.setTextColor(getColor(R.color.smoke_text_muted));
        warning.setLineSpacing(dp(2), 1.0f);
        warning.setPadding(dp(16), dp(14), dp(16), dp(14));
        warning.setBackground(rounded(getColor(R.color.smoke_warning_background), dp(18),
                dp(1), getColor(R.color.smoke_warning_border)));
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(24);
        root.addView(warning, warningParams);

        TextView search = label(R.string.standalone_search_placeholder, 15, Typeface.NORMAL);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setTextColor(getColor(R.color.smoke_text_muted));
        search.setPadding(dp(18), 0, dp(18), 0);
        search.setBackground(rounded(getColor(R.color.smoke_surface), dp(24), dp(1),
                getColor(R.color.smoke_border)));
        search.setOnClickListener(view -> Toast.makeText(this, R.string.standalone_search_toast,
                Toast.LENGTH_SHORT).show());
        LinearLayout.LayoutParams searchParams = matchWidth(dp(52));
        searchParams.topMargin = dp(22);
        root.addView(search, searchParams);

        TextView workspaceLabel = label(R.string.standalone_workspace_title, 13, Typeface.BOLD);
        workspaceLabel.setAllCaps(true);
        workspaceLabel.setTextColor(getColor(R.color.smoke_text_muted));
        LinearLayout.LayoutParams workspaceLabelParams = matchWidthWrapHeight();
        workspaceLabelParams.topMargin = dp(28);
        root.addView(workspaceLabel, workspaceLabelParams);

        GridLayout workspace = new GridLayout(this);
        workspace.setColumnCount(2);
        workspace.setRowCount(2);
        workspace.setPadding(dp(12), dp(12), dp(12), dp(12));
        workspace.setBackground(rounded(getColor(R.color.smoke_surface), dp(24), dp(1),
                getColor(R.color.smoke_border)));
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
        dock.setBackground(rounded(getColor(R.color.smoke_surface), dp(28), dp(1),
                getColor(R.color.smoke_border)));
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
        footer.setTextColor(getColor(R.color.smoke_text_muted));
        LinearLayout.LayoutParams footerParams = matchWidthWrapHeight();
        footerParams.topMargin = dp(24);
        root.addView(footer, footerParams);

        setContentView(scrollView);
    }


    private void showAppDrawerShell() {
        mCurrentScreen = SCREEN_DRAWER;
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(getColor(R.color.smoke_background));

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
        search.setTextColor(getColor(R.color.smoke_text_muted));
        search.setPadding(dp(18), 0, dp(18), 0);
        search.setBackground(rounded(getColor(R.color.smoke_surface), dp(24), dp(1),
                getColor(R.color.smoke_border)));
        search.setOnClickListener(view -> Toast.makeText(this, R.string.standalone_search_toast,
                Toast.LENGTH_SHORT).show());
        LinearLayout.LayoutParams searchParams = matchWidth(dp(52));
        searchParams.topMargin = dp(22);
        root.addView(search, searchParams);

        GridLayout apps = new GridLayout(this);
        apps.setColumnCount(4);
        apps.setPadding(dp(10), dp(10), dp(10), dp(10));
        apps.setBackground(rounded(getColor(R.color.smoke_surface), dp(24), dp(1),
                getColor(R.color.smoke_border)));
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
        warning.setTextColor(getColor(R.color.smoke_text_muted));
        warning.setLineSpacing(dp(2), 1.0f);
        warning.setPadding(dp(16), dp(14), dp(16), dp(14));
        warning.setBackground(rounded(getColor(R.color.smoke_warning_background), dp(18),
                dp(1), getColor(R.color.smoke_warning_border)));
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(16);
        root.addView(warning, warningParams);

        setContentView(scrollView);
    }

    private void showSettingsShell() {
        mCurrentScreen = SCREEN_SETTINGS;
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(getColor(R.color.smoke_background));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        scrollView.addView(root, matchParent());

        addPreviewHeader(root, R.string.standalone_settings_back,
                R.string.standalone_settings_title,
                R.string.standalone_settings_subtitle);

        TextView warning = label(R.string.standalone_settings_notice, 14, Typeface.NORMAL);
        warning.setTextColor(getColor(R.color.smoke_text_muted));
        warning.setLineSpacing(dp(2), 1.0f);
        warning.setPadding(dp(16), dp(14), dp(16), dp(14));
        warning.setBackground(rounded(getColor(R.color.smoke_warning_background), dp(18),
                dp(1), getColor(R.color.smoke_warning_border)));
        LinearLayout.LayoutParams warningParams = matchWidthWrapHeight();
        warningParams.topMargin = dp(24);
        root.addView(warning, warningParams);

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
        view.setTextColor(getColor(R.color.smoke_text));
        view.setTextSize(textSizeSp);
        view.setTypeface(Typeface.DEFAULT, typefaceStyle);
        return view;
    }

    private TextView label(CharSequence text, float textSizeSp, int typefaceStyle) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(getColor(R.color.smoke_text));
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
        backButton.setOnClickListener(view -> showHomeShell());
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
        status.setTextColor(getColor(R.color.smoke_text_muted));
        LinearLayout.LayoutParams statusParams = matchWidthWrapHeight();
        statusParams.topMargin = dp(2);
        titleGroup.addView(status, statusParams);
    }

    private void addWorkspaceTile(GridLayout workspace, int labelResId) {
        LinearLayout tile = new LinearLayout(this);
        tile.setGravity(Gravity.CENTER);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setPadding(dp(10), dp(12), dp(10), dp(12));
        tile.setBackground(rounded(getColor(R.color.smoke_tile_background), dp(18), 0,
                Color.TRANSPARENT));

        View marker = new View(this);
        marker.setBackground(rounded(getColor(R.color.smoke_accent_soft), dp(10), 0,
                Color.TRANSPARENT));
        tile.addView(marker, size(dp(42), dp(42)));

        TextView label = label(labelResId, 13, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(getColor(R.color.smoke_text_muted));
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
        dot.setTextColor(getColor(R.color.smoke_accent));
        dot.setBackground(rounded(getColor(R.color.smoke_tile_background), dp(14), 0,
                Color.TRANSPARENT));
        item.addView(dot, size(dp(48), dp(48)));

        TextView label = label(labelResId, 11, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(getColor(R.color.smoke_text_muted));
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
        item.setBackground(rounded(getColor(R.color.smoke_tile_background), dp(18), 0,
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
        icon.setTextColor(getColor(R.color.smoke_accent));
        icon.setBackground(rounded(getColor(R.color.smoke_accent_soft), dp(16), 0,
                Color.TRANSPARENT));
        item.addView(icon, size(dp(52), dp(52)));

        TextView label = label(labelResId, 12, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(getColor(R.color.smoke_text));
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
        section.setBackground(rounded(getColor(R.color.smoke_surface), dp(18), dp(1),
                getColor(R.color.smoke_border)));

        TextView title = label(titleResId, 16, Typeface.BOLD);
        section.addView(title, matchWidthWrapHeight());

        TextView body = label(bodyResId, 13, Typeface.NORMAL);
        body.setTextColor(getColor(R.color.smoke_text_muted));
        body.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams bodyParams = matchWidthWrapHeight();
        bodyParams.topMargin = dp(6);
        section.addView(body, bodyParams);

        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(12);
        root.addView(section, params);
    }

    private TextView actionButton(int labelResId) {
        TextView button = label(labelResId, 14, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(getColor(R.color.smoke_text));
        button.setPadding(dp(14), 0, dp(14), 0);
        button.setBackground(rounded(getColor(R.color.smoke_surface), dp(18), dp(1),
                getColor(R.color.smoke_border)));
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


    private void showPreviewToast(int labelResId) {
        Toast.makeText(this, getString(R.string.standalone_preview_toast,
                getString(labelResId)), Toast.LENGTH_SHORT).show();
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
