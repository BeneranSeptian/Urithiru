plugins {
    id("feature-convention")
}

dependencies {
    moduleImplementation(projects.apiA)

    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
    moduleImplementation(projects.coreNavigation)
    moduleImplementation(projects.coreUi)
}