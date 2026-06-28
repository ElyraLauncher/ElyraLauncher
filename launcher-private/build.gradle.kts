plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.android.launcher3"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.elyra.launcher.private"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1-private-launcher3"
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.srcDirs(
                "../src",
                "../quickstep/src",
                "../quickstep/dagger",
            )
            res.srcDirs("src/main/res")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }
}

dependencies {
    implementation(project(":launcher-private:quickstep-res"))
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.slice:slice-view:1.1.0-alpha02")
    implementation("androidx.window:window:1.3.0")
    implementation("com.google.dagger:dagger:2.52")
    implementation("com.google.protobuf:protobuf-javalite:4.29.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    annotationProcessor("com.google.dagger:dagger-compiler:2.52")
    kapt("com.google.dagger:dagger-compiler:2.52")
}
