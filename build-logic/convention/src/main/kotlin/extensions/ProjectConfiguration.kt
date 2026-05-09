package extensions

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

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

val Project.betterNeptunConfig
    get() = extensions.getByType<BetterNeptunExtension>()

data class ProjectConfiguration(
    val versionName: String,
    val versionCode: Int,
    val namespace: String,
    val minSdk: Int,
    val targetSdk: Int,
    val compileSdk: Int,
    val javaVersion: JavaVersion,
)