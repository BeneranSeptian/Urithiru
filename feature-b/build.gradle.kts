plugins {
    id("compose-convention")
}

dependencies {
    implementation(project(":api-a"))

    implementation(project(":core"))
    implementation(project(":core-entity"))
    implementation(project(":core-navigation"))
}