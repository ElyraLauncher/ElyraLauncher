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
include(":launcher-private")
include(":launcher-private:launcher3-res")
include(":launcher-private:quickstep-res")
