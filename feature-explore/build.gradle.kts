plugins {
    id("feature-convention")
}

dependencies {
    moduleImplementation(projects.apiEldenring)

    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
    moduleImplementation(projects.coreNavigation)
    moduleImplementation(projects.coreUi)
}