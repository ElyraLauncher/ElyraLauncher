// Gradle APK compatibility shim.
// SvgPathParser is AOSP-internal and not present in any released
// androidx.graphics:graphics-shapes Maven artifact.  This stub satisfies
// the compile-time reference in ShapeDelegate.kt; at runtime on an AOSP ROM
// the real class is provided by the platform or a bundled lib.
package androidx.graphics.shapes

object SvgPathParser {
    // RoundedPolygon.Feature is AOSP-internal and not in any released Maven artifact.
    // Return List<Nothing> (bottom type, subtype of all List<T>) to satisfy the import
    // reference; GenericPathShape's poly initialization is replaced in ShapeDelegate.kt
    // to avoid the also-internal RoundedPolygon(features, centerX, centerY) constructor.
    @Suppress("UNCHECKED_CAST")
    fun parseFeatures(pathData: String): List<Nothing> = emptyList()
}
