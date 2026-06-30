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
import android.widget.FrameLayout;

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
 *   <li><b>Smart region (greeting) + weather card</b> — attached to a {@link FrameLayout}
 *       container added as a child of {@link CellLayout} 0 (workspace page 0).  Because they
 *       live inside the workspace view hierarchy they:
 *       <ul>
 *         <li>scroll off-screen naturally when the user swipes to another page;</li>
 *         <li>fade with workspace alpha during gesture-based All Apps / Overview transitions
 *             (WorkspaceStateTransitionAnimation calls {@code CellLayout.setAlpha()} which
 *             propagates to all children), including the in-progress swipe before the
 *             state commit fires;</li>
 *         <li>require no explicit {@link StateManager.StateListener} for hiding.</li>
 *       </ul>
 *   </li>
 *   <li><b>Search trigger pill</b> — still in {@code DragLayer} above the hotseat (dock-area,
 *       page-independent).  Hidden via {@link StateManager.StateListener} so it disappears
 *       in ALL_APPS, OVERVIEW, and SPRING_LOADED.</li>
 * </ul>
 *
 * <p>Hook: {@code ElyraHomeWidgetsController.attachTo(launcher)} at the end of
 * {@code Launcher.setupViews()}.
 */
public final class ElyraHomeWidgetsController
        implements StateManager.StateListener<LauncherState> {

    private static final int SHOW_DURATION_MS  = 180;
    private static final int TRIGGER_SETTLE_MS = 300;

    private final Launcher mLauncher;
    private final Handler  mHandler = new Handler(Looper.getMainLooper());

    /** Only the search trigger needs explicit state gating — workspace views gate via CellLayout alpha. */
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

        // -- Smart region + search trigger --
        ElyraSmartSpaceController smartSpace = ElyraSmartSpaceController.create(launcher);
        View smartRegionView  = smartSpace != null ? smartSpace.getSmartRegionView()   : null;
        mSearchTriggerView    = smartSpace != null ? smartSpace.getSearchTriggerView() : null;

        // -- Weather / time card --
        View weatherCardView = null;
        if (ElyraFeatureFlags.WEATHER_TIME_CARD) {
            ElyraWeatherTimeController weather = ElyraWeatherTimeController.attach(launcher);
            weatherCardView = weather.getCardView();
        }

        // Workspace-bound: smart region + weather → CellLayout 0.
        // Deferred via post() because workspace pages may not yet exist at setupViews() time.
        if (smartRegionView != null || weatherCardView != null) {
            final View sr = smartRegionView;
            final View wc = weatherCardView;
            attachToWorkspacePage(sr, wc);
        }

        // Dock-area: search trigger → DragLayer (page-independent, state-gated).
        if (mSearchTriggerView != null) {
            attachSearchTriggerToDragLayer();
        }

        // StateManager listener — for search trigger only.
        launcher.getStateManager().addStateListener(this);

        // Single workspace scroll listener — search trigger fade only.
        launcher.getWorkspace().setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        onWorkspaceScrolled(scrollX, oldScrollX));
    }

    // ── Workspace attachment (smart region + weather) ─────────────────────────

    private void attachToWorkspacePage(View smartRegion, View weatherCard) {
        Workspace workspace = mLauncher.getWorkspace();
        workspace.post(() -> {
            if (workspace.getChildCount() > 0) {
                doAttachToPage0(workspace, smartRegion, weatherCard);
            } else {
                workspace.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override public void onGlobalLayout() {
                                if (workspace.getChildCount() > 0) {
                                    workspace.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                    doAttachToPage0(workspace, smartRegion, weatherCard);
                                }
                            }
                        });
            }
        });
    }

    private void doAttachToPage0(Workspace workspace, View smartRegion, View weatherCard) {
        if (workspace.getChildCount() == 0) return;
        View page = workspace.getChildAt(0);
        if (!(page instanceof CellLayout)) return;
        CellLayout page0 = (CellLayout) page;

        // Container spans the full width of page 0 and sits at the very top (y=0).
        // CellLayout.onLayout() only positions mShortcutsAndWidgets; our container
        // is positioned manually below via OnLayoutChangeListener.
        FrameLayout container = new FrameLayout(mLauncher);

        if (smartRegion != null) {
            FrameLayout.LayoutParams srLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP | Gravity.START);
            container.addView(smartRegion, srLp);
        }

        if (weatherCard != null) {
            FrameLayout.LayoutParams wcLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP | Gravity.END);
            wcLp.rightMargin = dpToPx(16);
            container.addView(weatherCard, wcLp);
        }

        // Add as last child of CellLayout so it draws above icons.
        page0.addView(container);

        // Measure + layout the container manually whenever page0 changes size.
        // (CellLayout.onLayout() won't do this for us.)
        page0.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) ->
                measureAndLayoutContainer(container, r - l));

        // Trigger an immediate layout pass.
        page0.post(() -> measureAndLayoutContainer(container, page0.getWidth()));
    }

    private void measureAndLayoutContainer(FrameLayout container, int pageWidth) {
        if (pageWidth <= 0) return;
        container.measure(
                View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        container.layout(0, 0, pageWidth, container.getMeasuredHeight());
    }

    // ── DragLayer attachment (search trigger) ─────────────────────────────────

    private void attachSearchTriggerToDragLayer() {
        DeviceProfile dp = mLauncher.getDeviceProfile();
        // workspacePadding.bottom is nav-bar-corrected; using hotseatBarSizePx directly
        // would double-count the nav-bar inset (consumed by LauncherRootView.fitsSystemWindows).
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
