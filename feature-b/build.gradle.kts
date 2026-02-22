plugins {
    id("compose-convention")
}

dependencies {
    moduleImplementation("api-b")

    moduleImplementation("core")
    moduleImplementation("core-entity")
    moduleImplementation("core-navigation")
}