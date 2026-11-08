plugins {
    id("com.android.library")
    id("base-convention")
    id("hilt-convention")
}

android {
    defaultConfig {
        readProperties(project.file("microservice.properties")).forEach { (key, value) ->
            buildConfigField("String", key, value.asBuildConfigString())
        }
    }
}

dependencies {
    apiDependencies()
}
