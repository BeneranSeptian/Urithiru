plugins {
    id("api-convention")
}

dependencies {
    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
}