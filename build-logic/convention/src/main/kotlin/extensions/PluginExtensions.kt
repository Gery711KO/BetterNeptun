package extensions

import androidx.room.gradle.RoomExtension
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

internal const val BETTER_NEPTUN_EXTENSION_NAME = "betterNeptun"

class BetterNeptunApplicationExtension(
    private val project: Project,
) {
    fun setup() {
        with(project) {
            val projects = rootProject.subprojects.mapNotNull { subProject ->
                if (subProject.path.contains("app")) null
                else subProject.path
            }
            setupProjects(projects)
            setupSerialization()
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
            setNamespace(namespaceSuffix)
            setupSerialization()

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
            setNamespace(namespaceSuffix)
            setupSerialization()
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
            if (useRoom) setupRoom()

            setNamespace(namespaceSuffix)
            setupSerialization()
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
            setNamespace(namespaceSuffix)
            setupSerialization()
            setupKoin()

            if (useCompose) setupCompose()
            if (useNavigation3) setupNavigation3()
        }
    }
}

private fun Project.setupCompose() {
    pluginManager.applyPluginFromLibs(libs to composePluginList)
    extensions.findByType(ApplicationExtension::class)?.apply {
        buildFeatures { compose = true }
    }
    extensions.findByType(LibraryExtension::class)?.apply {
        buildFeatures { compose = true }
    }
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

private fun Project.setupProjects(projectPaths: List<String>) {
    dependencies {
        implementDependency(
            libs = libs,
            dependency = Dependency(
                type = ImplType.PROJECT,
                aliases = projectPaths
            )
        )
    }
}

private fun Project.setupSerialization() {
    pluginManager.applyPluginFromLibs(libs to listOf(serializationPlugin))
}

private fun Project.setNamespace(namespaceSuffix: String) {
    extensions.getByType(LibraryExtension::class).apply {
        namespace = projectConfigs.namespace + ".$namespaceSuffix"
    }
}
