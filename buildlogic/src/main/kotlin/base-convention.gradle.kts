plugins {
    id("com.android.library")
    id("kotlinx-serialization")
}

android {
    namespace = "${AppConfig.projectNameSpace}.${project.name.replace("-", ".")}"
    configureAndroid(project)
}

dependencies {
    serializationDependencies()
    testDependencies()
}
