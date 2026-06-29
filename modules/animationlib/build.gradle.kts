plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val elyraSystemUiLibsDir = rootProject.projectDir.resolveSibling("ElyraSystemUILibs")

android {
    namespace = "com.android.app.animation"
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
    }
    sourceSets {
        named("main") {
            java.srcDirs("${elyraSystemUiLibsDir}/animationlib/src")
            res.srcDirs("${elyraSystemUiLibsDir}/animationlib/res")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-animation:1.0.0")
}
