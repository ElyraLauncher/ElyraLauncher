pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ElyraLauncher"
include(":app")
include(":modules:launcher-flags-compat")
include(":modules:animationlib")
include(":modules:iconloaderlib")
