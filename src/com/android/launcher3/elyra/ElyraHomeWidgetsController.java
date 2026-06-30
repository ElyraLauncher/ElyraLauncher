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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.views.BaseDragLayer;

/**
 * Master controller for Elyra home-screen widgets.
 *
 * <p><b>Attachment architecture:</b>
 * <ul>
 *   <li><b>Smart widget</b> (unified greeting + weather, {@code elyra_smart_region.xml}) —
 *       added directly to {@link CellLayout} 0 (workspace page 0).  It lives inside the
 *       workspace view hierarchy, so it:
 *       <ul>
 *         <li>scrolls off-screen when the user swipes to page 1+;</li>
 *         <li>fades with {@code CellLayout.setAlpha()} during gesture-based All Apps / Overview
 *             transitions, covering the full gesture duration (not just post-commit);</li>
 *         <li>requires no explicit {@link StateManager.StateListener} for hiding.</li>
 *       </ul>
 *   </li>
 *   <li><b>Search trigger pill</b> — added to {@code DragLayer} above the hotseat
 *       (page-independent).  Hidden via {@link StateManager.StateListener} in non-NORMAL
 *       states and fades out while the user swipes between workspace pages.</li>
 * </ul>
 */
public final class ElyraHomeWidgetsController
        implements StateManager.StateListener<LauncherState> {

    private static final int SHOW_DURATION_MS  = 180;
    private static final int TRIGGER_SETTLE_MS = 300;

    private final Launcher mLauncher;
    private final Handler  mHandler = new Handler(Looper.getMainLooper());

    private View    mSearchTriggerView;
    private boolean mIsNormal = true;

    private final Runnable mTriggerFadeInRunnable = () -> {
        if (mSearchTriggerView != null && mIsNormal) {
            mSearchTriggerView.animate().alpha(1f).setDuration(150).start();
        }
    };

    // ── Entry point ───────────────────────────────────────────────────────────

    /** Called from {@code Launcher.setupViews()}. */
    public static void attachTo(Launcher launcher) {
        if (!ElyraFeatureFlags.SMART_REGION
                && !ElyraFeatureFlags.SEARCH_TRIGGER
                && !ElyraFeatureFlags.WEATHER_TIME_CARD) {
            return;
        }
        new ElyraHomeWidgetsController(launcher);
    }

    private ElyraHomeWidgetsController(Launcher launcher) {
        mLauncher = launcher;

        ElyraSmartSpaceController smartSpace = ElyraSmartSpaceController.create(launcher);
        View smartWidgetView = smartSpace != null ? smartSpace.getSmartRegionView()   : null;
        mSearchTriggerView   = smartSpace != null ? smartSpace.getSearchTriggerView() : null;

        if (smartWidgetView != null) {
            attachToWorkspacePage(smartWidgetView);
        }

        if (mSearchTriggerView != null) {
            attachSearchTriggerToDragLayer();
        }

        launcher.getStateManager().addStateListener(this);

        launcher.getWorkspace().setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        onWorkspaceScrolled(scrollX, oldScrollX));
    }

    // ── Workspace attachment (smart widget) ───────────────────────────────────

    private void attachToWorkspacePage(View smartWidget) {
        Workspace workspace = mLauncher.getWorkspace();
        workspace.post(() -> {
            if (workspace.getChildCount() > 0) {
                doAttachToPage0(workspace, smartWidget);
            } else {
                workspace.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override public void onGlobalLayout() {
                                if (workspace.getChildCount() > 0) {
                                    workspace.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                    doAttachToPage0(workspace, smartWidget);
                                }
                            }
                        });
            }
        });
    }

    private void doAttachToPage0(Workspace workspace, View smartWidget) {
        if (workspace.getChildCount() == 0) return;
        View page = workspace.getChildAt(0);
        if (!(page instanceof CellLayout)) return;
        CellLayout page0 = (CellLayout) page;

        // The smart widget is MATCH_PARENT width, WRAP_CONTENT height.
        // CellLayout.onLayout() only positions mShortcutsAndWidgets; our widget is
        // measured and laid out manually via OnLayoutChangeListener.
        page0.addView(smartWidget);

        page0.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) ->
                measureAndLayout(smartWidget, r - l));

        page0.post(() -> measureAndLayout(smartWidget, page0.getWidth()));
    }

    private void measureAndLayout(View v, int pageWidth) {
        if (pageWidth <= 0) return;
        v.measure(
                View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, pageWidth, v.getMeasuredHeight());
    }

    // ── DragLayer attachment (search trigger) ─────────────────────────────────

    private void attachSearchTriggerToDragLayer() {
        DeviceProfile dp = mLauncher.getDeviceProfile();
        // workspacePadding.bottom is nav-bar-corrected; hotseatBarSizePx alone would
        // double-count the nav-bar inset consumed by LauncherRootView.fitsSystemWindows.
        int bottomMargin = dp.workspacePadding.bottom > 0
                ? dp.workspacePadding.bottom
                : (dp.hotseatBarSizePx - dp.getInsets().bottom);

        BaseDragLayer.LayoutParams lp = new BaseDragLayer.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity      = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = bottomMargin;
        lp.leftMargin   = dpToPx(20);
        lp.rightMargin  = dpToPx(20);

        mLauncher.getDragLayer().addView(mSearchTriggerView, lp);
    }

    // ── StateManager.StateListener — search trigger only ─────────────────────

    @Override
    public void onStateTransitionStart(LauncherState toState) {
        if (toState != LauncherState.NORMAL) {
            mIsNormal = false;
            hideTrigger();
        }
    }

    @Override
    public void onStateTransitionComplete(LauncherState finalState) {
        if (finalState == LauncherState.NORMAL) {
            mIsNormal = true;
            showTrigger();
        }
    }

    private void hideTrigger() {
        if (mSearchTriggerView == null) return;
        mHandler.removeCallbacks(mTriggerFadeInRunnable);
        mSearchTriggerView.animate().cancel();
        mSearchTriggerView.setVisibility(View.GONE);
    }

    private void showTrigger() {
        if (mSearchTriggerView == null) return;
        mSearchTriggerView.animate().cancel();
        mSearchTriggerView.setAlpha(0f);
        mSearchTriggerView.setVisibility(View.VISIBLE);
        mSearchTriggerView.animate().alpha(1f).setDuration(SHOW_DURATION_MS).start();
    }

    // ── Workspace scroll — search trigger fade only ───────────────────────────

    private void onWorkspaceScrolled(int scrollX, int oldScrollX) {
        if (mSearchTriggerView != null && mIsNormal && scrollX != oldScrollX) {
            mSearchTriggerView.animate().cancel();
            mSearchTriggerView.setAlpha(0f);
            mHandler.removeCallbacks(mTriggerFadeInRunnable);
            mHandler.postDelayed(mTriggerFadeInRunnable, TRIGGER_SETTLE_MS);
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private int dpToPx(int dp) {
        return Math.round(dp * mLauncher.getResources().getDisplayMetrics().density);
    }
}
