// Gradle-only compat stub for the internal AOSP MSDL haptics API.
// This shim exists solely to satisfy the compiler outside a full Android tree.
// No runtime haptic feedback is provided by this stub.
package com.google.android.msdl.data.model;

public enum MSDLToken {
    DRAG_INDICATOR_DISCRETE,
    SWIPE_THRESHOLD_INDICATOR,
    TAP_HIGH_EMPHASIS;
}
