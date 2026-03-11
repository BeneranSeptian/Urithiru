plugins {
    alias(libs.plugins.android.library)
    id("compose-convention")
}

dependencies {
    moduleImplementation("core")

    moduleImplementation("core-entity")
    moduleImplementation("core-navigation")
}