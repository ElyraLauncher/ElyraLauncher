/*
 * Copyright (C) 2024 ElyraLauncher Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.android.launcher3.elyra.home;

import static com.android.app.animation.Interpolators.EMPHASIZED_ACCELERATE;
import static com.android.app.animation.Interpolators.EMPHASIZED_DECELERATE;

import android.content.Intent;
import android.graphics.Insets;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;

import com.android.launcher3.DropTarget;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.elyra.settings.ElyraSettingsActivity;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.views.OptionsPopupView;

/**
 * Drives the fullscreen chrome (top Done/Group bar + bottom action bar + dim scrim) for Elyra
 * Home Edit Mode, in lockstep with {@link LauncherState#EDIT_MODE}.
 *
 * <p><b>Architecture.</b> The workspace scale/translation/dim and the dock fade are owned by
 * {@code EditModeState} and the standard Launcher3 state-transition animation — not by this
 * class. This controller only layers the edit chrome on top. Crucially, the chrome must NOT
 * consume horizontal touch over the workspace: the dim scrim is deliberately non-clickable so
 * finger swipes fall through to the real {@code Workspace}, which is fully page-scrollable in
 * {@code EDIT_MODE} ({@code Workspace#workspaceInScrollableState}). Only the Done/Group pills and
 * the four action buttons are interactive. Exit is via Done or Back (both route through the state
 * manager); there is intentionally no tap-to-exit scrim, as that would eat swipes.
 *
 * <p>On entry an extra empty workspace page is added ({@code Workspace#addExtraEmptyScreens}) so
 * the user can swipe to a blank page; on exit it is stripped again if unused
 * ({@code Workspace#removeExtraEmptyScreen}).
 *
 * <p>Entry point: {@link Launcher#showElyraHomeEditMode()} calls {@code goToState(EDIT_MODE)}.
 */
public final class ElyraHomeEditModeController implements StateManager.StateListener<LauncherState>,
        DragController.DragListener {

    private static final long ANIM_DURATION_MS = 220L;
    private static final long SCRIM_FADE_MS = 150L;
    private static final float BAR_SLIDE_DP = 18f;
    private static final float PAGE_INDICATOR_LIFT_DP = 132f;

    private final Launcher mLauncher;
    private final float mBarSlidePx;
    private View mOverlay;

    /** Called from {@code Launcher.setupViews()}. */
    public static void attachTo(Launcher launcher) {
        new ElyraHomeEditModeController(launcher);
    }

    private ElyraHomeEditModeController(Launcher launcher) {
        mLauncher = launcher;
        mBarSlidePx = BAR_SLIDE_DP * launcher.getResources().getDisplayMetrics().density;
        launcher.getStateManager().addStateListener(this);
        // Listen for real drags so we can drop the dim scrim out from under the dragged icon while
        // it is in flight, then bring it back on drop. Only the scrim fades — Group/Done, the
        // bottom actions and the DropTargetBar (Hapus) are untouched.
        launcher.getDragController().addDragListener(this);
    }

    /**
     * Fades the dim scrim only, leaving Group/Done and the bottom action bar in place. Used to
     * clear the dim from under a real dragged icon (which already renders topmost) without
     * removing any edit chrome.
     */
    private void setScrimDimmed(boolean dimmed) {
        if (mOverlay == null) {
            return;
        }
        View scrim = mOverlay.findViewById(R.id.elyra_home_edit_scrim);
        if (scrim == null) {
            return;
        }
        scrim.animate().cancel();
        scrim.animate().alpha(dimmed ? 1f : 0f).setDuration(SCRIM_FADE_MS).start();
    }

    @Override
    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        // A real app/icon drag is starting (DragViews only exist for real drags). While in edit
        // mode, reduce the dim scrim so the dragged icon reads clearly; the icon itself is already
        // topmost in the DragLayer, this just removes the dim behind it. The top bar also switches
        // to drag-aware controls (Cancel + dragged item title) in place of Group/Done.
        if (mLauncher.isInState(LauncherState.EDIT_MODE)) {
            setScrimDimmed(false);
            ItemInfo info = dragObject != null ? dragObject.dragInfo : null;
            setToolbarDragMode(true, info != null ? info.title : null);
        }
    }

    @Override
    public void onDragEnd() {
        // Restore the dim and the idle Group/Done toolbar once the drag settles (drop or cancel),
        // but only if we are still in edit mode. If the drag took us out of edit mode, exitEditMode()
        // fades the whole overlay and owns teardown. This runs for cancelDrag() too, so the Cancel
        // button below only needs to trigger the cancel.
        if (mLauncher.isInState(LauncherState.EDIT_MODE)) {
            setScrimDimmed(true);
            setToolbarDragMode(false, null);
        }
    }

    /**
     * Swaps the top bar between idle edit controls (Group left, Done right) and drag-aware
     * controls (Cancel left, dragged item title filling the middle). Only visibility and the title
     * text change — no view is added or removed, and the bottom action bar / DropTargetBar (Hapus)
     * are untouched. GONE views are not measured, so the title's weight is inert when idle.
     */
    private void setToolbarDragMode(boolean active, CharSequence title) {
        if (mOverlay == null) {
            return;
        }
        View group = mOverlay.findViewById(R.id.elyra_home_edit_group);
        View done = mOverlay.findViewById(R.id.elyra_home_edit_done);
        View spacer = mOverlay.findViewById(R.id.elyra_home_edit_top_spacer);
        View cancel = mOverlay.findViewById(R.id.elyra_home_edit_cancel);
        TextView dragTitle = mOverlay.findViewById(R.id.elyra_home_edit_drag_title);

        group.setVisibility(active ? View.GONE : View.VISIBLE);
        done.setVisibility(active ? View.GONE : View.VISIBLE);
        spacer.setVisibility(active ? View.GONE : View.VISIBLE);
        cancel.setVisibility(active ? View.VISIBLE : View.GONE);
        dragTitle.setVisibility(active ? View.VISIBLE : View.GONE);

        if (active) {
            dragTitle.setText(title != null && title.length() > 0
                    ? title
                    : mLauncher.getText(R.string.elyra_home_edit_drag_title_fallback));
        }
    }

    @Override
    public void onStateTransitionStart(LauncherState toState) {
        // Drive chrome from transition START so it animates in lockstep with the workspace
        // state animation (avoids the disjoint "chrome settles after workspace" jitter).
        if (toState == LauncherState.EDIT_MODE) {
            enterEditMode();
        } else {
            exitEditMode();
        }
    }

    @Override
    public void onStateTransitionComplete(LauncherState finalState) {
        // Safety net: guarantee full teardown once we've actually settled anywhere other than
        // EDIT_MODE (e.g. EDIT_MODE -> ALL_APPS via a stray swipe, or an instant state set that
        // skipped the animated start callback). Idempotent with exitEditMode().
        if (finalState != LauncherState.EDIT_MODE) {
            mLauncher.setElyraEmptyHomeEditMode(false);
            setStatusBarHidden(false);
            restorePageIndicatorAfterEditMode();
            if (!mLauncher.isWorkspaceLoading()) {
                mLauncher.getWorkspace().removeExtraEmptyScreen(false /* stripEmptyScreens */);
            }
            if (mOverlay != null && mOverlay.getVisibility() != View.GONE) {
                mOverlay.animate().cancel();
                mOverlay.setVisibility(View.GONE);
            }
        }
    }

    private void ensureInflated() {
        if (mOverlay != null) {
            return;
        }
        mOverlay = LayoutInflater.from(mLauncher).inflate(
                R.layout.elyra_home_edit_overlay, mLauncher.getDragLayer(), false);

        // Span under the system bars so the dim scrim is one continuous edge-to-edge surface.
        // Without this, DragLayer (an InsettableFrameLayout) adds status/nav insets as MARGINS to
        // the overlay, leaving undimmed bands behind the status and navigation bars.
        if (mOverlay.getLayoutParams() instanceof InsettableFrameLayout.LayoutParams) {
            ((InsettableFrameLayout.LayoutParams) mOverlay.getLayoutParams()).ignoreInsets = true;
        }

        // NOTE: the scrim is intentionally NOT given a click listener — it must not consume
        // workspace swipes. Exit is Done / Back only.
        mOverlay.findViewById(R.id.elyra_home_edit_group).setOnClickListener(v -> { });
        mOverlay.findViewById(R.id.elyra_home_edit_done).setOnClickListener(v -> {
            v.setEnabled(false);
            exitToNormal();
        });

        // Cancel is only visible during a real drag. Cancelling the drag routes back through
        // onDragEnd(), which restores the idle Group/Done toolbar; if the drag already finished,
        // fall back to restoring the toolbar directly so the button never sticks.
        mOverlay.findViewById(R.id.elyra_home_edit_cancel).setOnClickListener(v -> {
            if (mLauncher.getDragController().isDragging()) {
                mLauncher.getDragController().cancelDrag();
            } else {
                setToolbarDragMode(false, null);
            }
        });

        mOverlay.findViewById(R.id.elyra_home_edit_action_widget).setOnClickListener(v ->
                runAfterExit(() -> OptionsPopupView.openWidgets(mLauncher)));
        mOverlay.findViewById(R.id.elyra_home_edit_action_wallpaper).setOnClickListener(v ->
                runAfterExit(() -> OptionsPopupView.startWallpaperPicker(v)));
        mOverlay.findViewById(R.id.elyra_home_edit_action_layout).setOnClickListener(v ->
                runAfterExit(this::openLayoutSettings));
        mOverlay.findViewById(R.id.elyra_home_edit_action_settings).setOnClickListener(v ->
                runAfterExit(() -> OptionsPopupView.startSettings(v)));

        applyWindowInsets();
        mLauncher.getDragLayer().addView(mOverlay);
    }

    /**
     * Pushes the system-bar insets onto ONLY the top and bottom bars, leaving the scrim
     * edge-to-edge so the dim stays one continuous surface (no bands behind the system bars).
     * The status-bar inset collapses to 0 once the bar is hidden in edit mode, letting the top
     * pills rise close to the screen edge.
     */
    private void applyWindowInsets() {
        View topBar = mOverlay.findViewById(R.id.elyra_home_edit_top_bar);
        View bottomBar = mOverlay.findViewById(R.id.elyra_home_edit_bottom_bar);
        final int topBase = topBar.getPaddingTop();
        final int bottomBase = bottomBar.getPaddingBottom();
        mOverlay.setOnApplyWindowInsetsListener((v, insets) -> {
            Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            topBar.setPadding(topBar.getPaddingLeft(), topBase + bars.top,
                    topBar.getPaddingRight(), topBar.getPaddingBottom());
            bottomBar.setPadding(bottomBar.getPaddingLeft(), bottomBar.getPaddingTop(),
                    bottomBar.getPaddingRight(), bottomBase + bars.bottom);
            return insets;
        });
    }

    private void showPageIndicatorForEditMode() {
        View pageIndicator = mLauncher.getWorkspace().getPageIndicator();
        if (pageIndicator == null) {
            return;
        }
        pageIndicator.bringToFront();
        pageIndicator.animate().cancel();
        pageIndicator.setVisibility(View.VISIBLE);
        pageIndicator.setAlpha(1f);
        mLauncher.getWorkspace().showPageIndicatorAtCurrentScroll();
        pageIndicator.animate()
                .translationY(-dpToPx(PAGE_INDICATOR_LIFT_DP))
                .alpha(1f)
                .setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_DECELERATE)
                .withLayer()
                .start();
    }

    private void restorePageIndicatorAfterEditMode() {
        View pageIndicator = mLauncher.getWorkspace().getPageIndicator();
        if (pageIndicator == null) {
            return;
        }
        pageIndicator.animate().cancel();
        pageIndicator.animate()
                .translationY(0f)
                .setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_ACCELERATE)
                .withLayer()
                .start();
    }

    private int dpToPx(float dp) {
        return Math.round(dp * mLauncher.getResources().getDisplayMetrics().density);
    }

    private void exitToNormal() {
        mLauncher.getStateManager().goToState(LauncherState.NORMAL);
    }

    /**
     * Opens the Elyra settings shell deep-linked to the Home Screen (layout) detail, so the
     * "Layout" action lands on layout controls rather than the dashboard root. "Home screen
     * settings" still opens the dashboard root via the shared settings route.
     */
    private void openLayoutSettings() {
        mLauncher.startActivity(new Intent(mLauncher, ElyraSettingsActivity.class)
                .putExtra(ElyraSettingsActivity.EXTRA_ROUTE, ElyraSettingsActivity.ROUTE_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void runAfterExit(Runnable action) {
        if (mLauncher.isInState(LauncherState.NORMAL)) {
            action.run();
            return;
        }
        mLauncher.getStateManager().addStateListener(
                new StateManager.StateListener<LauncherState>() {
                    @Override
                    public void onStateTransitionComplete(LauncherState finalState) {
                        if (finalState == LauncherState.NORMAL) {
                            mLauncher.getStateManager().removeStateListener(this);
                            action.run();
                        } else if (finalState != LauncherState.EDIT_MODE) {
                            mLauncher.getStateManager().removeStateListener(this);
                        }
                    }
                });
        exitToNormal();
    }

    /**
     * Adds a real trailing blank workspace page to swipe to. Posted so it runs after the
     * EDIT_MODE transition frame is set up (insertNewWorkspaceScreen recomputes page scrolls and
     * applies the current state to the new page, making it immediately swipeable and reflected in
     * the page indicator). Guarded so we only add while still entering edit mode and not while the
     * model is loading.
     */
    private void addExtraEmptyPage() {
        mLauncher.getWorkspace().post(() -> {
            if (mLauncher.isInState(LauncherState.EDIT_MODE)
                    && !mLauncher.isWorkspaceLoading()
                    && !mLauncher.getWorkspace().hasExtraEmptyScreens()) {
                mLauncher.getWorkspace().addExtraEmptyScreens();
            }
        });
    }

    private void enterEditMode() {
        ensureInflated();
        setStatusBarHidden(true);
        addExtraEmptyPage();
        showPageIndicatorForEditMode();

        View done = mOverlay.findViewById(R.id.elyra_home_edit_done);
        done.setEnabled(true);

        // Reset the top bar to idle Group/Done in case a prior drag that exited edit mode left it
        // in drag-aware mode (Cancel + title).
        setToolbarDragMode(false, null);

        // Reset the dim to full in case a prior drag that exited edit mode left the scrim faded.
        View scrim = mOverlay.findViewById(R.id.elyra_home_edit_scrim);
        if (scrim != null) {
            scrim.animate().cancel();
            scrim.setAlpha(1f);
        }

        mOverlay.animate().cancel();
        mOverlay.setVisibility(View.VISIBLE);
        mOverlay.requestApplyInsets();
        mOverlay.setAlpha(0f);
        mOverlay.animate().alpha(1f).setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_DECELERATE).start();

        View topBar = mOverlay.findViewById(R.id.elyra_home_edit_top_bar);
        topBar.animate().cancel();
        topBar.setAlpha(0f);
        topBar.setTranslationY(-mBarSlidePx);
        topBar.animate().alpha(1f).translationY(0f).setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_DECELERATE).withLayer().start();

        View bottomBar = mOverlay.findViewById(R.id.elyra_home_edit_bottom_bar);
        bottomBar.animate().cancel();
        bottomBar.setAlpha(0f);
        bottomBar.setTranslationY(mBarSlidePx);
        bottomBar.animate().alpha(1f).translationY(0f).setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_DECELERATE).withLayer().start();
    }

    private void exitEditMode() {
        // Always clear the empty-edit flag on any exit (Done, Back, exit to NORMAL/ALL_APPS, or a
        // widget/wallpaper/settings action which routes through exitToNormal). Cleared before the
        // early return so it never sticks true even if the overlay was already torn down.
        mLauncher.setElyraEmptyHomeEditMode(false);
        if (mOverlay == null || mOverlay.getVisibility() != View.VISIBLE) {
            return;
        }
        setStatusBarHidden(false);
        restorePageIndicatorAfterEditMode();
        // Remove only the extra empty page we added (if still unused). Pass stripEmptyScreens=false
        // so we never delete a user's own pre-existing empty page. If the user dropped an icon onto
        // the extra page, Launcher3 already committed it (commitExtraEmptyScreens on drop), so it is
        // no longer an "extra" page and is preserved here.
        mLauncher.getWorkspace().removeExtraEmptyScreen(false /* stripEmptyScreens */);

        mOverlay.animate().cancel();
        mOverlay.animate().alpha(0f).setDuration(ANIM_DURATION_MS)
                .setInterpolator(EMPHASIZED_ACCELERATE).withLayer()
                .withEndAction(() -> mOverlay.setVisibility(View.GONE))
                .start();
    }

    /**
     * Hides/shows the status bar via {@link WindowInsetsController} (API 30+; minSdk here is 31).
     * Only the status bar is toggled — the navigation bar is left to the system so gesture nav /
     * back keep working. Restoring on exit returns the previous system-bar state.
     */
    private void setStatusBarHidden(boolean hidden) {
        WindowInsetsController controller = mLauncher.getWindow().getInsetsController();
        if (controller == null) {
            return;
        }
        if (hidden) {
            controller.setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            controller.hide(WindowInsets.Type.statusBars());
        } else {
            controller.show(WindowInsets.Type.statusBars());
        }
    }
}
