plugins {
    id("compose-convention")
}

dependencies {
    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
    moduleImplementation(projects.coreNavigation)
    moduleImplementation(projects.coreUi)

    moduleImplementation(projects.apiB)
}