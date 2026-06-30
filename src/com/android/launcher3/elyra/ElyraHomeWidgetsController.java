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

import static com.android.launcher3.LauncherAnimUtils.VIEW_ALPHA;
import static com.android.launcher3.states.StateAnimationConfig.ANIM_WORKSPACE_FADE;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.views.BaseDragLayer;

import java.util.List;

/**
 * Master controller for Elyra home-screen widgets.
 *
 * <p><b>Attachment architecture:</b>
 * <ul>
 *   <li><b>Smart widget</b> (unified greeting + weather, {@code elyra_smart_region.xml}) —
 *       added directly to {@link CellLayout} 0 (workspace page 0), so it scrolls off-screen
 *       naturally on page swipes.  Alpha is animated via {@link StateManager.StateHandler}
 *       so it fades IN SYNC with workspace icons during gesture-based ALL_APPS / OVERVIEW
 *       transitions (before the state is committed).</li>
 *   <li><b>Search trigger pill</b> — added to {@code DragLayer} above the hotseat
 *       (page-independent).  Hidden via {@link StateManager.StateListener} in non-NORMAL
 *       states and fades while the user swipes between workspace pages.</li>
 * </ul>
 *
 * <p>The smart widget's alpha is driven by the {@link StateManager.StateHandler} interface
 * (registered via {@link Launcher#collectStateHandlers}).  This makes it participate in the
 * same {@link PendingAnimation} as {@code ShortcutAndWidgetContainer}, including
 * gesture-scrubbed transitions.  A separate {@link StateManager.StateListener} handles the
 * search trigger only.</p>
 */
public final class ElyraHomeWidgetsController
        implements StateManager.StateHandler<LauncherState>,
                   StateManager.StateListener<LauncherState> {

    private static final int SHOW_DURATION_MS  = 180;
    private static final int TRIGGER_SETTLE_MS = 300;

    /** Single active instance — used by {@link #collectStateHandler}. */
    private static volatile ElyraHomeWidgetsController sInstance;

    private static final LinearInterpolator LINEAR = new LinearInterpolator();

    private final Launcher mLauncher;
    private final Handler  mHandler = new Handler(Looper.getMainLooper());

    /** The unified greeting + weather widget placed on CellLayout 0. */
    private View mSmartWidgetView;
    /** Search trigger pill placed in DragLayer above the hotseat. */
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
        sInstance = new ElyraHomeWidgetsController(launcher);
    }

    /**
     * Called from {@code Launcher.collectStateHandlers()} to add the smart widget
     * to the state animation chain.  The smart widget's alpha is then animated
     * in exact sync with {@code ShortcutAndWidgetContainer} during gesture transitions.
     */
    public static void collectStateHandler(List<StateManager.StateHandler<LauncherState>> out) {
        ElyraHomeWidgetsController inst = sInstance;
        if (inst != null && inst.mSmartWidgetView != null) {
            out.add(inst);
        }
    }

    private ElyraHomeWidgetsController(Launcher launcher) {
        mLauncher = launcher;

        ElyraSmartSpaceController smartSpace = ElyraSmartSpaceController.create(launcher);
        mSmartWidgetView   = smartSpace != null ? smartSpace.getSmartRegionView()   : null;
        mSearchTriggerView = smartSpace != null ? smartSpace.getSearchTriggerView() : null;

        if (mSmartWidgetView != null) {
            attachToWorkspacePage();
        }

        if (mSearchTriggerView != null) {
            attachSearchTriggerToDragLayer();
        }

        // StateListener: only for the search trigger (smart widget is covered by StateHandler).
        launcher.getStateManager().addStateListener(this);

        // Workspace scroll: fade out search trigger while swiping pages.
        launcher.getWorkspace().setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        onWorkspaceScrolled(scrollX, oldScrollX));
    }

    // ── Workspace attachment (smart widget) ───────────────────────────────────

    private void attachToWorkspacePage() {
        Workspace workspace = mLauncher.getWorkspace();
        workspace.post(() -> {
            if (workspace.getChildCount() > 0) {
                doAttachToPage0(workspace);
            } else {
                workspace.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override public void onGlobalLayout() {
                                if (workspace.getChildCount() > 0) {
                                    workspace.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                    doAttachToPage0(workspace);
                                }
                            }
                        });
            }
        });
    }

    private void doAttachToPage0(Workspace workspace) {
        if (workspace.getChildCount() == 0) return;
        View page = workspace.getChildAt(0);
        if (!(page instanceof CellLayout)) return;
        CellLayout page0 = (CellLayout) page;

        // Smart widget is MATCH_PARENT width, WRAP_CONTENT height.
        // CellLayout.onLayout() only positions mShortcutsAndWidgets; our widget is
        // measured and laid out manually via OnLayoutChangeListener.
        page0.addView(mSmartWidgetView);

        page0.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) ->
                measureAndLayout(mSmartWidgetView, r - l));

        page0.post(() -> measureAndLayout(mSmartWidgetView, page0.getWidth()));
    }

    private void measureAndLayout(View v, int pageWidth) {
        if (pageWidth <= 0) return;
        v.measure(
                View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, pageWidth, v.getMeasuredHeight());
    }

    // ── StateHandler — synchronizes smart widget alpha with workspace gesture ─

    /**
     * Immediate (no animation) state application — called during launcher restores,
     * config changes, and initial setup.
     */
    @Override
    public void setState(LauncherState state) {
        if (mSmartWidgetView == null) return;
        mSmartWidgetView.setAlpha(state == LauncherState.NORMAL ? 1f : 0f);
    }

    /**
     * Animated state application — called for every state transition including
     * gesture-scrubbed ones.  Adds smart widget alpha to the same {@link PendingAnimation}
     * that drives {@code ShortcutAndWidgetContainer} fade, so both fade in lock-step.
     */
    @Override
    public void setStateWithAnimation(LauncherState toState, StateAnimationConfig config,
            PendingAnimation animation) {
        if (mSmartWidgetView == null) return;
        float targetAlpha = (toState == LauncherState.NORMAL) ? 1f : 0f;
        animation.setFloat(mSmartWidgetView, VIEW_ALPHA, targetAlpha,
                config.getInterpolator(ANIM_WORKSPACE_FADE, LINEAR));
    }

    // ── StateListener — search trigger only ──────────────────────────────────

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

    // ── Workspace scroll — search trigger fade while swiping pages ───────────

    private void onWorkspaceScrolled(int scrollX, int oldScrollX) {
        if (mSearchTriggerView != null && mIsNormal && scrollX != oldScrollX) {
            mSearchTriggerView.animate().cancel();
            mSearchTriggerView.setAlpha(0f);
            mHandler.removeCallbacks(mTriggerFadeInRunnable);
            mHandler.postDelayed(mTriggerFadeInRunnable, TRIGGER_SETTLE_MS);
        }
    }

    // ── DragLayer attachment (search trigger) ─────────────────────────────────

    private void attachSearchTriggerToDragLayer() {
        DeviceProfile dp = mLauncher.getDeviceProfile();
        android.content.res.Resources res = mLauncher.getResources();

        // Position the trigger in the page indicator zone: just above the dock pill.
        // workspacePadding.bottom is the total bottom reserved area (hotseat + page-indicator gap).
        // We place the trigger at the hotseat top so it sits alongside the page indicator dots.
        int hotseatTop = dp.workspacePadding.bottom > 0
                ? dp.workspacePadding.bottom
                : (dp.hotseatBarSizePx - dp.getInsets().bottom);
        // Add a small nudge above the hotseat so the pill floats near the indicator dots.
        int nudge = (int)(res.getDisplayMetrics().density * 8);
        int bottomMargin = hotseatTop + nudge;

        int triggerWidthPx  = res.getDimensionPixelSize(
                com.android.launcher3.R.dimen.elyra_search_trigger_width);
        int triggerHeightPx = res.getDimensionPixelSize(
                com.android.launcher3.R.dimen.elyra_search_trigger_height);

        BaseDragLayer.LayoutParams lp = new BaseDragLayer.LayoutParams(
                triggerWidthPx, triggerHeightPx);
        lp.gravity      = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = bottomMargin;

        mLauncher.getDragLayer().addView(mSearchTriggerView, lp);
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private int dpToPx(int dp) {
        return Math.round(dp * mLauncher.getResources().getDisplayMetrics().density);
    }
}
