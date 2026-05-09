package extensions

import androidx.room.gradle.RoomExtension
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal const val BETTER_NEPTUN_EXTENSION_NAME = "betterNeptun"

class BetterNeptunApplicationExtension(
    private val project: Project,
) {
    fun setup() {
        with(project) {
            extensions.getByType(ApplicationExtension::class).apply {
                buildFeatures { compose = true }
            }
            dependencies {
                pluginManager.applyPluginFromLibs(libs to listOf(serializationPlugin))
                implementDependency(
                    libs = libs,
                    dependency = Dependency(
                        type = ImplType.PROJECT,
                        aliases = listOf(
                            ":common",
                            ":data",
                            ":domain",
                            ":ui"
                        )
                    )
                )
            }
            setupKoin()
            setupCompose()
            setupNavigation3()
        }
    }

    init {
        setup()
    }
}

/**
 * Base extension for BetterNeptun convention plugins.
 * Uses Gradle Property API for lazy evaluation and configuration caching.
 */
class BetterNeptunLibraryExtension(private val project: Project) {

    fun setupFeatureLayer(
        namespaceSuffix: String,
    ) {
        with(project) {
            extensions.getByType(LibraryExtension::class).apply {
                namespace = projectConfigs.namespace + ".$namespaceSuffix"
                buildFeatures { compose = true }
            }
            pluginManager.applyPluginFromLibs(libs to listOf(serializationPlugin))
            dependencies {
                implementDependencies(libs = libs, dependencyList = featureDependencies)
            }

            setupKoin()
            setupCompose()
            setupNavigation3()
        }
    }

    fun setupDomainLayer(
        namespaceSuffix: String,
    ) {
        with(project) {
            extensions.getByType(LibraryExtension::class).apply {
                namespace = projectConfigs.namespace + ".$namespaceSuffix"
            }
            pluginManager.applyPluginFromLibs(libs to listOf(serializationPlugin))
            setupKoin()
            dependencies {
                implementDependencies(libs = libs, dependencyList = domainDependencies)
            }
        }

    }

    fun setupDataLayer(
        namespaceSuffix: String,
        useRoom: Boolean = false,
    ) {
        with(project) {
            extensions.getByType(LibraryExtension::class).apply {
                namespace = projectConfigs.namespace + ".$namespaceSuffix"
            }
            pluginManager.applyPluginFromLibs(libs to listOf(serializationPlugin))
            if (useRoom) setupRoom()
            setupKoin()
            dependencies {
                implementDependencies(libs = libs, dependencyList = dataDependencies)
            }
        }
    }

    fun setupCommonDependencies(
        namespaceSuffix: String,
        useCompose: Boolean = false,
        useNavigation3: Boolean = false,
    ) {
        with(project) {
            extensions.getByType(LibraryExtension::class).apply {
                namespace = projectConfigs.namespace + ".$namespaceSuffix"
            }
            setupKoin()

            pluginManager.applyPluginFromLibs(libs to listOf(serializationPlugin))
            if (useCompose) setupCompose()
            if (useNavigation3) setupNavigation3()
        }
    }
}

private fun Project.setupCompose() {
    pluginManager.applyPluginFromLibs(libs to composePluginList)
    dependencies {
        implementDependencies(libs = libs, dependencyList = composeDependencies)
    }
}

private fun Project.setupNavigation3() {
    dependencies {
        implementDependencies(libs = libs, dependencyList = navigation3dependencies)
    }
}

private fun Project.setupRoom() {
    pluginManager.apply("androidx.room")
    pluginManager.apply("com.google.devtools.ksp")

    extensions.configure<RoomExtension> {
        // The directory where schemas should be stored.
        schemaDirectory("$projectDir/schemas")
    }

    dependencies {
        add("implementation", libs.findLibrary("androidx.room.runtime").get())
        add("implementation", libs.findLibrary("androidx.room.ktx").get())
        add("ksp", libs.findLibrary("androidx.room.compiler").get())
    }
}

private fun Project.setupKoin() {
    dependencies {
        implementDependencies(libs = libs, dependencyList = koinDependency)
    }
}
