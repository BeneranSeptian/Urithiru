plugins {
    id("app-convention")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.septianbeneran.template"

    defaultConfig {
        applicationId = "com.septianbeneran.template"
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
}

ksp {
    arg("hilt.rootPackage", "com.septianbeneran.template")
}

dependencies {
    composeDependencies()

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(project(":core"))
    implementation(project(":core-entity"))
    implementation(project(":core-navigation"))
    implementation(project(":api-a"))
    implementation(project(":api-b"))
    implementation(project(":feature-a"))
    implementation(project(":feature-b"))
}