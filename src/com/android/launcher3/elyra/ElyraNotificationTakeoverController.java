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

import android.view.View;

/**
 * Architecture foundation for the notification takeover region.
 *
 * <p>When {@link ElyraFeatureFlags#NOTIFICATION_TAKEOVER} is {@code true} this controller
 * will bind a high-priority notification into the smart region card, replacing the default
 * greeting with the notification title + body.</p>
 *
 * <p>Full integration requires a {@code NotificationListenerService} grant. That service
 * is NOT wired yet — this class holds the UI contract so the view hierarchy is ready when
 * the listener is added.</p>
 *
 * <p>Usage (from {@link ElyraSmartSpaceController}):
 * <pre>
 *     if (ElyraFeatureFlags.NOTIFICATION_TAKEOVER) {
 *         ElyraNotificationTakeoverController.attach(smartRegionView);
 *     }
 * </pre>
 * </p>
 */
public final class ElyraNotificationTakeoverController {

    /** Attaches the controller to the smart region view (no-op until listener is wired). */
    static void attach(View smartRegionView) {
        // Nothing to do yet. When the NotificationListenerService integration lands,
        // this method will register a callback, bind the notification section, and
        // call showNotification() / clearNotification() as appropriate.
    }

    /**
     * Shows a notification in the takeover section.
     * Called by the future NotificationListenerService integration.
     */
    @SuppressWarnings("unused")
    public static void showNotification(View smartRegionView, String title, String body) {
        if (!ElyraFeatureFlags.NOTIFICATION_TAKEOVER) return;

        View defaultSection      = smartRegionView.findViewById(
                com.android.launcher3.R.id.elyra_smart_default_section);
        View notificationSection = smartRegionView.findViewById(
                com.android.launcher3.R.id.elyra_notification_section);
        android.widget.TextView titleView = smartRegionView.findViewById(
                com.android.launcher3.R.id.elyra_notification_title);
        android.widget.TextView bodyView  = smartRegionView.findViewById(
                com.android.launcher3.R.id.elyra_notification_body);

        if (defaultSection != null)      defaultSection.setVisibility(View.GONE);
        if (notificationSection != null) notificationSection.setVisibility(View.VISIBLE);
        if (titleView != null)           titleView.setText(title);
        if (bodyView != null)            bodyView.setText(body);
    }

    /**
     * Restores the default greeting section.
     * Called when the priority notification is dismissed or replaced.
     */
    @SuppressWarnings("unused")
    public static void clearNotification(View smartRegionView) {
        if (!ElyraFeatureFlags.NOTIFICATION_TAKEOVER) return;

        View defaultSection      = smartRegionView.findViewById(
                com.android.launcher3.R.id.elyra_smart_default_section);
        View notificationSection = smartRegionView.findViewById(
                com.android.launcher3.R.id.elyra_notification_section);

        if (defaultSection != null)      defaultSection.setVisibility(View.VISIBLE);
        if (notificationSection != null) notificationSection.setVisibility(View.GONE);
    }

    private ElyraNotificationTakeoverController() {}
}
