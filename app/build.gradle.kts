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

    // Compose UI testing (JUnit and AndroidX test come from app-convention)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
    moduleImplementation(projects.coreNavigation)
    moduleImplementation(projects.coreUi)
    moduleImplementation(projects.apiEldenring)
    moduleImplementation(projects.apiJsonbin)
    moduleImplementation(projects.apiTwitch)
    moduleImplementation(projects.apiDummyjson)
    moduleImplementation(projects.featureExplore)
    moduleImplementation(projects.featureOnboarding)
    moduleImplementation(projects.featureSplash)

    ksp(projects.navigationProcessor)
}