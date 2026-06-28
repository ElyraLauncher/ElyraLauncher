package com.android.systemui.plugins;

/**
 * Base marker interface for all SystemUI launcher plugins.
 */
public interface Plugin {
    default void onCreate() {}
    default void onDestroy() {}
}
