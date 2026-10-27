import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the

// Configurations are referenced by name ("implementation", "ksp") instead of importing
// Gradle's generated accessors, whose package names are hashes that change between builds.

fun Project.baseDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        "implementation"(libs.hilt.android)
        "ksp"(libs.hilt.android.compiler)
        "implementation"(libs.kotlinx.serialization.json)
        "implementation"(libs.androidx.datastore.preferences)
    }
}

/** JUnit for `src/test` and AndroidX test for `src/androidTest`. Used by every module, so the generated test stubs compile. */
fun Project.testDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        "testImplementation"(libs.junit)
        "androidTestImplementation"(libs.androidx.junit)
        "androidTestImplementation"(libs.androidx.espresso.core)
    }
}

fun Project.apiDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        "implementation"(libs.retrofit)
        "implementation"(libs.kotlinx.serialization.json)
    }
}

/** Compose UI and navigation-compose. Used by every module with composables (core-ui, core-navigation, features, app). */
fun Project.composeDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        "implementation"(platform(libs.androidx.compose.bom))
        "implementation"(libs.androidx.compose.ui)
        "implementation"(libs.androidx.compose.ui.graphics)
        "implementation"(libs.androidx.compose.ui.tooling.preview)
        "implementation"(libs.androidx.compose.ui.tooling)
        "implementation"(libs.androidx.compose.material3)
        "implementation"(libs.androidx.navigation.compose)
        "implementation"(libs.androidx.activity.compose)
    }
}

/** Extras only feature modules need: hiltViewModel(), image loading, and the @FeatureRoute processor. */
fun Project.featureDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        "implementation"(libs.androidx.hilt.navigation.compose)
        "implementation"(libs.coil.compose)
        // Coil 3 has no network loader by default; this one registers itself and uses OkHttp
        "implementation"(libs.coil.network.okhttp)
        // buildlogic is an included build, so it can't use the `projects.*` accessors
        "ksp"(project(":navigation-processor"))
    }
}
