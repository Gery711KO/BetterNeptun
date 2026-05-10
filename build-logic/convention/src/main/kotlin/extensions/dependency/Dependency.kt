package extensions.dependency

import org.gradle.api.plugins.JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME

data class Dependency(
    val type: Type = Type.DEPENDENCY,
    val config: String = IMPLEMENTATION_CONFIGURATION_NAME,
    val aliases: List<String>
) {
    enum class Type { BUNDLE, DEPENDENCY, PROJECT, PLATFORM }
}