plugins {
    id("com.android.library")
}

android {
    namespace = "com.android.launcher3"
    compileSdk = 35

    defaultConfig {
        minSdk = 31
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            res.srcDirs("../../quickstep/res")
        }
    }
}

dependencies {
    implementation(project(":launcher-private:launcher3-res"))
}
