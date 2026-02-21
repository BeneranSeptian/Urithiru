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