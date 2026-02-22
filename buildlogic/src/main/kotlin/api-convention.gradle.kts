import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("base-convention")
}

android {
    defaultConfig {
        val propertiesFile = project.file("microservice.properties")
        if (propertiesFile.exists()) {
            Properties().apply {
                load(FileInputStream(propertiesFile))
                entries.forEach {
                    buildConfigField("String", it.key.toString(), "\"${it.value}\"")
                }
            }
        }
    }
}

dependencies {
    apiDependencies()
}