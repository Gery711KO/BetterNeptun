package extensions.gradle

enum class ProjectModule(
    val path: String,
    val isParentModule: Boolean,
    val allowedProjectDependencies: List<ProjectModule>
) {
    Common(
        path = ":common",
        isParentModule = true,
        allowedProjectDependencies = emptyList()
    ),
    Core(
        path = ":core",
        isParentModule = true,
        allowedProjectDependencies = emptyList()
    ),
    Domain(
        path = ":domain",
        isParentModule = false,
        listOf(Common)
    ),
    Data(
        path = ":data",
        isParentModule = false,
        allowedProjectDependencies = listOf(Common, Core, Domain)
    ),
    Ui(
        path = ":ui",
        isParentModule = false,
        allowedProjectDependencies = listOf(Common, Domain)
    )
}