pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

        includeBuild("buildlogic")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Urithiru Project"
include(":app")
include(":core")
include(":api-a")
include(":core-entity")
include(":core-navigation")
include(":feature-a")
include(":feature-b")
include(":api-b")
include(":core-ui")
include(":feature-splash")
include(":navigation-processor")
