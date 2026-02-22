plugins {
    id("com.android.library")
    id("base-convention")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    composeDependencies()
}