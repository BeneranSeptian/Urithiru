import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

fun Project.moduleImplementation(name: String) {
    dependencies.add("implementation", defineModule(name))
}

private fun Project.defineModule(name: String): Any {
    val moduleVersion = if (rootProject.extra.has(name)) {
        rootProject.extra[name].toString()
    } else {
        ""
    }

    return if (moduleVersion.isNotBlank()) {
        "${AppConfig.projectNameSpace}:$name:$moduleVersion"
    } else project(":$name")
}