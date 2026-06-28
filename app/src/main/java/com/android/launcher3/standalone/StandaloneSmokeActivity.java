package com.android.launcher3.standalone;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
    private static final int SCREEN_SEARCH_SETTINGS = 8;
    private static final int SCREEN_ABOUT_SETTINGS = 9;
    private static final int SCREEN_SELECTOR = 10;
    private static final int SCREEN_DRAWER_SETTINGS = 11;
    private static final int SCREEN_CUSTOMIZE_SEARCH = 12;

    private static final int GLASS_DEPTH_LIGHT = 35;
    private static final int GLASS_DEPTH_MEDIUM = 65;
    private static final int GLASS_DEPTH_DEEP = 88;

    private int mCurrentScreen = SCREEN_HOME;
    private int mSearchReturnScreen = SCREEN_HOME;
    private boolean mDarkPreview;
    private boolean mHomeIconLabels = true;
    private boolean mHomeWidgetArea = true;
    private boolean mDockLabels;
    private boolean mDockSearch = true;
    private boolean mDrawerCategoriesMode;
    private boolean mSearchShortcuts = true;
    private boolean mSearchVoiceButton = true;
    private boolean mSearchLensButton = true;
    private int mGlassDepth = GLASS_DEPTH_MEDIUM;
    private int mHomeGridSizeIndex = 1;
    private int mHomeIconSizeIndex = 1;
    private int mDockAppsIndex;
    private int mSearchThemeIndex;
    private int mSearchHue = 42;
    private int mSearchSaturation = 62;
    private int mSearchTransparency = 72;
    private Runnable mSelectorBackAction = this::showSettingsScreen;

    private final int[] mHomeGridSizeValues = {
            R.string.standalone_selector_grid_4x5,
            R.string.standalone_selector_grid_4x6,
            R.string.standalone_selector_grid_5x6,
            R.string.standalone_selector_grid_5x7
    };
    private final int[] mHomeIconSizeValues = {
            R.string.standalone_selector_size_small,
            R.string.standalone_selector_size_medium,
            R.string.standalone_selector_size_large
    };
    private final int[] mDockAppsValues = {
            R.string.standalone_selector_dock_apps_4,
            R.string.standalone_selector_dock_apps_5,
            R.string.standalone_selector_dock_apps_6
    };
    private final int[] mSearchThemeValues = {
            R.string.standalone_search_theme_system,
            R.string.standalone_search_theme_light,
            R.string.standalone_search_theme_dark,
            R.string.standalone_search_theme_custom
    };

    private interface SelectionHandler { void onSelected(int index); }
    private interface IntSetter { void set(int value); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeShell();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentScreen == SCREEN_SEARCH) { showSearchReturnShell(); return; }
        if (mCurrentScreen == SCREEN_GLASS_DEPTH) { showAppearanceScreen(); return; }
        if (mCurrentScreen == SCREEN_HOME_SETTINGS || mCurrentScreen == SCREEN_DOCK_SETTINGS
                || mCurrentScreen == SCREEN_SEARCH_SETTINGS || mCurrentScreen == SCREEN_DRAWER_SETTINGS
                || mCurrentScreen == SCREEN_ABOUT_SETTINGS) { showSettingsScreen(); return; }
        if (mCurrentScreen == SCREEN_CUSTOMIZE_SEARCH) { showSearchSettingsScreen(); return; }
        if (mCurrentScreen == SCREEN_SELECTOR) { mSelectorBackAction.run(); return; }
        if (mCurrentScreen == SCREEN_APPEARANCE) { showSettingsScreen(); return; }
        if (mCurrentScreen == SCREEN_SETTINGS || mCurrentScreen == SCREEN_DRAWER) { showHomeShell(); return; }
        super.onBackPressed();
    }

    private void showHomeShell() {
        mCurrentScreen = SCREEN_HOME;
        applySystemBarColors(true);
        FrameLayout root = createWallpaperRoot();
        addHomeExperience(root, true, false);
        setContentView(root);
    }

    private void showAppDrawerShell() {
        mCurrentScreen = SCREEN_DRAWER;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_app_drawer_title, R.string.standalone_app_drawer_subtitle, this::showHomeShell);
        TextView search = pill(R.string.standalone_app_drawer_search_placeholder, 15);
        search.setOnClickListener(view -> showSearchShell(SCREEN_DRAWER));
        LinearLayout.LayoutParams searchParams = matchWidth(dp(52));
        searchParams.topMargin = dp(22);
        root.addView(search, searchParams);
        LinearLayout switcher = new LinearLayout(this);
        switcher.setGravity(Gravity.CENTER);
        switcher.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams switcherParams = matchWidthWrapHeight();
        switcherParams.topMargin = dp(14);
        root.addView(switcher, switcherParams);
        TextView all = segmentButton(R.string.standalone_drawer_mode_all, !mDrawerCategoriesMode);
        all.setOnClickListener(view -> { mDrawerCategoriesMode = false; showAppDrawerShell(); });
        switcher.addView(all, actionButtonParams(false));
        TextView categories = segmentButton(R.string.standalone_drawer_mode_categories, mDrawerCategoriesMode);
        categories.setOnClickListener(view -> { mDrawerCategoriesMode = true; showAppDrawerShell(); });
        switcher.addView(categories, actionButtonParams(true));
        if (mDrawerCategoriesMode) addCategoryGrid(root); else addAllAppsGrid(root);
        createStandaloneNotice(root);
    }

    private void showSearchShell(int returnScreen) {
        mCurrentScreen = SCREEN_SEARCH;
        mSearchReturnScreen = returnScreen;
        applySystemBarColors(true);
        FrameLayout root = createWallpaperRoot();
        addHomeExperience(root, false, true);
        View dim = new View(this);
        dim.setBackgroundColor(mDarkPreview ? Color.argb(178, 0, 0, 0) : Color.argb(136, 16, 24, 28));
        root.addView(dim, matchParent());
        LinearLayout overlay = new LinearLayout(this);
        overlay.setOrientation(LinearLayout.VERTICAL);
        overlay.setPadding(dp(22), dp(58), dp(22), dp(10));
        root.addView(overlay, matchParent());
        TextView close = actionButton(R.string.standalone_search_back);
        close.setOnClickListener(view -> showSearchReturnShell());
        overlay.addView(close, new LinearLayout.LayoutParams(dp(88), dp(42)));
        overlay.addView(new SpaceView(this), new LinearLayout.LayoutParams(1, 0, 1.0f));
        LinearLayout suggestions = searchOverlayCard(R.string.standalone_search_suggested_apps);
        LinearLayout suggestedApps = new LinearLayout(this);
        suggestedApps.setGravity(Gravity.CENTER);
        suggestedApps.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams suggestedParams = matchWidthWrapHeight();
        suggestedParams.topMargin = dp(12);
        suggestions.addView(suggestedApps, suggestedParams);
        addOverlayApp(suggestedApps, R.string.standalone_search_phone, 0);
        addOverlayApp(suggestedApps, R.string.standalone_search_messages, 1);
        addOverlayApp(suggestedApps, R.string.standalone_drawer_gallery, 2);
        addOverlayApp(suggestedApps, R.string.standalone_drawer_play_store, 3);
        overlay.addView(suggestions, bottomCardParams());
        LinearLayout quick = searchOverlayCard(R.string.standalone_search_quick_actions);
        GridLayout actions = new GridLayout(this);
        actions.setColumnCount(4);
        LinearLayout.LayoutParams actionsParams = matchWidthWrapHeight();
        actionsParams.topMargin = dp(10);
        quick.addView(actions, actionsParams);
        addQuickAction(actions, R.string.standalone_search_ai_mode);
        addQuickAction(actions, R.string.standalone_search_translate);
        addQuickAction(actions, R.string.standalone_search_song_search);
        addQuickAction(actions, R.string.standalone_search_weather);
        addQuickAction(actions, R.string.standalone_search_sports);
        addQuickAction(actions, R.string.standalone_search_dictionary);
        addQuickAction(actions, R.string.standalone_search_finance);
        addQuickAction(actions, R.string.standalone_search_saved);
        overlay.addView(quick, bottomCardParams());
        LinearLayout searchPill = new LinearLayout(this);
        searchPill.setGravity(Gravity.CENTER_VERTICAL);
        searchPill.setOrientation(LinearLayout.HORIZONTAL);
        searchPill.setPadding(dp(16), 0, dp(10), 0);
        searchPill.setBackground(rounded(searchPreviewColor(), dp(28), dp(1), previewGlassBorderColor()));
        TextView provider = label(R.string.standalone_search_provider_initial, 18, Typeface.BOLD);
        provider.setGravity(Gravity.CENTER);
        provider.setTextColor(accentColor());
        searchPill.addView(provider, size(dp(30), dp(48)));
        EditText input = new EditText(this);
        input.setHint(R.string.standalone_google_search_placeholder);
        input.setSingleLine(true);
        input.setTextSize(15);
        input.setTextColor(textColor());
        input.setHintTextColor(mutedTextColor());
        input.setBackgroundColor(Color.TRANSPARENT);
        searchPill.addView(input, new LinearLayout.LayoutParams(0, dp(56), 1f));
        TextView mic = label(R.string.standalone_search_voice_short, 14, Typeface.BOLD);
        mic.setGravity(Gravity.CENTER);
        mic.setOnClickListener(view -> showPreviewToast(R.string.standalone_search_voice_search));
        searchPill.addView(mic, size(dp(40), dp(48)));
        TextView lens = label(R.string.standalone_search_lens_short, 14, Typeface.BOLD);
        lens.setGravity(Gravity.CENTER);
        lens.setOnClickListener(view -> showPreviewToast(R.string.standalone_search_lens));
        searchPill.addView(lens, size(dp(42), dp(48)));
        LinearLayout.LayoutParams pillParams = matchWidth(dp(58));
        pillParams.topMargin = dp(12);
        overlay.addView(searchPill, pillParams);
        addFakeKeyboard(overlay);
        setContentView(root);
        input.requestFocus();
        input.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        }, 250);
    }

    private void showSettingsShell() { showSettingsScreen(); }

    private LinearLayout createScreenContainer() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        applySystemBarColors(false);
        scrollView.setBackgroundColor(backgroundColor());
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        scrollView.addView(root, matchParent());
        setContentView(scrollView);
        return root;
    }

    private void showSettingsScreen() {
        mCurrentScreen = SCREEN_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_settings_title, R.string.standalone_settings_subtitle, this::showHomeShell);
        createHeroInfoCard(root);
        LinearLayout section = createGroupedSection(root, 0);
        createSettingsRow(section, R.string.standalone_settings_appearance, 0, true, view -> showAppearanceScreen());
        createSettingsRow(section, R.string.standalone_settings_home_screen, 0, true, view -> showHomeSettingsScreen());
        createSettingsRow(section, R.string.standalone_settings_dock, 0, true, view -> showDockSettingsScreen());
        createSettingsRow(section, R.string.standalone_settings_search, 0, true, view -> showSearchSettingsScreen());
        createSettingsRow(section, R.string.standalone_settings_drawer, 0, true, view -> showDrawerSettingsScreen());
        createSettingsRow(section, R.string.standalone_settings_about, 0, true, view -> showAboutSettingsScreen());
        createStandaloneNotice(root);
    }

    private void showAppearanceScreen() {
        mCurrentScreen = SCREEN_APPEARANCE;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_appearance_title, R.string.standalone_settings_subtitle, this::showSettingsScreen);
        createPreviewLauncherCard(root);
        LinearLayout section = createGroupedSection(root, 0);
        createSettingsRow(section, R.string.standalone_appearance_theme_mode,
                mDarkPreview ? R.string.standalone_appearance_dark : R.string.standalone_appearance_light,
                true, view -> setThemePreview(!mDarkPreview));
        createSettingsRow(section, R.string.standalone_appearance_accent_color, R.string.standalone_appearance_color_value, true,
                view -> showPreviewToast(R.string.standalone_appearance_accent_color));
        createSettingsRow(section, R.string.standalone_elyra_glass, R.string.standalone_glass_depth_value, true, view -> showGlassDepthScreen());
        createSettingsRow(section, R.string.standalone_icon_pack, R.string.standalone_icon_pack_value, true,
                view -> Toast.makeText(this, R.string.standalone_elyraicons_preview_only, Toast.LENGTH_SHORT).show());
        createSettingsRow(section, R.string.standalone_icon_shape, R.string.standalone_icon_shape_value, true,
                view -> showPreviewToast(R.string.standalone_icon_shape));
        createSettingsRow(section, R.string.standalone_visual_style, R.string.standalone_visual_style_value, true,
                view -> showPreviewToast(R.string.standalone_visual_style));
    }

    private void showHomeSettingsScreen() {
        mCurrentScreen = SCREEN_HOME_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_home_settings_title, R.string.standalone_settings_subtitle, this::showSettingsScreen);
        createPreviewLauncherCard(root);
        LinearLayout section = createGroupedSection(root, 0);
        createSettingsRow(section, R.string.standalone_home_grid_size, mHomeGridSizeValues[mHomeGridSizeIndex], true,
                view -> showSelectorScreen(R.string.standalone_home_grid_size, mHomeGridSizeValues, mHomeGridSizeIndex,
                        index -> { mHomeGridSizeIndex = index; showHomeSettingsScreen(); }, this::showHomeSettingsScreen));
        createSettingsRow(section, R.string.standalone_icon_size, mHomeIconSizeValues[mHomeIconSizeIndex], true,
                view -> showSelectorScreen(R.string.standalone_icon_size, mHomeIconSizeValues, mHomeIconSizeIndex,
                        index -> { mHomeIconSizeIndex = index; showHomeSettingsScreen(); }, this::showHomeSettingsScreen));
        createSettingsRow(section, R.string.standalone_home_icon_labels,
                mHomeIconLabels ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> { mHomeIconLabels = !mHomeIconLabels; showHomeSettingsScreen(); });
        createSettingsRow(section, R.string.standalone_home_widget_area,
                mHomeWidgetArea ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> { mHomeWidgetArea = !mHomeWidgetArea; showHomeSettingsScreen(); });
        createSettingsRow(section, R.string.standalone_home_page_indicator, R.string.standalone_home_page_indicator_value, true,
                view -> showPreviewToast(R.string.standalone_home_page_indicator));
        createStandaloneNotice(root);
    }

    private void showDockSettingsScreen() {
        mCurrentScreen = SCREEN_DOCK_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_dock_settings_title, R.string.standalone_settings_subtitle, this::showSettingsScreen);
        createDockPreviewCard(root);
        LinearLayout section = createGroupedSection(root, 0);
        createSettingsRow(section, R.string.standalone_dock_apps, mDockAppsValues[mDockAppsIndex], true,
                view -> showSelectorScreen(R.string.standalone_dock_apps, mDockAppsValues, mDockAppsIndex,
                        index -> { mDockAppsIndex = index; showDockSettingsScreen(); }, this::showDockSettingsScreen));
        createSettingsRow(section, R.string.standalone_dock_labels,
                mDockLabels ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> { mDockLabels = !mDockLabels; showDockSettingsScreen(); });
        createSettingsRow(section, R.string.standalone_dock_style, R.string.standalone_dock_style_value, true,
                view -> showPreviewToast(R.string.standalone_dock_style));
        createSettingsRow(section, R.string.standalone_dock_search,
                mDockSearch ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> { mDockSearch = !mDockSearch; showDockSettingsScreen(); });
        createStandaloneNotice(root);
    }

    private void showSearchSettingsScreen() {
        mCurrentScreen = SCREEN_SEARCH_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_search_title, R.string.standalone_settings_subtitle, this::showSettingsScreen);
        createSearchPreviewCard(root);
        LinearLayout section = createGroupedSection(root, 0);
        createSettingsRow(section, R.string.standalone_search_provider, R.string.standalone_search_provider_google, true,
                view -> showPreviewToast(R.string.standalone_search_provider_google));
        createSettingsRow(section, R.string.standalone_search_overlay_style, R.string.standalone_search_overlay_dim_blur, true,
                view -> showSearchShell(SCREEN_SETTINGS));
        createSettingsRow(section, R.string.standalone_search_shortcuts,
                mSearchShortcuts ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> { mSearchShortcuts = !mSearchShortcuts; showSearchSettingsScreen(); });
        createSettingsRow(section, R.string.standalone_search_voice_button,
                mSearchVoiceButton ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> { mSearchVoiceButton = !mSearchVoiceButton; showSearchSettingsScreen(); });
        createSettingsRow(section, R.string.standalone_search_lens_button,
                mSearchLensButton ? R.string.standalone_value_on : R.string.standalone_value_off,
                true, view -> { mSearchLensButton = !mSearchLensButton; showSearchSettingsScreen(); });
        createSettingsRow(section, R.string.standalone_customize_search, 0, true, view -> showCustomizeSearchScreen());
        createStandaloneNotice(root);
    }

    private void showDrawerSettingsScreen() {
        mCurrentScreen = SCREEN_DRAWER_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_settings_drawer, R.string.standalone_settings_subtitle, this::showSettingsScreen);
        LinearLayout section = createGroupedSection(root, 0);
        createSettingsRow(section, R.string.standalone_drawer_mode,
                mDrawerCategoriesMode ? R.string.standalone_drawer_mode_categories : R.string.standalone_drawer_mode_all,
                true, view -> { mDrawerCategoriesMode = !mDrawerCategoriesMode; showDrawerSettingsScreen(); });
        createSettingsRow(section, R.string.standalone_drawer_categories, R.string.standalone_value_on, true,
                view -> showPreviewToast(R.string.standalone_drawer_categories));
        createSettingsRow(section, R.string.standalone_drawer_search, R.string.standalone_value_on, true,
                view -> showSearchShell(SCREEN_SETTINGS));
        createSettingsRow(section, R.string.standalone_drawer_app_suggestions, R.string.standalone_value_on, true,
                view -> showPreviewToast(R.string.standalone_drawer_app_suggestions));
        createStandaloneNotice(root);
    }

    private void showCustomizeSearchScreen() {
        mCurrentScreen = SCREEN_CUSTOMIZE_SEARCH;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_customize_google_search, R.string.standalone_search_private_preview_only, this::showSearchSettingsScreen);
        createGoogleSearchPreview(root);
        LinearLayout theme = createGroupedSection(root, R.string.standalone_customize_theme);
        for (int i = 0; i < mSearchThemeValues.length; i++) {
            final int index = i;
            createSelectableRow(theme, mSearchThemeValues[i], i == mSearchThemeIndex, view -> { mSearchThemeIndex = index; showCustomizeSearchScreen(); });
        }
        LinearLayout color = createGroupedSection(root, R.string.standalone_customize_color);
        createSettingsRow(color, R.string.standalone_appearance_accent_color, R.string.standalone_appearance_color_value, true,
                view -> showPreviewToast(R.string.standalone_appearance_accent_color));
        createSliderRow(color, R.string.standalone_customize_hue, mSearchHue, value -> mSearchHue = value);
        createSliderRow(color, R.string.standalone_customize_saturation, mSearchSaturation, value -> mSearchSaturation = value);
        createSliderRow(color, R.string.standalone_customize_transparency, mSearchTransparency, value -> mSearchTransparency = value);
        LinearLayout shortcuts = createGroupedSection(root, R.string.standalone_customize_shortcuts);
        int[] shortcutIds = { R.string.standalone_search_ai_mode, R.string.standalone_search_voice_search,
                R.string.standalone_search_lens, R.string.standalone_search_translate,
                R.string.standalone_search_song_search, R.string.standalone_search_weather,
                R.string.standalone_search_sports, R.string.standalone_search_dictionary,
                R.string.standalone_search_finance, R.string.standalone_search_saved };
        for (int id : shortcutIds) createSettingsRow(shortcuts, id, R.string.standalone_value_on, true, view -> showPreviewToast(id));
        createStandaloneNotice(root);
    }

    private void showAboutSettingsScreen() {
        mCurrentScreen = SCREEN_ABOUT_SETTINGS;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_settings_about, R.string.standalone_settings_subtitle, this::showSettingsScreen);
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(20), dp(20), dp(20), dp(20));
        card.setBackground(rounded(surfaceColor(), dp(24), dp(1), borderColor()));
        TextView name = label(R.string.standalone_about_name, 22, Typeface.BOLD);
        card.addView(name, matchWidthWrapHeight());
        TextView preview = label(R.string.standalone_about_preview, 14, Typeface.NORMAL);
        preview.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams previewParams = matchWidthWrapHeight();
        previewParams.topMargin = dp(4);
        card.addView(preview, previewParams);
        addAboutRow(card, R.string.standalone_about_version, R.string.standalone_about_version_value);
        addAboutRow(card, R.string.standalone_about_build_type, R.string.standalone_about_build_type_value);
        addAboutRow(card, R.string.standalone_about_rom_target, R.string.standalone_about_rom_target_value);
        addAboutRow(card, R.string.standalone_about_quickstep_status, R.string.standalone_about_requires_rom_build);
        addAboutRow(card, R.string.standalone_about_recents_status, R.string.standalone_about_requires_rom_build);
        LinearLayout.LayoutParams cardParams = matchWidthWrapHeight();
        cardParams.topMargin = dp(24);
        root.addView(card, cardParams);
        TextView note = label(R.string.standalone_about_note, 14, Typeface.NORMAL);
        note.setTextColor(mutedTextColor());
        note.setLineSpacing(dp(3), 1.0f);
        note.setPadding(dp(18), dp(16), dp(18), dp(16));
        note.setBackground(rounded(surfaceColor(), dp(22), dp(1), borderColor()));
        LinearLayout.LayoutParams noteParams = matchWidthWrapHeight();
        noteParams.topMargin = dp(16);
        root.addView(note, noteParams);
    }

    private void showGlassDepthScreen() {
        mCurrentScreen = SCREEN_GLASS_DEPTH;
        LinearLayout root = createScreenContainer();
        createHeader(root, R.string.standalone_glass_depth, R.string.standalone_elyra_glass, this::showAppearanceScreen);
        TextView description = label(R.string.standalone_glass_depth_description, 14, Typeface.NORMAL);
        description.setTextColor(mutedTextColor());
        description.setLineSpacing(dp(3), 1.0f);
        LinearLayout.LayoutParams descriptionParams = matchWidthWrapHeight();
        descriptionParams.topMargin = dp(22);
        root.addView(description, descriptionParams);
        createPreviewLauncherCard(root);
        createModernSeekBarRow(root);
        LinearLayout presets = createGroupedSection(root, R.string.standalone_glass_depth_presets);
        createPresetRow(presets, R.string.standalone_glass_depth_light_preset, GLASS_DEPTH_LIGHT);
        createPresetRow(presets, R.string.standalone_glass_depth_medium_preset, GLASS_DEPTH_MEDIUM);
        createPresetRow(presets, R.string.standalone_glass_depth_deep_preset, GLASS_DEPTH_DEEP);
        createPresetRow(presets, R.string.standalone_glass_depth_custom_preset, mGlassDepth);
    }

    private void addHomeExperience(FrameLayout parent, boolean interactive, boolean dimmed) {
        LinearLayout home = new LinearLayout(this);
        home.setOrientation(LinearLayout.VERTICAL);
        home.setPadding(dp(24), dp(46), dp(24), dp(18));
        parent.addView(home, matchParent());
        LinearLayout widget = new LinearLayout(this);
        widget.setOrientation(LinearLayout.VERTICAL);
        TextView date = label(R.string.standalone_home_date, 18, Typeface.BOLD);
        date.setTextColor(previewTextColor());
        widget.addView(date, matchWidthWrapHeight());
        TextView weather = label(R.string.standalone_home_weather, 14, Typeface.NORMAL);
        weather.setTextColor(Color.argb(220, 255, 255, 255));
        LinearLayout.LayoutParams weatherParams = matchWidthWrapHeight();
        weatherParams.topMargin = dp(4);
        widget.addView(weather, weatherParams);
        home.addView(widget, matchWidthWrapHeight());
        GridLayout icons = new GridLayout(this);
        icons.setColumnCount(4);
        LinearLayout.LayoutParams iconsParams = matchWidthWrapHeight();
        iconsParams.topMargin = dp(46);
        home.addView(icons, iconsParams);
        addHomeIcon(icons, R.string.standalone_dock_phone, 0, interactive);
        addHomeIcon(icons, R.string.standalone_dock_messages, 1, interactive);
        addHomeIcon(icons, R.string.standalone_drawer_gallery, 2, interactive);
        addHomeIcon(icons, R.string.standalone_drawer_camera, 3, interactive);
        addHomeIcon(icons, R.string.standalone_drawer_browser, 4, interactive);
        addHomeIcon(icons, R.string.standalone_drawer_files, 5, interactive);
        addHomeIcon(icons, R.string.standalone_drawer_weather, 6, interactive);
        addHomeIcon(icons, R.string.standalone_drawer_settings, 7, interactive);
        home.addView(new SpaceView(this), new LinearLayout.LayoutParams(1, 0, 1.0f));
        TextView indicator = label(R.string.standalone_home_page_indicator_preview, 18, Typeface.BOLD);
        indicator.setGravity(Gravity.CENTER);
        indicator.setTextColor(Color.argb(210, 255, 255, 255));
        home.addView(indicator, matchWidthWrapHeight());
        LinearLayout dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setPadding(dp(14), dp(12), dp(14), dp(12));
        dock.setBackground(rounded(previewGlassColor(), dp(30), dp(1), previewGlassBorderColor()));
        LinearLayout.LayoutParams dockParams = matchWidthWrapHeight();
        dockParams.topMargin = dp(12);
        home.addView(dock, dockParams);
        addHomeDockIcon(dock, R.string.standalone_dock_phone, 0, interactive);
        addHomeDockIcon(dock, R.string.standalone_dock_messages, 1, interactive);
        addHomeDockIcon(dock, R.string.standalone_drawer_browser, 2, interactive);
        addHomeDockIcon(dock, R.string.standalone_dock_camera, 3, interactive);
        if (interactive) addHomeDockIcon(dock, R.string.standalone_settings_search, 4, true);
        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.CENTER);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionsParams = matchWidthWrapHeight();
        actionsParams.topMargin = dp(12);
        home.addView(actions, actionsParams);
        if (interactive) {
            TextView drawerButton = actionButton(R.string.standalone_app_drawer_button);
            drawerButton.setOnClickListener(view -> showAppDrawerShell());
            actions.addView(drawerButton, actionButtonParams(false));
            TextView settingsButton = actionButton(R.string.standalone_settings_button);
            settingsButton.setOnClickListener(view -> showSettingsShell());
            actions.addView(settingsButton, actionButtonParams(true));
        }
        TextView footer = label(R.string.standalone_smoke_footer, 12, Typeface.NORMAL);
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(Color.argb(dimmed ? 130 : 190, 255, 255, 255));
        LinearLayout.LayoutParams footerParams = matchWidthWrapHeight();
        footerParams.topMargin = dp(10);
        home.addView(footer, footerParams);
    }

    private FrameLayout createWallpaperRoot() {
        FrameLayout root = new FrameLayout(this);
        root.setBackground(previewWallpaperBackground());
        return root;
    }

    private void addAllAppsGrid(LinearLayout root) {
        GridLayout apps = new GridLayout(this);
        apps.setColumnCount(4);
        apps.setPadding(dp(10), dp(10), dp(10), dp(10));
        apps.setBackground(rounded(surfaceColor(), dp(24), dp(1), borderColor()));
        LinearLayout.LayoutParams appsParams = matchWidthWrapHeight();
        appsParams.topMargin = dp(16);
        root.addView(apps, appsParams);
        addDrawerApp(apps, R.string.standalone_drawer_phone, 0, false);
        addDrawerApp(apps, R.string.standalone_drawer_messages, 1, false);
        addDrawerApp(apps, R.string.standalone_drawer_camera, 2, false);
        addDrawerApp(apps, R.string.standalone_drawer_gallery, 3, false);
        addDrawerApp(apps, R.string.standalone_drawer_browser, 4, false);
        addDrawerApp(apps, R.string.standalone_drawer_files, 5, false);
        addDrawerApp(apps, R.string.standalone_drawer_settings, 6, true);
        addDrawerApp(apps, R.string.standalone_drawer_clock, 7, false);
        addDrawerApp(apps, R.string.standalone_drawer_calendar, 0, false);
        addDrawerApp(apps, R.string.standalone_drawer_calculator, 1, false);
        addDrawerApp(apps, R.string.standalone_drawer_weather, 2, false);
        addDrawerApp(apps, R.string.standalone_drawer_play_store, 3, false);
    }

    private void addCategoryGrid(LinearLayout root) {
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(16);
        root.addView(grid, params);
        addCategoryCard(grid, R.string.standalone_category_communication, R.string.standalone_drawer_phone);
        addCategoryCard(grid, R.string.standalone_category_tools, R.string.standalone_drawer_files);
        addCategoryCard(grid, R.string.standalone_category_media, R.string.standalone_drawer_gallery);
        addCategoryCard(grid, R.string.standalone_category_games, R.string.standalone_category_games_apps);
        addCategoryCard(grid, R.string.standalone_category_system, R.string.standalone_drawer_settings);
        addCategoryCard(grid, R.string.standalone_category_finance, R.string.standalone_search_finance);
    }

    private void createHeader(LinearLayout root, int titleResId, int subtitleResId, Runnable backAction) {
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(header, matchWidthWrapHeight());
        TextView backButton = actionButton(R.string.standalone_settings_back);
        backButton.setOnClickListener(view -> backAction.run());
        header.addView(backButton, new LinearLayout.LayoutParams(dp(88), dp(44)));
        LinearLayout titleGroup = new LinearLayout(this);
        titleGroup.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleGroupParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
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

    private void createSettingsRow(LinearLayout section, int titleResId, int valueResId, boolean chevron, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(18), 0, dp(14), 0);
        row.setMinimumHeight(dp(54));
        row.setOnClickListener(listener);
        TextView title = label(titleResId, 15, Typeface.NORMAL);
        row.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        if (valueResId != 0) {
            TextView value = label(valueResId, 14, Typeface.NORMAL);
            value.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            value.setTextColor(mutedTextColor());
            row.addView(value, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        if (chevron) {
            TextView arrow = label(R.string.standalone_settings_chevron, 20, Typeface.NORMAL);
            arrow.setGravity(Gravity.CENTER);
            arrow.setTextColor(mutedTextColor());
            LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(dp(22), ViewGroup.LayoutParams.WRAP_CONTENT);
            arrowParams.leftMargin = dp(6);
            row.addView(arrow, arrowParams);
        }
        section.addView(row, matchWidth(dp(56)));
    }

    private void createPreviewLauncherCard(LinearLayout root) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(previewWallpaperBackground(dp(28)));
        TextView widget = label(R.string.standalone_home_weather, 12, Typeface.BOLD);
        widget.setTextColor(previewTextColor());
        card.addView(widget, matchWidthWrapHeight());
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
        addPreviewDockIcon(dock, 0); addPreviewDockIcon(dock, 1); addPreviewDockIcon(dock, 2); addPreviewDockIcon(dock, 3);
        TextView indicator = label(R.string.standalone_home_page_indicator_preview, 18, Typeface.BOLD);
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
        card.setPadding(dp(14), dp(70), dp(14), dp(14));
        card.setBackground(previewWallpaperBackground(dp(28)));
        LinearLayout dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setPadding(dp(12), dp(12), dp(12), dp(10));
        dock.setBackground(rounded(previewGlassColor(), dp(26), dp(1), previewGlassBorderColor()));
        card.addView(dock, matchWidthWrapHeight());
        int[] dockLabels = { R.string.standalone_dock_phone, R.string.standalone_dock_messages, R.string.standalone_dock_browser,
                R.string.standalone_dock_camera, R.string.standalone_drawer_files, R.string.standalone_drawer_clock };
        int dockAppCount = mDockAppsIndex + 4;
        for (int i = 0; i < dockAppCount; i++) addPreviewDockIcon(dock, dockLabels[i], i, mDockLabels);
        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(22);
        root.addView(card, params);
    }

    private void createSearchPreviewCard(LinearLayout root) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(50), dp(14), dp(14));
        card.setBackground(previewWallpaperBackground(dp(28)));
        LinearLayout results = new LinearLayout(this);
        results.setOrientation(LinearLayout.VERTICAL);
        results.setPadding(dp(12), dp(12), dp(12), dp(12));
        results.setBackground(rounded(previewGlassColor(), dp(22), dp(1), previewGlassBorderColor()));
        card.addView(results, matchWidthWrapHeight());
        TextView appsTitle = label(R.string.standalone_search_suggested_apps, 12, Typeface.BOLD);
        appsTitle.setTextColor(previewTextColor());
        results.addView(appsTitle, matchWidthWrapHeight());
        addSearchPreviewResult(results, R.string.standalone_search_ai_mode);
        addSearchPreviewResult(results, R.string.standalone_search_translate);
        TextView search = label(R.string.standalone_google_search_placeholder, 12, Typeface.NORMAL);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setTextColor(previewTextColor());
        search.setPadding(dp(14), 0, dp(14), 0);
        search.setBackground(rounded(searchPreviewColor(), dp(21), dp(1), previewGlassBorderColor()));
        LinearLayout.LayoutParams searchParams = matchWidth(dp(44));
        searchParams.topMargin = dp(12);
        card.addView(search, searchParams);
        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(22);
        root.addView(card, params);
    }

    private void createGoogleSearchPreview(LinearLayout root) {
        LinearLayout preview = new LinearLayout(this);
        preview.setGravity(Gravity.CENTER_VERTICAL);
        preview.setOrientation(LinearLayout.HORIZONTAL);
        preview.setPadding(dp(16), 0, dp(12), 0);
        preview.setBackground(rounded(searchPreviewColor(), dp(26), dp(1), previewGlassBorderColor()));
        TextView g = label(R.string.standalone_search_provider_initial, 18, Typeface.BOLD);
        g.setGravity(Gravity.CENTER);
        g.setTextColor(accentColor());
        preview.addView(g, size(dp(34), dp(54)));
        TextView hint = label(R.string.standalone_google_search_placeholder, 15, Typeface.NORMAL);
        hint.setTextColor(mutedTextColor());
        preview.addView(hint, new LinearLayout.LayoutParams(0, dp(54), 1f));
        TextView voice = label(R.string.standalone_search_voice_short, 13, Typeface.BOLD);
        voice.setGravity(Gravity.CENTER);
        preview.addView(voice, size(dp(42), dp(54)));
        TextView lens = label(R.string.standalone_search_lens_short, 13, Typeface.BOLD);
        lens.setGravity(Gravity.CENTER);
        preview.addView(lens, size(dp(42), dp(54)));
        LinearLayout.LayoutParams params = matchWidth(dp(56));
        params.topMargin = dp(24);
        root.addView(preview, params);
    }

    private void createSliderRow(LinearLayout section, int titleResId, int value, IntSetter setter) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(18), dp(10), dp(18), dp(10));
        TextView title = label(getString(titleResId) + "  " + value + "%", 14, Typeface.NORMAL);
        row.addView(title, matchWidthWrapHeight());
        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(100);
        seekBar.setProgress(value);
        seekBar.setProgressDrawable(createSeekBarDrawable());
        seekBar.setThumb(rounded(accentColor(), dp(8), dp(2), surfaceColor()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { setter.set(progress); title.setText(getString(titleResId) + "  " + progress + "%"); }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { showCustomizeSearchScreen(); }
        });
        row.addView(seekBar, matchWidth(dp(42)));
        section.addView(row, matchWidthWrapHeight());
    }

    private LinearLayout searchOverlayCard(int titleResId) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(rounded(mDarkPreview ? Color.argb(218, 32, 38, 43) : Color.argb(230, 250, 253, 251), dp(24), dp(1), previewGlassBorderColor()));
        TextView title = label(titleResId, 13, Typeface.BOLD);
        title.setTextColor(mDarkPreview ? textColor() : Color.rgb(30, 43, 48));
        card.addView(title, matchWidthWrapHeight());
        return card;
    }

    private LinearLayout.LayoutParams bottomCardParams() { LinearLayout.LayoutParams params = matchWidthWrapHeight(); params.topMargin = dp(10); return params; }

    private void addFakeKeyboard(LinearLayout root) {
        LinearLayout keyboard = new LinearLayout(this);
        keyboard.setOrientation(LinearLayout.VERTICAL);
        keyboard.setPadding(dp(6), dp(8), dp(6), dp(4));
        keyboard.setBackground(rounded(Color.argb(mDarkPreview ? 232 : 238, 232, 236, 239), dp(20), 0, Color.TRANSPARENT));
        String[] rows = { "Q W E R T Y U I O P", "A S D F G H J K L", "Z X C V B N M" };
        for (String rowText : rows) {
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams rowParams = matchWidth(dp(28));
            rowParams.topMargin = dp(4);
            keyboard.addView(row, rowParams);
            for (String key : rowText.split(" ")) {
                TextView keyView = label(key, 11, Typeface.BOLD);
                keyView.setGravity(Gravity.CENTER);
                keyView.setTextColor(Color.rgb(43, 48, 52));
                keyView.setBackground(rounded(Color.WHITE, dp(6), 0, Color.TRANSPARENT));
                LinearLayout.LayoutParams keyParams = new LinearLayout.LayoutParams(0, dp(24), 1f);
                keyParams.leftMargin = dp(2);
                keyParams.rightMargin = dp(2);
                row.addView(keyView, keyParams);
            }
        }
        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(8);
        root.addView(keyboard, params);
    }

    private void addHomeIcon(GridLayout grid, int labelResId, int colorIndex, boolean interactive) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);
        if (interactive) item.setOnClickListener(view -> { if (labelResId == R.string.standalone_drawer_settings) showSettingsShell(); else showPreviewToast(labelResId); });
        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(16), 0, Color.TRANSPARENT));
        item.addView(icon, size(dp(54), dp(54)));
        TextView label = label(labelResId, 12, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(previewTextColor());
        LinearLayout.LayoutParams labelParams = matchWidthWrapHeight();
        labelParams.topMargin = dp(7);
        item.addView(label, labelParams);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dp(94);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(4), dp(4), dp(4), dp(8));
        grid.addView(item, params);
    }

    private void addHomeDockIcon(LinearLayout dock, int labelResId, int colorIndex, boolean interactive) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);
        if (interactive) item.setOnClickListener(view -> { if (labelResId == R.string.standalone_settings_search) showSearchShell(SCREEN_HOME); else showPreviewToast(labelResId); });
        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(15), 0, Color.TRANSPARENT));
        item.addView(icon, size(dp(46), dp(46)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.leftMargin = dp(4);
        params.rightMargin = dp(4);
        dock.addView(item, params);
    }

    private void addDrawerApp(GridLayout apps, int labelResId, int colorIndex, boolean opensSettings) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(dp(8), dp(12), dp(8), dp(10));
        item.setBackground(rounded(tileBackgroundColor(), dp(18), 0, Color.TRANSPARENT));
        item.setOnClickListener(view -> { if (opensSettings) showSettingsShell(); else showPreviewToast(labelResId); });
        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(16), 0, Color.TRANSPARENT));
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

    private void addCategoryCard(GridLayout grid, int titleResId, int subtitleResId) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(14));
        card.setBackground(rounded(surfaceColor(), dp(22), dp(1), borderColor()));
        card.setOnClickListener(view -> showPreviewToast(titleResId));
        TextView title = label(titleResId, 15, Typeface.BOLD);
        card.addView(title, matchWidthWrapHeight());
        TextView subtitle = label(subtitleResId, 12, Typeface.NORMAL);
        subtitle.setTextColor(mutedTextColor());
        LinearLayout.LayoutParams subtitleParams = matchWidthWrapHeight();
        subtitleParams.topMargin = dp(8);
        card.addView(subtitle, subtitleParams);
        LinearLayout icons = new LinearLayout(this);
        icons.setGravity(Gravity.LEFT);
        LinearLayout.LayoutParams iconsParams = matchWidthWrapHeight();
        iconsParams.topMargin = dp(14);
        card.addView(icons, iconsParams);
        addMiniDot(icons, 0); addMiniDot(icons, 1); addMiniDot(icons, 2);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dp(132);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(6), dp(6), dp(6), dp(6));
        grid.addView(card, params);
    }

    private void addOverlayApp(LinearLayout row, int labelResId, int colorIndex) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setOnClickListener(view -> showPreviewToast(labelResId));
        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(14), 0, Color.TRANSPARENT));
        item.addView(icon, size(dp(44), dp(44)));
        TextView label = label(labelResId, 11, Typeface.NORMAL);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(mDarkPreview ? textColor() : Color.rgb(30, 43, 48));
        LinearLayout.LayoutParams labelParams = matchWidthWrapHeight();
        labelParams.topMargin = dp(6);
        item.addView(label, labelParams);
        row.addView(item, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
    }

    private void addQuickAction(GridLayout grid, int labelResId) {
        TextView item = label(labelResId, 12, Typeface.NORMAL);
        item.setGravity(Gravity.CENTER);
        item.setSingleLine(false);
        item.setTextColor(mDarkPreview ? textColor() : Color.rgb(30, 43, 48));
        item.setPadding(dp(6), 0, dp(6), 0);
        item.setBackground(rounded(Color.argb(150, 255, 255, 255), dp(14), dp(1), previewGlassBorderColor()));
        item.setOnClickListener(view -> showPreviewToast(labelResId));
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dp(42);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(3), dp(4), dp(3), dp(4));
        grid.addView(item, params);
    }

    private void addSearchPreviewResult(LinearLayout results, int labelResId) {
        TextView item = label(labelResId, 12, Typeface.NORMAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setTextColor(previewTextColor());
        item.setPadding(dp(12), 0, dp(12), 0);
        item.setBackground(rounded(previewGlassColor(), dp(14), dp(1), previewGlassBorderColor()));
        LinearLayout.LayoutParams params = matchWidth(dp(38));
        params.topMargin = dp(10);
        results.addView(item, params);
    }

    private void showSelectorScreen(int titleResId, int[] options, int selectedIndex, SelectionHandler selectionHandler, Runnable backAction) {
        mCurrentScreen = SCREEN_SELECTOR;
        mSelectorBackAction = backAction;
        LinearLayout root = createScreenContainer();
        createHeader(root, titleResId, R.string.standalone_settings_subtitle, backAction);
        LinearLayout section = createGroupedSection(root, 0);
        for (int i = 0; i < options.length; i++) {
            final int index = i;
            createSelectableRow(section, options[i], index == selectedIndex, view -> selectionHandler.onSelected(index));
        }
        createStandaloneNotice(root);
    }

    private void createSelectableRow(LinearLayout section, int titleResId, boolean selected, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(18), 0, dp(18), 0);
        row.setMinimumHeight(dp(54));
        row.setOnClickListener(listener);
        TextView title = label(titleResId, 15, Typeface.NORMAL);
        row.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView mark = label(selected ? R.string.standalone_settings_selected : R.string.standalone_settings_empty, 14, Typeface.BOLD);
        mark.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mark.setTextColor(accentColor());
        row.addView(mark, new LinearLayout.LayoutParams(dp(34), ViewGroup.LayoutParams.WRAP_CONTENT));
        section.addView(row, matchWidth(dp(56)));
    }

    private void addAboutRow(LinearLayout card, int titleResId, int valueResId) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = matchWidthWrapHeight();
        rowParams.topMargin = dp(12);
        card.addView(row, rowParams);
        TextView title = label(titleResId, 14, Typeface.NORMAL);
        title.setTextColor(mutedTextColor());
        row.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView value = label(valueResId, 14, Typeface.BOLD);
        value.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        row.addView(value, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
        top.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView value = label(mGlassDepth + "%", 14, Typeface.BOLD);
        value.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        value.setTextColor(accentColor());
        top.addView(value, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { mGlassDepth = progress; value.setText(progress + "%"); }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { Toast.makeText(StandaloneSmokeActivity.this, R.string.standalone_glass_depth_preview_only, Toast.LENGTH_SHORT).show(); showGlassDepthScreen(); }
        });
        LinearLayout.LayoutParams params = matchWidthWrapHeight();
        params.topMargin = dp(18);
        root.addView(card, params);
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

    private TextView pill(int labelResId, float size) {
        TextView view = label(labelResId, size, Typeface.NORMAL);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setTextColor(mutedTextColor());
        view.setPadding(dp(18), 0, dp(18), 0);
        view.setBackground(rounded(surfaceColor(), dp(24), dp(1), borderColor()));
        return view;
    }

    private TextView segmentButton(int labelResId, boolean selected) {
        TextView button = label(labelResId, 14, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(selected ? selectedThemeTextColor() : textColor());
        button.setPadding(dp(14), 0, dp(14), 0);
        button.setBackground(rounded(selected ? accentColor() : surfaceColor(), dp(18), dp(1), selected ? accentColor() : borderColor()));
        return button;
    }

    private void addPreviewIcon(GridLayout icons, int labelResId, int colorIndex) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);
        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(14), 0, Color.TRANSPARENT));
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
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(13), 0, Color.TRANSPARENT));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(36), 1f);
        params.leftMargin = dp(5);
        params.rightMargin = dp(5);
        dock.addView(icon, params);
    }

    private void addPreviewDockIcon(LinearLayout dock, int labelResId, int colorIndex, boolean showLabel) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        item.setOrientation(LinearLayout.VERTICAL);
        View icon = new View(this);
        icon.setBackground(rounded(previewIconColor(colorIndex), dp(14), 0, Color.TRANSPARENT));
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.leftMargin = dp(4);
        params.rightMargin = dp(4);
        dock.addView(item, params);
    }

    private void addMiniDot(LinearLayout row, int colorIndex) {
        View dot = new View(this);
        dot.setBackground(rounded(previewIconColor(colorIndex), dp(9), 0, Color.TRANSPARENT));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(24), dp(24));
        params.rightMargin = dp(6);
        row.addView(dot, params);
    }

    private void addSeekLabel(LinearLayout labels, int labelResId, int gravity) {
        TextView label = label(labelResId, 12, Typeface.NORMAL);
        label.setGravity(gravity | Gravity.CENTER_VERTICAL);
        label.setTextColor(mutedTextColor());
        labels.addView(label, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
    }

    private void createPresetRow(LinearLayout section, int titleResId, int value) {
        boolean selected = isPresetSelected(titleResId, value);
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(18), 0, dp(18), 0);
        row.setMinimumHeight(dp(54));
        row.setOnClickListener(view -> { mGlassDepth = value; Toast.makeText(this, R.string.standalone_glass_depth_preview_only, Toast.LENGTH_SHORT).show(); showGlassDepthScreen(); });
        TextView title = label(titleResId, 15, Typeface.NORMAL);
        row.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView mark = label(selected ? R.string.standalone_settings_selected : R.string.standalone_settings_empty, 14, Typeface.BOLD);
        mark.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mark.setTextColor(accentColor());
        row.addView(mark, new LinearLayout.LayoutParams(dp(34), ViewGroup.LayoutParams.WRAP_CONTENT));
        section.addView(row, matchWidth(dp(56)));
    }

    private boolean isPresetSelected(int titleResId, int value) { return titleResId == R.string.standalone_glass_depth_custom_preset ? nearestPreset(mGlassDepth) == 0 : nearestPreset(mGlassDepth) == value; }
    private int nearestPreset(int value) { if (Math.abs(value - GLASS_DEPTH_LIGHT) <= 4) return GLASS_DEPTH_LIGHT; if (Math.abs(value - GLASS_DEPTH_MEDIUM) <= 4) return GLASS_DEPTH_MEDIUM; if (Math.abs(value - GLASS_DEPTH_DEEP) <= 4) return GLASS_DEPTH_DEEP; return 0; }

    private LayerDrawable createSeekBarDrawable() {
        GradientDrawable inactive = rounded(seekInactiveColor(), dp(5), 0, Color.TRANSPARENT);
        GradientDrawable active = rounded(accentColor(), dp(5), 0, Color.TRANSPARENT);
        ClipDrawable progress = new ClipDrawable(active, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        LayerDrawable drawable = new LayerDrawable(new android.graphics.drawable.Drawable[] { inactive, progress });
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
        button.setBackground(rounded(surfaceColor(), dp(18), dp(1), borderColor()));
        return button;
    }

    private LinearLayout.LayoutParams actionButtonParams(boolean hasLeftMargin) { LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(48), 1f); if (hasLeftMargin) params.leftMargin = dp(12); return params; }
    private LinearLayout.LayoutParams matchWidthWrapHeight() { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); }
    private LinearLayout.LayoutParams matchWidth(int height) { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height); }
    private LinearLayout.LayoutParams size(int width, int height) { return new LinearLayout.LayoutParams(width, height); }
    private FrameLayout.LayoutParams matchParent() { return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); }

    private GradientDrawable rounded(int color, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    private void applySystemBarColors(boolean wallpaper) {
        int color = wallpaper ? (mDarkPreview ? Color.rgb(22, 26, 30) : Color.rgb(73, 122, 111)) : backgroundColor();
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        if (mDarkPreview || wallpaper) { flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; }
        else { flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR; }
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private int backgroundColor() { return mDarkPreview ? Color.rgb(18, 22, 25) : getColor(R.color.smoke_background); }
    private int surfaceColor() { return mDarkPreview ? Color.rgb(31, 36, 41) : getColor(R.color.smoke_surface); }
    private int tileBackgroundColor() { return mDarkPreview ? Color.rgb(43, 50, 56) : getColor(R.color.smoke_tile_background); }
    private int borderColor() { return mDarkPreview ? Color.rgb(61, 69, 77) : getColor(R.color.smoke_border); }
    private int textColor() { return mDarkPreview ? Color.rgb(238, 242, 245) : getColor(R.color.smoke_text); }
    private int mutedTextColor() { return mDarkPreview ? Color.rgb(175, 185, 194) : getColor(R.color.smoke_text_muted); }
    private int accentColor() { return mDarkPreview ? Color.rgb(111, 211, 190) : getColor(R.color.smoke_accent); }
    private int accentSoftColor() { return mDarkPreview ? Color.rgb(38, 86, 76) : getColor(R.color.smoke_accent_soft); }
    private int previewTextColor() { return Color.rgb(255, 255, 255); }
    private int previewGlassColor() { int alpha = Math.max(150, 246 - mGlassDepth); return mDarkPreview ? Color.argb(218, 38 + mGlassDepth / 10, 46 + mGlassDepth / 12, 52 + mGlassDepth / 14) : Color.argb(alpha, 248, 253, 250); }
    private int searchPreviewColor() { int alpha = 130 + (mSearchTransparency * 110 / 100); if (mSearchThemeIndex == 2 || (mSearchThemeIndex == 0 && mDarkPreview)) return Color.argb(alpha, 36, 42, 47); return Color.argb(alpha, 255, 255, 255); }
    private int previewGlassBorderColor() { return mDarkPreview ? Color.rgb(82, 96, 103) : Color.rgb(224, 239, 235); }
    private int previewIconColor(int index) { int[][] light = { {72,153,133}, {76,132,170}, {92,105,170}, {181,128,70}, {93,149,118}, {164,111,143}, {88,137,177}, {205,157,74} }; int[][] dark = { {62,142,124}, {58,112,148}, {82,90,150}, {158,112,66}, {72,124,98}, {142,88,120}, {70,112,150}, {170,132,64} }; int[] color = (mDarkPreview ? dark : light)[index % light.length]; return Color.rgb(color[0], color[1], color[2]); }
    private int seekInactiveColor() { return mDarkPreview ? Color.rgb(67, 76, 83) : Color.rgb(218, 226, 232); }
    private GradientDrawable previewWallpaperBackground() { return previewWallpaperBackground(0); }
    private GradientDrawable previewWallpaperBackground(int radius) { int[] colors = mDarkPreview ? new int[] { Color.rgb(24, 31, 36), Color.rgb(38, 62, 58), Color.rgb(22, 26, 30) } : new int[] { Color.rgb(109, 152, 166), Color.rgb(67, 125, 111), Color.rgb(236, 242, 238) }; GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors); drawable.setCornerRadius(radius); if (radius > 0) drawable.setStroke(dp(1), borderColor()); return drawable; }
    private int selectedThemeTextColor() { return mDarkPreview ? Color.rgb(7, 31, 27) : Color.WHITE; }

    private void setThemePreview(boolean darkPreview) {
        if (mDarkPreview == darkPreview) return;
        mDarkPreview = darkPreview;
        if (mCurrentScreen == SCREEN_APPEARANCE) showAppearanceScreen();
        else if (mCurrentScreen == SCREEN_GLASS_DEPTH) showGlassDepthScreen();
        else if (mCurrentScreen == SCREEN_HOME_SETTINGS) showHomeSettingsScreen();
        else if (mCurrentScreen == SCREEN_DOCK_SETTINGS) showDockSettingsScreen();
        else if (mCurrentScreen == SCREEN_SEARCH_SETTINGS) showSearchSettingsScreen();
        else if (mCurrentScreen == SCREEN_DRAWER_SETTINGS) showDrawerSettingsScreen();
        else if (mCurrentScreen == SCREEN_ABOUT_SETTINGS) showAboutSettingsScreen();
        else if (mCurrentScreen == SCREEN_SELECTOR) mSelectorBackAction.run();
        else showSettingsScreen();
    }

    private void showPreviewToast(int labelResId) { Toast.makeText(this, getString(R.string.standalone_preview_toast, getString(labelResId)), Toast.LENGTH_SHORT).show(); }
    private void showSearchReturnShell() { if (mSearchReturnScreen == SCREEN_DRAWER) { showAppDrawerShell(); return; } if (mSearchReturnScreen == SCREEN_SETTINGS) { showSettingsScreen(); return; } showHomeShell(); }
    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    private static final class SpaceView extends View { SpaceView(Context context) { super(context); } }
}
