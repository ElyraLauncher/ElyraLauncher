plugins {
    id("com.android.library")
}

android {
    namespace = "com.elyra.launcher.compat.flags"
    compileSdk = 35
    defaultConfig {
        minSdk = 31
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets {
        named("main") {
            java.srcDirs("src/main/java")
        }
    }
}
