plugins {
    alias(libs.plugins.android.library)
    id("compose-convention")
}

dependencies {
    moduleImplementation(projects.core)

    moduleImplementation(projects.coreEntity)
    moduleImplementation(projects.coreNavigation)
}