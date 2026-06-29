plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val elyraSystemUiLibsDir = rootProject.projectDir.resolveSibling("ElyraSystemUILibs")

android {
    namespace = "com.android.launcher3.icons"
    compileSdk = 35
    defaultConfig {
        minSdk = 31
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjvm-default=all", "-opt-in=kotlin.RequiresOptIn")
    }
    sourceSets {
        named("main") {
            java.srcDirs(
                "${elyraSystemUiLibsDir}/iconloaderlib/src",
                // src_full_lib excluded: SimpleIconCache uses hidden UserHandle.getIdentifier()
                // and is not referenced from Launcher3 sources
            )
            res.srcDirs("${elyraSystemUiLibsDir}/iconloaderlib/res")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }
}

dependencies {
    api(project(":modules:animationlib"))
    // Launcher3 Flags provided compileOnly — at runtime the app module supplies them
    compileOnly(project(":modules:launcher-flags-compat"))
    implementation("androidx.core:core-ktx:1.13.1")
}
