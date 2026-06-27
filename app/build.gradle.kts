plugins {
    id("com.android.application")
}

android {
    namespace = "com.android.launcher3.standalone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.launcher3.standalone"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1-smoke"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
