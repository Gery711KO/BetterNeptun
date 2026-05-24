plugins {
    `kotlin-dsl`
    alias(libs.plugins.koin.compiler)
}

group = "hu.kocsisgeri.betterneptun.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.koin.gradlePlugin)

    implementation(libs.androidSecrets.gradlePlugin)
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
    KoinPlugin(
        pluginName = "koinPlugin",
        pluginId = "betterneptun.android.koin",
        pluginImplementationClass = "AndroidKoinConventionPlugin"
    ),
    SecretsPlugin(
        pluginName = "secretsPlugin",
        pluginId = "betterneptun.android.secrets",
        pluginImplementationClass = "AndroidSecretsConventionPlugin"
    )
}
