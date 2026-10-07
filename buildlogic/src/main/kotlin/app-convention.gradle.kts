plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    configureAndroid(project)

    defaultConfig {
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
    }
}

dependencies {
    baseDependencies()
}
