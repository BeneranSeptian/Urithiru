plugins {
    id("compose-convention")
}

dependencies {
    moduleImplementation(projects.core)
    moduleImplementation(projects.coreEntity)
}