package extensions.gradle

import androidx.room.gradle.RoomExtension
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import extensions.dependency.applyPluginFromLibs
import extensions.dependency.implementDependencies
import extensions.config.libs
import extensions.config.projectConfigs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

internal const val BETTER_NEPTUN_EXTENSION_NAME = "betterNeptun"

val filteredModules = ProjectModule.entries
    .filter { it.isParentModule }
    .map { it.path }
    .plus(":app")

fun Project.setupCompose() {
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

fun Project.setupNavigation3() {
    dependencies {
        implementDependencies(libs = libs, dependencyList = navigation3dependencies)
    }
}

fun Project.setupRoom() {
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

fun Project.setupKoin() {
    dependencies {
        implementDependencies(libs = libs, dependencyList = koinDependency)
    }
}

fun Project.setupSerialization() {
    pluginManager.applyPluginFromLibs(libs to listOf(serializationPlugin))
}

fun Project.setNamespace(namespaceSuffix: String) {
    extensions.getByType(LibraryExtension::class).apply {
        namespace = projectConfigs.namespace + ".$namespaceSuffix"
    }
}