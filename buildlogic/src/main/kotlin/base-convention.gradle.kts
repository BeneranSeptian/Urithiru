import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "${AppConfig.projectNameSpace}.${project.name.replace("-", ".")}"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
    }

    buildFeatures {
        buildConfig = true
        resValues = true
    }

    flavorDimensions += AppConfig.flavorDimension
    productFlavors {
        ProductFlavor.entries.forEach {
            val flavorProperties = Properties().apply {
                val propertiesFile = file("${rootDir}/productFlavorProperties/${it.flavor}.properties")
                if (propertiesFile.exists()) {
                    load(FileInputStream(propertiesFile))
                }
            }.entries.associate { entry -> entry.key.toString() to entry.value.toString() }

            create(it.flavor) {
                isDefault = it == ProductFlavor.DEV
                dimension = AppConfig.flavorDimension

                resValue("string", "app_name", flavorProperties["APP_NAME"] ?: "My App")

                flavorProperties.forEach { (key, value) ->
                    buildConfigField("String", key, "\"$value\"")
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    baseDependencies()
}
