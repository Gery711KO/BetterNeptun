package extensions.gradle

import extensions.gradle.BaseLayers.Localization

val projectModules = BaseLayers.entries.filterIsInstance<ProjectModule>() +
        SpecialLayers.entries.filterIsInstance<ProjectModule>()

interface ProjectModule {
    val path: String
    val isPackageModule: Boolean
    val allowedProjectDependencies: List<ProjectModule>
}

enum class BaseLayers(
    override val path: String,
    override val isPackageModule: Boolean,
    override val allowedProjectDependencies: List<ProjectModule>
): ProjectModule {
    Common(
        path = ":common",
        isPackageModule = true,
        allowedProjectDependencies = emptyList()
    ),
    Domain(
        path = ":domain",
        isPackageModule = false,
        allowedProjectDependencies = emptyList()
    ),
    Localization(
        path = ":localization",
        isPackageModule = false,
        allowedProjectDependencies = listOf(Common, Domain)
    ),
    Core(
        path = ":core",
        isPackageModule = true,
        allowedProjectDependencies = listOf(Common, Domain)
    ),
    Data(
        path = ":data",
        isPackageModule = false,
        allowedProjectDependencies = listOf(Common, Core, Domain)
    ),
    Ui(
        path = ":ui",
        isPackageModule = false,
        allowedProjectDependencies = listOf(
            Common,
            Domain,
            Localization,
            SpecialLayers.Navigation,
            SpecialLayers.Theme
        )
    ),
}

enum class SpecialLayers(
    override val path: String,
    override val isPackageModule: Boolean,
    override val allowedProjectDependencies: List<ProjectModule>
): ProjectModule {
    Theme(
        path = ":ui:designsystem",
        isPackageModule = false,
        allowedProjectDependencies = listOf(BaseLayers.Common, BaseLayers.Domain,  Localization)
    ),
    Navigation(
        path = ":ui:navigation",
        isPackageModule = false,
        allowedProjectDependencies = listOf(BaseLayers.Common, BaseLayers.Domain, Theme)
    )
}