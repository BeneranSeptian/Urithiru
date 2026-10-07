plugins {
    id("feature-convention")
}

dependencies {
    moduleImplementation(projects.apiB)

    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
    moduleImplementation(projects.coreNavigation)
    moduleImplementation(projects.coreUi)
}