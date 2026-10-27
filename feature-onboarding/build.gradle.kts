plugins {
    id("feature-convention")
}

dependencies {
    moduleImplementation(projects.apiJsonbin)
    moduleImplementation(projects.apiDummyjson)

    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
    moduleImplementation(projects.coreNavigation)
    moduleImplementation(projects.coreUi)
}