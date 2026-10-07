plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlinx-serialization")
}

android {
    namespace = "${AppConfig.projectNameSpace}.${project.name.replace("-", ".")}"
    configureAndroid(project)
}

dependencies {
    baseDependencies()
}
