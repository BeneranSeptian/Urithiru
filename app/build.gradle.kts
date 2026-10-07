plugins {
    id("app-convention")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.septianbeneran.urithiru"

    defaultConfig {
        applicationId = "com.septianbeneran.urithiru"
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
}

ksp {
    arg("hilt.rootPackage", "com.septianbeneran.urithiru")
    arg("isAppModule", "true")
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

    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
    moduleImplementation(projects.coreNavigation)
    moduleImplementation(projects.coreUi)
    moduleImplementation(projects.apiA)
    moduleImplementation(projects.apiB)
    moduleImplementation(projects.apiTwitch)
    moduleImplementation(projects.featureA)
    moduleImplementation(projects.featureB)
    moduleImplementation(projects.featureSplash)

    ksp(projects.navigationProcessor)
}