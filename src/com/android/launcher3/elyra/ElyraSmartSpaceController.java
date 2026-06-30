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

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.views.BaseDragLayer;

/**
 * Master controller for all Elyra home screen overlays.
 *
 * <p>Adds two views to {@link com.android.launcher3.dragndrop.DragLayer}:
 * <ol>
 *   <li>Smart region card — upper-left, shows greeting / charging / notification takeover.</li>
 *   <li>Search trigger pill — above the hotseat, opens the search surface on tap.</li>
 * </ol>
 *
 * <p>Hook: {@code ElyraSmartSpaceController.attachTo(launcher)} at the end of
 * {@code Launcher.setupViews()}. All logic is guarded by compile-time
 * {@link ElyraFeatureFlags} so disabling a flag removes the feature entirely.</p>
 */
public final class ElyraSmartSpaceController {

    private static final long SUBTITLE_ROTATE_MS = 5_000L;

    private static final int[] SUBTITLES = {
            R.string.elyra_subtitle_1,
            R.string.elyra_subtitle_2,
            R.string.elyra_subtitle_3,
            R.string.elyra_subtitle_4,
            R.string.elyra_subtitle_5,
    };

    private final Launcher mLauncher;
    private View mSmartRegionView;
    private View mSearchTriggerView;

    private TextView mSubtitleView;
    private int mSubtitleIndex = 0;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Runnable mSubtitleRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSubtitleView == null) return;
            mSubtitleIndex = (mSubtitleIndex + 1) % SUBTITLES.length;
            mSubtitleView.setText(SUBTITLES[mSubtitleIndex]);
            mHandler.postDelayed(this, SUBTITLE_ROTATE_MS);
        }
    };

    // Restores search trigger alpha after workspace scroll settles.
    private final Runnable mTriggerFadeInRunnable = () -> {
        if (mSearchTriggerView != null) {
            mSearchTriggerView.animate().alpha(1f).setDuration(200).start();
        }
    };

    /**
     * Called from {@link ElyraHomeWidgetsController} to create the smart region and
     * search trigger. Returns {@code null} if both flags are disabled.
     */
    static ElyraSmartSpaceController attachTo(Launcher launcher) {
        if (!ElyraFeatureFlags.SMART_REGION && !ElyraFeatureFlags.SEARCH_TRIGGER) return null;
        return new ElyraSmartSpaceController(launcher);
    }

    /** Returns the smart region card view added to DragLayer, or {@code null}. */
    View getSmartRegionView()  { return mSmartRegionView; }

    /** Returns the lower search trigger view added to DragLayer, or {@code null}. */
    View getSearchTriggerView() { return mSearchTriggerView; }

    private ElyraSmartSpaceController(Launcher launcher) {
        mLauncher = launcher;
        if (ElyraFeatureFlags.SMART_REGION)    setupSmartRegion();
        if (ElyraFeatureFlags.SEARCH_TRIGGER)  setupSearchTrigger();
    }

    // ── Smart region ──────────────────────────────────────────────────────────

    private void setupSmartRegion() {
        mSmartRegionView = LayoutInflater.from(mLauncher)
                .inflate(R.layout.elyra_smart_region, null, false);

        int statusBarH = getStatusBarHeight();
        // Must use BaseDragLayer.LayoutParams — FrameLayout.LayoutParams loses gravity
        // when BaseDragLayer.generateLayoutParams() wraps it via the (ViewGroup.LayoutParams)
        // copy constructor, which does not copy the gravity field.
        BaseDragLayer.LayoutParams lp = new BaseDragLayer.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.topMargin = statusBarH + dpToPx(8);

        mLauncher.getDragLayer().addView(mSmartRegionView, lp);

        // Wire interactive elements as non-interactive on the container so workspace
        // icon touches fall through.
        mSmartRegionView.setClickable(false);
        mSmartRegionView.setFocusable(false);

        initGreeting();
        startSubtitleRotation();

        if (ElyraFeatureFlags.CHARGING_TAKEOVER) {
            ElyraChargingTakeoverController.attach(mLauncher, mSmartRegionView);
        }
        if (ElyraFeatureFlags.NOTIFICATION_TAKEOVER) {
            ElyraNotificationTakeoverController.attach(mSmartRegionView);
        }
    }

    private void initGreeting() {
        TextView greetingView = mSmartRegionView.findViewById(R.id.elyra_smart_greeting);
        if (greetingView == null) return;
        greetingView.setText(currentGreeting());
    }

    private int currentGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 11)  return R.string.elyra_greeting_morning;
        if (hour >= 11 && hour < 15) return R.string.elyra_greeting_noon;
        if (hour >= 15 && hour < 19) return R.string.elyra_greeting_afternoon;
        return R.string.elyra_greeting_night;
    }

    private void startSubtitleRotation() {
        mSubtitleView = mSmartRegionView.findViewById(R.id.elyra_smart_subtitle);
        if (mSubtitleView == null) return;
        mHandler.postDelayed(mSubtitleRunnable, SUBTITLE_ROTATE_MS);
    }

    // ── Search trigger ────────────────────────────────────────────────────────

    private void setupSearchTrigger() {
        mSearchTriggerView = LayoutInflater.from(mLauncher)
                .inflate(R.layout.elyra_search_trigger, null, false);

        DeviceProfile dp = mLauncher.getDeviceProfile();
        // workspacePadding.bottom = distance from DragLayer bottom to workspace content end
        // (already accounts for nav-bar inset consumed by LauncherRootView.fitsSystemWindows).
        // Using hotseatBarSizePx directly would double-count the nav-bar inset and push the
        // trigger above the DragLayer top (visible because clipChildren=false).
        int bottomMargin = dp.workspacePadding.bottom > 0
                ? dp.workspacePadding.bottom
                : (dp.hotseatBarSizePx - dp.getInsets().bottom);

        // BaseDragLayer.LayoutParams required — see setupSmartRegion() comment.
        BaseDragLayer.LayoutParams lp = new BaseDragLayer.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = bottomMargin;
        lp.leftMargin  = dpToPx(20);
        lp.rightMargin = dpToPx(20);

        mLauncher.getDragLayer().addView(mSearchTriggerView, lp);

        mSearchTriggerView.setOnClickListener(v -> openSearch());

        // Fade out while the workspace is being swiped, fade back in when it settles.
        mLauncher.getWorkspace().setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollX != oldScrollX) {
                        mSearchTriggerView.animate().alpha(0f).setDuration(80).start();
                        mHandler.removeCallbacks(mTriggerFadeInRunnable);
                        mHandler.postDelayed(mTriggerFadeInRunnable, 350);
                    }
                });
    }

    private void openSearch() {
        mLauncher.getStateManager().goToState(LauncherState.ALL_APPS, true);
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private int getStatusBarHeight() {
        int id = mLauncher.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        return id > 0 ? mLauncher.getResources().getDimensionPixelSize(id) : 0;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * mLauncher.getResources().getDisplayMetrics().density);
    }
}
