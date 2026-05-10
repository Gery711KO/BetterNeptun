package extensions.config

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import kotlin.jvm.optionals.getOrElse

val Project.versionConfig
    get() = extensions.getByType<VersionCatalogsExtension>().named("config")

val Project.libs
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

val Project.projectConfigs: ProjectConfiguration
    get() = versionConfig.run {
        ProjectConfiguration(
            versionName = getVersion("versionName"),
            versionCode = getVersion("versionCode").toInt(),
            namespace = getVersion("namespace"),
            minSdk = getVersion("minSdk").toInt(),
            targetSdk = getVersion("targetSdk").toInt(),
            compileSdk = getVersion("compileSdk").toInt(),
            javaVersion = JavaVersion.toVersion(getVersion("javaVersion").toInt()),
        )
    }

/**
 * Safely retrieves a version from the catalog or throws a descriptive error.
 */
private fun VersionCatalog.getVersion(alias: String): String {
    return findVersion(alias).getOrElse {
        throw IllegalStateException("Version alias '$alias' not found in catalog '${this.name}'")
    }.toString()
}