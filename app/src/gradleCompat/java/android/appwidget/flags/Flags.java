// Gradle-only compat stub for android.appwidget.flags (aconfig-generated framework API).
// generatedPreviews() returns false so the generated-preview widget path is disabled
// in Gradle APK builds without affecting normal widget management.
package android.appwidget.flags;

public final class Flags {
    private Flags() {}

    public static boolean generatedPreviews() {
        return false;
    }
}
