plugins {
    id("compose-convention")
}

dependencies {
    moduleImplementation("api-a")

    moduleImplementation("core")
    moduleImplementation("core-entity")
    moduleImplementation("core-navigation")
    moduleImplementation("core-ui")
}