import gradle.kotlin.dsl.accessors._ed55803fd744f8f83380669f6271a2df.implementation
import gradle.kotlin.dsl.accessors._ed55803fd744f8f83380669f6271a2df.ksp
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the

fun Project.baseDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        implementation (libs.hilt.android)
        ksp(libs.hilt.android.compiler)
        implementation(libs.kotlinx.serialization.json)
    }
}

fun Project.apiDependencies() {
    val libs = the<LibrariesForLibs>()
    dependencies {
        implementation(libs.retrofit)
        implementation(libs.retrofit2.kotlinx.serialization.converter)
        implementation(libs.converter.gson)
        implementation(libs.kotlinx.serialization.json)
    }
}

fun Project.composeDependencies() {
    val libs = the<LibrariesForLibs>()

    dependencies {
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.graphics)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.ui.tooling)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.hilt.navigation.compose)
        implementation(libs.coil.compose)
    }
}