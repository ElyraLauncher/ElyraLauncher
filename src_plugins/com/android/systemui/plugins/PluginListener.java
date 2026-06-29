package com.android.systemui.plugins;

/**
 * Callback interface for plugin lifecycle events.
 */
public interface PluginListener<T extends Plugin> {
    void onPluginConnected(T plugin, android.content.Context pluginContext);
    default void onPluginDisconnected(T plugin) {}
}
