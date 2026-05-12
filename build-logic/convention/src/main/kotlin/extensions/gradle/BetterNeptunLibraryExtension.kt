package extensions.gradle

import extensions.config.libs
import extensions.dependency.Dependency
import extensions.dependency.implementDependencies
import extensions.dependency.implementDependency
import org.gradle.api.Project
import org.gradle.api.tasks.GradleBuild
import org.gradle.kotlin.dsl.DependencyHandlerScope
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

    fun setupFeatureLayer(
        useAutoProjectConfiguration: Boolean = true,
        dependencies: DependencyHandlerScope.() -> Unit = {},
    ) {
        project.baseLayerSetup(ProjectModule.Ui) { allowedModules ->
            if (useAutoProjectConfiguration) {
                includeProjects(getAllowedProjects(allowedModules))
            }
            setupCompose()
            setupNavigation3()

            dependencies {
                implementDependencies(libs, featureDependencies)
                dependencies()
            }
        }
    }

    fun setupDomainLayer(
        dependencies: DependencyHandlerScope.() -> Unit = {}
    ) {
        project.baseLayerSetup(ProjectModule.Domain) { allowedModules ->
            includeProjects(getAllowedProjects(allowedModules))

            dependencies { dependencies() }
        }
    }

    fun setupDataLayer(
        useRoom: Boolean = false,
        useAutoProjectConfiguration: Boolean = true,
        dependencies: DependencyHandlerScope.() -> Unit = {},
    ) {
        project.baseLayerSetup(ProjectModule.Data) { allowedModules ->
            if (useRoom) setupRoom()
            if (useAutoProjectConfiguration) includeProjects(getAllowedProjects(allowedModules))

            dependencies { dependencies() }
        }
    }

    fun setupCommonDependencies(
        useRoom: Boolean = false,
        useCompose: Boolean = false,
        useNavigation3: Boolean = false,
        dependencies: DependencyHandlerScope.() -> Unit = {},
    ) {
        project.baseLayerSetup(ProjectModule.Common) {
            if (useRoom) setupRoom()
            if (useCompose) setupCompose()
            if (useNavigation3) setupNavigation3()

            dependencies { dependencies() }
        }
    }

    fun setupCoreDependencies(
        useRoom: Boolean = false,
        dependencies: DependencyHandlerScope.() -> Unit = {},
    ) {
        project.baseLayerSetup(ProjectModule.Core) { allowedModules ->
            if (useRoom) setupRoom()
            includeProjects(getAllowedProjects(allowedModules))
            dependencies { dependencies() }
        }
    }

    private fun Project.baseLayerSetup(
        layer: ProjectModule,
        configExtra: Project.(List<ProjectModule>) -> Unit,
    ) {
        val moduleString = moduleStringFromLayerAndSuffix()

        setNamespace()
        setupSerialization()
        setupKoin()

        configExtra(layer.allowedProjectDependencies)

        registerCleanArchitectureCheckTask(
            currentLayer = moduleString,
            allowed = layer.allowedProjectDependencies
        )
    }

    private fun Project.moduleStringFromLayerAndSuffix(): String =
        ":" + projectDir.path
            .split("BetterNeptun\\")
            .last()
            .split("\\")
            .joinToString(":") { it }

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

        val allowedDependenciesJoined = allowedDeps.joinToString { it }

        val dependencies = configurations
            .asSequence()
            .flatMap {  config ->
                config.dependencies.map { it.toString() }
            }
            .distinct()
            .filter { it.contains("project") && it.contains(currentLayer).not() }
            .map {
                it.removePrefix("project '").removeSuffix("'").let { project ->
                    val split = project.split(":")
                    val group = if (split.size == 3) split[1] else split[1]
                    val module = if (split.size == 3) split[2] else split[1]

                    ProjectDependency(
                        group = group,
                        module = module,
                        isAllowed = allowedDependenciesJoined.contains(group)
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

        val syncHookTask = tasks.matching {
            it.name == "prepareKotlinBuildScriptModel"
        }

        if (syncHookTask.isEmpty()) {
            tasks.register("prepareKotlinBuildScriptModel") {
                group = "ide"
                dependsOn("verifyCleanArchitecture")
            }
        } else {
            syncHookTask.configureEach {
                dependsOn("verifyCleanArchitecture")
            }
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