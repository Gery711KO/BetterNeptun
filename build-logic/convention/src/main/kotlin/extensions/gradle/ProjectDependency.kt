package extensions.gradle

data class ProjectDependency(
    val group: String,
    val module: String,
    val isAllowed: Boolean
) {
    override fun toString(): String {
        return if (group == module) {
            ":$group"
        } else {
            ":$group:$module"
        }
    }
}