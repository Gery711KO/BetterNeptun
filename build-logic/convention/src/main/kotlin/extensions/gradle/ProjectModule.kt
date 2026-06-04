package extensions.gradle

import extensions.gradle.BaseLayers.Common

val projectModules = BaseLayers.entries.filterIsInstance<ProjectModule>() +
        SpecialLayers.entries.filterIsInstance<ProjectModule>()

interface ProjectModule {
    val path: String
    val isParentModule: Boolean
    val allowedProjectDependencies: List<ProjectModule>
}

enum class BaseLayers(
    override val path: String,
    override val isParentModule: Boolean,
    override val allowedProjectDependencies: List<ProjectModule>
): ProjectModule {
    Common(
        path = ":common",
        isParentModule = true,
        allowedProjectDependencies = emptyList()
    ),
    Domain(
        path = ":domain",
        isParentModule = false,
        allowedProjectDependencies = emptyList()
    ),
    Localization(
        path = ":localization",
        isParentModule = false,
        allowedProjectDependencies = listOf(Common, Domain)
    ),
    Core(
        path = ":core",
        isParentModule = true,
        allowedProjectDependencies = listOf(Common, Domain)
    ),
    Data(
        path = ":data",
        isParentModule = false,
        allowedProjectDependencies = listOf(Common, Core, Domain)
    ),
    Ui(
        path = ":ui",
        isParentModule = false,
        allowedProjectDependencies = listOf(Common, Domain, Localization, SpecialLayers.Navigation)
    ),
}

enum class SpecialLayers(
    override val path: String,
    override val isParentModule: Boolean,
    override val allowedProjectDependencies: List<ProjectModule>
): ProjectModule {
    Navigation(
        path = ":ui:navigation",
        isParentModule = false,
        allowedProjectDependencies = listOf(Common)
    ),
}