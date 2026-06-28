plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.protobuf")
}

android {
    namespace = "com.android.launcher3"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.elyra.launcher"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        // Manual BuildConfig in src_build_config; disable Gradle auto-generation to avoid conflict
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Suppress warnings from AOSP source that use deprecated APIs
        freeCompilerArgs += listOf("-Xjvm-default=all", "-opt-in=kotlin.RequiresOptIn")
    }

    sourceSets {
        named("main") {
            java.srcDirs(
                "../src",
                "../src_no_quickstep",
                "../src_build_config",
                "../src_plugins",
                "../compose/facade/disabled",
                "../compose/facade/core",
                "../shared/src",
                "src/gradleCompat/java",
            )
            res.srcDirs("../res")
            proto.srcDirs("../protos", "../protos_overrides")
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    packaging {
        resources.excludes += setOf(
            "META-INF/DEPENDENCIES",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
        )
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.4"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    // AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.dynamicanimation:dynamicanimation:1.1.0-alpha03")
    implementation("androidx.fragment:fragment:1.8.1")
    implementation("androidx.slice:slice-core:1.1.0-alpha02")
    implementation("androidx.slice:slice-view:1.1.0-alpha02")
    implementation("androidx.preference:preference:1.2.1")

    // Protobuf
    implementation("com.google.protobuf:protobuf-javalite:3.25.4")

    // Dagger 2
    implementation("com.google.dagger:dagger:2.51.1")
    kapt("com.google.dagger:dagger-compiler:2.51.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
