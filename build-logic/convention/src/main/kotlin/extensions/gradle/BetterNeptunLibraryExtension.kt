package extensions.gradle

import extensions.config.libs
import extensions.dependency.Dependency
import extensions.dependency.implementDependencies
import extensions.dependency.implementDependency
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Base extension for BetterNeptun convention plugins.
 * Uses Gradle Property API for lazy evaluation and configuration caching.
 */
class BetterNeptunLibraryExtension(private val project: Project) {

    private val subProjects = project.rootProject.subprojects.mapNotNull { subProject ->
        if (filteredModules.contains(subProject.path).not()) subProject.path
        else null
    }

    private fun Project.baseLayerSetup(
        namespaceSuffix: String,
        layer: ProjectModule,
        configExtra: Project.(List<ProjectModule>) -> Unit,
    ) {
        setNamespace(namespaceSuffix)
        setupSerialization()
        setupKoin()

        configExtra(layer.allowedProjectDependencies)

        registerCleanArchitectureCheckTask(
            currentLayer = ":$namespaceSuffix",
            allowed = layer.allowedProjectDependencies
        )
    }

    fun setupFeatureLayer(
        namespaceSuffix: String,
        useAutoProjectConfiguration: Boolean = true
    ) {
        project.baseLayerSetup(
            namespaceSuffix = namespaceSuffix,
            layer = ProjectModule.Ui
        ) { allowedModules ->
            if (useAutoProjectConfiguration) {
                includeProjects(getAllowedProjects(allowedModules))
            }
            setupCompose()
            setupNavigation3()

            dependencies { implementDependencies(libs, featureDependencies) }
        }
    }

    fun setupDomainLayer(
        namespaceSuffix: String,
    ) {
        project.baseLayerSetup(
            namespaceSuffix = namespaceSuffix,
            layer = ProjectModule.Domain
        ) { allowedModules ->
            includeProjects(getAllowedProjects(allowedModules))
        }
    }

    fun setupDataLayer(
        namespaceSuffix: String,
        useRoom: Boolean = false,
        useAutoProjectConfiguration: Boolean = true
    ) {
        project.baseLayerSetup(
            namespaceSuffix = namespaceSuffix,
            layer = ProjectModule.Data
        ) { allowedModules ->
            if (useRoom) setupRoom()
            if (useAutoProjectConfiguration) includeProjects(getAllowedProjects(allowedModules))
        }
    }

    fun setupCommonDependencies(
        namespaceSuffix: String,
        useRoom: Boolean = false,
        useCompose: Boolean = false,
        useNavigation3: Boolean = false,
    ) {
        project.baseLayerSetup(
            namespaceSuffix = namespaceSuffix,
            layer = ProjectModule.Common
        ) {
            if (useRoom) setupRoom()
            if (useCompose) setupCompose()
            if (useNavigation3) setupNavigation3()
        }
    }

    fun setupCoreDependencies(
        namespaceSuffix: String,
        useRoom: Boolean = false,
    ) {
        project.baseLayerSetup(
            namespaceSuffix = namespaceSuffix,
            layer = ProjectModule.Core
        ) {
            if (useRoom) setupRoom()
        }
    }

    private fun Project.includeProjects(projects: List<String>) {
        logger.lifecycle("Auto configure: $projects")
        with(project) {
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
    }

    private fun Project.checkCleanDependencies(
        currentLayer: String,
        allowed: List<ProjectModule>
    ) {
        val allowedDeps = allowed.map {
            it.path + if (it.isParentModule) ":*" else ""
        }

        val dependencies = configurations
            .asSequence()
            .flatMap { it.dependencies.map { it.toString() } }
            .distinct()
            .filter { it.contains("project") }
            .filter { it.contains(currentLayer.replace(".", ":")).not() }
            .map {
                it.removePrefix("project '").removeSuffix("'").let { project ->
                    val split = project.split(":")
                    val group = if (split.size == 3) split[1] else split[1]
                    val module = if (split.size == 3) split[2] else split[1]

                    ProjectDependency(
                        group = group,
                        module = module,
                        isAllowed = allowedDeps.joinToString { it }.contains(group)
                    )
                }
            }.toList()

        dependencies.forEach { projectDependency ->
            check(projectDependency.isAllowed) {
                "[${ProjectModule.entries.find { currentLayer.contains(it.path) }}] - " +
                        "Project dependencies violate clean architecture.\n" +
                        "You are only allowed to depend on ${allowedDeps}.\n" +
                        "You currently depend on $dependencies"
            }
        }
    }

    private fun Project.registerCleanArchitectureCheckTask(
        currentLayer: String,
        allowed: List<ProjectModule>
    ) {
        tasks.register("verifyCleanArchitecture") {
            group = "verification"

            doLast {
                checkCleanDependencies(currentLayer, allowed)
            }
        }

        tasks.getByName("preBuild") {
            dependsOn("verifyCleanArchitecture")
        }
    }

    private fun getAllowedProjects(
        allowedModules: List<ProjectModule>,
    ): List<String> =
        allowedModules.flatMap { module ->
            subProjects.filter {
                it.contains(module.path)
            }
        }.distinct()
}