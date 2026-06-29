// Gradle-only compat stub for the internal WM Shell bubbles flag API.
// enableCreateAnyBubble() returns false so the bubble-creation code path is
// disabled in Gradle APK builds; this does not affect normal launcher function.
package com.android.wm.shell.shared.bubbles;

public final class BubbleAnythingFlagHelper {
    private BubbleAnythingFlagHelper() {}

    public static boolean enableCreateAnyBubble() {
        return false;
    }
}
