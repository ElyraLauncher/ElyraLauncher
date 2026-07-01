/*
 * Copyright (C) 2024 ElyraLauncher Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */

package com.android.launcher3.elyra.homeedit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;

import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.elyra.settings.ElyraSettingsActivity;
import com.android.launcher3.views.AbstractSlideInView;
import com.android.launcher3.views.OptionsPopupView;

/**
 * Elyra Home Edit Mode bottom action panel.
 *
 * <p>Shown instead of the default Launcher3 {@link OptionsPopupView} popup when the user
 * long-presses empty workspace. Dims and slightly shrinks the workspace behind it, then
 * offers Wallpaper / Widget / Layout / Settings actions.
 *
 * <p>Entry point: {@link Launcher#showElyraHomeEditMode()}, wired from
 * {@code WorkspaceTouchListener#maybeShowMenu()}. Icon long-press and folder long-press are
 * unaffected — they never call into this class.
 */
public final class ElyraHomeEditPanel extends AbstractSlideInView<Launcher>
        implements View.OnApplyWindowInsetsListener {

    private static final int DEFAULT_CLOSE_DURATION = 200;
    private static final float WORKSPACE_EDIT_SCALE = 0.94f;
    private static final long WORKSPACE_SCALE_DURATION = 220L;

    private final Rect mInsets = new Rect();
    private int mBasePaddingBottomPx;

    public ElyraHomeEditPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElyraHomeEditPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** Inflates and shows the panel above a dimmed, slightly shrunk workspace. */
    public static ElyraHomeEditPanel show(Launcher launcher) {
        ElyraHomeEditPanel panel = (ElyraHomeEditPanel) LayoutInflater.from(launcher).inflate(
                R.layout.elyra_home_edit_panel, launcher.getDragLayer(), false);
        panel.show();
        return panel;
    }

    private void show() {
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this);
        }
        attachToContainer();
        setOnApplyWindowInsetsListener(this);
        animateOpen();
        animateWorkspace(WORKSPACE_EDIT_SCALE);
    }

    private void animateOpen() {
        if (mIsOpen || mOpenCloseAnimation.getAnimationPlayer().isRunning()) {
            return;
        }
        mIsOpen = true;
        setUpDefaultOpenAnimation().start();
    }

    private void animateWorkspace(float scale) {
        View workspace = mActivityContext.getWorkspace();
        workspace.animate().cancel();
        workspace.animate().scaleX(scale).scaleY(scale).setDuration(WORKSPACE_SCALE_DURATION)
                .start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContent = findViewById(R.id.elyra_home_edit_panel_content);
        mBasePaddingBottomPx = mContent.getPaddingBottom();

        findViewById(R.id.elyra_home_edit_action_wallpaper).setOnClickListener(v -> {
            close(true);
            OptionsPopupView.startWallpaperPicker(v);
        });
        findViewById(R.id.elyra_home_edit_action_widget).setOnClickListener(v -> {
            close(true);
            OptionsPopupView.openWidgets(mActivityContext);
        });
        findViewById(R.id.elyra_home_edit_action_layout).setOnClickListener(v -> {
            close(true);
            mActivityContext.startActivity(new Intent(mActivityContext, ElyraSettingsActivity.class)
                    .putExtra(ElyraSettingsActivity.EXTRA_SHOW_HOME_DETAIL, true)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
        findViewById(R.id.elyra_home_edit_action_settings).setOnClickListener(v -> {
            close(true);
            OptionsPopupView.startSettings(v);
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildWithMargins(mContent, widthMeasureSpec, mInsets.left + mInsets.right,
                heightMeasureSpec, 0);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        int contentWidth = mContent.getMeasuredWidth();
        int contentLeft = (width - contentWidth) / 2;
        mContent.layout(contentLeft, height - mContent.getMeasuredHeight(),
                contentLeft + contentWidth, height);
        setTranslationShift(mTranslationShift);
    }

    @Override
    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
        mInsets.set(insets.left, insets.top, insets.right, insets.bottom);
        mContent.setPadding(mContent.getPaddingLeft(), mContent.getPaddingTop(),
                mContent.getPaddingRight(), mBasePaddingBottomPx + mInsets.bottom);
        requestLayout();
        return windowInsets;
    }

    @Override
    protected void handleClose(boolean animate) {
        animateWorkspace(1f);
        handleClose(animate, DEFAULT_CLOSE_DURATION);
    }

    @Override
    protected boolean isOfType(@FloatingViewType int type) {
        return (type & TYPE_ELYRA_HOME_EDIT) != 0;
    }

    @Override
    protected int getScrimColor(Context context) {
        return context.getColor(R.color.elyra_home_edit_scrim);
    }
}
