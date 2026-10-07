import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.extra

/**
 * Adds another module of this project as an `implementation` dependency.
 * Pass a type-safe accessor generated from settings.gradle.kts, e.g. `moduleImplementation(projects.coreUi)`,
 * so only modules that are actually included can be referenced.
 * If the root project's `extra` defines a version for the module name, the published Maven artifact is used instead.
 */
fun Project.moduleImplementation(module: ProjectDependency) {
    dependencies.add("implementation", defineModule(module))
}

private fun Project.defineModule(module: ProjectDependency): Any {
    val name = module.path.removePrefix(":")
    val moduleVersion = if (rootProject.extra.has(name)) {
        rootProject.extra[name].toString()
    } else {
        ""
    }

    return if (moduleVersion.isNotBlank()) {
        "${AppConfig.projectNameSpace}:$name:$moduleVersion"
    } else module
}
