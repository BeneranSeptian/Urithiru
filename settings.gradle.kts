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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Urithiru"
include(":app")
include(":navigation-processor")

/** Includes each module from its cluster folder, keeping the flat Gradle path (e.g. `:core-ui` lives in `core/core-ui`). */
fun includeModules(cluster: String, vararg modules: String) {
    modules.forEach { module ->
        include(":$module")
        project(":$module").projectDir = file("$cluster/$module")
    }
}

includeModules("core", "core", "core-entity", "core-navigation", "core-ui")
includeModules("api", "api-dummyjson", "api-eldenring", "api-jsonbin")
includeModules(
    "feature",
    "feature-auth",
    "feature-catalog",
    "feature-explore",
    "feature-onboarding",
    "feature-profile",
    "feature-shop",
    "feature-splash",
)
