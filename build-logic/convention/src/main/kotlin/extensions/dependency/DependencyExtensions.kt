package extensions.dependency

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project
import kotlin.jvm.optionals.getOrElse

/**
 * Applies plugins from the catalog. Throws if any plugin is missing.
 */
internal fun PluginManager.applyPluginFromLibs(vararg pluginGroups: Pair<VersionCatalog, List<String>>) {
    pluginGroups.forEach { (catalog, aliases) ->
        aliases.forEach { alias ->
            val plugin = catalog.findPlugin(alias).orElseThrow {
                IllegalStateException("Plugin alias '$alias' not found in Version Catalog")
            }.get()

            // Gradle's PluginManager.hasPlugin is efficient,
            // but we can just use 'apply' directly for most built-in plugins.
            // However, for KSP/Hilt, checking first prevents 're-configuration' overhead.
            if (!hasPlugin(plugin.pluginId)) {
                apply(plugin.pluginId)
            }
        }
    }
}

/**
 * Applies a dependency using the catalog.
 */
internal fun DependencyHandlerScope.implementDependency(
    libs: VersionCatalog,
    dependency: Dependency
) {
    when (dependency.type) {
        Dependency.Type.BUNDLE -> implement(libs, dependency.config, dependency.aliases, isBundle = true)
        Dependency.Type.DEPENDENCY -> implement(libs, dependency.config, dependency.aliases)
        Dependency.Type.PROJECT -> dependency.aliases.forEach { alias ->
            add(dependency.config, project(alias))
        }
        Dependency.Type.PLATFORM -> implement(libs, dependency.config, dependency.aliases, isPlatform = true)
    }
}

/**
 * Applies a list of dependencies using the catalog.
 */
internal fun DependencyHandlerScope.implementDependencies(
    libs: VersionCatalog,
    dependencyList: List<Dependency>
) {
    dependencyList.forEach { dep ->
        implementDependency(libs, dep)
    }
}

private fun DependencyHandlerScope.implement(
    libs: VersionCatalog,
    config: String,
    aliases: List<String>,
    isBundle: Boolean = false,
    isPlatform: Boolean = false
) {
    aliases.forEach { alias ->
        libs.run {
            if (isBundle) {
                findBundle(alias).getOrElse {
                    throw IllegalStateException("Bundle '$alias' not found in catalog '${name}'")
                }.let { add(config, it) }
            } else {
                findLibrary(alias).getOrElse {
                    throw IllegalStateException("Library '$alias' not found in catalog '${name}'")
                }.let {
                    add(config, if (isPlatform) platform(it) else it)
                }
            }
        }
    }
}