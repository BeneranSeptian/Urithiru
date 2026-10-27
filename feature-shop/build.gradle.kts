plugins {
    id("feature-convention")
}

dependencies {
    moduleImplementation(projects.core)
    moduleImplementation(projects.coreNavigation)
    moduleImplementation(projects.coreUi)
}
