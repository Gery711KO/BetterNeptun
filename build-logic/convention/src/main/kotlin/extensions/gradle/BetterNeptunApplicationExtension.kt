package extensions.gradle

import extensions.dependency.implementDependency
import extensions.config.libs
import extensions.dependency.Dependency
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class BetterNeptunApplicationExtension(private val project: Project) {

    private val projects = project.rootProject.subprojects.mapNotNull { subProject ->
        if (filteredModules.contains(subProject.path).not()) subProject.path
        else null
    }

    private fun Project.includeAllSubprojects() {
        dependencies {
            implementDependency(
                libs = libs,
                dependency = Dependency(
                    type = Dependency.Type.PROJECT,
                    aliases = projects
                )
            )
        }
    }

    private fun setup() {
        with(project) {
            includeAllSubprojects()
            setupSerialization()
            setupKoin()
            setupCompose()
            setupNavigation3()
        }
    }

    init {
        setup()
    }
}
