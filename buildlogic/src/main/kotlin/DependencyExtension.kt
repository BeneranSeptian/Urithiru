import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the

// Configurations are referenced by name ("implementation", "ksp") instead of importing
// Gradle's generated accessors, whose package names are hashes that change between builds.

/** Hilt runtime and its KSP compiler. Applied through `hilt-convention` and `app-convention`. */
fun Project.hiltDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        "implementation"(libs.hilt.android)
        "ksp"(libs.hilt.android.compiler)
    }
}

fun Project.serializationDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        "implementation"(libs.kotlinx.serialization.json)
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
        "implementation"(libs.androidx.annotation.experimental)
    }
}

/** Compose UI and navigation-compose. Used by every module with composables (core-ui, core-navigation, features, app). The tooling behind previews and the layout inspector is debug-only. */
fun Project.composeDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        "implementation"(platform(libs.androidx.compose.bom))
        "implementation"(libs.androidx.compose.ui)
        "implementation"(libs.androidx.compose.ui.graphics)
        "implementation"(libs.androidx.compose.ui.tooling.preview)
        "debugImplementation"(libs.androidx.compose.ui.tooling)
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
        "implementation"(libs.coil.network.okhttp)
        "ksp"(project(":navigation-processor"))
    }
}
