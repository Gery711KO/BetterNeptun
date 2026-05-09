plugins {
    `kotlin-dsl`
}

group = "hu.kocsisgeri.betterneptun.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        PluginStack.entries.forEach { plugin ->
            register(plugin.pluginName) {
                id = plugin.pluginId
                implementationClass = plugin.pluginImplementationClass
            }
        }
    }
}

enum class PluginStack(
    val pluginName: String,
    val pluginId: String,
    val pluginImplementationClass: String,
) {
    AndroidApplication(
        pluginName = "androidApplication",
        pluginId = "betterneptun.android.application",
        pluginImplementationClass = "AndroidApplicationConventionPlugin"
    ),
    AndroidLibrary(
        pluginName = "androidLibrary",
        pluginId = "betterneptun.android.library",
        pluginImplementationClass = "AndroidLibraryConventionPlugin"
    ),
    AndroidFeature(
        pluginName = "androidFeature",
        pluginId = "betterneptun.android.feature",
        pluginImplementationClass = "AndroidFeatureConventionPlugin"
    ),
    AndroidRoom(
        pluginName = "androidRoom",
        pluginId = "betterneptun.android.room",
        pluginImplementationClass = "AndroidRoomConventionPlugin"
    ),
}
